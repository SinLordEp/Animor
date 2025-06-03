package com.example.animor.Model;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private long messageId;         // PRIMARY KEY
    private long fromUserId;       // ID del usuario remitente
    private long toUserId;         // ID del usuario destinatario
    private String content;        // Contenido del mensaje
    private long timestamp;        // Marca de tiempo del mensaje

    // Constructor vacío
    public Message() {
    }

    // Constructor con todos los campos
    public Message(long messageId, long fromUserId, long toUserId, String content, long timestamp) {
        this.messageId = messageId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Método toString para representación de cadena
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    // Métodos equals y hashCode para comparación de objetos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId == message.messageId &&
                fromUserId == message.fromUserId &&
                toUserId == message.toUserId &&
                timestamp == message.timestamp &&
                Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, fromUserId, toUserId, content, timestamp);
    }
}
