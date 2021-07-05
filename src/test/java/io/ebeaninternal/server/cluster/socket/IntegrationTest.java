package io.ebeaninternal.server.cluster.socket;


import io.ebean.AccessEbeanServerFactory;
import io.ebean.Database;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.event.AbstractBeanPersistListener;
import io.ebean.event.BeanPersistListener;
import io.ebean.service.SpiContainer;
import org.example.domain.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

class IntegrationTest {

  private final BeanListener server0Listener;
  private final BeanListener server1Listener;
  private final SpiContainer container0;
  private final SpiContainer container1;
  private final Database server0;
  private final Database server1;

  private Customer customer;

  IntegrationTest() throws InterruptedException {

    this.server0Listener = new BeanListener();
    this.server1Listener = new BeanListener();

    ContainerConfig containerConfig0 = createContainerConfig("127.0.0.1:9876", "node0");
    this.container0 = AccessEbeanServerFactory.createContainer(containerConfig0);
    this.server0 = container0.createServer(createServerConfig("db0", server0Listener));

    ContainerConfig containerConfig1 = createContainerConfig("127.0.0.1:9866", "node1");
    this.container1 = AccessEbeanServerFactory.createContainer(containerConfig1);
    this.server1 = container1.createServer(createServerConfig("db0", server1Listener));

    sleep();
  }

  private DatabaseConfig createServerConfig(String name, BeanPersistListener listener) {
    DatabaseConfig serverConfig = new DatabaseConfig();
    serverConfig.setName(name);
    serverConfig.loadFromProperties();
    serverConfig.setName("db");
    serverConfig.add(listener);
    serverConfig.setRegister(false);
    serverConfig.setDefaultServer(false);
    return serverConfig;
  }


  private ContainerConfig createContainerConfig(String local, String threadPoolName) {

    ContainerConfig container = new ContainerConfig();
    container.setActive(true);
    Properties properties = new Properties();
    properties.setProperty("ebean.cluster.threadPoolName", threadPoolName);
    properties.setProperty("ebean.cluster.localHostPort", local);
    properties.setProperty("ebean.cluster.members", "127.0.0.1:9876,127.0.0.1:9866");
    container.setProperties(properties);
    return container;
  }

  @Test
  void runAllTests() throws Exception {
    insert();
    update();
    delete();
    deleteById();
    tableIUD();
    bulkInsert();
    bulkUpdate();
    bulkDelete();
  }

  void insert() throws Exception {

    server1.getServerCacheManager().getBeanCache(Customer.class);
    server1.getServerCacheManager().getNaturalKeyCache(Customer.class);
    server1.getServerCacheManager().getQueryCache(Customer.class);

    customer = new Customer("customer");
    server0.save(customer);

    sleep();
    server1.find(Customer.class).setUseCache(true).setId(customer.getId());

    assertSame(server0Listener.localInserted, customer);
  }

  void update() throws InterruptedException {

    customer.setName("customerMod");
    server0.save(customer);
    sleep();

    assertSame(server0Listener.localUpdated, customer);
  }

  void delete() throws InterruptedException {

    server0.delete(customer);
    sleep();

    assertSame(server0Listener.localDeleted, customer);
  }

  void deleteById() {

    Customer other = new Customer("shortLived");
    server0.save(other);
    Customer other2 = new Customer("shortLived");
    server0.save(other2);
    Customer other3 = new Customer("shortLived");
    server0.save(other3);

    try (Transaction transaction = server0.beginTransaction()) {
      server0.delete(Customer.class, other.getId());
      server0.delete(Customer.class, other2.getId());
      server0.delete(other3);
      transaction.commit();
    }
  }

  void tableIUD() throws InterruptedException {
    server0.externalModification("customer", true, true, true);
    sleep();
  }

  void bulkInsert() throws InterruptedException {

    SqlUpdate sqlInsert = server0.sqlUpdate("insert into customer (id, name, version) values(900, :name, 1)");
    sqlInsert.setParameter("name", "bulkTest");
    sqlInsert.execute();

    sleep();
  }

  void bulkUpdate() throws InterruptedException {
    SqlUpdate sqlUpdate = server0.sqlUpdate("update customer set notes = 'foo' where notes is null");
    sqlUpdate.execute();

    sleep();
  }

  void bulkDelete() throws InterruptedException {

    SqlUpdate sqlDelete = server0.sqlUpdate("delete from customer where id > 100");
    sqlDelete.execute();

    sleep();
  }

  private void sleep() throws InterruptedException {
    Thread.sleep(50);
  }

  @AfterEach
  void shutdown() {
    container0.shutdown();
    container1.shutdown();
  }

  static class BeanListener extends AbstractBeanPersistListener {

    Object localInserted;
    Object localUpdated;
    Object localDeleted;

    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return cls.equals(Customer.class);
    }

    @Override
    public void inserted(Object bean) {
      this.localInserted = bean;
    }

    @Override
    public void updated(Object bean, Set<String> updatedProperties) {
      this.localUpdated = bean;
    }

    @Override
    public void deleted(Object bean) {
      this.localDeleted = bean;
    }
  }

}
