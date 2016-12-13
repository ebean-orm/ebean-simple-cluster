package io.ebeaninternal.server.cluster.socket;

import io.ebeaninternal.server.cluster.ClusterBroadcast;
import io.ebeaninternal.server.cluster.ClusterManager;
import io.ebeaninternal.server.cluster.SocketConfig;
import io.ebeaninternal.server.cluster.message.ClusterMessage;
import io.ebeaninternal.server.cluster.message.MessageReadWrite;
import io.ebeaninternal.server.transaction.RemoteTransactionEvent;
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

  private static final Logger clusterLogger = LoggerFactory.getLogger("org.avaje.ebean.Cluster");

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
    clusterLogger.info("Clustering using local[{}] members[{}]",localHostPort, members);

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
    this.listener = new SocketClusterListener(this, local.getPort(), config.getThreadPoolName());
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
      clusterLogger.info("Register as online with member [{}]", members[i].getHostPort(), online);
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
      clusterLogger.info("Cluster member [{}] online[{}]", fullName, online);
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
      byte[] data = messageReadWrite.write(remoteTransEvent);
      ClusterMessage msg = ClusterMessage.transEvent(data);
      broadcast(msg);
    } catch (Exception e) {
      logger.error("Error sending RemoteTransactionEvent " + remoteTransEvent + " to cluster members.", e);
    }
  }

  private void broadcast(ClusterMessage msg) {
    for (int i = 0; i < members.length; i++) {
      send(members[i], msg);
    }
  }

  /**
   * Leave the cluster.
   */
  private void deregister() {
    clusterLogger.info("Leaving cluster");
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
      ClusterMessage message = ClusterMessage.read(request.getDataInputStream());
      if (logger.isTraceEnabled()) {
        logger.trace("... received msg: {}", message);
      }

      if (message.isRegisterEvent()) {
        setMemberOnline(message.getRegisterHost(), message.isRegister());

      } else {
        countIncoming.incrementAndGet();
        RemoteTransactionEvent transEvent = messageReadWrite.read(message.getData());
        transEvent.run();
      }

      // instance shutting down
      return message.isRegisterEvent() && !message.isRegister();

    } catch (InterruptedIOException e) {
      logger.info("Timeout waiting for message", e);
      try {
        request.disconnect();
      } catch (IOException ex) {
        logger.info("Error disconnecting after timeout", ex);
      }
      return true;

    } catch (EOFException e) {
      logger.debug("EOF disconnecting");
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
