package com.avaje.ebeaninternal.server.cluster.message;

import com.avaje.ebeaninternal.server.cluster.BinaryMessageList;
import com.avaje.ebeaninternal.server.cluster.ClusterManager;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;

import java.io.IOException;

/**
 * Mechanism to convert RemoteTransactionEvent to/from byte[] content.
 */
public class MessageReadWrite {

  private final ClusterManager clusterManager;

  public MessageReadWrite(ClusterManager clusterManager) {
    this.clusterManager = clusterManager;
  }

  /**
   * Convert the RemoteTransactionEvent to byte[] content.
   */
  public byte[] write(RemoteTransactionEvent transEvent) throws IOException {

    BinaryMessageList messageList = new BinaryMessageList();
    transEvent.writeBinaryMessage(messageList);

    BinaryDataWriter writer = new BinaryDataWriter(transEvent.getServerName(), messageList);
    return writer.write();
  }

  /**
   * Convert the byte[] content to RemoteTransactionEvent.
   */
  public RemoteTransactionEvent read(byte[] data) throws IOException {

    BinaryDataReader reader = new BinaryDataReader(clusterManager, data);
    return reader.read();
  }
}
