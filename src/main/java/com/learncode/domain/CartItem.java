package com.learncode.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	private int ProductId;
	private String name;
	private int quantity;
	private double unitPrice;
}
