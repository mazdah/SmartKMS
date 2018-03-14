package com.innotree.smartkms.dataanalysis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@EnableAutoConfiguration
public class DataRegistController {
	
	Logger logger = LoggerFactory.getLogger(DataRegistController.class);

	/**
	 * 데이터 등록 - 엑셀 파일로 작성된 데이터 등록
	 */
	
	@Value("${file.save.dir}")
	private String saveDir;
	
	@PostMapping(value="/fileupload")
	@ResponseBody
	public synchronized Map<String, Object> fileUpload(@RequestParam("indexName") String indexName,
													  @RequestParam("type") String type,
													  @RequestParam("file") MultipartFile file) {
		
		logger.debug("##### file = {}", file);
		logger.debug("##### indexName = " + indexName);
		logger.debug("##### type = " + type);
		logger.debug("##### File Name = " + file.getOriginalFilename());
		
		// jquery file upload 플러그인의 스펙에 맞춘 리턴 처리
		Map <String, Object> resultMap = new HashMap <String, Object>();
		Map <String, Object> fileMap = new HashMap <String, Object>();
		
		List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
		fileMap.put("name", file.getOriginalFilename());
		fileMap.put("size", new Long(file.getSize()));
		fileMap.put("url", "");
		fileMap.put("thumbnailUrl", "");
		fileMap.put("deleteUrl", "");
		fileMap.put("size", "DELETE");
		
		files.add(fileMap);
		
		resultMap.put("files", files);
		
		
		// 1. 저장할 디렉토리 생성
		// 2.파일 저장 : file.transgerTo (저장할 디렉토리)
		try {
			file.transferTo(new File(saveDir + "/" + file.getOriginalFilename()));
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			logger.debug(e.getLocalizedMessage());
		}
		
		return resultMap;
	}
	
	@GetMapping(value="/result")
	public String result () {
		return "/bigdata/result";
	}
}
