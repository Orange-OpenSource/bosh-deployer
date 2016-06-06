package com.orange.oss.bosh.deployer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.client.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.orange.oss.bosh.deployer.ApiMappings.Release;
import com.orange.oss.bosh.deployer.ApiMappings.ReleaseVersion;

import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("mock")

public class MockRestAPITest {

	@Autowired
	ConfigurableEnvironment env;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080
	
	
	@Autowired
	BoshFeignClient client;
	
	
	@Test
	public void testGetInfo() {
		stubFor(get(urlEqualTo("/info"))
	            //.withHeader("Accept", equalTo("application/json"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("info.json")));
		
		ApiMappings.Info infos=client.getInfo();
	}

	@Test
	public void testGetStemcells() {
		stubFor(get(urlEqualTo("/stemcells"))
        	    .willReturn(aResponse()            	    
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBodyFile("stemcells.json")));
	
	List<ApiMappings.Stemcell> stemcells=client.getStemcells();
	Assert.assertEquals(1,stemcells.size());
	Assert.assertEquals("bosh-openstack-kvm-ubuntu-trusty-go_agent", stemcells.get(0).name);
	Assert.assertEquals("ubuntu-trusty", stemcells.get(0).operating_system);
	Assert.assertEquals("3232.3", stemcells.get(0).version);		
	}

	@Test
	public void testGetReleases() {
		stubFor(get(urlEqualTo("/releases"))
        	    .willReturn(aResponse()            	    
                .withStatus(200)
                //.withHeader("Content-Type", "text/plain")
                .withHeader("Content-Type", "application/json")
                .withBodyFile("releases.json")));
		List<Release> releases=client.getReleases();
		
		Assert.assertEquals("broker-registrar", releases.get(0).name);
		ReleaseVersion rv=releases.get(0).release_versions.get(0);
		
		Assert.assertEquals("1", rv.version);
		Assert.assertEquals("b4336774", rv.commit_hash);		
		Assert.assertEquals(Boolean.TRUE, rv.currently_deployed);
		Assert.assertEquals(Boolean.TRUE, rv.uncommitted_changes);
		List<String> expectedJobNames=new ArrayList<>();
		expectedJobNames.add("broker-deregistrar");
		expectedJobNames.add("broker-registrar");
		Assert.assertEquals(expectedJobNames, rv.job_names);
		


	}

	@Test
	public void testGetDeployments() {
		stubFor(get(urlEqualTo("/deployments"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("deployments.json")));
		
		ApiMappings.Deployments deployments=client.getDeployments();
	}

	@Test
	public void testGetDeployment() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateupdateDeployment() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteDeployments() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetVms() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTasksbyState() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTasksByDeployment() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskDebug() {
		fail("Not yet implemented");
	}

	
	
	
}
