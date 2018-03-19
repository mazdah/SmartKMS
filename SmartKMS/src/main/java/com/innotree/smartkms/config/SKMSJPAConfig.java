package com.innotree.smartkms.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("com.innotree.smartkms")
@EnableTransactionManagement

public class SKMSJPAConfig {
	
	@Bean(destroyMethod = "close")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSourceJPA() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
    public EntityManagerFactory entityManagerFactory() {
 
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        factory.setPackagesToScan("com.innotree.smartkms");
        factory.setDataSource(dataSourceJPA());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
	
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }
}
