package com.orange.oss.bosh.deployer.boshapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.SingleDeployment;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Task;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.TaskOutput;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.TaskStatus;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.VmFull;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping;
import com.orange.oss.bosh.deployer.manifest.ManifestParser;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping.Network;
import com.orange.oss.bosh.deployer.plantuml.PlantUmlRender;



@Component
public class BoshClient {

	
	private static Logger logger=LoggerFactory.getLogger(BoshClient.class.getName());
	
	@Autowired
	private BoshFeignClient client;
	
	@Autowired
	private ManifestParser manifestParser;
	
	@Autowired
	private PlantUmlRender umlRenderer;
	
	private int pollingFrequencySeconds = 2; //polls director every 2s for task status
	
	
	/**
	 * Generates a plantuml description file, for graphic rendering
	 * @param deploymentName
	 * @return
	 */
	public String renderUml(String deploymentName){
		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();
		
		//parse manifest.yaml
		SingleDeployment manifestText=client.getDeployment(deploymentName);
		ManifestMapping.Manifest manifest=this.manifestParser.parser(manifestText.manifest);
		
		return this.umlRenderer.renderUml(d,manifest);
		

	}
	
	/**
	 * Create a new deployment, copying an existing one.
	 * 
	 * @param deploymentName
	 * @return
	 */
	public List<VmFull> cloneAndDeploy(String deploymentName,String  newDeploymentName){
		
		ApiMappings.Deployment d=client.getDeployments().stream()
				.filter(depl-> depl.name.equals(deploymentName))
				.findFirst()
				.get();
		//retrieve manifest
		String manifest=client.getDeployment(deploymentName).manifest;
		
		
		//fix manifest to clone depl
		
		ManifestMapping.Manifest pojoManifest=this.manifestParser.parser(manifest);

		pojoManifest.name=newDeploymentName;
		
		//FIXME: change network ?patch network to target ondemand network
		pojoManifest.instance_groups.forEach(ig -> {
			Network n=new Network();
			n.name="net-bosh-ondemand";
			ig.networks=new ArrayList<Network>();
			ig.networks.add(n);
		});
		
		
		String newManifest=this.manifestParser.generate(pojoManifest);
		logger.info("generated new manifest {}",newManifest);
		
		
		return deploy(newDeploymentName, newManifest);
	}

	private List<VmFull> deploy(String deploymentName, String manifest) {
		//now post, with flag recreate
		
		ApiMappings.Task task=client.createupdateDeployment(manifest); 

		Task finalResultTask=this.waitForTaskDone(task, 60*20); //20mins
		
		//assert success
		if (finalResultTask.state!=TaskStatus.done) {
			logger.error("Failed deployment, ends with status {}:\n {}",finalResultTask.state,finalResultTask.result);
			
			//retrieve complete logs error
			String detailledResult=client.getTaskDebug(finalResultTask.id, TaskOutput.result);
			//FIXME: retrieve the complete result
			
			
			throw new IllegalArgumentException("Failed Deployment "+finalResultTask+":"+finalResultTask.result+"\n"+detailledResult);
		}
		logger.info("created deployment : {}",deploymentName);		
		
		//retrieves vms and ip address
		 List<VmFull> detailsVMs=this.detailsVMs(deploymentName);
		 return detailsVMs;
	}

	
	/**
	 * simple aync deployment
	 * @param deploymentName
	 * @param manifest
	 * @return the bosh task Id
	 */
	public int asyncDeploy(String deploymentName, String manifest) {
		ApiMappings.Task task=client.createupdateDeployment(manifest); 
		logger.info("launched deployment : {}. Task Id is {}",deploymentName,task.id);		
		return task.id;
	}
	
	
	
	
	/**
	 * deploys a manifest.
	 * Updates stemcell and release references to latest in bosh director
	 * @param manifest
	 * @return
	 */
	public List<VmFull> deploy(ManifestMapping.Manifest manifest){
		String deploymentName=manifest.name;
		
		//TODO change all release version to lastest available on director ? 
		//TODO change all stemcell version to lastest available on director
		// use full with bosh2 ?
		
		
		
		return this.deploy(deploymentName, this.manifestParser.generate(manifest));
	}
	
	
	/**
	 * get vms details for a given deployment
	 * @param deploymentName
	 * @return
	 */
	public List<VmFull> detailsVMs(String deploymentName){
		
		//launch task
		Task fetchVmsInfos=client.getVmsFormat(deploymentName,"full");
		
		//check task finished
		waitForTaskDone(fetchVmsInfos, 60); //timeout 60s

		//get task details
		String result=client.getTaskDebug(fetchVmsInfos.id, TaskOutput.result);
		
		logger.debug("raw vms detailled vms result :\n{}",result);
		
		//1 ligne per vm json fragment
		ObjectMapper mapper=new ObjectMapper();		
		String[] lines = result.split(System.getProperty("line.separator"));
		List<VmFull> details=new ArrayList<VmFull>();
		for (String vmJson:lines){
			logger.debug("parsing vm \n{}",vmJson);
			try {
				VmFull vmfull = mapper.readValue(vmJson,ApiMappings.VmFull.class);
				details.add(vmfull);
			} catch (IOException e) {
				logger.error("failure parsing vm details: {}",e);
				throw new IllegalArgumentException(e);
			}
		}

		return details;
	}


	/**
	 * Force delete a bosh deployment
	 * 
	 * @param newDeploymentName
	 */
	public void deleteForceDeployment(String deploymentName) {
		Task deleteTask=this.client.deleteDeployments(deploymentName, true);
		Task finalResultTask=this.waitForTaskDone(deleteTask, 60*20); //20mins
		//assert success
		if (finalResultTask.state!=TaskStatus.done) {
			logger.error("Delete deployment Failed, ends with status {}:\n {}",finalResultTask.state,finalResultTask.result);
			throw new IllegalArgumentException("Delete Deployment Failed "+finalResultTask);
		}
		logger.info("deleted deployment : {}",deploymentName);
	}
	
	/**
	 * polls the director, waiting for task to be done
	 * @param task
	 * @param timeoutSeconds
	 * @return
	 */
	
	private Task waitForTaskDone(Task task, int timeoutSeconds) {
		int taskId=task.id;
		
		//Pool task for deploy done
		Task currentTask =null;
		do {
			currentTask = client.getTask(taskId);
			logger.info("current deployment status:  "+ currentTask.state);
			try {

				Thread.sleep(pollingFrequencySeconds*1000); //poll period is 2s
			} catch (InterruptedException e) {
			}
			
		}while(currentTask.state!=TaskStatus.done && currentTask.state!=TaskStatus.error);
		
		if (currentTask.state==TaskStatus.error){
			logger.error("Error ",currentTask.result);
		}
		
		return currentTask;
		
	}
	/**
	 * retrieve a task from director
	 * @param taskId
	 */
	public Task getTask(int taskId) {
		return this.client.getTask(taskId);
	}
	
	
	
}
