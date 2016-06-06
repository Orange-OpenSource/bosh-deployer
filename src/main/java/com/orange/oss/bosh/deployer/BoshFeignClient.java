package com.orange.oss.bosh.deployer;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="bosh-client",url="${director.url}",configuration=com.orange.oss.bosh.deployerfeigncfg.FeignConfiguration.class)
public interface BoshFeignClient {

	//------------------------------
	@RequestMapping(method = RequestMethod.GET, value="/info")
	ApiMappings.Info getInfo();
	
	
	//-------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/stemcells",consumes = "text/html",          
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            })
	List<ApiMappings.Stemcell> getStemcells();

	//-------------------------	
	
	@RequestMapping(method = RequestMethod.GET, value="/releases")
	List<ApiMappings.Release> getReleases();
	
	
	//-------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/deployments")
	ApiMappings.Deployments getDeployments();
	
	@RequestMapping(method = RequestMethod.GET, value="/deployments/{name}")
	ApiMappings.SingleDeployment getDeployment(@PathVariable("name") String deploymentName);
	
	
	@RequestMapping(method = RequestMethod.POST, value="/deployments/{name}",consumes="text/yaml")
	//@RequestHeader(name="Content-Type",value="text/yaml")
	ApiMappings.Task  createupdateDeployment(@PathVariable("name") String deploymentName,
						@RequestBody String manifest,
						@RequestParam("recreate") Boolean recreate,
						@RequestParam("skipDrain") String skipDrain); //coma separated jobs names to skip or *

	@RequestMapping(method = RequestMethod.DELETE, value="/deployments/{name}")
	ApiMappings.Task deleteDeployments(@PathVariable("name") String name, @RequestParam("force") boolean force);

//--------------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/deployments/{name}/vms")
	ApiMappings.Tasks getVms(@PathVariable("name") String deploymentName,@RequestParam("format") String format); //full
	
	



	//-------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	ApiMappings.Tasks getTasks(@RequestParam("force") int verbose); //2 is verbose ?
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	ApiMappings.Tasks getTasksbyState(@RequestParam("state") String state); //2 is verbose ?
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	ApiMappings.Tasks getTasksByDeployment(@RequestParam("deployment") String deployment); //2 is verbose ?
	
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks/{id}")
	String getTask(@PathVariable("id") String id); //2 is verbose ?
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks/{id}/output")
	String getTaskDebug(@PathVariable("id") String id,@RequestParam("type") String type); //debug or event or result
	
	
	//----------------------------------



}
