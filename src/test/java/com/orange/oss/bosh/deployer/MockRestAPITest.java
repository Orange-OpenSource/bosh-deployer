package com.orange.oss.bosh.deployer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("mock")

public class MockRestAPITest {

	@Autowired
	ConfigurableEnvironment env;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080
	
	
	@Before
	public void setup(){
		env.setActiveProfiles("mock");
	}
	
	
	@Autowired
	BoshFeignClient client;
	
	@Test
	public void testInfos() {
		
		stubFor(get(urlEqualTo("/info"))
	            //.withHeader("Accept", equalTo("application/json"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("info.json")));
		
		ApiMappings.Info infos=client.getInfo();
		
	}

	@Test
	public void testDeployments() {
		
		stubFor(get(urlEqualTo("/deployments"))
	            //.withHeader("Accept", equalTo("application/json"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("deployments.json")));
		
		ApiMappings.Deployments deployments=client.getDeployments();
		
	}
	
	@Test
	public void testStemcells() {
		
		stubFor(get(urlEqualTo("/stemcells"))
	            //.withHeader("Accept", equalTo("application/json"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("stemcells.json")));
		
		
		List<ApiMappings.Stemcell> stemcells=client.getStemcells();
		
	}
	
	
	
}
