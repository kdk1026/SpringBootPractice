package common.spring.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	private RestTemplateUtil() {
		super();
	}
	
	private static RestTemplate restTemplate;
	private static final int TIMEOUT = 5000;
	
	private static RestTemplate getRestTemplate(boolean isSSL) {
		restTemplate = null;
		
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(TIMEOUT)
				.setSocketTimeout(TIMEOUT)
				.build();
		
		CloseableHttpClient httpClient = null;
		if (isSSL) {
			// HttpClient version 4.4 Standard
			httpClient = HttpClients.custom()
					.setDefaultRequestConfig(config)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		} else {
			httpClient = HttpClientBuilder.create()
					.setDefaultRequestConfig(config)
					.build();
		}
		
		HttpComponentsClientHttpRequestFactory requestFactory
			= new HttpComponentsClientHttpRequestFactory(httpClient);
		
		return new RestTemplate(requestFactory);
	}

	public static ResponseEntity<String> getString(boolean isSSL, String url, Map<String, Object> header) {
		HttpHeaders requestHeaders = new HttpHeaders();
		
		if (header != null) {
	        Iterator<String> it = header.keySet().iterator();
	        String key = "";
	        
	        while(it.hasNext()) {
	        	 key = it.next();
	        	 requestHeaders.set(key, String.valueOf(header.get(key)));
	        }
		}
		
		ResponseEntity<String> result = null;
		
		try {
			restTemplate = getRestTemplate(isSSL);
			HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);
			
			result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return result;
	}
	
	public static ResponseEntity<String> postString(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param) {
		HttpHeaders requestHeaders = new HttpHeaders();
		
		if (header != null) {
			Iterator<String> it = header.keySet().iterator();
			String key = "";
			
			while(it.hasNext()) {
				key = it.next();
				requestHeaders.set(key, String.valueOf(header.get(key)));
			}
		}
		
		
		ResponseEntity<String> result = null;
		
		try {
			restTemplate = getRestTemplate(isSSL);
			HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);
			
			result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return result;
	}
	
}
