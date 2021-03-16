/**
 * 
 */
package com.altran.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.altran.orderservice.dto.Order;
import com.altran.orderservice.dto.OrderRequest;
import com.altran.orderservice.dto.OrderResponse;
import com.altran.orderservice.dto.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Alex
 *
 */

@Service
public class OrderService {
	
	
	@Autowired
    private RestTemplate restTemplate; 
	
	@Value("${ms.payment-service.endpoint.uri}")
    private String ENDPOINT_PAYMENT;
	
	Logger logger= LoggerFactory.getLogger(OrderService.class);
	
	
	public OrderResponse creaOrdine(OrderRequest request) throws JsonProcessingException {
        //String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setImporto(order.getPrezzo());
        //rest call per pagamento
        logger.info("Order-Service Request : "+new ObjectMapper().writeValueAsString(request));
        Payment paymentResponse = restTemplate.postForObject(ENDPOINT_PAYMENT, payment, Payment.class);

        String response = paymentResponse.getStatoPagamento().equals("OK") ? "pagamento elaborato con successo e ordine creato" : "pagamento in errore , ordine non elaborato";
        logger.info("Order Service Response from Payment-Service : "+new ObjectMapper().writeValueAsString(response));
        return new OrderResponse(order, paymentResponse.getImporto(), paymentResponse.getIdTransazione(), response);
    }

}
