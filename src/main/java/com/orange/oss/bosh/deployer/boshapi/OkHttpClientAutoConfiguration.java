package com.orange.oss.bosh.deployer.boshapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.oss.bosh.deployerfeigncfg.FeignConfiguration;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;



@Configuration
@ConditionalOnClass(com.squareup.okhttp.OkHttpClient.class)
public class OkHttpClientAutoConfiguration {
 
	private static org.slf4j.Logger logger=LoggerFactory.getLogger(FeignConfiguration.class.getName());
	
	
	@Value("${director.proxyHost:null}")
	private String proxyHost;
	
	@Value("${director.proxyPort:0}")	
	private int proxyPort;
	
 
    @Bean
    public OkHttpClient squareHttpClient() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllCerts() };

		SSLSocketFactory sslSocketFactory=null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			sslSocketFactory = (SSLSocketFactory) sc.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			new IllegalArgumentException(e);
		}

		logger.info("===> configuring OkHttp");
		com.squareup.okhttp.OkHttpClient ohc=new com.squareup.okhttp.OkHttpClient();
		ohc.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
		
		//Okhttp will follow http redirects (ie: for redirect with task)
		ohc.setFollowRedirects(true);
		ohc.setFollowSslRedirects(true);
		
		ohc.setHostnameVerifier(hostnameVerifier);
		ohc.setSslSocketFactory(sslSocketFactory);
		ohc.interceptors().add(LOGGING_INTERCEPTOR);
		ohc.interceptors().add(REWRITE_CONTENT_TYPE_INTERCEPTOR);
		
		if ((this.proxyHost!=null )&&(this.proxyHost.length()>0)){
		logger.info("Activatin proxy on host {} port {}",this.proxyHost,this.proxyPort);
		Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
		ohc.setProxy(proxy);
        ohc.setProxySelector(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
            return Arrays.asList(proxy);
            }
			@Override
			public void connectFailed(URI uri, SocketAddress socket, IOException e) {
				throw new IllegalArgumentException("connect to proxy failed",e);
			}
        });}
		
	 return ohc;
    }    	
    
    
	  public static class TrustAllCerts extends X509ExtendedTrustManager {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}

		   }
	  
	  
	  private static final Interceptor REWRITE_CONTENT_TYPE_INTERCEPTOR = new Interceptor() {
	    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
	      Response originalResponse = chain.proceed(chain.request());
	      return originalResponse.newBuilder()
	          .header("Content-Type", "application/json")
	          .build();
	    }
	  };
	  
	  
	  private static final Interceptor LOGGING_INTERCEPTOR= new Interceptor(){
	  		  @Override public Response intercept(Interceptor.Chain chain) throws IOException {
		    Request request = chain.request();

		    long t1 = System.nanoTime();
		    logger.info(String.format("Sending request %s on %s%n%s",
		        request.url(), chain.connection(), request.headers()));

		    Response response = chain.proceed(request);

		    long t2 = System.nanoTime();
		    logger.info(String.format("Received response for %s in %.1fms%n%s",
		        response.request().url(), (t2 - t1) / 1e6d, response.headers()));

		    return response;
		  }
		};
	  
    
}