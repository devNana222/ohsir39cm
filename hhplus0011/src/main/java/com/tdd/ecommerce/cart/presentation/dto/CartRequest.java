package com.tdd.ecommerce.cart.presentation.dto;

import java.util.List;

public record CartRequest(
   Long customerId,
   List<com.tdd.ecommerce.cart.application.dto.CartRequest> products
) {}
