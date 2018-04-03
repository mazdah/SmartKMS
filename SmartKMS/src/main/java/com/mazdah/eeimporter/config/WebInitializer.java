package com.mazdah.eeimporter.config;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;
 
import lombok.extern.slf4j.Slf4j;
 
/**
 * <pre>
 * web.xml을 대체하는 java config class
 * @Modification
 * </pre>
 */
@Slf4j
public class WebInitializer implements WebApplicationInitializer {
	
	Logger logger = LoggerFactory.getLogger(WebInitializer.class);
	
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    	
    		servletContext.setInitParameter("contextConfigLocation", "<NONE>");
    	
    		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    		context.setConfigLocation("com.mazdah.eeimporter.config");
    		context.register(WebMvcConfig.class);
    		context.register(WebSecurityConfig.class);
    		//servletContext.addListener(new ContextLoaderListener(context));
    		

    		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
       
    		dispatcher.setLoadOnStartup(1);
    		dispatcher.addMapping("/");

    		// WebApplicationInitializer 구현체에서 properties 파일 사용하는 방법
    		ConfigurableEnvironment environment = null;
    		try {
    			environment = context.getEnvironment();
    			environment.getPropertySources().addFirst(new ResourcePropertySource(new ClassPathResource("common.properties")));
    			   			
    			String tempDir = environment.getProperty("file.temp.dir");
    			int maxUploadSize = new Integer(environment.getProperty("max.upload.size"));
    			
    			MultipartConfigElement multipartConfigElement = new MultipartConfigElement(tempDir, 
        				maxUploadSize, maxUploadSize * 2, maxUploadSize / 2);
        		
        		dispatcher.setMultipartConfig(multipartConfigElement);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug(e.getLocalizedMessage());
		}
    		
    		/*
         * 인코딩 필터 등록
         */
        FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class);
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
        
        /*
         * ETag지원을 위한 필터 등록...
         */
        FilterRegistration.Dynamic etagFilter = servletContext.addFilter("etagFilter", ShallowEtagHeaderFilter.class);
        etagFilter.addMappingForUrlPatterns(null, false, "/*");
        
        /*
         * RESTFul지원을 위한 필터 등록...
         */
        FilterRegistration.Dynamic methodFilter = servletContext.addFilter("HttpMethodFilter", HiddenHttpMethodFilter.class);
        methodFilter.addMappingForUrlPatterns(null, false, "/*");
 
        servletContext.setInitParameter("defaultHtmlEscape", "true");
    }
}