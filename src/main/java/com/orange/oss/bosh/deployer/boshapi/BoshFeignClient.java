package com.orange.oss.bosh.deployer.boshapi;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Release;
import com.orange.oss.bosh.deployer.boshapi.ApiMappings.Vm;


/**
 * Director API Feign description
 * for missing verns, see https://github.com/cloudfoundry/bosh/blob/master/bosh_cli/lib/cli/client/director.rb
 * @author poblin-orange
 *
 */
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
	List<Release> getReleases();
	
	
	//-------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/deployments")
	List<Deployment> getDeployments();
	
	/**
	 * single deployment
	 * @param deploymentName
	 * @return last know correct manifest yaml file
	 */
	@RequestMapping(method = RequestMethod.GET, value="/deployments/{name}")
	ApiMappings.SingleDeployment getDeployment(@PathVariable("name") String deploymentName);
	
	
	@RequestMapping(method = RequestMethod.POST, value="/deployments/{name}",produces="text/yaml")
	ApiMappings.Task  createupdateDeployment(@PathVariable("name") String deploymentName,
						@RequestBody String manifest,
						@RequestParam("recreate") boolean recreate,
						@RequestParam("skipDrain") String skipDrain); //coma separated jobs names to skip or *

	@RequestMapping(method = RequestMethod.POST, value="/deployments",consumes="text/yaml")
	ApiMappings.Task  createupdateDeployment(@RequestBody String manifest);
	
	
	
	
	@RequestMapping(method = RequestMethod.DELETE, value="/deployments/{name}")
	ApiMappings.Task deleteDeployments(@PathVariable("name") String name, @RequestParam("force") boolean force);

//--------------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/deployments/{name}/vms")
	List<Vm> getVms(@PathVariable("name") String deploymentName);
	
	
	@RequestMapping(method = RequestMethod.GET, value="/deployments/{name}/vms")
	ApiMappings.Task getVmsFormat(@PathVariable("name") String deploymentName,@RequestParam("format") String format); //full



	//-------------------------	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	List<ApiMappings.Task> getTasks(@RequestParam("force") int verbose); //2 is verbose ?
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	List<ApiMappings.Task> getTasksByState(@RequestParam("state") ApiMappings.TaskStatus state);
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks")
	List<ApiMappings.Task> getTasksByDeployment(@RequestParam("deployment") String deployment); //2 is verbose ?
	
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks/{id}")
	ApiMappings.Task getTask(@PathVariable("id") int id);
	
	@RequestMapping(method = RequestMethod.GET, value="/tasks/{id}/output")
	String getTaskDebug(@PathVariable("id") int id,@RequestParam("type") ApiMappings.TaskOutput type); //debug or event or result
	
	
	//----------------------------------



}
