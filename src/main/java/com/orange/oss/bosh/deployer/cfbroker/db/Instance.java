package com.orange.oss.bosh.deployer.cfbroker.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Instance {

	@Id
	private String serviceInstanceId;

	protected Instance(){
	}
	
	public Instance(String id){
		this.serviceInstanceId=id;
	}
	
	
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	
}
