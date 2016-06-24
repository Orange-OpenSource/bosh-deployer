package com.orange.oss.bosh.deployer.cfbroker;

import java.util.UUID;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.BoshDeployerApplication;

import io.swagger.model.Binding;
import io.swagger.model.CatalogServices;
import io.swagger.model.DashboardUrl;
import io.swagger.model.Parameter;
import io.swagger.model.Plan;
import io.swagger.model.Service;
import io.swagger.model.Services;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
//@WebIntegrationTest({"server.port=0", "management.port=0"})
//@EnableSwagger2
@ActiveProfiles("integration")

public class BrokerClientIntegrationTest {
	
	private static Logger logger=LoggerFactory.getLogger(BrokerClientIntegrationTest.class);
	
	
	@Autowired
	CatalogFeignClient catalog;
	
	@Autowired
	ServiceInstancesFeignClient services;
	
	
	
	@Test
	public void testBrokerLifecycle(){
		
		
		logger.info("catalog");
		ResponseEntity<CatalogServices> cat2=catalog.catalog();
		Assertions.assertThat(cat2).isNotNull();
		
		Services service=cat2.getBody().getServices().get(0);
		Assertions.assertThat(service).isNotNull();
		Assertions.assertThat(service.getBindable()).isTrue();
		
		Plan plan=service.getPlans().get(0);
		Assertions.assertThat(plan.getId()).isNotEmpty();
		
		
		//now create a service instance
		logger.info("service instance creation");		
		String instanceId=UUID.randomUUID().toString();
		String spaceGuid=UUID.randomUUID().toString();
		String organizationGuid=UUID.randomUUID().toString();		
		
		Service srv=new Service();
		//Parameter parameteres;
		//srv.setParameteres(parameteres);
		srv.setPlanId(plan.getId());
		srv.setServiceId(service.getId());

		srv.setOrganizationGuid(organizationGuid);
		srv.setSpaceGuid(spaceGuid);
		ResponseEntity<DashboardUrl> dashboard=services.createServiceInstance(instanceId, srv);

		String url=dashboard.getBody().getDashboardUrl();
		//Assertions.assertThat(url).isNotEmpty();
		
		HttpStatus statusCode=dashboard.getStatusCode();
		Assertions.assertThat(statusCode).isEqualTo(HttpStatus.ACCEPTED); //ie 202, async processing
		
		
		
		
		//now poll until service ready
		
//		services.lastOperation(instanceId,null);
		
		
		//bind the service
		logger.info("service instance bind");		
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
		logger.info("service instance unbind");		
		
		//delete the service instance
		logger.info("delete service instance bind");		
		
		
	}

}
