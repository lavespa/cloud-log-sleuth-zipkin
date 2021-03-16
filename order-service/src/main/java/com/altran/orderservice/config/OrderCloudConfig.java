/**
 * 
 */
package com.altran.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alex
 *
 */

@Configuration
public class OrderCloudConfig {
	
	
	@Bean
	public  RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
