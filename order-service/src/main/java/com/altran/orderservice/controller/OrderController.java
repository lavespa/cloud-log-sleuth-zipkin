/**
 * 
 */
package com.altran.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.altran.orderservice.dto.OrderRequest;
import com.altran.orderservice.dto.OrderResponse;
import com.altran.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Alex
 *
 */

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
    private OrderService service;
	
	@PostMapping("/creaOrdine")
	public OrderResponse createOrder(@RequestBody OrderRequest request) throws JsonProcessingException {
		return service.creaOrdine(request);
	}

}
