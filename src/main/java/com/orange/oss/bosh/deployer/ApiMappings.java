package com.orange.oss.bosh.deployer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JSON mapping 
 * see for reference: https://bosh.io/docs/director-api-v1.html#overview
 * @author poblin-orange
 *
 */
public class ApiMappings {
	public static class Info {
		
		public String name;
		public String uuid;
		public String version;
		public String user; // user [String or null]: Logged in user’s user name if authentication is provided, otherwise null.
		public String cpi; //[String]: Name of the CPI the Director will use.
		//public Map <String,InfoAuth>  user_authentication=new HashMap<String,InfoAuth>(); // [Hash]:

		
	}

	public static class InfoAuth {
		public String type;// [String]: Type of the authentication the Director is configured to expect.
        public Map<String,String>options; // [Hash]: Additional information provided to how authentication should be performed.
		
	}
	
	
	public static class Tasks {
		public List<Task> tasks;
	}
	
	public static class Task  {
		public Integer id;// [Integer]: Numeric ID of the task.
		public String state; // [String]: Current state of the task. Possible values are: queued, processing, cancelled, cancelling, done, errored.
		public String description; //description [String]: Description of the task’s purpose.
		public Integer timestamp;// [Integer]: todo.
		public String result; // [String or null]: Description of the task’s result. Will not be populated (string) unless tasks finishes.
		public String user; // [String]: User which started the task.
	}
	
	public static class Stemcells {
		public List<Stemcell> stemcells;
	}
	
	public static class Stemcell {

		public String name;// [String]: Name of the stemcell.
		public String version; // [String]: Version of the stemcell.
		public String operating_system;// [String]: Operating system identifier. Example: ubuntu-trusty and centos-7.
		public String cid; // [String]: Cloud ID of the stemcell.
		public List<StemcellDeployment> deployments; // [Array]: List of deployments currently using this stemcell version.

	}
	
	public static class StemcellDeployment {
		public String name; // [String]: Deployment name.
	}
	
//	public static class Releases {
//		public List<Release> releases;
//	}
	
	public static class Release{
		public String name;
		public List<ReleaseVersion>release_versions;
	}
	
	public static class ReleaseVersion{
		public String version;// [String]: Version of the release version.
		public String commit_hash; // [String]: Identifier in the SCM repository for the release version source code.
		public Boolean uncommitted_changes; // [Boolean]: Whether or not the release version was created from a SCM repository with unsaved changes.
		public Boolean currently_deployed; // [Boolean]: Whether or not the release version is used by any deployments.
		public List<String>job_names; // [Array of strings]: List of job names associated with the release version.
		
	}
	
	public static class Deployments {
		public List<Deployment> deployments=new ArrayList<Deployment>();
		
	}
	
	public static class Deployment {
		public String name;// [String]: Name of the deployment.
	    public String cloud_config;// [String]: Indicator whether latest cloud config is used for this deployment. Possible values: none, outdated, latest.
	    public List<DeploymentRelease> releases;
	    public List<Stemcell> stemcells;// [Array] : List of stemcells used by the deploymemt. name [String]: Name of the stemcell. version [String]: Version of the stemcell. 
		
	}
	
	public static class DeploymentRelease {
        public String name; //[String]: Name of the release.
        public String version; // [String]: Version of the release.
		
	}
	
	public static class SingleDeployment {
		public String manifest;
	}
	
	
	public static class Vms {
		public List<Vm> vms;
	}
	
	public static class Vm {
	    public String agent_id;// [String]: Unique ID of the Agent associated with the VM.
	    public String cid;// [String]: Cloud ID of the VM.
	    public String job;// [String]: Name of the job.
	    public Integer index;// [Integer]: Numeric job index.

	}
	

	public static class VmsFull {
		public List<VmFull> vms;
	}
	
	public static class VmFull {
		public String agent_id;// [String]: Unique ID of the Agent associated with the VM.
		public String vm_cid;// [String]: Cloud ID of the VM.
		public String resource_pool;// [String]: Name of the resource pool used for the VM.
		public String disk_cid;// [String or null]: Cloud ID of the associated persistent disk if one is attached.
		public String job_name;// [String]: Name of the job.
		public Integer index;// [Integer]: Numeric job index.
		public Boolean resurrection_paused;// [Boolean]: Whether or not ressurector will try to bring back the VM is it goes missing.
		public String job_state;// [String]: Aggregate state of job. Possible values: running and other values that represent unhealthy state.
		public List<String> ips;// [Array of strings]: List of IPs.
		public List<String> dns;// [Array of strings]: List of DNS records.
		public Map<String,String>vitals;// [Hash]: VM vitals.
		public List<Map<String,String >> processes;// [Array of hashes]: List of processes running as part of the job.
	}

	
		
	
}
