package com.travelai.chat.controller;

import com.travelai.chat.dto.ConversationResponse;
import com.travelai.chat.dto.MessageResponse;
import com.travelai.chat.dto.SendMessageRequest;
import com.travelai.chat.service.ChatService;
import com.travelai.common.result.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conversations")
    public ResponseEntity<Result<ConversationResponse>> createConversation() {
        Long userId = 0L;
        ConversationResponse response = chatService.createConversation(userId);
        return ResponseEntity.ok(Result.success("对话创建成功", response));
    }

    @GetMapping("/conversations")
    public ResponseEntity<Result<List<ConversationResponse>>> getConversations() {
        Long userId = 0L;
        List<ConversationResponse> response = chatService.getConversationsByUser(userId);
        return ResponseEntity.ok(Result.success(response));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Result<ConversationResponse>> getConversation(
            @PathVariable Long id) {
        Long userId = 0L;
        ConversationResponse response = chatService.getConversation(id, userId);
        return ResponseEntity.ok(Result.success(response));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<Result<MessageResponse>> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        Long userId = 0L;
        request.setConversationId(id);
        MessageResponse response = chatService.sendMessage(userId, request);
        return ResponseEntity.ok(Result.success(response));
    }

    @PostMapping("/messages")
    public ResponseEntity<Result<MessageResponse>> sendMessageAuto(
            @Valid @RequestBody SendMessageRequest request) {
        Long userId = 0L;
        MessageResponse response = chatService.sendMessage(userId, request);
        return ResponseEntity.ok(Result.success(response));
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Result<Void>> deleteConversation(
            @PathVariable Long id) {
        Long userId = 0L;
        chatService.deleteConversation(id, userId);
        return ResponseEntity.ok(Result.success("对话删除成功", null));
    }
}