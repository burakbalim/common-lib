package com.thales.common.datasource;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnExpression("'${POSTGRES_REPLICA_HOST:}'.length() > 0")
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.primary.hikari")
    public HikariDataSource primaryDataSource(
            @Qualifier("primaryDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.replica")
    public DataSourceProperties replicaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.replica.hikari")
    public HikariDataSource replicaDataSource(
            @Qualifier("replicaDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("primaryDataSource") HikariDataSource primary,
            @Qualifier("replicaDataSource") HikariDataSource replica) {
        RoutingDataSource routing = new RoutingDataSource();
        routing.setDefaultTargetDataSource(primary);
        routing.setTargetDataSources(Map.of(
                DataSourceType.PRIMARY, primary,
                DataSourceType.REPLICA, replica
        ));
        routing.afterPropertiesSet();
        // Warm up both pools immediately so the first requests don't pay pool-init cost.
        warmUp("primary", primary);
        warmUp("replica", replica);
        return new LazyConnectionDataSourceProxy(routing);
    }

    private void warmUp(String name, HikariDataSource ds) {
        try (Connection c = ds.getConnection()) {
            c.isValid(1);
            log.info("DataSource warmup OK — pool={} total={}", name, ds.getHikariPoolMXBean().getTotalConnections());
        } catch (Exception e) {
            log.warn("DataSource warmup failed — pool={} : {}", name, e.getMessage());
        }
    }
}
