package server.client;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

import server.model.Credentials;
import server.model.Match;
import server.model.User;

public class Client {
	
	private JsonService jsonService;
	private HttpClientContext httpClientContext;
	private User userLogged;
	
	public Client() {
		this.jsonService = new JsonService();
		this.httpClientContext = new HttpClientContext();
	}

	public User addMatch(Match match) {
		
		String matchJson = jsonService.match2Json(match);
		
		String playerJson = this.sendHttp("/client/addMatch", matchJson);
	
		return jsonService.json2Player(playerJson);
	}
	
	public User logUser(Credentials credentials) {
		
		String credentialsJson = jsonService.credentials2Json(credentials);
		
		String playerJson = this.sendHttp("/client/logPlayer", credentialsJson);
	
		return jsonService.json2Player(playerJson);
	}
	
	public boolean logUser(String username, String password) {
		Credentials credentials = new Credentials(username, password);
		
		this.userLogged = this.logUser(credentials);
		
		if(this.userLogged != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public User getUser() {
		return this.userLogged;
	}
	
	private String sendHttp(String function, String json) {
		
		String host = "localhost";
		int port = 8080;
		
		UsernamePasswordCredentials credentialsClient = new UsernamePasswordCredentials("SnakeReborn", "Snake123");		
		AuthScope authScope = new AuthScope(host, port);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(authScope, credentialsClient);
		
		HttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		
		String responseGSon = null;

		try {
			
		    HttpPost request = new HttpPost("http://" + host + ":" + port + function);
		    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		    
		    StringEntity entity = new StringEntity(json);
		    entity.setContentType(MediaType.APPLICATION_JSON);
		    
		    request.setEntity(entity);
		    HttpResponse response = httpClient.execute(request, this.httpClientContext);
		    
		    InputStream is = response.getEntity().getContent();	    
		    responseGSon = IOUtils.toString(is, "UTF-8");
		    
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return responseGSon;
	}
}
