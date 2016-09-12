package com.orange.oss.bosh.deployer.boshapi;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.fest.assertions.Assertions;
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
import com.orange.oss.bosh.deployer.BoshDeployerApplication;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings;
import com.orange.oss.bosh.deployer.boshapi.BoshFeignClient;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Release;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.ReleaseVersion;




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
                //.withHeader("Content-Type", "text/plain")
                .withHeader("Content-Type", "application/json")
                .withBodyFile("stemcells.json")));
	
	List<ApiMappings.Stemcell> stemcells=client.getStemcells();
	Assertions.assertThat(stemcells.size()).isEqualTo(1);
	Assertions.assertThat(stemcells.get(0).name).isEqualTo("bosh-openstack-kvm-ubuntu-trusty-go_agent");
	
	Assertions.assertThat(stemcells.get(0).operating_system).isEqualTo("ubuntu-trusty");
	Assertions.assertThat(stemcells.get(0).version).isEqualTo("3232.3");

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
		Release release=releases.get(0);
		Assertions.assertThat(release.name).isEqualTo("broker-registrar");

		ReleaseVersion rv=release.release_versions.get(0);
		
		Assertions.assertThat(rv.version).isEqualTo("1");
		Assertions.assertThat(rv.commit_hash).isEqualTo("b4336774");		
		Assertions.assertThat(rv.currently_deployed).isEqualTo(Boolean.TRUE); 
		Assertions.assertThat(rv.uncommitted_changes).isEqualTo(Boolean.TRUE);
		List<String> expectedJobNames=new ArrayList<>();
		expectedJobNames.add("broker-deregistrar");
		expectedJobNames.add("broker-registrar");
		Assertions.assertThat(rv.job_names).isEqualTo(expectedJobNames);

	}

	@Test
	public void testGetDeployments() {
		stubFor(get(urlEqualTo("/deployments"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("deployments.json")));
		
		List<Deployment> deployments=client.getDeployments();
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
