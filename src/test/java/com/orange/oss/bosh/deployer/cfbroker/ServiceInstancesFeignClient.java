package com.orange.oss.bosh.deployer.cfbroker;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.orange.oss.bosh.deployer.cfbroker.swagger.CatalogApi;
import com.orange.oss.bosh.deployer.cfbroker.swagger.ServiceInstancesApi;


/**
 * Cf service broker API Feign description
 * @author poblin-orange
 *
 */
@FeignClient(name="broker-client",url="${test.broker.url}",configuration=com.orange.oss.bosh.brokerfeigncfg.BrokerFeignConfiguration.class)
public interface ServiceInstancesFeignClient extends ServiceInstancesApi {



}
