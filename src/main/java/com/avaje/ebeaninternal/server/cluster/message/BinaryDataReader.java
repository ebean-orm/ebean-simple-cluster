package com.avaje.ebeaninternal.server.cluster.message;

import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.TransactionEventTable;
import com.avaje.ebeaninternal.server.cluster.BinaryMessage;
import com.avaje.ebeaninternal.server.cluster.ClusterManager;
import com.avaje.ebeaninternal.server.transaction.BeanPersistIds;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Reads the binary message returning RemoteTransactionEvent.
 */
class BinaryDataReader {

  private final ClusterManager clusterManager;
  private final DataInputStream dataInput;

  private SpiEbeanServer server;
  private RemoteTransactionEvent event;

  BinaryDataReader(ClusterManager clusterManager, byte[] data) {
    this.clusterManager = clusterManager;
    this.dataInput = new DataInputStream(new ByteArrayInputStream(data));
  }

  /**
   * Read the binary message returning a RemoteTransactionEvent.
   */
  RemoteTransactionEvent read() throws IOException {

    String serverName = dataInput.readUTF();

    this.server = (SpiEbeanServer)clusterManager.getServer(serverName);
    if (server == null) {
      throw new IllegalStateException("EbeanServer not found for name ["+serverName+"]");
    }
    this.event = new RemoteTransactionEvent(server);
    boolean more = dataInput.readBoolean();
    while (more) {
      readMessage();
      more = dataInput.readBoolean();
    }
    return event;
  }

  private void readMessage() throws IOException {

    int msgType = dataInput.readInt();
    switch (msgType) {
      case BinaryMessage.TYPE_BEANIUD:
        event.addBeanPersistIds(BeanPersistIds.readBinaryMessage(server, dataInput));
        break;

      case BinaryMessage.TYPE_TABLEIUD:
        event.addTableIUD(TransactionEventTable.TableIUD.readBinaryMessage(dataInput));
        break;

      default:
        throw new RuntimeException("Invalid Transaction msgType " + msgType);
    }
  }
}
