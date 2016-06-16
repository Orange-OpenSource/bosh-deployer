package com.orange.oss.bosh.deployer;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.ApiMappings.Task;

import junit.framework.Assert;




@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BoshDeployerApplication.class})
@WebIntegrationTest({"server.port=0", "management.port=0"})
@ActiveProfiles("integration")


public class RestAPITest {

	private static Logger logger=LoggerFactory.getLogger(RestAPITest.class.getName());
	
	@Autowired
	HttpMessageConverters messageConverters;
	
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
		
		Assert.assertEquals(1,stemcells.size());
		Assert.assertEquals("bosh-openstack-kvm-ubuntu-trusty-go_agent", stemcells.get(0).name);
		Assert.assertEquals("ubuntu-trusty", stemcells.get(0).operating_system);
		Assert.assertEquals("3232.3", stemcells.get(0).version);		
	}
	
	
	@Test
	public void testVms(){
		
		
		String deploymentName="hazelcast";
		//String deploymentName="concourse1";
		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();

		client.getVms(d.name).stream().forEach(vm -> logger.info("vm: cid {} {}/{}  ",vm.cid,vm.job,vm.index));

		Task vmsListTask=client.getVmsFormat(d.name, "full");
	}
	
	@Test
	public void TestTasks(){
		
		client.getTasks(2).forEach(task -> logger.info("\ntask: \tnid: {} \tstate: {}\tresult: {}",task.id,task.state,task.result));
		client.getTasksByState(ApiMappings.TaskStatus.error).forEach(task -> logger.info("\ntask: \tnid: {} \tstate: {}\tresult: {}",task.id,task.state,task.result));
		client.getTasksByDeployment("hazelcast").forEach(task -> logger.info("\ntask: \tnid: {} \tstate: {}\tresult: {}",task.id,task.state,task.result));
		
		
		int taskId=client.getTasksByState(ApiMappings.TaskStatus.error).get(0).id;
		Task task=client.getTask(taskId);
		org.fest.assertions.Assertions.assertThat(task.state==ApiMappings.TaskStatus.error);
		
		client.getTaskDebug(taskId, ApiMappings.TaskOutput.debug);
		client.getTaskDebug(taskId, ApiMappings.TaskOutput.event);
		client.getTaskDebug(taskId, ApiMappings.TaskOutput.result);
		
		
	}
	
	
}
