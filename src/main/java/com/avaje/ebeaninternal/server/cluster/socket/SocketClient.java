package com.avaje.ebeaninternal.server.cluster.socket;

import com.avaje.ebeaninternal.server.cluster.message.ClusterMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * The client side of the socket clustering.
 */
class SocketClient {

  private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

  private final InetSocketAddress address;

  private final String hostPort;

  private boolean online;

  private Socket socket;

  private OutputStream os;

  private ObjectOutputStream oos;

  /**
   * Construct with an IP address and port.
   */
  SocketClient(InetSocketAddress address) {
    this.address = address;
    this.hostPort = address.getHostName() + ":" + address.getPort();
  }

  public String toString() {
    return address.toString();
  }

  String getHostPort() {
    return hostPort;
  }

  int getPort() {
    return address.getPort();
  }

  boolean isOnline() {
    return online;
  }

  void setOnline(boolean online) throws IOException {
    if (online) {
      setOnline();
    } else {
      disconnect();
    }
  }

  void reconnect() throws IOException {
    disconnect();
    connect();
  }


  void disconnect() {
    this.online = false;
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        String msg = "Error disconnecting from Cluster member " + hostPort;
        logger.info(msg, e);
      }
      os = null;
      oos = null;
      socket = null;
    }
  }

  boolean register(ClusterMessage registerMsg) {
    try {
      setOnline();
      send(registerMsg);
      return true;
    } catch (IOException e) {
      disconnect();
      return false;
    }
  }

  void send(ClusterMessage msg) throws IOException {
    if (online) {
      writeObject(msg);
    }
  }

  private void writeObject(ClusterMessage msg) throws IOException {
    if (oos == null) {
      this.oos = new ObjectOutputStream(os);
    }
    oos.writeObject(msg);
    oos.flush();
  }

  /**
   * Set whether the client is thought to be online.
   */
  private void setOnline() throws IOException {
    connect();
    this.online = true;
  }

  private void connect() throws IOException {
    if (socket != null) {
      throw new IllegalStateException("Already got a socket connection?");
    }
    Socket s = new Socket();
    s.setKeepAlive(true);
    s.connect(address);

    this.socket = s;
    this.os = socket.getOutputStream();
  }

}
