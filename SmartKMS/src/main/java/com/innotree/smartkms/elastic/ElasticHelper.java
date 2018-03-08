package com.innotree.smartkms.elastic;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

public class ElasticHelper {
	
	static Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	
	public static void makeIndex (Map<String, String> indexMap) {
		HashMap<String, String> idxMap;
		
		if (indexMap != null) {
			idxMap = (HashMap<String, String>) indexMap;
		}

		XContentBuilder indexSettings = null;
		  try {
		   indexSettings = XContentFactory.jsonBuilder();
		   indexSettings.startObject()
		       .field("index.number_of_shards", 3)   // 샤드 갯수 지정
		       .field("index.number_of_replicas", 1)  // 레플리카 갯수 지정
		    // index
		       .startObject("index")
		       	// analysis
		       		.startObject("analysis")
		       		// analyzer
		       			.startObject("analyzer")
		       				.startObject("common_analyzer")
		       					.field("type", "cjk")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       				.startObject("pattern_analyzer")
		       					.field("type", "custom")
		       					.field("tokenizer", "pattern_tokenizer")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       				.startObject("ngram_analyzer")
		       					.field("type", "custom")
		       					.field("tokenizer", "ngram_tokenizer")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       			.endObject()
		       		// analyzer
		       		// tokenizer
		       			.startObject("tokenizer")
		       				.startObject("ngram_tokenizer")
		       					.field("type", "nGram")
		       					.field("min_gram", "2")
		       					.field("max_gram", "10")
		       					.field("token_char", Arrays.asList("letter", "digit"))
		       				.endObject()
		       				.startObject("pattern_tokenizer")
		       					.field("type", "pattern")
		       					.field("pattern", ",")
		       				.endObject()
		       			.endObject()
		       		// tokenizer
		       		.endObject()
		       	// analysis
		       	// store
		       		.startObject("store")
		       			.field("type", "mmapfs")
		       			.startObject("compress")
		       				.field("stored", true)
		       				.field("tv", true)
		       			.endObject()
		       		.endObject()
		       	// store
		     // index
		       	.endObject()
		      .endObject()
		      .prettyPrint();
		   
		   	logger.info("##### setting info : " + indexSettings.string());
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
	}
	
	public static void makeSimpleIndex (Map<String, String> indexMap) {
		Client client = ElasticClientHelper.newTransportClient();
		IndicesAdminClient indicesClient = client.admin().indices();
		
		client.admin().indices().prepareCreate("real_estate")
        .setSettings(Settings.builder()             
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 0)
        )
        .get();
	}
}
