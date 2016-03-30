package com.avaje.ebean;

import com.avaje.ebean.common.SpiContainer;
import com.avaje.ebean.config.ContainerConfig;

/**
 * To access protected createContainer() method.
 */
public class AccessEbeanServerFactory {

  public static SpiContainer createContainer(ContainerConfig config) {

    return EbeanServerFactory.createContainer(config);
  }

}
