package com.orange.oss.bosh.deployer;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.ApiMappings.SingleDeployment;
import com.orange.oss.bosh.deployer.ApiMappings.Task;
import com.orange.oss.bosh.deployer.ApiMappings.TaskStatus;
import com.orange.oss.bosh.deployer.ApiMappings.Vm;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping;
import com.orange.oss.bosh.deployer.manifest.ManifestParser;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping.Network;



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
	
	
	@Test
	public void testBoshDeployRecreate(){
		
		String deploymentName="hazelcast";

		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();
		//retrieve manifest
		String manifest=client.getDeployment(deploymentName).manifest;
		
		
		//fix manifest to clone depl
		
		ManifestMapping.Manifest pojoManifest=this.manifestParser.parser(manifest);
		pojoManifest.name="clone-hazelcast-"+UUID.randomUUID();
		
		//patch network to target ondemand network
		pojoManifest.instance_groups.forEach(ig -> {
			Network n=new Network();
			n.name="net-bosh-ondemand";
			ig.networks=new ArrayList<Network>();
			ig.networks.add(n);
		});
		
		
		String newManifest=this.manifestParser.generate(pojoManifest);
		logger.info("generated new manifest {}",newManifest);
		
		
		//now post, with flag recreate
		//ApiMappings.Task task=client.createupdateDeployment(deploymentName, manifest, true, "*");
		ApiMappings.Task task=client.createupdateDeployment(/** deploymentName,**/ newManifest); //no use, depl name is in manifest
		
		int taskId=task.id;
		
		//Pool task for deploy done
		Task currentTask =null;
		do {
			currentTask = client.getTask(taskId);
			logger.info("current deployment status:  "+ currentTask.state);
			try {
				Thread.sleep(2000); //poll period is 2s
			} catch (InterruptedException e) {
			}
			
		}while(currentTask.state!=TaskStatus.done && currentTask.state!=TaskStatus.error);
		
		if (currentTask.state==TaskStatus.error){
			logger.error("Error ",currentTask.result); 
		}
		
		//assert success
		assertThat(currentTask.state).isEqualTo(TaskStatus.done);
		
		
	}
	
	@Test
	public void testInventory(){
		
		List<Deployment> deployments=client.getDeployments();
		for (Deployment depl:deployments){

			client.getVms(depl.name).stream()
			.forEach(vmf -> logger.info("vm {} {} {}/{} ",vmf.agent_id,vmf.cid,vmf.job,vmf.index));

			//task bosh vms vitals si full. String vmsList=client.getVmsFormat(depl.name, "full");
			
			
			
			
			
			List<Vm> vms=client.getVms(depl.name);
			vms.stream().forEach(v -> logger.info("vm: {} {}/{} ",v.agent_id,v.job,v.index));
			
			
			
			

			
		}
		
	}

	
	
}