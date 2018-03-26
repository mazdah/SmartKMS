package com.innotree.smartkms.filemanager.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.innotree.smartkms.datafiles.model.DataFiles;
import com.innotree.smartkms.datafiles.repository.DataFilesRepository;

@Controller
@EnableAutoConfiguration
public class FileManagerController {
	
	Logger logger = LoggerFactory.getLogger(FileManagerController.class);

	@Autowired
	DataFilesRepository dataFilesRepository;
	
	@Value("${file.save.dir}")
	private String saveDir;
	
	@PostMapping(value="/fileupload")
	@ResponseBody
	public synchronized Map<String, Object> fileUpload(@RequestParam("indexName") String indexName,
													  @RequestParam("type") String type,
													  @RequestParam(value="isImport", defaultValue="false") boolean isImport,
													  @RequestParam("file") MultipartFile file) {
		
		logger.debug("##### file = {}", file);
		logger.debug("##### indexName = " + indexName);
		logger.debug("##### type = " + type);
		logger.debug("##### isImport = " + isImport);
		logger.debug("##### File Name = " + file.getOriginalFilename());
		logger.debug("##### File size = " + file.getSize());
		
		String orgFileName = file.getOriginalFilename();
		long fileSize = file.getSize();
		
		String fileExt = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
		
		// jquery file upload 플러그인의 스펙에 맞춘 리턴 처리
		Map <String, Object> resultMap = new HashMap <String, Object>();
		Map <String, Object> fileMap = new HashMap <String, Object>();
		
		List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
		fileMap.put("name", orgFileName);
		fileMap.put("size", Long.valueOf(fileSize));
		
		if ("xlsx".equalsIgnoreCase(fileExt)) {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/xlsx.png");
		} else if ("xls".equalsIgnoreCase(fileExt)) {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/xls.png");
		} else {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/csv.png");
		}
		
		fileMap.put("deleteType", "GET");
		fileMap.put("isImport", isImport);
		
		
		
		
		// 1. 저장할 디렉토리 생성
		// 2.파일 저장 : file.transgerTo (저장할 디렉토리)
		try {
			String filePath = saveDir + "/" + file.getOriginalFilename();
			File excelFile = new File(filePath);
			file.transferTo(excelFile);
			
			DataFiles dataFiles = new DataFiles();
			dataFiles.setOrgFileName(orgFileName);
			dataFiles.setFilePath(filePath);
			dataFiles.setFileSize(fileSize);
			dataFiles.setImport(false);
			dataFiles.setSavedFileName(orgFileName);
			dataFiles.setUploadDate(new Date());
			dataFiles.setElasticIndex(indexName);
			dataFiles.setElasticType(type);
			
			DataFiles insertedFile = dataFilesRepository.saveAndFlush(dataFiles);
			
			fileMap.put("deleteUrl", "/SmartKMS/filedelete?fileId=" + insertedFile.getFileId() + "&fileName=" + orgFileName);		
			fileMap.put("url", "/SmartKMS/download?filePath=" + insertedFile.getFilePath() + "&fileName=" + orgFileName);
			fileMap.put("id", insertedFile.getFileId());
			fileMap.put("indexName", indexName);
			fileMap.put("type", type);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			logger.debug(e.getLocalizedMessage());
		}
		
		files.add(fileMap);		
		resultMap.put("files", files);
		
		return resultMap;
	}
	
	@GetMapping("/filedelete")
	@ResponseBody
	public synchronized Map<String, Object> fileDelete(@RequestParam("fileId") String fileId, @RequestParam("fileName") String fileName) {
		logger.debug("##### file delete : fileName = " + fileName);

		
		Map <String, Object> resultMap = new HashMap <String, Object>();
		
		File file = new File(saveDir + "/" + fileName);
		
		if (file.exists()) {
			
			if (file.delete()) {
				
				resultMap.put("message", "파일 삭제를 완료하였습니다.\n삭제된 파일명 :  " + fileName);
				resultMap.put("result", true);
				
				dataFilesRepository.deleteById(Long.valueOf(fileId));
			} else {
				resultMap.put("message", "파일 삭제에 실패하였습니다.\n파일명 :  " + fileName);
				resultMap.put("result", false);
			}
		} else {
			resultMap.put("message", "삭제할 파일이 존재하지 않습니다.\n삭제할 파일명 : " + fileName);
			resultMap.put("result", false);
		}
		
		return resultMap;
	}
	
	@GetMapping("/filelist")
	@ResponseBody
	public Map<String, Object> getfileList(@PageableDefault(sort = { "uploadDate" }, direction = Direction.DESC, size = 10) Pageable pageable) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Page<DataFiles> filePage = dataFilesRepository.findAll(pageable);
		
		resultMap.put("data",filePage.getContent());
//		resultMap.put("draw",draw);
		resultMap.put("recordsTotal",filePage.getTotalElements());
		resultMap.put("recordsFiltered",filePage.getTotalElements());
		
		return resultMap;
	}
	
	@GetMapping("/download")
	public void downloadFile(@RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName, HttpServletResponse response) {
		logger.debug("##### downloadFile : fileName = " + fileName);
		
		try {
			File dFile = new File(filePath);
			
			response.setCharacterEncoding("utf-8");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
			response.setContentLength((int) dFile.length());
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
			response.setHeader("Content-Transfer-Encoding", "binary");
			
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(dFile));
	        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
	 
	        byte[] buffer = new byte[1024];
	        int bytesRead = 0;
	        while ((bytesRead = inStream.read(buffer)) != -1) {
	            outStream.write(buffer, 0, bytesRead);
	        }
	        outStream.flush();
	        inStream.close();
			
			logger.debug("##### downloadFile : fileDownload end! - fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
		} catch (IOException e) {
			logger.debug("##### IOException : " + e.getLocalizedMessage());
		}
		
	}
}
