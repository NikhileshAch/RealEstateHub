package ch.unil.doplab;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    public enum MessageDirection { SENT, RECEIVED }

    private final UUID messageId;
    private final UUID senderId;
    private final UUID recipientId;
    private final String subject;
    private final String content;
    private final LocalDateTime sentAt;
    private final MessageDirection direction;
    private boolean read;

    private Message(UUID senderId, UUID recipientId, String subject, String content, MessageDirection direction) {
        this.messageId = UUID.randomUUID();
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.subject = subject;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.direction = direction;
        this.read = direction == MessageDirection.SENT;
    }

    public Message(Message other) {
        this.messageId = other.messageId;
        this.senderId = other.senderId;
        this.recipientId = other.recipientId;
        this.subject = other.subject;
        this.content = other.content;
        this.sentAt = other.sentAt;
        this.direction = other.direction;
        this.read = other.read;
    }

    public static Message outbound(UUID senderId, UUID recipientId, String subject, String content) {
        return new Message(senderId, recipientId, subject, content, MessageDirection.SENT);
    }

    public static Message inbound(UUID senderId, UUID recipientId, String subject, String content) {
        return new Message(senderId, recipientId, subject, content, MessageDirection.RECEIVED);
    }

    public UUID getMessageId() { return messageId; }
    public UUID getSenderId() { return senderId; }
    public UUID getRecipientId() { return recipientId; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public MessageDirection getDirection() { return direction; }
    public boolean isRead() { return read; }
    public void markAsRead() { this.read = true; }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", subject='" + subject + '\'' +
                ", sentAt=" + sentAt +
                ", direction=" + direction +
                ", read=" + read +
                '}';
    }
}
