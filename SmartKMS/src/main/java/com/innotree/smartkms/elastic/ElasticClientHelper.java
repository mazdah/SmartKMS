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

public class ElasticClientHelper {
	public static Client newTransportClient() {
		Settings settings = Settings.builder()
	            .put("cluster.name", "elasticsearch")
	            .put("xpack.security.user", "elastic:w00hj8928")
//	            .put("client.transport.sniff", true)
	            .build();
		
		Client client = null;
		try {
			client = new PreBuiltXPackTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
        return client;
    }
    
    public static Client newTransportClient(String cluster, InetAddress host) {
        Settings settings = Settings.builder()
            .put("cluster.name", cluster)
            .put("xpack.security.user", "elastic:w00hj8928")
//            .put("client.transport.sniff", true)
            .build();
              
        Client client = null;
		client = new PreBuiltXPackTransportClient(settings)
				.addTransportAddress(new TransportAddress(host, 9300));;
        
        return client;
    }
    
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
