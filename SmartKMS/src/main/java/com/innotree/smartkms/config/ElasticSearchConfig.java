package com.innotree.smartkms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class ElasticSearchConfig extends AbstractFactoryBean {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

	@Value("${cluster.node}")
    private String clusterNodes;
	
    @Value("${cluster.name}")
    private String clusterName;
    
	@Override
	protected Object createInstance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return super.isSingleton();
	}
	
	
}
