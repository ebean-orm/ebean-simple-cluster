package com.avaje.ebeaninternal.server.cluster.message;

import com.avaje.ebeaninternal.server.cluster.BinaryMessage;
import com.avaje.ebeaninternal.server.cluster.BinaryMessageList;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Writes BinaryMessageList to DataHolder raw byte[].
 */
public class BinaryDataWriter {

  private final BinaryMessageList messageList;

  private final ByteArrayOutputStream buffer;

  private final DataOutputStream dataOut;

  private final String serverName;

  public BinaryDataWriter(String serverName, BinaryMessageList messageList) {
    this.serverName = serverName;
    this.messageList = messageList;
    this.buffer = new ByteArrayOutputStream(256);
    this.dataOut = new DataOutputStream(buffer);
  }

  public DataHolder createDataHolder() throws IOException {

    dataOut.writeUTF(serverName);
    for (BinaryMessage msg : messageList.getList()) {
      write(msg);
    }
    dataOut.writeBoolean(false);
    dataOut.flush();
    return new DataHolder(buffer.toByteArray());
  }

  private void write(BinaryMessage msg) throws IOException {

    dataOut.writeBoolean(true);
    dataOut.write(msg.getByteArray());
  }

}
