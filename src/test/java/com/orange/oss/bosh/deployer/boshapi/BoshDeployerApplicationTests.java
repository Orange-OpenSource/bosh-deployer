package com.orange.oss.bosh.deployer.boshapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.orange.oss.bosh.deployer.BoshDeployerApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BoshDeployerApplication.class)
@WebAppConfiguration
@EnableFeignClients
public class BoshDeployerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
