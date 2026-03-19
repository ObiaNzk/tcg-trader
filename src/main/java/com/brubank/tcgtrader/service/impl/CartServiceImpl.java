package com.brubank.tcgtrader.service.impl;

import com.brubank.tcgtrader.dto.CartItemRequest;
import com.brubank.tcgtrader.dto.CartResponse;
import com.brubank.tcgtrader.entity.Card;
import com.brubank.tcgtrader.entity.Cart;
import com.brubank.tcgtrader.entity.CartItem;
import com.brubank.tcgtrader.exception.BusinessException;
import com.brubank.tcgtrader.exception.ResourceNotFoundException;
import com.brubank.tcgtrader.repository.CardRepository;
import com.brubank.tcgtrader.repository.CartRepository;
import com.brubank.tcgtrader.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        return CartResponse.from(getOrFail(userId));
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID userId, CartItemRequest request) {
        Cart cart = getOrFail(userId);
        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + request.cardId()));

        if (card.getStock() < request.quantity()) {
            throw new BusinessException("Insufficient stock for card: " + card.getName());
        }

        // If already in cart, increase quantity
        cart.getItems().stream()
                .filter(i -> i.getCard().getId().equals(card.getId()))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.setQuantity(i.getQuantity() + request.quantity()),
                        () -> cart.getItems().add(CartItem.builder()
                                .cart(cart)
                                .card(card)
                                .quantity(request.quantity())
                                .unitPrice(card.getPrice())
                                .build())
                );

        return CartResponse.from(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID userId, UUID itemId, CartItemRequest request) {
        Cart cart = getOrFail(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (item.getCard().getStock() < request.quantity()) {
            throw new BusinessException("Insufficient stock for card: " + item.getCard().getName());
        }

        item.setQuantity(request.quantity());
        return CartResponse.from(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID userId, UUID itemId) {
        Cart cart = getOrFail(userId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return CartResponse.from(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrFail(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrFail(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }
}
