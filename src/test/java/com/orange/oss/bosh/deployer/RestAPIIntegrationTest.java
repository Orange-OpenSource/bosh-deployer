package com.orange.oss.bosh.deployer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orange.oss.bosh.deployer.ApiMappings.SingleDeployment;
import com.orange.oss.bosh.deployer.ApiMappings.TaskStatus;
import static org.fest.assertions.Assertions.*;



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
		//String deploymentName="concourse1";
		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();
		//retrieve manifest
		String manifest=client.getDeployment(deploymentName).manifest;
		
		
		//fix manifest to clone depl
		
		
		//now post, with flag recreate
		//ApiMappings.Task task=client.createupdateDeployment(deploymentName, manifest, true, "*");
		ApiMappings.Task task=client.createupdateDeployment(/** deploymentName,**/ manifest); //no use, depl name is in manifest
		
		int taskId=task.id;
		
		//Pool task for deploy done
		ApiMappings.TaskStatus status=null;
		do {
			status=client.getTask(taskId).state;
			logger.info("current deployment status:  "+status);
			
		}while(status==TaskStatus.processing);
		
		//assert success
		assertThat(status==TaskStatus.done);
		
		
		
	}

	
	
}