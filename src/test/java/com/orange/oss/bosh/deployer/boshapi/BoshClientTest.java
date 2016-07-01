package com.orange.oss.bosh.deployer.boshapi;


import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.orange.oss.bosh.deployer.BoshDeployerApplication;
import com.orange.oss.bosh.deployer.boshapi.BoshClient;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.VmFull;


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
		
		//vm
		assertThat(vmsDetails.get(0).agent_id).isNotEmpty();
		assertThat(vmsDetails.get(0).az).isNotEmpty();
		assertThat(vmsDetails.get(0).bootstrap).isNotNull();
		//assertThat(vmsDetails.get(0).disk_cid).isNotEmpty();
		assertThat(vmsDetails.get(0).id).isNotEmpty();
		assertThat(vmsDetails.get(0).job_name).isNotEmpty();
		assertThat(vmsDetails.get(0).job_state).isNotEmpty();
		assertThat(vmsDetails.get(0).resource_pool).isNotEmpty();
		assertThat(vmsDetails.get(0).resurrection_paused).isNotNull();
		assertThat(vmsDetails.get(0).vm_cid).isNotNull();		
		assertThat(vmsDetails.get(0).vm_type).isNotNull();		
		
		//vitals
		assertThat(vmsDetails.get(0).vitals.cpu.user).isNotNull();
		assertThat(vmsDetails.get(0).vitals.cpu.sys).isNotNull();
		assertThat(vmsDetails.get(0).vitals.cpu.wait).isNotNull();
		
		assertThat(vmsDetails.get(0).dns.get(0)).isNotEmpty();
		
		//ips
		assertThat(vmsDetails.get(0).ips.get(0)).isNotEmpty();

		//processes
		assertThat(vmsDetails.get(0).processes.get(0).name).isNotEmpty();
		assertThat(vmsDetails.get(0).processes.get(0).state).isNotEmpty();
		
		assertThat(vmsDetails.get(0).processes.get(0).uptime.secs).isPositive();
		assertThat(vmsDetails.get(0).processes.get(0).cpu.total).isNotNull();
		
	}

	
	@Test
	public void testRetrieveServiceVms(){
		String deploymentName="hazelcast"; //fix get deployment from director
		String jobName="hazelcast_node";
		Map<String,Object> credentials=this.client.retrieveServiceVms(deploymentName,jobName);
		
		
	}
	
	
	@Test
	public void testCloneAndDeploy(){
		String newDeploymentName="clone-hazelcast-"+UUID.randomUUID();
		List<VmFull> details=this.client.cloneAndDeploy("hazelcast",newDeploymentName);
		for (VmFull vm: details){
			logger.info("vm status ");
			
			List<String> ips=vm.ips;
			for (String ip:ips){
				logger.info("deployed vm {}/{} : {}",vm.job_name,vm.index,ip);
			}
		}
		
		//FIXME: now delete deployment
		this.client.deleteForceDeployment(newDeploymentName);
		
		
	}
	
}
