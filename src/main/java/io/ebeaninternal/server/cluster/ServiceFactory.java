package io.ebeaninternal.server.cluster;

import io.ebean.config.ContainerConfig;
import io.ebeaninternal.server.cluster.socket.SocketClusterBroadcast;

/**
 * Factory for creating the ClusterBroadcast service.
 */
public class ServiceFactory implements ClusterBroadcastFactory {

  @Override
  public ClusterBroadcast create(ClusterManager manager, ContainerConfig config) {

    SocketConfig socketConfig = new SocketConfig();
    socketConfig.loadFromProperties(config.getProperties());

    return new SocketClusterBroadcast(manager, socketConfig);
  }
}
