package io.ebean;

import io.ebean.common.SpiContainer;
import io.ebean.config.ContainerConfig;

/**
 * To access protected createContainer() method.
 */
public class AccessEbeanServerFactory {

  public static SpiContainer createContainer(ContainerConfig config) {

    return EbeanServerFactory.createContainer(config);
  }

}
