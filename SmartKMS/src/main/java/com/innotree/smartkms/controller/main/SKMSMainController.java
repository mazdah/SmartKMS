package com.innotree.smartkms.controller.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.innotree.smartkms.elastic.ElasticHelper;

@Controller
@EnableAutoConfiguration
public class SKMSMainController {
	
	Logger logger = LoggerFactory.getLogger(SKMSMainController.class);

	@RequestMapping(value="/")
	public String index () {
		
		logger.info("##### call ElasticHelper.makeIndex");
		ElasticHelper.makeIndex(null);
		
		return "index";
	}
	
	@RequestMapping(value="/admin")
	public String admin() {
		ElasticHelper.makeSimpleIndex(null);
		
		return "admin/admin";
	}
}
