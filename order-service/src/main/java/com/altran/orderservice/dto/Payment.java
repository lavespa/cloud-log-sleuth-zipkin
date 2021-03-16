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
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
	
	private int idPagamento;
	private String statoPagamento;
	private int orderId;
	private double importo;
	private String idTransazione;

}
