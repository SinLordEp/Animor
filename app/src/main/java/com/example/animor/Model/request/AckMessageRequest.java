package com.example.animor.Model.request;

import java.util.List;

public class AckMessageRequest {
    List<Long> messageIdList;

    public List<Long> getMessageIdList() {
        return messageIdList;
    }

    public void setMessageIdList(List<Long> messageIdList) {
        this.messageIdList = messageIdList;
    }
}
