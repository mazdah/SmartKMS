package com.innotree.smartkms.elastic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElasticRESTHelper {
	
	private final static Logger logger = LoggerFactory.getLogger(ElasticRESTHelper.class);
	
	private static boolean isAsyncImportComplete = false;
	private static boolean isAsyncBulkImportComplete = false;
	
	@Value("${index.number_of_shards}")
	public static String numberOfShards;
	
	@Value("${index.number_of_replicas}")
	public static String numberOfReplica;
	
	@Value("${index.number_of_shards}")
    public void setNumberOfShards(String shardNum) {
		numberOfShards = shardNum;
    }
	
	@Value("${index.number_of_replicas}")
    public void setNumberOfReplica(String replicaNum) {
		logger.debug("##### elport = {}", replicaNum);
		numberOfReplica = replicaNum;
    }
	
	public static boolean getIsAsyncImportComplete() {
		return isAsyncImportComplete;
	}
	
	public static boolean getIsAsyncBulkImportComplete() {
		return isAsyncBulkImportComplete;
	}
	
	public static CreateIndexResponse createIndex(String indexName) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		
		request.settings(Settings.builder() 
		    .put("index.number_of_shards", Integer.valueOf(numberOfShards))
		    .put("index.number_of_replicas", Integer.valueOf(numberOfReplica))
		);
		
		// mapping sample
//		request.mapping("tweet", 
//			    "  {\n" +
//			    "    \"tweet\": {\n" +
//			    "      \"properties\": {\n" +
//			    "        \"message\": {\n" +
//			    "          \"type\": \"text\"\n" +
//			    "        }\n" +
//			    "      }\n" +
//			    "    }\n" +
//			    "  }", 
//			    XContentType.JSON);
		
		// Alias sample
//		request.alias(
//		    new Alias("re_data")  
//		);
		
		//Timeout to wait for the all the nodes to acknowledge the index creation as a TimeValue
		//Timeout to wait for the all the nodes to acknowledge the index creation as a String
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");
		
		//The number of active shard copies to wait for before the create index API returns a response, as an int.
		//The number of active shard copies to wait for before the create index API returns a response, as an ActiveShardCount.
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1));
		request.masterNodeTimeout("1m");
		
		CreateIndexResponse createIndexResponse = null;
		try {
			createIndexResponse = client.indices().create(request);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("##### [IOException] createIndex : " + e.getLocalizedMessage());
		}
		
		return createIndexResponse;
	}
	
	public static void createIndexAsync(String indexName) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		
		request.settings(Settings.builder() 
		    .put("index.number_of_shards", Integer.valueOf(numberOfShards))
		    .put("index.number_of_replicas", Integer.valueOf(numberOfReplica))
		);
		
		// mapping sample
//		request.mapping("tweet", 
//			    "  {\n" +
//			    "    \"tweet\": {\n" +
//			    "      \"properties\": {\n" +
//			    "        \"message\": {\n" +
//			    "          \"type\": \"text\"\n" +
//			    "        }\n" +
//			    "      }\n" +
//			    "    }\n" +
//			    "  }", 
//			    XContentType.JSON);
		
		// Alias sample
//		request.alias(
//		    new Alias("re_data")  
//		);
		
		//Timeout to wait for the all the nodes to acknowledge the index creation as a TimeValue
		//Timeout to wait for the all the nodes to acknowledge the index creation as a String
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");
		
		//The number of active shard copies to wait for before the create index API returns a response, as an int.
		//The number of active shard copies to wait for before the create index API returns a response, as an ActiveShardCount.
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1));
		request.masterNodeTimeout("1m");
		
		ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
		    @Override
		    public void onResponse(CreateIndexResponse createIndexResponse) {
		    		try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }

		    @Override
		    public void onFailure(Exception e) {
			    	logger.debug("##### [Exception] createIndexAsync onFailure : " + e.getLocalizedMessage());
			    	
		    		try {
					client.close();
				} catch (IOException ie) {
					// TODO Auto-generated catch block
					logger.debug("##### [IOException] createIndexAsync client.close() : " + ie.getLocalizedMessage());
				}
		    }
		};
		
		client.indices().createAsync(request, listener);
	}
	
	public static DeleteIndexResponse deleteIndex(String indexName) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		
		//Timeout to wait for the all the nodes to acknowledge the index deletion as a TimeValue
		//Timeout to wait for the all the nodes to acknowledge the index deletion as a String
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");
		
		//Timeout to connect to the master node as a TimeValue
		//Timeout to connect to the master node as a String
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1)); 
		request.masterNodeTimeout("1m"); 
		
		//Setting IndicesOptions controls how unavailable indices are resolved and how wildcard expressions are expanded
		request.indicesOptions(IndicesOptions.lenientExpandOpen());
		
		DeleteIndexResponse deleteIndexResponse = null;
		try {
			deleteIndexResponse = client.indices().delete(request);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("##### [IOException] : deleteIndex " + e.getLocalizedMessage());
		}
		
		return deleteIndexResponse;
	}
	
	public static void deleteIndexAsync(String indexName) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		
		//Timeout to wait for the all the nodes to acknowledge the index deletion as a TimeValue
		//Timeout to wait for the all the nodes to acknowledge the index deletion as a String
		request.timeout(TimeValue.timeValueMinutes(2));
		request.timeout("2m");
		
		//Timeout to connect to the master node as a TimeValue
		//Timeout to connect to the master node as a String
		request.masterNodeTimeout(TimeValue.timeValueMinutes(1)); 
		request.masterNodeTimeout("1m"); 
		
		//Setting IndicesOptions controls how unavailable indices are resolved and how wildcard expressions are expanded
		request.indicesOptions(IndicesOptions.lenientExpandOpen());
		
		ActionListener<DeleteIndexResponse> listener = new ActionListener<DeleteIndexResponse>() {
		    @Override
		    public void onResponse(DeleteIndexResponse deleteIndexResponse) {
		    		//boolean acknowledged = deleteIndexResponse.isAcknowledged();
		    		
		    		//Do something if the index to be deleted was not found
			    	try {
			    	    DeleteIndexRequest request = new DeleteIndexRequest("does_not_exist");
			    	    client.indices().delete(request);
			    	} catch (ElasticsearchException exception) {
			    	    if (exception.status() == RestStatus.NOT_FOUND) {
			    	    		logger.debug("##### [ElasticsearchException] onResponse index not found : " + exception.getLocalizedMessage());
			    	    } else {
			    	    		logger.debug("##### [ElasticsearchException] onResponse : " + exception.getLocalizedMessage());
			    	    }
			    	} catch (IOException e) {
						// TODO Auto-generated catch block
			    		logger.debug("##### [IOException] : onResponse client.close() : " + e.getLocalizedMessage());
				}
		    	
		        try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.debug("##### [IOException] deleteIndexAsync client.close() : " + e.getLocalizedMessage());
				}
		    }

		    @Override
		    public void onFailure(Exception e) {
		    		logger.debug("##### [Exception] deleteIndexAsync onFailure : " + e.getLocalizedMessage());
		    	
		    		try {
					client.close();
				} catch (IOException ie) {
					// TODO Auto-generated catch block
					logger.debug("##### [IOException] deleteIndexAsync client.close() : " + ie.getLocalizedMessage());
				}
		    }
		};
		
		client.indices().deleteAsync(request, listener);
	}
	
	public static IndexResponse importData (String id, String index, String type, Map<String, String> keyValsMap) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		IndexRequest request = new IndexRequest(index, type, id)
		        .source(keyValsMap);

		
		//Routing value
		//request.routing("routing");
		
		//Parent value
		//request.parent("parent");
		
		//Timeout to wait for primary shard to become available as a TimeValue
		//Timeout to wait for primary shard to become available as a String
//		request.timeout(TimeValue.timeValueSeconds(1));
//		request.timeout("1s");
		
		//Refresh policy as a WriteRequest.RefreshPolicy instance
		//Refresh policy as a String
//		request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
//		request.setRefreshPolicy("wait_for");
		
		//Version
		//request.version(2);
		
		//Version type
		//request.versionType(VersionType.EXTERNAL);
		
		//Operation type provided as an DocWriteRequest.OpType value
		//Operation type provided as a String: can be create or update (default)
//		request.opType(DocWriteRequest.OpType.CREATE);
//		request.opType("create");
		
		//The name of the ingest pipeline to be executed before indexing the document
		//request.setPipeline("pipeline");
		
		IndexResponse indexResponse = null;
		try {
			indexResponse = client.index(request);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("##### [Exception] importData : " + e.getLocalizedMessage());
		}
		
		return indexResponse;
	}
	
	public static void importDataAsync (String id, String index, String type, Map<String, String> keyValsMap) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		IndexRequest request = new IndexRequest(index, type, id)
		        .source(keyValsMap);
		
		//Routing value
		//request.routing("routing");
		
		//Parent value
		//request.parent("parent");
		
		//Timeout to wait for primary shard to become available as a TimeValue
		//Timeout to wait for primary shard to become available as a String
//		request.timeout(TimeValue.timeValueSeconds(1));
//		request.timeout("1s");
		
		//Refresh policy as a WriteRequest.RefreshPolicy instance
		//Refresh policy as a String
//		request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
//		request.setRefreshPolicy("wait_for");
		
		//Version
		//request.version(2);
		
		//Version type
		//request.versionType(VersionType.EXTERNAL);
		
		//Operation type provided as an DocWriteRequest.OpType value
		//Operation type provided as a String: can be create or update (default)
//		request.opType(DocWriteRequest.OpType.CREATE);
//		request.opType("create");
		
		//The name of the ingest pipeline to be executed before indexing the document
		//request.setPipeline("pipeline");
		
		ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
		    @Override
		    public void onResponse(IndexResponse indexResponse) {
		    		try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.debug("##### [IOException] importDataAsync onResponse client.close() : " + e.getLocalizedMessage());
				}
		    		isAsyncImportComplete = true;
		    		//Handle (if needed) the case where the document was created for the first time
				//Handle (if needed) the case where the document was rewritten as it was already existing
				//Handle the situation where number of successful shards is less than total shards
				//Handle the potential failures
//			    	String index = indexResponse.getIndex();
//			    	String type = indexResponse.getType();
//			    	String id = indexResponse.getId();
//			    	long version = indexResponse.getVersion();
//			    	
//			    	if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
//			    	    
//			    	} else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
//			    	    
//			    	}
//			    	
//			    	ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
//			    	if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
//			    	    
//			    	}
//			    	
//			    	if (shardInfo.getFailed() > 0) {
//			    	    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
//			    	        String reason = failure.reason(); 
//			    	    }
//			    	}
		    	
		    }

		    @Override
		    public void onFailure(Exception e) {
		    		logger.debug("##### [IOException] importDataAsync onFailure : " + e.getLocalizedMessage());
		    }
		};
		
		client.indexAsync(request, listener);
	}
	
	public static BulkResponse bulkImportData (List<String> idList, String index, String type, List<Map<String,String>> keyVals) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		BulkRequest request = new BulkRequest();
		
		int cnt = keyVals.size();
		for (int i = 0; i < cnt; i++) {
			Map<String,String> keyValMap = keyVals.get(i);
			
			request.add(new IndexRequest(index, type, idList.get(i))
			        .source(keyValMap)
			);
		}
		
		//Timeout to wait for the bulk request to be performed as a TimeValue
		//Timeout to wait for the bulk request to be performed as a String
//		request.timeout(TimeValue.timeValueMinutes(2));
//		request.timeout("2m");
		
		//Refresh policy as a WriteRequest.RefreshPolicy instance
		//Refresh policy as a String
//		request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); 
//		request.setRefreshPolicy("wait_for");
		
		//Sets the number of shard copies that must be active before proceeding with the index/update/delete operations.
		//Number of shard copies provided as a ActiveShardCount: can be ActiveShardCount.ALL, ActiveShardCount.ONE or ActiveShardCount.DEFAULT (default)
//		request.waitForActiveShards(2); 
//		request.waitForActiveShards(ActiveShardCount.ALL);
		
		BulkResponse bulkResponse = null;
		try {
			bulkResponse = client.bulk(request);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("##### [Exception] bulkImportData : " + e.getLocalizedMessage());
		}
		
		return bulkResponse;
	}
	
	public static void bulkImportDataAsync (List<String> idList, String index, String type, List<Map<String,String>> keyVals) {
		RestHighLevelClient client = ElasticClientHelper.newRestHighLevelClient();
		BulkRequest request = new BulkRequest();
		
		int cnt = keyVals.size();
		for (int i = 0; i < cnt; i++) {
			Map<String,String> keyValMap = keyVals.get(i);
			
			request.add(new IndexRequest(index, type, idList.get(i))
			        .source(keyValMap)
			);
		}
		
		//Timeout to wait for the bulk request to be performed as a TimeValue
		//Timeout to wait for the bulk request to be performed as a String
//		request.timeout(TimeValue.timeValueMinutes(2));
//		request.timeout("2m");
		
		//Refresh policy as a WriteRequest.RefreshPolicy instance
		//Refresh policy as a String
//		request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); 
//		request.setRefreshPolicy("wait_for");
		
		//Sets the number of shard copies that must be active before proceeding with the index/update/delete operations.
		//Number of shard copies provided as a ActiveShardCount: can be ActiveShardCount.ALL, ActiveShardCount.ONE or ActiveShardCount.DEFAULT (default)
//		request.waitForActiveShards(2); 
//		request.waitForActiveShards(ActiveShardCount.ALL);
		
		ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
		    @Override
		    public void onResponse(BulkResponse bulkResponse) {
		    		isAsyncBulkImportComplete = true;
		    		try {
		    			client.close();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			logger.debug("##### [IOException] bulkImportDataAsync onResponse client.close() : " + e.getLocalizedMessage());
		    		}
		    }

		    @Override
		    public void onFailure(Exception e) {
		    		logger.debug("##### [IOException] bulkImportDataAsync onFailure : " + e.getLocalizedMessage());
		    		try {
		    			client.close();
		    		} catch (IOException ie) {
		    			// TODO Auto-generated catch block
		    			logger.debug("##### [IOException] bulkImportDataAsync onFailure client.close() : " + ie.getLocalizedMessage());
		    		}
		    }
		};
		
		client.bulkAsync(request, listener);
	}
}
