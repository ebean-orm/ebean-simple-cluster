package com.avaje.ebeaninternal.server.cluster;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.testng.Assert.*;

public class SocketConfigTest {

  @Test
  public void loadFromProperties() throws Exception {

    Properties properties = new Properties();
    properties.setProperty("ebean.cluster.localHostPort", "127.0.0.1:9898 ");
    properties.setProperty("ebean.cluster.members", "127.0.0.1:9901,127.0.0.1:9902; 127.0.0.1:9903 ;  127.0.0.1:9904 ");
    properties.setProperty("ebean.cluster.coreThreads", "4");
    properties.setProperty("ebean.cluster.maxThreads", "44");
    properties.setProperty("ebean.cluster.threadPoolName", "somePoolName");

    SocketConfig config = new SocketConfig();
    config.loadFromProperties(properties);

    assertEquals(config.getCoreThreads(), 4);
    assertEquals(config.getMaxThreads(), 44);
    assertEquals(config.getLocalHostPort(), "127.0.0.1:9898");
    assertEquals(config.getThreadPoolName(), "somePoolName");
    assertThat(config.getMembers()).containsExactly("127.0.0.1:9901","127.0.0.1:9902","127.0.0.1:9903","127.0.0.1:9904");

  }

  @Test
  public void setters() {

    SocketConfig config = new SocketConfig();
    config.setCoreThreads(4);
    config.setMaxThreads(44);
    config.setThreadPoolName("somePoolName");
    config.setMembers(Arrays.asList("127.0.0.1:9901","127.0.0.1:9902","127.0.0.1:9903","127.0.0.1:9904"));
    config.setLocalHostPort("127.0.0.1:9898");

    assertEquals(config.getCoreThreads(), 4);
    assertEquals(config.getMaxThreads(), 44);
    assertEquals(config.getLocalHostPort(), "127.0.0.1:9898");
    assertEquals(config.getThreadPoolName(), "somePoolName");
    assertThat(config.getMembers()).containsExactly("127.0.0.1:9901","127.0.0.1:9902","127.0.0.1:9903","127.0.0.1:9904");

  }
}