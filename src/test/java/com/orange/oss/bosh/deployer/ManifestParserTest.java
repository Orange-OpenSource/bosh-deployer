package com.orange.oss.bosh.deployer;

import java.io.IOException;
import java.nio.file.Files;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ManifestParser.class})

public class ManifestParserTest {

	private static Logger logger=LoggerFactory.getLogger(ManifestParserTest.class.getName());
	
	
	@Value("classpath:/manifests/hazelcast.yml")
    private Resource manifestResource;
	
	
	@Test
	public void testParser() throws IOException {
		ManifestParser parser=new ManifestParser();
		ManifestMapping.Manifest m=parser.parser(new String(Files.readAllBytes(manifestResource
				.getFile()
				.toPath())));
		
		logger.info("parsed manifest"+m );
		Assertions.assertThat(true);

		
		
		
	}

}
