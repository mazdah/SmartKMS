package com.innotree.smartkms.controller.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.innotree.smartkms.elastic.ElasticHelper;

@Controller
@EnableAutoConfiguration
public class SKMSMainController {
	
	Logger logger = LoggerFactory.getLogger(SKMSMainController.class);

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String index () {
		
		logger.info("##### call ElasticHelper.makeIndex");
		ElasticHelper.makeIndex(null);
		
		return "index";
	}
	
	@RequestMapping(value="/admin", method = RequestMethod.GET)
	public String admin() {
		//ElasticHelper.makeSimpleIndex(null);
		
		return "/admin/admin";
	}
	
	@RequestMapping(value="/dataregist", method = RequestMethod.GET)
	public String dataregist() {
		logger.info("##### call SKMSMainController.dataregist()");
		return "/bigdata/registdata";
	}
	
	@RequestMapping(value="/indexlist", method = RequestMethod.GET)
	public String indexlist() {
		logger.info("##### call SKMSMainController.indexlist()");
		return "/bigdata/indexlist";
	}
	
	@RequestMapping(value="/datachart", method = RequestMethod.GET)
	public String datachart() {
		logger.info("##### call SKMSMainController.datachart()");
		return "/bigdata/datachart";
	}
	
	@RequestMapping(value="/realtimechart", method = RequestMethod.GET)
	public String realtimechart() {
		logger.info("##### call SKMSMainController.realtimechart()");
		return "/bigdata/realtimechart";
	}
}
