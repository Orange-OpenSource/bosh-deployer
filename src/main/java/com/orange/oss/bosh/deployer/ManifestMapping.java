package com.orange.oss.bosh.deployer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManifestMapping {

	public static class Manifest {
		public String director_uuid;
		public String name;
		public List<Release> releases;
		public Update update;
		public List<Stemcell> stemcells;
		public List<InstanceGroup> instance_groups;
		
		public Map<String,Object> properties=new HashMap<String,Object>(); // yaml structure		

	}

	public static class Release {
		public String name;
		public String version;
	}

	public static class Update {
		public int canaries;
		public String canary_watch_time; // 30000-240000
		public String update_watch_time; // 30000-240000
		
		public int max_in_flight; // 1 #<-- important to limit max in flight,
									// for consul update, and for hazelcast
									// cluster smooth mem migration
		public boolean serial;
	}

	public static class Stemcell {
		public String alias; // trusty
		public String os; // ubuntu-trusty
		public String version; // latest
	}

	public static class InstanceGroup {
		public String name;
		public int instances; // 1
		
		public String vm_type; // default
		public String stemcell; // trusty
		
		public String lifecycle; //errand
		public String persistent_disk_type; //name in cloudconfig
		public List<String> azs;// [z1]
		public List<Network> networks;
		public List<Job> jobs;
		public Map<String,Object> properties=new HashMap<String,Object>();; // deprecated in favor of job level properties
	}

	public static class Network {
		public String name; // networks: [{name: net-hazelcast}]
	}

	public static class Job {
		public String name;
		public String release;
		
		public Map<String,Object> consumes=new HashMap<String, Object>(); //FIXME: correctly parse link
		public Map<String,Object> provides=new HashMap<String, Object>(); ////FIXME: correctly parse link
		public Map<String,Object> properties=new HashMap<String,Object>();// job level properties
		
		
//		public List<Link> consumes; //FIXME: correctly parse link
//		public List<Link> provides; ////FIXME: correctly parse link
	}

	public static class Link {
		public String name;
		public Map<String, String> attributes;
	}

}
