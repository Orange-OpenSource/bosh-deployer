package com.orange.oss.bosh.deployer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class BoshClient {

	@Autowired
	BoshFeignClient client;
	

	
	
}
