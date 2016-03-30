package com.avaje.ebeaninternal.server.cluster.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The message broadcast around the cluster.
 */
public class ClusterMessage {

  private final String registerHost;

  private final boolean register;

  private final byte[] data;

  /**
   * Create a register message.
   */
  public static ClusterMessage register(String registerHost, boolean register) {
    return new ClusterMessage(registerHost, register);
  }

  /**
   * Create a transaction message.
   */
  public static ClusterMessage transEvent(byte[] data) {
    return new ClusterMessage(data);
  }

  /**
   * Create for register online/offline message.
   */
  private ClusterMessage(String registerHost, boolean register) {
    this.registerHost = registerHost;
    this.register = register;
    this.data = null;
  }

  /**
   * Create for a transaction message.
   */
  private ClusterMessage(byte[] data) {
    this.data = data;
    this.registerHost = null;
    this.register = false;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (registerHost != null) {
      sb.append("register ");
      sb.append(register);
      sb.append(" ");
      sb.append(registerHost);
    } else {
      sb.append("transEvent ");
    }
    return sb.toString();
  }

  public boolean isRegisterEvent() {
    return registerHost != null;
  }

  public String getRegisterHost() {
    return registerHost;
  }

  public boolean isRegister() {
    return register;
  }

  /**
   * Write the message in binary form.
   */
  public void write(DataOutputStream dataOutput) throws IOException {

    boolean dataMessage = data != null;
    dataOutput.writeBoolean(dataMessage);
    if (dataMessage) {
      dataOutput.writeInt(data.length);
      dataOutput.write(data);
    } else {
      // write register message
      dataOutput.writeUTF(getRegisterHost());
      dataOutput.writeBoolean(isRegister());
    }
    dataOutput.flush();
  }

  /**
   * Read the message from binary form.
   */
  public static ClusterMessage read(DataInputStream dataInput) throws IOException {

    boolean dataMessage = dataInput.readBoolean();
    if (dataMessage) {
      int length = dataInput.readInt();
      byte[] data = new byte[length];
      dataInput.readFully(data);
      return new ClusterMessage(data);

    } else {
      String host = dataInput.readUTF();
      boolean registered = dataInput.readBoolean();
      return new ClusterMessage(host, registered);
    }
  }

  public byte[] getData() {
    return data;
  }
}
