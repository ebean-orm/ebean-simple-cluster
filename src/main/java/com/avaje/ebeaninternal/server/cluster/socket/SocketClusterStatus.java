package com.avaje.ebeaninternal.server.cluster.socket;

/**
 * The current state of this cluster member.
 */
public class SocketClusterStatus {

  private final int currentGroupSize;
  private final long incoming;
  private final long outgoing;

  public SocketClusterStatus(int currentGroupSize, long incoming, long txnOutgoing) {
    this.currentGroupSize = currentGroupSize;
    this.incoming = incoming;
    this.outgoing = txnOutgoing;
  }

  /**
   * Return the number of members of the cluster currently online.
   */
  public int size() {
    return currentGroupSize;
  }

  /**
   * Return the number of Remote transactions received.
   */
  public long getIncoming() {
    return incoming;
  }

  /**
   * Return the number of transactions sent to the cluster.
   */
  public long getOutgoing() {
    return outgoing;
  }

}
