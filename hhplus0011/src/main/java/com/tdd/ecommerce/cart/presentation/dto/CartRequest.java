package com.tdd.ecommerce.cart.presentation.dto;

import com.tdd.ecommerce.cart.application.dto.CartInfo;

import java.util.List;

public record CartRequest(
   Long customerId,
   List<CartInfo> products
) {}
