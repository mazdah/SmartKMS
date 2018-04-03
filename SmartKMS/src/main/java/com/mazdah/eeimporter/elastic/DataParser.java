package com.mazdah.eeimporter.elastic;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataParser {
	
	private final static Logger logger = LoggerFactory.getLogger(DataParser.class);
	
	public static String getJsonStringFromMap(Map<String, String> map) {
		JSONObject jsonObj = new JSONObject(map);
		
		String retStr = "";
		try {
			retStr = jsonObj.toString(2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.debug(e.getLocalizedMessage());
		}
		
		return retStr;
	}
}
