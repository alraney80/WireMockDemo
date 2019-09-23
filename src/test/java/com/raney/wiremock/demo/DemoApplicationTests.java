package com.raney.wiremock.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("Configure the returned HTTP information")
public class DemoApplicationTests {
	
	private String body = "\"body\": \"Accepted\"";
	
//	@Rule
//	public WireMockRule wireMockRule = new WireMockRule(port);
	
	private RestTemplate restTemplate;
	private WireMockServer wireMockServer;
	  
	@BeforeEach
	void configureSystemUnderTest() {  
		this.wireMockServer = new WireMockServer();
	    this.wireMockServer.start();
	}
	  
	@AfterEach
	void stopWireMockServer() {
	    this.wireMockServer.stop();
	}
	
	@Test
	@DisplayName("Positive Test")
	public void testUser() throws ClientProtocolException, IOException {
		//Stub the end user
		stubFor(get(urlEqualTo("/group/owner"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(body))
		);
		
		//make request
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/group/owner");
		HttpResponse httpResponse = httpClient.execute(request);
		String stringResponse = convertHttpResponseToString(httpResponse);
		
		//verify
		verify(getRequestedFor(urlEqualTo("/group/owner")));
		assertEquals(200, httpResponse.getStatusLine().getStatusCode());
		assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
		assertEquals("\"body\": \"Accepted\"", stringResponse);
		
	}
	
	@Test
	@DisplayName("Negative Test")
	void negativeTest() throws ClientProtocolException, IOException {
		//Stub the end user
		stubFor(get(urlEqualTo("/group/owner"))
				.willReturn(aResponse()
						.withStatus(404)
						.withHeader("Content-Type", "application/json")
						.withBody(body))
		);
		
		//make request
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/group/owner");
		HttpResponse httpResponse = httpClient.execute(request);
		String stringResponse = convertHttpResponseToString(httpResponse);
		
		//verify
		verify(getRequestedFor(urlEqualTo("/group/owner")));
		assertEquals(404, httpResponse.getStatusLine().getStatusCode());
	}
	
	private String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
	    InputStream inputStream = httpResponse.getEntity().getContent();
	    return convertInputStreamToString(inputStream);
	}
	
	private String convertInputStreamToString(InputStream inputStream) {
	    Scanner scanner = new Scanner(inputStream, "UTF-8");
	    String string = scanner.useDelimiter("\\Z").next();
	    scanner.close();
	    return string;
	}

}
