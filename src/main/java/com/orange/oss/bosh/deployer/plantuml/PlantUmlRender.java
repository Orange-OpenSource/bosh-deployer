package com.orange.oss.bosh.deployer.plantuml;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.orange.oss.bosh.deployer.ApiMappings;
import com.orange.oss.bosh.deployer.ApiMappings.Deployment;
import com.orange.oss.bosh.deployer.manifest.ManifestMapping;

@Configuration
public class PlantUmlRender {
	private static Logger logger = LoggerFactory.getLogger(PlantUmlRender.class.getName());

	/**
	 * Render an plantuml markup for a given deployment see
	 * http://plantuml.com/deployment.html
	 * 
	 * @param deployment
	 * @return
	 */
	public String renderUml(ApiMappings.Deployment deployment, ManifestMapping.Manifest manifest) {

		PlantUmlDiagram uml=new PlantUmlDiagram();
		
		//scan releases
		deployment.releases.stream().forEach(r -> {
			logger.info("deployment {} release {} ", deployment.name, r.name);
			uml.addArtifact(r.name,"releases");
		});

		deployment.stemcells.stream().forEach(s -> {
			logger.info("deployment {} stemcell {} ", deployment.name, s.name);
			uml.addArtifact(s.name,"stemcell");
		});

		manifest.instance_groups.forEach(ig -> {
			logger.info("deployment {} instance_group {} ", deployment.name, ig.name);
			uml.addNode(ig.name);
			
			ig.jobs.stream().forEach(job -> {
				uml.addNode(job.name);
				uml.addLink(job.name,ig.name);
			});
		});
		
		
		
		

		

		String plantUml = uml.toString();
		logger.info("generated plantuml diag :\n{}", plantUml);

		return plantUml;
	}

	/**
	 * private class to map plantuml artifact
	 * 
	 * @author pierre
	 *
	 */
	private class PlantUmlDiagram {
		
		private class Link{
			public Link(String from, String to) {
				this.from=from;
				this.to=to;
			}
			public String from;
			public String to;
		}
		
		private String umlMarkup="";
		private List<Link> links=new ArrayList<Link>(); 
		
		public PlantUmlDiagram(){
		}
		
		public void addNode(String nodeName) {
			umlMarkup+="node \""+nodeName+"\"\n";
			
		}

		public void addArtifact(String artifactName,String stereotype){
			umlMarkup+="artifact \""+artifactName+"\" ";
			umlMarkup+="<<"+stereotype+">>\n";
		}
		
		
		public void addLink(String from, String to){
			Link l=new Link(from,to);
			this.links.add(l);
		}
		
		
		public String toString(){
			
			String rendered=new String("@startUml\n");
			rendered=rendered+umlMarkup;
			for (Link l : this.links){
				rendered+="\""+l.from+"\""+" -- "+"\""+l.to+"\""+"\n";
			}
			return rendered+"@enduml\n";
		}

	}

}
