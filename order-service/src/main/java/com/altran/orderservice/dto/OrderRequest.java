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
public class OrderRequest {
	
	private Order order;
    private Payment payment;

}
