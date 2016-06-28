package com.orange.oss.bosh.deployer.cfbroker.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Instance {

	@Id
	private String serviceInstanceId;
	
	private String deployment;
	
	
	public String getDeployment() {
		return deployment;
	}

	public void setDeployment(String deployment) {
		this.deployment = deployment;
	}


	private Integer lastTaskId;

	public Integer getLastTaskId() {
		return lastTaskId;
	}

	public void setLastTaskId(Integer lastTaskId) {
		this.lastTaskId = lastTaskId;
	}

	protected Instance(){
	}
	
	public Instance(String id){
		this.serviceInstanceId=id;
	}
	
	
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	
}
