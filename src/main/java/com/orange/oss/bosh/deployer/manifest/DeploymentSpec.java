package com.orange.oss.bosh.deployer.manifest;

import java.util.List;
import java.util.Map;

/**
 * Simple description of a target bosh deployment.
 * Use to express a compact desc for a bosh-ondemand deployment to implement a service
 * 
 * @author poblin-orange
 *
 */
public class DeploymentSpec {

	public String deploymentNamePrefix;
	
	public List<InstanceGroup> instanceGroups;
	
	public static class InstanceGroup {
		public String releaseName;
		public String jobName;
		public int instances=1;
		public int maxInFlight=1;
		
		public boolean registerInstancesOnConsul=false;
		public boolean exposeWithRouteRegistrar=false;
		public boolean exposeAsVip=false;
		public boolean useMultipleAzs=false;
		
		//properties, key is flat dot separated key (ww.xx.yy), bosh job sec format and value is String
		public Map<String, String> properties;
		public Map<String, String> randomCredentials;
		
	}
	
	
	
	
	
}
