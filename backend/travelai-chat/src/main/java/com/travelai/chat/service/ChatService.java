package com.travelai.chat.service;

import com.travelai.chat.client.AIChatClient;
import com.travelai.chat.dto.ConversationResponse;
import com.travelai.chat.dto.MessageResponse;
import com.travelai.chat.dto.SendMessageRequest;
import com.travelai.chat.entity.Conversation;
import com.travelai.chat.entity.Message;
import com.travelai.chat.repository.ConversationRepository;
import com.travelai.chat.repository.MessageRepository;
import com.travelai.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AIChatClient aiChatClient;

    @Transactional
    public ConversationResponse createConversation(Long userId) {
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .title("新对话")
                .status(1)
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);
        log.info("对话创建成功，用户ID: {}", userId);
        return ConversationResponse.fromEntity(savedConversation);
    }

    public List<ConversationResponse> getConversationsByUser(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return conversations.stream()
                .map(ConversationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ConversationResponse getConversation(Long id, Long userId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "对话不存在"));

        if (!conversation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此对话");
        }

        ConversationResponse response = ConversationResponse.fromEntity(conversation);
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAt(id);
        response.setMessages(messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList()));

        return response;
    }

    @Transactional
    public MessageResponse sendMessage(Long userId, SendMessageRequest request) {
        Long conversationId = request.getConversationId();

        if (conversationId == null) {
            ConversationResponse conversation = createConversation(userId);
            conversationId = conversation.getId();
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(404, "对话不存在"));

        if (!conversation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此对话");
        }

        Message userMessage = Message.builder()
                .conversationId(conversationId)
                .sender("USER")
                .content(request.getContent())
                .build();
        messageRepository.save(userMessage);

        String context = buildContext(conversationId);
        String aiResponse = aiChatClient.chatWithAI(request.getContent(), context);

        Message aiMessage = Message.builder()
                .conversationId(conversationId)
                .sender("AI")
                .content(aiResponse)
                .build();
        Message savedAiMessage = messageRepository.save(aiMessage);

        conversation.setContext(updateContext(context, request.getContent(), aiResponse));
        if (conversation.getTitle().equals("新对话")) {
            conversation.setTitle(extractTitle(request.getContent()));
        }
        conversationRepository.save(conversation);

        log.info("消息发送成功，对话ID: {}", conversationId);
        return MessageResponse.fromEntity(savedAiMessage);
    }

    @Transactional
    public void deleteConversation(Long id, Long userId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "对话不存在"));

        if (!conversation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此对话");
        }

        messageRepository.deleteByConversationId(id);
        conversationRepository.delete(conversation);

        log.info("对话删除成功，对话ID: {}", id);
    }

    private String buildContext(Long conversationId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAt(conversationId);
        StringBuilder context = new StringBuilder();

        for (Message message : messages) {
            context.append(message.getSender()).append(": ").append(message.getContent()).append("\n");
        }

        return context.toString();
    }

    private String updateContext(String existingContext, String userMessage, String aiResponse) {
        StringBuilder context = new StringBuilder(existingContext);
        context.append("USER: ").append(userMessage).append("\n");
        context.append("AI: ").append(aiResponse).append("\n");

        if (context.length() > 4000) {
            int startIndex = context.length() - 4000;
            return context.substring(startIndex);
        }

        return context.toString();
    }

    private String extractTitle(String content) {
        if (content.length() <= 20) {
            return content;
        }
        return content.substring(0, 20) + "...";
    }
}