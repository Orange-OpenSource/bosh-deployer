package com.orange.oss.bosh.deployer;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.Deployment;




@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("integration")


public class RestAPITest {

	
	
	@Autowired
	BoshFeignClient client;
	
	@Test
	public void testInfos() {
		ApiMappings.Info infos=client.getInfo();
		
		
	}

	@Test
	public void testDeployments() {
		List<Deployment> deployments=client.getDeployments();
		
	}
	
	@Test
	public void testStemcells() {
		List<ApiMappings.Stemcell> stemcells=client.getStemcells();
		
	}
	
	
	
}
