package com.orange.oss.bosh.deployer.manifest;

import org.springframework.stereotype.Component;

@Component
public class DeploymentSpecFactory {

	public DeploymentSpec spec(){
		DeploymentSpec spec=new DeploymentSpec();
		
		
		//FIXME: generate spec from properties ?
		
		
		return spec;
	}
}
