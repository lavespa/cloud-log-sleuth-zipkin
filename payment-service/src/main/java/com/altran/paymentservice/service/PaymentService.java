/**
 * 
 */
package com.altran.paymentservice.service;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.altran.paymentservice.dto.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Alex
 *
 */

@Service
public class PaymentService {
	
	Logger logger= LoggerFactory.getLogger(PaymentService.class);
	
	
	public Payment effettuaPagamento(Payment pay) throws JsonProcessingException {
		
		pay.setStatoPagamento(simulaPagamento());
		pay.setIdTransazione(UUID.randomUUID().toString());
		logger.info("payment-Service effettuaPagamento : {}",new ObjectMapper().writeValueAsString(pay));
		
		return pay;
		
		
	}
	
	private String  simulaPagamento() {
		return new Random().nextBoolean() ? "OK" : "KO";
	}
	

}
