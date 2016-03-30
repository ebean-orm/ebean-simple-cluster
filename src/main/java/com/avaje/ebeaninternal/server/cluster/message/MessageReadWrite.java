package com.avaje.ebeaninternal.server.cluster.message;

import com.avaje.ebeaninternal.server.cluster.BinaryMessageList;
import com.avaje.ebeaninternal.server.cluster.ClusterManager;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public DataHolder createDataHolder(RemoteTransactionEvent transEvent) throws IOException {

    BinaryMessageList messageList = new BinaryMessageList();
    transEvent.writeBinaryMessage(messageList);

    BinaryDataWriter writer = new BinaryDataWriter(transEvent.getServerName(), messageList);
    return writer.createDataHolder();
  }

  /**
   * Convert the byte[] content to RemoteTransactionEvent.
   */
  public RemoteTransactionEvent read(DataHolder dataHolder) throws IOException {

    BinaryDataReader reader = new BinaryDataReader(clusterManager, dataHolder.getData());

    return reader.read();
  }
}
