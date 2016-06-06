package com.orange.oss.bosh.deployer;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;




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
		ApiMappings.Deployments deployments=client.getDeployments();
		
	}
	
	@Test
	public void testStemcells() {
		List<ApiMappings.Stemcell> stemcells=client.getStemcells();
		
	}
	
	
	
}
