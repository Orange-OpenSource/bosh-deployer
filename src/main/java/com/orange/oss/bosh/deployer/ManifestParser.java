package com.orange.oss.bosh.deployer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Configuration
public class ManifestParser {

	private static Logger logger=LoggerFactory.getLogger(ManifestParser.class.getName());
	
	public ManifestMapping.Manifest parser(String yamlManifest){
		
		Yaml yaml = new Yaml();
		Object map=yaml.load(yamlManifest);
		
		
		ObjectMapper mapper=new ObjectMapper(new YAMLFactory());
		ManifestMapping.Manifest m=null;
		try {
			m = mapper.readValue(yamlManifest,ManifestMapping.Manifest.class);
		} catch (IOException e) {
			logger.error("failure parsing yaml: {}",e);
			throw new IllegalArgumentException(e);
		}
		return m;
		
		
	}
	
}
