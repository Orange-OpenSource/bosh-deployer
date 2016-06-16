package com.orange.oss.bosh.deployer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.orange.oss.bosh.deployer.ApiMappings.Task;
import com.orange.oss.bosh.deployer.ApiMappings.TaskOutput;
import com.orange.oss.bosh.deployer.ApiMappings.TaskStatus;
import com.orange.oss.bosh.deployer.ApiMappings.VmFull;
import com.orange.oss.bosh.deployer.ApiMappings.VmsFull;



@Component
public class BoshClient {

	
	private static Logger logger=LoggerFactory.getLogger(BoshClient.class.getName());
	
	@Autowired
	BoshFeignClient client;
	
	@Autowired
	ManifestParser parser;
	
	int pollingFrequencySeconds = 2; //polls director every 2s for task status
	
	
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


	private void waitForTaskDone(Task task, int timeoutSeconds) {
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
		
	}
	
	
	
	
}
