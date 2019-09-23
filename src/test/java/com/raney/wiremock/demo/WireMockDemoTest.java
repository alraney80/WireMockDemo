package com.raney.wiremock.demo;

import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.springframework.boot.test.context.SpringBootTest;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("Demo WireMock Tests")
class WireMockDemoTest {
	
	private String body = "\"body\": \"Accepted\"";
	
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
	@DisplayName("HTTP API mock server test")
	void positiveTest() throws ClientProtocolException, IOException {
		
		//1. stub the end user
		stubFor(get(urlEqualTo("/group/owner"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(body))
		);
		
		
		
		//2. make request
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8080/group/owner");
		HttpResponse httpResponse = httpClient.execute(request);
		String stringResponse = convertHttpResponseToString(httpResponse);
		
		
		//3. verify
		verify(getRequestedFor(urlEqualTo("/group/owner")));

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
