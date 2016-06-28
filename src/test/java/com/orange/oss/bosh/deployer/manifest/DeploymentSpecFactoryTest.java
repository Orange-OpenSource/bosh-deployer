package com.orange.oss.bosh.deployer.manifest;



import org.fest.assertions.Assertions;
import org.junit.Test;

public class DeploymentSpecFactoryTest {

	
	
	
	@Test
	public void testSpec() {
		DeploymentSpecFactory factory=new DeploymentSpecFactory();
		
		
		DeploymentSpec spec=factory.spec();
		
		Assertions.assertThat(spec).isNotNull();
		
	}

}
