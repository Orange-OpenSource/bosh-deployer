package com.orange.oss.bosh.deployer.cfbroker;

import java.util.UUID;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.BoshDeployerApplication;

import io.swagger.model.Binding;
import io.swagger.model.CatalogServices;
import io.swagger.model.DashboardUrl;
import io.swagger.model.Parameter;
import io.swagger.model.Plan;
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
	CatalogFeignClient catalog;
	
	@Autowired
	ServiceInstancesFeignClient services;
	
	
	
	@Test
	public void testBrokerLifecycle(){
		
		ResponseEntity<CatalogServices> cat2=catalog.catalog();
		Assertions.assertThat(cat2).isNotNull();
		
		Services service=cat2.getBody().getServices().get(0);
		Assertions.assertThat(service).isNotNull();
		Assertions.assertThat(service.getBindable()).isTrue();
		

		
		Plan plan=service.getPlans().get(0);
		Assertions.assertThat(plan.getId()).isNotEmpty();
		
		
		//now create a service instance
		
		String instanceId=UUID.randomUUID().toString();
		ResponseEntity<DashboardUrl> dashboard=services.createServiceInstance(instanceId, null);

		String url=dashboard.getBody().getDashboardUrl();
		Assertions.assertThat(url).isNotEmpty();
		
		//now poll until service ready
		
//		services.lastOperation(instanceId,null);
		
		
		//bind the service
		String appGuid=UUID.randomUUID().toString();
		
		String bindingId=UUID.randomUUID().toString();
		Binding binding=new Binding();
		binding.setAppGuid(appGuid);
		Parameter parameters=new Parameter();
//		parameters.setName("");
//		parameters.setValue("");
		binding.setParameters(parameters);
		binding.setPlanId(plan.getId());
		binding.setServiceId(service.getId());
		
//		ResponseEntity<BindingResponse> bindingResponse=services.serviceBind(instanceId, bindingId, binding);
		
		
		
		
		
		
		
		
		//check and assert resulting credentials
		
		
		//check dashboard
		
		
		//unbind the services
		
		
		//delete the service instance
		
		
	}

}
