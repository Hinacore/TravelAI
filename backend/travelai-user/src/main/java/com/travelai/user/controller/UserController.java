package com.travelai.user.controller;

import com.travelai.common.result.Result;
import com.travelai.user.dto.*;
import com.travelai.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Result<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(Result.success("注册成功", response));
    }

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(Result.success("登录成功", response));
    }

    @GetMapping("/info")
    public ResponseEntity<Result<UserResponse>> getUserInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(Result.success(response));
    }

    @PutMapping("/info")
    public ResponseEntity<Result<UserResponse>> updateUserInfo(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(Result.success("更新成功", response));
    }

    @PutMapping("/password")
    public ResponseEntity<Result<Void>> updatePassword(
            Authentication authentication,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updatePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(Result.success("密码修改成功", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Result<Void>> forgotPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        userService.forgotPassword(email, newPassword);
        return ResponseEntity.ok(Result.success("密码重置成功", null));
    }
}