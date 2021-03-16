/**
 * 
 */
package com.altran.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alex
 *
 */

@Configuration
public class PaymentCloudConfig {

	@Bean
	public  RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}
