package com.tdd.ecommerce.cart.presentation.dto;

import com.tdd.ecommerce.cart.application.dto.CartRequest;

import java.util.List;

public record CartRequestDto(
   Long customerId,
   List<CartRequest> products
) {}
