/**
 * 
 */
package com.altran.paymentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.altran.paymentservice.dto.Payment;
import com.altran.paymentservice.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Alex
 *
 */

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	@Autowired
	private PaymentService payService;
	
	@PostMapping("/effettuaPagamento")
	public Payment effettuaPagamento(@RequestBody Payment pay) throws JsonProcessingException
	{
		return payService.effettuaPagamento(pay);
	}

}
