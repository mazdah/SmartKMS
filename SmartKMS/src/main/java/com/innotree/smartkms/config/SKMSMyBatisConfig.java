package com.innotree.smartkms.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@ComponentScan(basePackages= {"com.innotree.smartkms"}, useDefaultFilters=false, includeFilters= {@Filter(Service.class)})
public class SKMSMyBatisConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SKMSMyBatisConfig.class);
	
	@Primary
	@Bean(name="mysqlDataSource", destroyMethod="close")
	@ConfigurationProperties(prefix = "spring.mysql.datasource")
	public DataSource dataSourceMysql() {
		return DataSourceBuilder.create().build();
	}

//	@Bean(name = "slaveDataSource") 
//	@ConfigurationProperties(prefix = "spring.other.datasource") 
//	public DataSource slaveDataSource() { 
//		return DataSourceBuilder.create().build(); 
//	} 
	
	@Bean(name = "transactionManager") 
	public PlatformTransactionManager transactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) { 
		return new DataSourceTransactionManager(dataSource); 
	} 
	
	@Bean(name = "sqlSessionFactory") 
	public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception { 
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean(); 
		sqlSessionFactoryBean.setDataSource(dataSource); 
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml")); 
		return sqlSessionFactoryBean.getObject(); 
	} 
	
	@Bean(name = "sqlSessionTemplate") public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception { 
		return new SqlSessionTemplate(sqlSessionFactory); 
	}

}
