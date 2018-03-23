package com.innotree.smartkms.elastic;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
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
	
	@Value("${elastic.host.data1}")
	public static String hostData1;
	
	@Value("${elastic.host.data2}")
	public static String hostData2;
	
	@Value("${elastic.host.data3}")
	public static String hostData3;
	
	@Value("${elastic.port}")
	public static String port;
	
	@Value("${elastic.http.port}")
	public static String httpPort;
	
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
	
	@Value("${elastic.host.data1}")
    public void setHostData1(String elhost) {
		hostData1 = elhost;
    }
	
	@Value("${elastic.host.data2}")
    public void setHostData2(String elhost) {
		hostData2 = elhost;
    }
	
	@Value("${elastic.host.data3}")
    public void setHostData3(String elhost) {
		hostData3 = elhost;
    }
	
	@Value("${elastic.port}")
    public void setPort(String elport) {
		logger.debug("##### elport = {}", elport);
		port = elport;
    }
	
	@Value("${elastic.http.port}")
    public void setHttpPort(String elhttpport) {
		logger.debug("##### elhttpport = {}", elhttpport);
		httpPort = elhttpport;
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
					.addTransportAddress(new TransportAddress(InetAddress.getByName(hostData1), Integer.valueOf(port)))
					.addTransportAddress(new TransportAddress(InetAddress.getByName(hostData2), Integer.valueOf(port)))
					.addTransportAddress(new TransportAddress(InetAddress.getByName(hostData3), Integer.valueOf(port)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
        return client;
    }
	
	public static RestHighLevelClient newRestHighLevelClient() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
		        new UsernamePasswordCredentials(user, password));
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(hostData1, Integer.valueOf(httpPort), "http"))
		        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
		            @Override
		            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
		                httpClientBuilder.disableAuthCaching(); 
		                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
		            }
		        }));
	
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
    
	// for TransportClient
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
