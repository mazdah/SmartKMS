package com.innotree.smartkms.config;

import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
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
    
    private static final String CONFIG_LOCATION = "com.lgup.iaic.common.config";
 
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    		ServletRegistration.Dynamic registration = 
    				servletContext.addServlet("dispatcher", new DispatcherServlet());
    	    registration.setLoadOnStartup(1);
    	    registration.addMapping("*.request");
    	      
        WebApplicationContext rootContext = createRootContext(servletContext);
        configureServletContext(servletContext, rootContext);
    }
 
    private WebApplicationContext createRootContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        // TODO 개발환경은 JVM Option에 추가한다. -Dspring.profiles.active=development
        // rootContext.getEnvironment().setDefaultProfiles("production");
        rootContext.getEnvironment().setDefaultProfiles("development");
        rootContext.register(SKMSMvcConfig.class);
        //rootContext.register(ApplicationContext.class, DatabaseContext.class);
        rootContext.refresh();
 
        servletContext.addListener(new ContextLoaderListener(rootContext));
 
        /*
         * 인코딩 필터 등록
         */
        FilterRegistration characterEncodingFilter = servletContext.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class);
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
 
        /*
         * RESTFul지원을 위한 필터 등록...
         */
        servletContext.addFilter(
                "HttpMethodFilter",
                HiddenHttpMethodFilter.class
        ).addMappingForUrlPatterns(null, false, "/*");
 
        servletContext.setInitParameter("defaultHtmlEscape", "true");
 
        return rootContext;
    }
    
    public void configureServletContext(ServletContext servletContext, WebApplicationContext rootContext) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setDisplayName("appServlet");
        ctx.setConfigLocation(CONFIG_LOCATION);
        
        ServletRegistration.Dynamic restServlet = servletContext.addServlet("appServlet", new DispatcherServlet(ctx));
        restServlet.setLoadOnStartup(1);
        Set<String> mappingConflicts = restServlet.addMapping("/*");
    
        if (!mappingConflicts.isEmpty()) {
            for (String s : mappingConflicts) {
                logger.error("Mapping conflict : " + s);
            }
            throw new IllegalStateException("'appServlet' cannot be mapped to '/*'");
        }
    }
}