package com.brubank.tcgtrader.service;

import com.brubank.tcgtrader.dto.UserRequest;
import com.brubank.tcgtrader.dto.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse findById(UUID id);
}
