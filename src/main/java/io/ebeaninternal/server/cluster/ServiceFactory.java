package io.ebeaninternal.server.cluster;

import io.ebeaninternal.server.cluster.socket.SocketClusterBroadcast;

import java.util.Properties;

/**
 * Factory for creating the ClusterBroadcast service.
 */
public class ServiceFactory implements ClusterBroadcastFactory {

  @Override
  public ClusterBroadcast create(ClusterManager manager, Properties properties) {

    SocketConfig config = new SocketConfig();
    config.loadFromProperties(properties);

    return new SocketClusterBroadcast(manager, config);
  }
}
