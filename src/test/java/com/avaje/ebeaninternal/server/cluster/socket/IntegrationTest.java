package com.avaje.ebeaninternal.server.cluster.socket;


import com.avaje.ebean.AccessEbeanServerFactory;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.common.SpiContainer;
import com.avaje.ebean.config.ContainerConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.AbstractBeanPersistListener;
import com.avaje.ebean.event.BeanPersistListener;
import org.example.domain.Customer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class IntegrationTest {

  private final BeanListener server0Listener;
  private final BeanListener server1Listener;
  private final SpiContainer container0;
  private final SpiContainer container1;
  private EbeanServer server0;

  private Customer customer;

  IntegrationTest() throws InterruptedException {

    this.server0Listener = new BeanListener();
    this.server1Listener = new BeanListener();

    ContainerConfig containerConfig0 = createContainerConfig("127.0.0.1:9876", "node0");
    this.container0 = AccessEbeanServerFactory.createContainer(containerConfig0);
    this.server0 = container0.createServer(createServerConfig("db0", server0Listener));

    ContainerConfig containerConfig1 = createContainerConfig("127.0.0.1:9866", "node1");
    this.container1 = AccessEbeanServerFactory.createContainer(containerConfig1);
    container1.createServer(createServerConfig("db0", server1Listener));

    sleep();
  }

  private ServerConfig createServerConfig(String name, BeanPersistListener listener) {
    ServerConfig serverConfig = new ServerConfig();
    serverConfig.setName(name);
    serverConfig.loadFromProperties();
    serverConfig.setName("db");
    serverConfig.add(listener);
    serverConfig.setRegister(false);
    serverConfig.setDefaultServer(false);
    return  serverConfig;
  }


  private ContainerConfig createContainerConfig(String local, String threadPoolName) {

    ContainerConfig container = new ContainerConfig();
    container.setClusterActive(true);
    Properties properties = new Properties();
    properties.setProperty("ebean.cluster.threadPoolName", threadPoolName);
    properties.setProperty("ebean.cluster.localHostPort", local);
    properties.setProperty("ebean.cluster.members", "127.0.0.1:9876,127.0.0.1:9866");
    container.setProperties(properties);
    return container;
  }


  @Test
  public void insert() throws Exception {

    customer = new Customer("customer");
    server0.save(customer);

    sleep();

    assertSame(server0Listener.localInserted, customer);
    assertEquals(server1Listener.remoteInsertId, customer.getId());
  }

  @Test(dependsOnMethods = "insert")
  public void update() throws InterruptedException {

    customer.setName("customerMod");
    server0.save(customer);
    sleep();

    assertSame(server0Listener.localUpdated, customer);
    assertEquals(server1Listener.remoteUpdateId, customer.getId());
  }

  @Test(dependsOnMethods = "update")
  public void delete() throws InterruptedException {

    server0.delete(customer);
    sleep();

    assertSame(server0Listener.localDeleted, customer);
    assertEquals(server1Listener.remoteDeleteId, customer.getId());
  }

  private void sleep() throws InterruptedException {
    Thread.sleep(1000);
  }

  @AfterClass
  public void shutdown() {
    container0.shutdown();
    container1.shutdown();
  }

  class BeanListener extends AbstractBeanPersistListener {

    Object remoteInsertId;
    Object remoteUpdateId;
    Object remoteDeleteId;

    Object localInserted;
    Object localUpdated;
    Object localDeleted;

    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return cls.equals(Customer.class);
    }

    @Override
    public void remoteInsert(Object id) {
      this.remoteInsertId = id;
    }

    @Override
    public void remoteUpdate(Object id) {
      this.remoteUpdateId = id;
    }

    @Override
    public void remoteDelete(Object id) {
      this.remoteDeleteId = id;
    }

    @Override
    public boolean inserted(Object bean) {
      this.localInserted = bean;
      return true;
    }

    @Override
    public boolean updated(Object bean, Set<String> updatedProperties) {
      this.localUpdated = bean;
      return true;
    }

    @Override
    public boolean deleted(Object bean) {
      this.localDeleted = bean;
      return true;
    }
  }

}