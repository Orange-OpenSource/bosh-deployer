package com.orange.oss.bosh.deployer;

import java.util.List;


import org.fest.assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.ApiMappings.SingleDeployment;

import junit.framework.Assert;




@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("integration")


public class RestAPIIntegrationTest {

	private static Logger logger=LoggerFactory.getLogger(RestAPIIntegrationTest.class.getName());
	
	@Autowired
	BoshFeignClient client;
	
	@Autowired
	ManifestParser manifestParser;
	
	@Test
	public void testNavigation() {
		ApiMappings.Info infos=client.getInfo();
		client.getReleases().stream().forEach(r -> logger.info("release: {} version {} ",r.name));
		
		client.getDeployments().stream().forEach(d -> logger.info("release: ",d.name));
		client.getStemcells().stream().forEach(s -> logger.info("stemcell: ",s.name));
		
		
		
		client.getDeployments().stream()
			.forEach(d -> {
				d.releases.stream().forEach(r ->  logger.info("deployment {} release {} ",d.name,r.name));
			});

		client.getDeployments().stream()
		.forEach(d -> {
			d.releases.stream().forEach(r ->  logger.info("deployment {} release {} ",d.name,r.name));
			d.stemcells.stream().forEach(s ->  logger.info("deployment {} stemcell {} ",d.name,s.name));
		});
		
		
		
		//client.getTasks(2).tasks.stream().forEach(t -> logger.info("tasks: ",t.id));
	}

	
	@Test
	public void testNavigation2() {
		
		
		String deploymentName="hazelcast";
		//String deploymentName="concourse1";
		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();
		
		SingleDeployment depl=client.getDeployment(d.name);
		String manifest=depl.manifest;
		//parse deployment manifest
		logger.info("retrieved manifest \n {}",manifest );
		ManifestMapping.Manifest m=this.manifestParser.parser(manifest);
			
		
		
	
	}

	
	
}