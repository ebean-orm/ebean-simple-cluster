package io.ebean.testdouble;

import io.ebean.DtoQuery;
import io.ebean.FutureIds;
import io.ebean.FutureList;
import io.ebean.FutureRowCount;
import io.ebean.PagedList;
import io.ebean.PersistenceContextScope;
import io.ebean.Query;
import io.ebean.QueryIterator;
import io.ebean.RowConsumer;
import io.ebean.RowMapper;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import io.ebean.Transaction;
import io.ebean.TxScope;
import io.ebean.Version;
import io.ebean.bean.BeanCollection;
import io.ebean.bean.CallOrigin;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.event.readaudit.ReadAuditLogger;
import io.ebean.event.readaudit.ReadAuditPrepare;
import io.ebean.meta.MetricVisitor;
import io.ebean.mocker.TDDatabase;
import io.ebeaninternal.api.LoadBeanRequest;
import io.ebeaninternal.api.LoadManyRequest;
import io.ebeaninternal.api.SpiDtoQuery;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.api.SpiJsonContext;
import io.ebeaninternal.api.SpiLogManager;
import io.ebeaninternal.api.SpiQuery;
import io.ebeaninternal.api.SpiQueryBindCapture;
import io.ebeaninternal.api.SpiQueryPlan;
import io.ebeaninternal.api.SpiSqlQuery;
import io.ebeaninternal.api.SpiSqlUpdate;
import io.ebeaninternal.api.SpiTransaction;
import io.ebeaninternal.api.SpiTransactionManager;
import io.ebeaninternal.api.TransactionEventTable;
import io.ebeaninternal.server.core.SpiResultSet;
import io.ebeaninternal.server.core.timezone.DataTimeZone;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.query.CQuery;
import io.ebeaninternal.server.transaction.RemoteTransactionEvent;

import java.lang.reflect.Type;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Test double for SpiEbeanServer.
 */
public class TDSpiEbeanServer extends TDDatabase implements SpiEbeanServer {

  String name;

  public TDSpiEbeanServer(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isDisableL2Cache() {
    return false;
  }

  @Override
  public SpiLogManager log() {
    return null;
  }

  @Override
  public SpiJsonContext jsonExtended() {
    return null;
  }

  @Override
  public boolean isUpdateAllPropertiesInBatch() {
    return false;
  }

  @Override
  public Object currentTenantId() {
    return null;
  }

  @Override
  public DatabaseConfig getServerConfig() {
    return null;
  }

  @Override
  public DatabasePlatform getDatabasePlatform() {
    return null;
  }

  @Override
  public CallOrigin createCallOrigin() {
    return null;
  }

  @Override
  public PersistenceContextScope getPersistenceContextScope(SpiQuery<?> query) {
    return null;
  }

  @Override
  public void clearQueryStatistics() {

  }

  @Override
  public SpiTransactionManager getTransactionManager() {
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
  public BeanDescriptor<?> getBeanDescriptorById(String className) {
    return null;
  }

  @Override
  public BeanDescriptor<?> getBeanDescriptorByQueueId(String queueId) {
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
  public void clearServerTransaction() {

  }

  @Override
  public SpiTransaction beginServerTransaction() {
    return null;
  }

  @Override
  public SpiTransaction currentServerTransaction() {
    return null;
  }

  @Override
  public SpiTransaction createReadOnlyTransaction(Object tenantId) {
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
  public <A, T> List<A> findIdsWithCopy(Query<T> query, Transaction t) {
    return null;
  }

  @Override
  public <T> int findCountWithCopy(Query<T> query, Transaction t) {
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
  public ReadAuditLogger getReadAuditLogger() {
    return null;
  }

  @Override
  public ReadAuditPrepare getReadAuditPrepare() {
    return null;
  }

  @Override
  public DataTimeZone getDataTimeZone() {
    return null;
  }

  @Override
  public void slowQueryCheck(long executionTimeMicros, int rowCount, SpiQuery<?> query) {

  }

  @Override
  public void scopedTransactionEnter(TxScope txScope) {

  }

  @Override
  public void scopedTransactionExit(Object returnOrThrowable, int opCode) {

  }

  @Override
  public <T> T findSingleAttribute(SpiSqlQuery query, Class<T> cls) {
    return null;
  }

  @Override
  public <T> List<T> findSingleAttributeList(SpiSqlQuery query, Class<T> cls) {
    return null;
  }

  @Override
  public <T> void findSingleAttributeEach(
          SpiSqlQuery spiSqlQuery,
          Class<T> aClass,
          Consumer<T> consumer
  ) {

  }

  @Override
  public <T> T findOneMapper(SpiSqlQuery query, RowMapper<T> mapper) {
    return null;
  }

  @Override
  public <T> List<T> findListMapper(SpiSqlQuery query, RowMapper<T> mapper) {
    return null;
  }

  @Override
  public void findEachRow(SpiSqlQuery query, RowConsumer consumer) {

  }

  @Override
  public <T> QueryIterator<T> findDtoIterate(SpiDtoQuery<T> spiDtoQuery) {
    return null;
  }

  @Override
  public <T> Stream<T> findDtoStream(SpiDtoQuery<T> spiDtoQuery) {
    return null;
  }

  @Override
  public <T> List<T> findDtoList(SpiDtoQuery<T> query) {
    return null;
  }

  @Override
  public <T> T findDtoOne(SpiDtoQuery<T> query) {
    return null;
  }

  @Override
  public <T> void findDtoEach(SpiDtoQuery<T> query, Consumer<T> consumer) {

  }

  @Override
  public <T> void findDtoEach(
          SpiDtoQuery<T> spiDtoQuery,
          int i,
          Consumer<List<T>> consumer
  ) {

  }

  @Override
  public <T> void findDtoEachWhile(SpiDtoQuery<T> query, Predicate<T> consumer) {

  }

  @Override
  public <D> DtoQuery<D> findDto(Class<D> dtoType, SpiQuery<?> ormQuery) {
    return null;
  }

  @Override
  public SpiResultSet findResultSet(SpiQuery<?> ormQuery, SpiTransaction transaction) {
    return null;
  }

  @Override
  public void visitMetrics(MetricVisitor visitor) {

  }

  @Override
  public boolean exists(Class<?> beanType, Object beanId, Transaction transaction) {
    return false;
  }

  @Override
  public void addBatch(SpiSqlUpdate defaultSqlUpdate, SpiTransaction transaction) {

  }

  @Override
  public int[] executeBatch(SpiSqlUpdate defaultSqlUpdate, SpiTransaction transaction) {
    return new int[0];
  }

  @Override
  public int executeNow(SpiSqlUpdate sqlUpdate) {
    return 0;
  }

  @Override
  public SpiQueryBindCapture createQueryBindCapture(SpiQueryPlan queryPlan) {
    return null;
  }

  @Override
  public long clockNow() {
    return 0;
  }

  @Override
  public void setClock(Clock clock) {

  }

  @Override
  public <T> boolean exists(Query<?> ormQuery, Transaction transaction) {
    return false;
  }

  @Override
  public <T> int findCount(Query<T> query, Transaction transaction) {
    return 0;
  }

  @Override
  public <A, T> List<A> findIds(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> Stream<T> findStream(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> Stream<T> findLargeStream(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> void findEach(Query<T> query, Consumer<T> consumer, Transaction transaction) {

  }

  @Override
  public <T> void findEach(
          Query<T> query,
          int i,
          Consumer<List<T>> consumer,
          Transaction transaction
  ) {

  }

  @Override
  public <T> void findEachWhile(Query<T> query, Predicate<T> consumer, Transaction transaction) {

  }

  @Override
  public <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <K, T> Map<K, T> findMap(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <A, T> List<A> findSingleAttributeList(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T findOne(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> Optional<T> findOneOrEmpty(Query<T> query, Transaction transaction) {
    return Optional.empty();
  }

  @Override
  public <T> int delete(Query<T> query, Transaction transaction) {
    return 0;
  }

  @Override
  public <T> int update(Query<T> query, Transaction transaction) {
    return 0;
  }

  @Override
  public List<SqlRow> findList(SqlQuery query, Transaction transaction) {
    return null;
  }

  @Override
  public void findEach(SqlQuery query, Consumer<SqlRow> consumer, Transaction transaction) {

  }

  @Override
  public void findEachWhile(SqlQuery query, Predicate<SqlRow> consumer, Transaction transaction) {

  }

  @Override
  public SqlRow findOne(SqlQuery query, Transaction transaction) {
    return null;
  }

  @Override
  public void loadMany(BeanCollection<?> collection, boolean onlyIds) {

  }
}
