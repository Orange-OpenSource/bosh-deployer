package com.orange.oss.bosh.deployer.cfbroker;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.orange.oss.bosh.deployer.cfbroker.swagger.CatalogApi;
import com.orange.oss.bosh.deployer.cfbroker.swagger.ServiceInstancesApi;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.model.Empty;
import io.swagger.model.UnbindParameters;


/**
 * Cf service broker API Feign description
 * @author poblin-orange
 *
 */
@FeignClient(name="services",url="${test.broker.url}",configuration=com.orange.oss.bosh.brokerfeigncfg.BrokerFeignConfiguration.class)
public interface ServiceInstancesFeignClient extends ServiceInstancesApi {

	/**
	 * added to swagger generated 2.5 stub.
	 * 2.8 introduces async service provisionning. Need to implement getLastOperation verb
	 * see: 
	 */
	
    @ApiOperation(value = "Last operation on a service instance.", notes = "When a broker receives a last_operation request from Cloud Controller, ", response = Empty.class)
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Service instance status. The expected response body is  is {}.", response = LastOperationResponse.class),
        @ApiResponse(code = 410, message = "Appropriate only for asynchronous delete requests. Cloud Foundry will consider this response a success and remove the resource from its database. The expected response body is {}.", response = Empty.class) })
    @RequestMapping(value = "/service_instances/{instance_id}/last_operation",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<LastOperationResponse> lastOperation(@ApiParam(value = "The instance_id of a service instance is provided by the Cloud Controller. This ID will be used for future requests (bind and deprovision), so the broker must use it to correlate the resource it creates.",required=true ) @PathVariable("instance_id") String instanceId
        );


}
