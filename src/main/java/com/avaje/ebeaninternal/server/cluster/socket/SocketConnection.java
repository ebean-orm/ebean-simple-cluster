package com.avaje.ebeaninternal.server.cluster.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The client side of a TCP Socket connection.
 */
class SocketConnection {

  /**
   * The underlying ObjectInputStream.
   */
  private DataInputStream ois;

  /**
   * The underlying inputStream.
   */
  private InputStream is;

  /**
   * The underlying outputStream.
   */
  private OutputStream os;

  /**
   * The underlying socket.
   */
  private Socket socket;

  /**
   * Create for a given Socket.
   */
  SocketConnection(Socket socket) throws IOException {
    this.is = socket.getInputStream();
    this.os = socket.getOutputStream();
    this.socket = socket;
  }

  /**
   * Disconnect from the server.
   */
  void disconnect() throws IOException {
    os.flush();
    socket.close();
  }

  /**
   * Get the DataInputStream to read the message.
   */
  DataInputStream getDataInputStream() throws IOException {
    if (ois == null) {
      ois = new DataInputStream(is);
    }
    return ois;
  }

}
