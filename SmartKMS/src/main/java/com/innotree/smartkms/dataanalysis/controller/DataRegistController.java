package com.innotree.smartkms.dataanalysis.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.innotree.smartkms.datafiles.model.DataFiles;
import com.innotree.smartkms.datafiles.repository.DataFilesRepository;
import com.innotree.smartkms.elastic.DataParser;
import com.innotree.smartkms.elastic.ElasticHelper;
import com.innotree.smartkms.elastic.ElasticRESTHelper;
import com.monitorjbl.xlsx.StreamingReader;

@Controller
@EnableAutoConfiguration
public class DataRegistController {
	
	Logger logger = LoggerFactory.getLogger(DataRegistController.class);
	
	@Autowired
	DataFilesRepository dataFilesRepository;

	/**
	 * 데이터 등록 - 엑셀 파일로 작성된 데이터 등록
	 */
	
	@Value("${file.save.dir}")
	private String saveDir;
	
	private int totalLine = 0;
	private int importedLine = 0;
	private boolean isImportComplete = false;
	
	@PostMapping(value="/fileupload")
	@ResponseBody
	public synchronized Map<String, Object> fileUpload(@RequestParam("indexName") String indexName,
													  @RequestParam("type") String type,
													  @RequestParam("isImport") boolean isImport,
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
		fileMap.put("url", "");
		
		if ("xlsx".equalsIgnoreCase(fileExt)) {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/xlsx.png");
		} else if ("xls".equalsIgnoreCase(fileExt)) {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/xls.png");
		} else {
			fileMap.put("thumbnailUrl", "/SmartKMS/images/icons/csv.png");
		}
		
		fileMap.put("deleteUrl", "/SmartKMS/filedelete?fileName=" + orgFileName);
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
	public synchronized Map<String, Object> fileDelete(@RequestParam("fileName") String fileName) {
		logger.debug("##### file delete : fileName = " + fileName);

		
		Map <String, Object> resultMap = new HashMap <String, Object>();
		
		File file = new File(saveDir + "/" + fileName);
		
		if (file.exists()) {
			
			if (file.delete()) {
				resultMap.put("message", "file delete Fail! " + fileName + " file not deleted!");
				resultMap.put("result", false);
			} else {
				resultMap.put("message", "file delete Success! " + fileName + " file deleted!");
				resultMap.put("result", true);
			}
		} else {
			resultMap.put("message", "file delete Fail! " + fileName + " file not exist!");
			resultMap.put("result", false);
		}
		
		return resultMap;
	}
	
	@GetMapping("/startimport")
	@ResponseBody
	public synchronized Map<String, Object> startImport(@RequestParam("id") int id, 
													   @RequestParam("fileName") String fileName,
													   @RequestParam("indexName") String indexName,
													   @RequestParam("type") String type) {
		logger.debug("##### startImport");
		Map <String, Object> resultMap = new HashMap <String, Object>();
		
		totalLine = 0;
		importedLine = 0;
		isImportComplete = false;
		
		File excelFile = new File(saveDir + "/" + fileName);
		String docId = fileName.substring(0, fileName.lastIndexOf("."));
		List<Map<String, String>> importDataList = new ArrayList<Map<String, String>>();
		List<String> idList = new ArrayList<String>();
		
		try (
		    InputStream is = new FileInputStream(excelFile);
		    Workbook workbook = StreamingReader.builder()
		          .rowCacheSize(100)
		          .bufferSize(4096)
		          .open(is)) {	
			
			List<String> keyList = new ArrayList<String>();
		    for (Sheet sheet : workbook){
		      totalLine = sheet.getLastRowNum();
		      logger.debug("##### sheet name = " + sheet.getSheetName());
		      logger.debug("##### totalLine = " + totalLine);
		      
		      for (Row r : sheet) {
		    	  	Map<String, String> keyValMap = new HashMap<String, String>();
		    	  	
		    	  	int cellCnt = r.getLastCellNum();
		        for (int i = 0; i < cellCnt; i++) {
		          //logger.debug("##### values = " + c.getStringCellValue());    	
		        		Cell c = r.getCell(i);
		        		if (importedLine == 0) {
		        			keyList.add(c.getStringCellValue());
		        		} else {
		        			keyValMap.put(keyList.get(i), c==null?"":c.getStringCellValue());
		        		}
		        }
		        
		        String documentId = docId + "_" + importedLine;
		        documentId = documentId.replaceAll("\\s+", "_");
		        String jsonStr = DataParser.getJsonStringFromMap(keyValMap);
		        
		        ElasticRESTHelper.importDataAsync(documentId, indexName, type, keyValMap);
		        
		        //ElasticHelper.importData(documentId, indexName, type, jsonStr);
		        //logger.debug("##### " + jsonStr);
//		        importDataList.add(keyValMap);
//		        idList.add(documentId);
		        importedLine++;
		      }
		    }
		    
//		    ElasticRESTHelper.bulkImportDataAsync(idList, indexName, type, importDataList);
//		    
//		    if (!response.hasFailures()) {
		    		DataFiles dataFiles = dataFilesRepository.findByFileId(id);
			    
			    dataFiles.setImport(true);
			    dataFiles.setImportDate(new Date());
			    dataFiles.setTotalRows(importedLine - 1);
			    
			    dataFilesRepository.save(dataFiles);
			    isImportComplete = true;
			    resultMap.put("code", "9999");
			    resultMap.put("message", "데이터 import를 성공적으로 마쳤습니다.");
//		    } 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.debug("##### FileNotFoundException : " + e.getLocalizedMessage());
			resultMap.put("code", "0001");
		    resultMap.put("message", "데이터를 정상적으로 import하지 못했습니다. : FileNotFoundException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("##### IOException : " + e.getLocalizedMessage());
			resultMap.put("code", "0002");
		    resultMap.put("message", "데이터를 정상적으로 import하지 못했습니다. : IOException");
		}
		
		return resultMap;
	}
	
//	private static class SheetHandler extends DefaultHandler {
//		private SharedStringsTable sst;
//		private String lastContents;
//		private boolean nextIsString;
//		
//		private SheetHandler(SharedStringsTable sst) {
//			this.sst = sst;
//		}
//		
//		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//			if ("c".equals(name)) {
//				String cellType = attributes.getValue("t");
//				if (cellType != null && "s".equals(cellType)) {
//					nextIsString = true;
//				} else {
//					nextIsString = false;
//				}
//			}
//			
//			lastContents = "";
//		}
//		
//		public void endElement(String uri, String localName, String name) throws SAXException {
//			if (nextIsString) {
//				int idx = Integer.parseInt(lastContents);
//				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
//				nextIsString = false;
//			}
//		}
//		
//		public void characters(char[] ch, int start, int length) throws SAXException {
//			lastContents += new String(ch, start, length);
//		}
//	}
	
	@GetMapping("/checkprocess")
	@ResponseBody
	public Map<String, Object> checkProcess() {
		logger.debug("##### checkProcess");
		Map <String, Object> resultMap = new HashMap <String, Object>();
		resultMap.put("totalLine", new Long(totalLine));
		resultMap.put("importedLine", new Long(importedLine));
		resultMap.put("isImportComplete", isImportComplete);
		
		if (importedLine > totalLine && isImportComplete == true) {
		    totalLine = 0;
			importedLine = 0;
			isImportComplete = false;
		}
		
		return resultMap;
	}
	
	@GetMapping(value="/indices")
	@ResponseBody
	public List<String> getIndices() {
		List<String> indexList = new ArrayList<String>();
		
		String[] indices = ElasticHelper.getIndexList();
		
		for(String index : indices) {
			if (!index.startsWith(".")) {
				indexList.add(index);
			}
		}
		
		return indexList;
	}
	
	@GetMapping(value="/types")
	@ResponseBody
	public List<String> getTypes(@RequestParam("index") String index) {
		return ElasticHelper.getTypeList(index);
	}
	
	@GetMapping(value="/result")
	public String result () {
		return "/bigdata/result";
	}
}
