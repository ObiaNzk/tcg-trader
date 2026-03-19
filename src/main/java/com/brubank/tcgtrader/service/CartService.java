package com.brubank.tcgtrader.service;

import com.brubank.tcgtrader.dto.CartItemRequest;
import com.brubank.tcgtrader.dto.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse getCart(UUID userId);
    CartResponse addItem(UUID userId, CartItemRequest request);
    CartResponse updateItem(UUID userId, UUID itemId, CartItemRequest request);
    CartResponse removeItem(UUID userId, UUID itemId);
    void clearCart(UUID userId);
}
