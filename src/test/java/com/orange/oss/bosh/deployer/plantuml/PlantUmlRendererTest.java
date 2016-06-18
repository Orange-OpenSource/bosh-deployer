package com.orange.oss.bosh.deployer.plantuml;



import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.BoshClient;
import com.orange.oss.bosh.deployer.BoshDeployerApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("integration")

public class PlantUmlRendererTest {

	@Autowired
	BoshClient client;

	
	
	@Test
	public void testRender() {
		
		
		//given a deployment
		
		String deploymentName="hazelcast";
		
		//when
		String umlText=this.client.renderUml(deploymentName);
		
		//then
		assertThat(umlText).isNotEmpty();
		
		
	}

}
