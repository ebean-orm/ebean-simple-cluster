package com.avaje.ebean.testdouble;

import com.avaje.ebean.AutoTune;
import com.avaje.ebean.BeanState;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.PersistenceContextScope;
import com.avaje.ebean.Query;
import com.avaje.ebean.TDEbeanServer;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.TxScope;
import com.avaje.ebean.ValuePair;
import com.avaje.ebean.bean.BeanCollection;
import com.avaje.ebean.bean.CallStack;
import com.avaje.ebean.bean.EntityBeanIntercept;
import com.avaje.ebean.bean.ObjectGraphNode;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.DatabasePlatform;
import com.avaje.ebean.event.readaudit.ReadAuditLogger;
import com.avaje.ebean.event.readaudit.ReadAuditPrepare;
import com.avaje.ebean.meta.MetaInfoManager;
import com.avaje.ebeaninternal.api.LoadBeanRequest;
import com.avaje.ebeaninternal.api.LoadManyRequest;
import com.avaje.ebeaninternal.api.ScopeTrans;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.api.SpiTransaction;
import com.avaje.ebeaninternal.api.TransactionEventTable;
import com.avaje.ebeaninternal.server.core.timezone.DataTimeZone;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.query.CQuery;
import com.avaje.ebeaninternal.server.transaction.RemoteTransactionEvent;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * Test double for SpiEbeanServer.
 */
public class TDSpiEbeanServer extends TDEbeanServer implements SpiEbeanServer {

  String name;

  public TDSpiEbeanServer(String name) {
    this.name = name;
  }

  @Override
  public void shutdownManaged() {

  }

  @Override
  public boolean isCollectQueryOrigins() {
    return false;
  }

  @Override
  public boolean isUpdateAllPropertiesInBatch() {
    return false;
  }

  @Override
  public ServerConfig getServerConfig() {
    return null;
  }

  @Override
  public DatabasePlatform getDatabasePlatform() {
    return null;
  }

  @Override
  public CallStack createCallStack() {
    return null;
  }

  @Override
  public PersistenceContextScope getPersistenceContextScope(SpiQuery<?> query) {
    return null;
  }

  @Override
  public DataTimeZone getDataTimeZone() {
    return null;
  }

  @Override
  public ReadAuditLogger getReadAuditLogger() {
    return null;
  }

  @Override
  public ReadAuditPrepare getReadAuditPrepare() {
    return null;
  }


  @Override
  public void clearQueryStatistics() {

  }

  @Override
  public BeanDescriptor<?> getBeanDescriptorByQueueId(String queueId) {
    return null;
  }

  @Override
  public List<BeanDescriptor<?>> getBeanDescriptors() {
    return null;
  }

  @Override
  public <T> BeanDescriptor<T> getBeanDescriptor(Class<T> type) {
    return null;
  }

  @Override
  public BeanDescriptor<?> getBeanDescriptorById(String descriptorId) {
    return null;
  }

  @Override
  public List<BeanDescriptor<?>> getBeanDescriptors(String tableName) {
    return null;
  }

  @Override
  public void externalModification(TransactionEventTable event) {

  }

  @Override
  public SpiTransaction createServerTransaction(boolean isExplicit, int isolationLevel) {
    return null;
  }

  @Override
  public SpiTransaction getCurrentServerTransaction() {
    return null;
  }

  @Override
  public ScopeTrans createScopeTrans(TxScope txScope) {
    return null;
  }

  @Override
  public SpiTransaction createQueryTransaction() {
    return null;
  }

  @Override
  public void remoteTransactionEvent(RemoteTransactionEvent event) {

  }

  @Override
  public <T> CQuery<T> compileQuery(Query<T> query, Transaction t) {
    return null;
  }

  @Override
  public <T> List<Object> findIdsWithCopy(Query<T> query, Transaction t) {
    return null;
  }

  @Override
  public <T> int findRowCountWithCopy(Query<T> query, Transaction t) {
    return 0;
  }

  @Override
  public void loadBean(LoadBeanRequest loadRequest) {

  }

  @Override
  public void loadMany(LoadManyRequest loadRequest) {

  }

  @Override
  public int getLazyLoadBatchSize() {
    return 0;
  }

  @Override
  public boolean isSupportedType(Type genericType) {
    return false;
  }

  @Override
  public void collectQueryStats(ObjectGraphNode objectGraphNode, long loadedBeanCount, long timeMicros) {

  }

  @Override
  public void loadMany(BeanCollection<?> collection, boolean onlyIds) {

  }

  @Override
  public void loadBean(EntityBeanIntercept ebi) {

  }

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {

  }

  @Override
  public AutoTune getAutoTune() {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ExpressionFactory getExpressionFactory() {
    return null;
  }

  @Override
  public MetaInfoManager getMetaInfoManager() {
    return null;
  }

  @Override
  public BeanState getBeanState(Object bean) {
    return null;
  }

  @Override
  public Object getBeanId(Object bean) {
    return null;
  }

  @Override
  public Map<String, ValuePair> diff(Object a, Object b) {
    return null;
  }

  @Override
  public <T> T createEntityBean(Class<T> type) {
    return null;
  }

}
