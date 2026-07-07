package com.travelai.chat.repository;

import com.travelai.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAt(Long conversationId);

    void deleteByConversationId(Long conversationId);
}