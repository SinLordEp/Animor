package com.example.animor.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WsManager {
    private static WsManager instance;
    private StompClient stompClient;
    private boolean connected = false;

    private final List<StompMessageCallback> listeners = new ArrayList<>();
    private String wsUrl;
    private Map<String, String> headers;

    // 定义回调类型
    public interface StompMessageCallback {
        void onMessage(String payload);
    }

    private WsManager() {}

    public static synchronized WsManager getInstance() {
        if (instance == null) instance = new WsManager();
        return instance;
    }

    public void init(String wsUrl, Map<String, String> headers) {
        this.wsUrl = wsUrl;
        this.headers = headers;
    }

    public void connect() {
        if (stompClient != null && connected) return;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        // TODO
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("X-Device-Token", "token"));
        headers.add(new StompHeader("X-User-Token", "token"));
        stompClient.connect(headers);

        // subscribe message
        stompClient.topic("/user/queue/chat");
        connected = true;
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
            connected = false;
        }
    }

    // 页面注册监听
    public void addMessageListener(StompMessageCallback callback) {
        synchronized (listeners) {
            if (!listeners.contains(callback)) listeners.add(callback);
        }
    }

    public void removeMessageListener(StompMessageCallback callback) {
        synchronized (listeners) {
            listeners.remove(callback);
        }
    }

    public void sendMessage(String dest, String body) {
        if (stompClient != null && connected) {
            stompClient.send(dest, body);
        }
    }
}


