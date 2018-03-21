package com.innotree.smartkms.elastic;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ElasticClientHelper {
	
	private final static Logger logger = LoggerFactory.getLogger(ElasticClientHelper.class);
	
	@Value("${xpack.security.user}")
	public static String user;
	
	@Value("${xpack.security.password}")
	public static String password;
	
	@Value("${elastic.host}")
	public static String host;
	
	@Value("${elastic.port}")
	public static String port;
	
	@Value("${cluster.name}")
	public static String clusterName;
	
	@Value("${xpack.security.user}")
    public void setUser(String eluser) {
		user = eluser;
    }
	
	@Value("${xpack.security.password}")
    public void setPassword(String passwd) {
		password = passwd;
    }
	
	@Value("${elastic.host}")
    public void setHost(String elhost) {
		host = elhost;
    }
	
	@Value("${elastic.port}")
    public void setPort(String elport) {
		logger.debug("##### elport = {}", elport);
		port = elport;
    }
	
	@Value("${cluster.name}")
    public void setClusterName(String elClusterName) {
		clusterName = elClusterName;
    }
	
	public static Client newTransportClient() {
		logger.debug("##### port = {}", port);
		
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
