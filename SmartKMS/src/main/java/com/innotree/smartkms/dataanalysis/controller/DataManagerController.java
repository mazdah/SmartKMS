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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.innotree.smartkms.datafiles.model.DataFiles;
import com.innotree.smartkms.datafiles.repository.DataFilesRepository;
import com.innotree.smartkms.elastic.DataParser;
import com.innotree.smartkms.elastic.ElasticHelper;
import com.innotree.smartkms.elastic.ElasticRESTHelper;
import com.monitorjbl.xlsx.StreamingReader;

@Controller
@EnableAutoConfiguration
public class DataManagerController {
	
	Logger logger = LoggerFactory.getLogger(DataManagerController.class);
	
	@Autowired
	DataFilesRepository dataFilesRepository;

	/**
	 * 데이터 등록 - 엑셀 파일로 작성된 데이터 등록
	 */
	
	@Value("${file.save.dir}")
	private String saveDir;
	
	private int totalLine = 0;
	private int importedLine = 0;
	private int importedCnt = 0;
	private boolean isImportComplete = false;
	
	@GetMapping("/startimport")
	@ResponseBody
	public synchronized Map<String, Object> startImport(@RequestParam("id") long id, 
													   @RequestParam("fileName") String fileName,
													   @RequestParam("indexName") String indexName,
													   @RequestParam("type") String type) {
		logger.debug("##### startImport");
		Map <String, Object> resultMap = new HashMap <String, Object>();
		
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
			BulkResponse response = null;
			boolean epochresult = false;
			
		    for (Sheet sheet : workbook) {
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
		        			continue;
		        		} else {
		        			keyValMap.put(keyList.get(i), c==null?"":c.getStringCellValue());
		        		}
		        }
		        
		        String documentId = docId + "_" + importedLine;
		        documentId = documentId.replaceAll("\\s+", "_");
		        //String jsonStr = DataParser.getJsonStringFromMap(keyValMap);
		        
//		        ElasticRESTHelper.importData(documentId, indexName, type, keyValMap);
		        
		        //ElasticHelper.importData(documentId, indexName, type, jsonStr);
		        //logger.debug("##### " + jsonStr);
		        importDataList.add(keyValMap);
		        idList.add(documentId);
		        importedLine++;
		        
		        if (importedLine >= totalLine || importedLine == (100000 * (importedCnt + 1))) {
		        		response = ElasticRESTHelper.bulkImportData(idList, indexName, type, importDataList);
				    boolean batchresult = response.hasFailures();
				    epochresult = batchresult || epochresult;
				  
				    if (!batchresult) {
				    		importDataList.clear();
				    		idList.clear();
					    importedCnt++;
					    
					    logger.debug("##### batch-" + importedCnt + " end");
				    }
		        }
		      } 
		    }
		     
//		    
		    if (!epochresult) {
		    		DataFiles dataFiles = dataFilesRepository.findByFileId(id);
			    
			    dataFiles.setImport(true);
			    dataFiles.setImportDate(new Date());
			    dataFiles.setTotalRows(importedLine - 1);
			    
			    dataFilesRepository.save(dataFiles);
			    isImportComplete = true;
			    resultMap.put("code", "9999");
			    resultMap.put("message", "데이터 import를 성공적으로 마쳤습니다.");
		    } else {
		    		
		    }
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
		} catch (OutOfMemoryError e) {
			logger.debug("##### OutOfMemoryError : " + e.getLocalizedMessage());
			resultMap.put("code", "1000");
		    resultMap.put("message", "데이터를 정상적으로 import하지 못했습니다. : OutOfMemoryError");
		} finally {
			totalLine = 0;
			importedLine = 0;
			importedCnt = 0;
			isImportComplete = false;
		}
		
		return resultMap;
	}
	
	@GetMapping("/checkprocess")
	@ResponseBody
	public Map<String, Object> checkProcess() {
		logger.debug("##### checkProcess");
		Map <String, Object> resultMap = new HashMap <String, Object>();
		resultMap.put("totalLine", new Integer(totalLine));
		resultMap.put("importedLine", new Integer(importedLine));
		resultMap.put("isImportComplete", isImportComplete);
		
		if (importedCnt * 100000 > totalLine) {
			resultMap.put("importedCnt", new Integer(importedLine));
		} else {
			resultMap.put("importedCnt", new Integer(importedCnt * 100000));			
		}
		
		
		
//		if (importedLine > totalLine && isImportComplete == true) {
//		    totalLine = 0;
//			importedLine = 0;
//			importedCnt = 0;
//			isImportComplete = false;
//		}
		
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
