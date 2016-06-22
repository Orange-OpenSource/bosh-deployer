package com.orange.oss.bosh.deployer.cfbroker;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Cf service broker API Feign description
 * @author poblin-orange
 *
 */
@FeignClient(name="broker-client",url="http://localhost:8080",configuration=com.orange.oss.bosh.brokerfeigncfg.BrokerFeignConfiguration.class)
public interface BrokerFeignClient {

	//------------------------------
	@RequestMapping(method = RequestMethod.GET, value="/v2/catalog")
	public String catalog();



}
