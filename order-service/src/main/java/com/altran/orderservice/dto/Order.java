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
public class Order {
	
	private int id;
    private String nome;
    private int quantita;
    private double prezzo;
	

}
