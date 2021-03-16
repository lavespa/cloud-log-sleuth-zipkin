/**
 * 
 */
package com.altran.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	
	private Order order;
    private double importo;
    private String transactionId;
    private String message;

}
