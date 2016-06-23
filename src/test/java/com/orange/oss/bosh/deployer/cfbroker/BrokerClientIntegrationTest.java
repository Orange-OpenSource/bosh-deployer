package com.orange.oss.bosh.deployer.cfbroker;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.BoshDeployerApplication;

import io.swagger.model.CatalogServices;
import io.swagger.model.Services;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
//@WebIntegrationTest({"server.port=0", "management.port=0"})
@EnableSwagger2
//@ComponentScan(basePackages = "io.swagger")
@ActiveProfiles("integration")

public class BrokerClientIntegrationTest {
	
	
	@Autowired
	BrokerFeignClient broker;
	
	@Autowired
	CatalogFeignClient catalog;
	
	
	
	@Test
	public void testBrokerLifecycle(){
		
		
//		String cat= broker.catalog();
//		Assertions.assertThat(cat).isNotEmpty();
		
		ResponseEntity<CatalogServices> cat2=catalog.catalog();
		Assertions.assertThat(cat2).isNotNull();
		
		Services service=cat2.getBody().getServices().get(0);
		Assertions.assertThat(service).isNotNull();
		Assertions.assertThat(service.getBindable()).isTrue();
		
		
	}

}
