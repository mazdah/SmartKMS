package com.innotree.smartkms.elastic;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Value;

public class ElasticClientHelper {
	@Value("${xpack.security.user}")
	private static String user;
	
	@Value("${xpack.security.password}")
	private static String password;
	
	@Value("${elastic.host}")
	private static String host;
	
	@Value("${elastic.port}")
	private static String port;
	
	@Value("${cluster.name}")
	private static String clusterName;
	
	public static Client newTransportClient() {
		Settings settings = Settings.builder()
	            .put("cluster.name", clusterName)
	            .put("xpack.security.user", user + ":" + password)
//	            .put("client.transport.sniff", true)
	            .build();
		
		Client client = null;
		try {
			client = new PreBuiltXPackTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName(host), Integer.valueOf(port)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
        return client;
    }
    
//    public static Client newTransportClient(String cluster, InetAddress host) {
//        Settings settings = Settings.builder()
//            .put("cluster.name", cluster)
//            .put("xpack.security.user", user + ":" + password)
////            .put("client.transport.sniff", true)
//            .build();
//              
//        Client client = null;
//		client = new PreBuiltXPackTransportClient(settings)
//				.addTransportAddress(new TransportAddress(host, 9300));;
//        
//        return client;
//    }
    
    public static void connectDisconnect(Client client) {
        //connect
        ClusterHealthResponse response = client.admin().cluster().prepareHealth().execute().actionGet();
        System.out.println(
            "Cluster Name: " + response.getClusterName() + "\n" +
            "Cluster Health: " + response.getStatus());
        
        //disconnect
        client.close();
    }
}
