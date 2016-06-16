package com.orange.oss.bosh.deployer;


import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.VmFull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("integration")

public class BoshClientTest {

	private static Logger logger=LoggerFactory.getLogger(BoshClientTest.class.getName());
	
	@Autowired
	BoshClient client;

	@Test
	public void testDetailsVMs() {
		
		String deploymentName="hazelcast"; //fix get deployment from director
		List<VmFull> vmsDetails=this.client.detailsVMs(deploymentName);
		Assertions.assertThat(vmsDetails).isNotEmpty();
	}

}
