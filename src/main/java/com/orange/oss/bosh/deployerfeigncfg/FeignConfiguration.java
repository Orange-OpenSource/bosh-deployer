package com.orange.oss.bosh.deployerfeigncfg;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;



@Configuration
public class FeignConfiguration {

	private static org.slf4j.Logger logger=LoggerFactory.getLogger(FeignConfiguration.class.getName());
	
	
	
	
	
	@Value("${director.proxyHost}")
	private String proxyHost;
	
	@Value("${director.proxyPort}")	
	private int proxyPort;
	
	
	@Value("${director.user}")
	String directorUser;

	@Value("${director.password}")
	String directorPassword;

	
	@Autowired
	@Qualifier("squareHttpClient")
	com.squareup.okhttp.OkHttpClient  customOkHttpClient;

	

	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor(directorUser, directorPassword);
	}
	

	@Bean
	Logger.Level customFeignLoggerLevel() {
		return Logger.Level.FULL;
	}
	@Bean
	Logger customFeignLogger(){
		return new Slf4jLogger();
	}

	@Bean
	Feign.Builder customFeignBuilder(){
		return Feign.builder().client(new OkHttpClient(customOkHttpClient));
	}

}
