package com.avaje.ebeaninternal.server.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Configuration for clustering using TCP sockets.
 */
public class SocketConfig {

  /**
   * This local server in host:port format.
   */
  private String localHostPort;

  /**
   * All the cluster members in host:port format.
   */
  private List<String> members = new ArrayList<String>();

  /**
   * core threads for the associated thread pool.
   */
  private int coreThreads = 2;

  /**
   * Max threads for the associated thread pool.
   */
  private int maxThreads = 16;

  private String threadPoolName = "EbeanCluster";

  private Properties properties;

  /**
   * Return the host and port for this server instance.
   */
  public String getLocalHostPort() {
    return localHostPort;
  }

  /**
   * Set the host and port for this server instance.
   */
  public void setLocalHostPort(String localHostPort) {
    this.localHostPort = localHostPort;
  }

  /**
   * Return all the host and port for all the members of the cluster.
   */
  public List<String> getMembers() {
    return members;
  }

  /**
   * Set all the host and port for all the members of the cluster.
   */
  public void setMembers(List<String> members) {
    this.members = members;
  }

  /**
   * Return the number of core threads to use.
   */
  public int getCoreThreads() {
    return coreThreads;
  }

  /**
   * Set the number of core threads to use.
   */
  public void setCoreThreads(int coreThreads) {
    this.coreThreads = coreThreads;
  }

  /**
   * Return the number of max threads to use.
   */
  public int getMaxThreads() {
    return maxThreads;
  }

  /**
   * Set the number of max threads to use.
   */
  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }

  /**
   * Return the thread pool name.
   */
  public String getThreadPoolName() {
    return threadPoolName;
  }

  /**
   * Set the thread pool name.
   */
  public void setThreadPoolName(String threadPoolName) {
    this.threadPoolName = threadPoolName;
  }

  /**
   * Load the properties into the configuration.
   */
  public void loadFromProperties(Properties properties) {

    this.properties = properties;
    this.threadPoolName = getProperty("ebean.cluster.threadPoolName", threadPoolName);
    this.localHostPort = getProperty("ebean.cluster.localHostPort", localHostPort);
    this.coreThreads = getInt("ebean.cluster.coreThreads", coreThreads);
    this.maxThreads = getInt("ebean.cluster.maxThreads", maxThreads);

    String rawMembers = getProperty("ebean.cluster.members", "");
    String[] split = rawMembers.split("[,;]");
    for (String rawMember : split) {
      members.add(rawMember.trim());
    }
  }

  private int getInt(String key, int value) {
    return Integer.parseInt(getProperty(key, Integer.toString(value)));
  }

  private String getProperty(String key, String localHostPort) {
    return properties.getProperty(key, localHostPort).trim();
  }
}
