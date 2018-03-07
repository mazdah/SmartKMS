package com.innotree.smartkms.controller.main;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class SKMSMainController {

	@RequestMapping(value="/")
	public String index () {
		return "index";
	}
	
	@RequestMapping(value="/admin")
	public String admin() {
		return "admin/admin";
	}
}
