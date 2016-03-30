package com.avaje.ebeaninternal.server.cluster.socket;

import com.avaje.ebeaninternal.server.cluster.ClusterBroadcast;
import com.avaje.ebeaninternal.server.cluster.ClusterManager;
import com.avaje.ebeaninternal.server.cluster.message.DataHolder;
import com.avaje.ebeaninternal.server.cluster.message.MessageReadWrite;
import com.avaje.ebeaninternal.server.cluster.SocketConfig;
import com.avaje.ebeaninternal.server.cluster.message.ClusterMessage;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Broadcast messages across the cluster using sockets.
 */
public class SocketClusterBroadcast implements ClusterBroadcast {

  private static final Logger logger = LoggerFactory.getLogger(SocketClusterBroadcast.class);

  private final SocketClient local;

  private final HashMap<String, SocketClient> clientMap;

  private final SocketClusterListener listener;

  private final SocketClient[] members;

  private final MessageReadWrite messageReadWrite;

  private final AtomicLong countOutgoing = new AtomicLong();

  private final AtomicLong countIncoming = new AtomicLong();

  public SocketClusterBroadcast(ClusterManager manager, SocketConfig config) {

    this.messageReadWrite = new MessageReadWrite(manager);

    String localHostPort = config.getLocalHostPort();
    List<String> members = config.getMembers();
    logger.info("Clustering using Sockets local[{}] members[{}]",localHostPort, members);

    this.local = new SocketClient(parseFullName(localHostPort));
    this.clientMap = new HashMap<String, SocketClient>();

    for (String memberHostPort : members) {
      InetSocketAddress member = parseFullName(memberHostPort);
      SocketClient client = new SocketClient(member);
      if (!local.getHostPort().equalsIgnoreCase(client.getHostPort())) {
        // don't add the local one ...
        clientMap.put(client.getHostPort(), client);
      }
    }

    this.members = clientMap.values().toArray(new SocketClient[clientMap.size()]);
    this.listener = new SocketClusterListener(this, local.getPort(), config.getCoreThreads(), config.getMaxThreads(), config.getThreadPoolName());
  }

  String getHostPort() {
    return local.getHostPort();
  }

  /**
   * Return the current status of this instance.
   */
  public SocketClusterStatus getStatus() {

    // count of online members
    int currentGroupSize = 0;
    for (int i = 0; i < members.length; i++) {
      if (members[i].isOnline()) {
        ++currentGroupSize;
      }
    }
    long txnIn = countIncoming.get();
    long txnOut = countOutgoing.get();

    return new SocketClusterStatus(currentGroupSize, txnIn, txnOut);
  }

  public void startup() {
    listener.startListening();
    register();
  }

  public void shutdown() {
    deregister();
    listener.shutdown();
  }

  /**
   * Register with all the other members of the Cluster.
   */
  private void register() {

    ClusterMessage h = ClusterMessage.register(local.getHostPort(), true);

    for (int i = 0; i < members.length; i++) {
      boolean online = members[i].register(h);
      logger.info("Register as online with Member [{}]", members[i].getHostPort(), online);
    }
  }

  private void send(SocketClient client, ClusterMessage msg) {

    try {
      // alternative would be to connect/disconnect here but prefer to use keep alive
      if (logger.isTraceEnabled()) {
        logger.trace("... send to member {} broadcast msg: {}", client, msg);
      }
      client.send(msg);

    } catch (Exception ex) {
      logger.error("Error sending message", ex);
      try {
        client.reconnect();
      } catch (IOException e) {
        logger.error("Error trying to reconnect", ex);
      }
    }
  }

  private void setMemberOnline(String fullName, boolean online) throws IOException {
    synchronized (clientMap) {
      logger.info("Cluster Member [{}] online[{}]", fullName, online);
      SocketClient member = clientMap.get(fullName);
      member.setOnline(online);
    }
  }

  /**
   * Send the payload to all the members of the cluster.
   */
  public void broadcast(RemoteTransactionEvent remoteTransEvent) {
    try {
      countOutgoing.incrementAndGet();
      DataHolder dataHolder = messageReadWrite.createDataHolder(remoteTransEvent);
      ClusterMessage msg = ClusterMessage.transEvent(dataHolder);
      broadcast(msg);
    } catch (Exception e) {
      logger.error("Error sending RemoteTransactionEvent " + remoteTransEvent + " to cluster members.", e);
    }
  }

  private void broadcast(ClusterMessage msg) {

    if (logger.isTraceEnabled()) {
      logger.trace("... broadcast msg: {}", msg);
    }
    for (int i = 0; i < members.length; i++) {
      send(members[i], msg);
    }
  }

  /**
   * Leave the cluster.
   */
  private void deregister() {

    ClusterMessage h = ClusterMessage.register(local.getHostPort(), false);
    broadcast(h);
    for (int i = 0; i < members.length; i++) {
      members[i].disconnect();
    }
  }

  /**
   * Process an incoming Cluster message.
   */
  boolean process(SocketConnection request) throws ClassNotFoundException {

    try {
      ClusterMessage h = (ClusterMessage) request.readObject();
      if (logger.isTraceEnabled()) {
        logger.trace("... received msg: {}", h);
      }

      if (h.isRegisterEvent()) {
        setMemberOnline(h.getRegisterHost(), h.isRegister());

      } else {
        countIncoming.incrementAndGet();
        DataHolder dataHolder = h.getDataHolder();
        RemoteTransactionEvent transEvent = messageReadWrite.read(dataHolder);
        transEvent.run();
      }

      // instance shutting down
      return h.isRegisterEvent() && !h.isRegister();

    } catch (InterruptedIOException e) {
      logger.info("Timeout waiting for message", e);
      try {
        request.disconnect();
      } catch (IOException ex) {
        logger.info("Error disconnecting after timeout", ex);
      }
      return true;

    } catch (EOFException e) {
      logger.info("EOF disconnecting");
      return true;
    } catch (IOException e) {
      logger.info("IO Error waiting/reading message", e);
      return true;
    }
  }

  /**
   * Parse a host:port into a InetSocketAddress.
   */
  private InetSocketAddress parseFullName(String hostAndPort) {

    try {
      hostAndPort = hostAndPort.trim();
      int colonPos = hostAndPort.indexOf(":");
      if (colonPos == -1) {
        String msg = "No colon \":\" in " + hostAndPort;
        throw new IllegalArgumentException(msg);
      }
      String host = hostAndPort.substring(0, colonPos);
      String sPort = hostAndPort.substring(colonPos + 1, hostAndPort.length());
      int port = Integer.parseInt(sPort);

      return new InetSocketAddress(host, port);

    } catch (Exception ex) {
      throw new RuntimeException("Error parsing [" + hostAndPort + "] for the form [host:port]", ex);
    }
  }

}
