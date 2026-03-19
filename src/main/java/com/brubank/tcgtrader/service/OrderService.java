package com.brubank.tcgtrader.service;

import com.brubank.tcgtrader.dto.CheckoutRequest;
import com.brubank.tcgtrader.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(UUID userId, CheckoutRequest request);
    OrderResponse findById(UUID id);
    List<OrderResponse> findByUser(UUID userId);
}
