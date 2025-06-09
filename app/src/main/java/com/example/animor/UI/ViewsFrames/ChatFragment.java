package com.example.animor.UI.ViewsFrames;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.dto.UserSimple;
import com.example.animor.Model.entity.Message;
import com.example.animor.R;
import com.example.animor.Utils.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatFragment extends Fragment {
    private static final String ARG_USER_SIMPLE = "user_simple";
    private static final String ARG_MY_USER_ID = "my_user_id";

    public static ChatFragment newInstance(UserSimple userSimple, long myUserId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_SIMPLE, userSimple);
        args.putLong(ARG_MY_USER_ID, myUserId);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private UserSimple userSimple;
    private long myUserId;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private StompClient stompClient;
    private Disposable stompSubscription;
    private String deviceToken = "你的设备token";
    private String userToken = "你的用户token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        // top bar
        userSimple = (UserSimple) getArguments().getSerializable(ARG_USER_SIMPLE);
        myUserId = getArguments().getLong(ARG_MY_USER_ID);

        ImageButton buttonBack = root.findViewById(R.id.button_back);
        ImageView imageUserPhoto = root.findViewById(R.id.image_user_photo);
        TextView textUserName = root.findViewById(R.id.text_user_name);

        textUserName.setText(userSimple.getUserName());
        // Load photo
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(userSimple.getUserPhoto());
                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(url.openStream());
                imageUserPhoto.post(() -> imageUserPhoto.setImageBitmap(bmp));
            } catch (Exception e) {
                // default photo
            }
        }).start();
        buttonBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // context
        recyclerView = root.findViewById(R.id.recycler_view_messages);
        editTextMessage = root.findViewById(R.id.edit_text_message);
        buttonSend = root.findViewById(R.id.button_send);

        messageAdapter = new MessageAdapter(messages, myUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        connectStomp();

        return root;
    }

    private void connectStomp() {
        String wsUrl = "ws://https://www.animor.es/ws-chat/websocket";
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Device-Token", deviceToken);
        headers.put("X-User-Token", userToken);

        stompClient.connect(headers);

        // 订阅私聊队列
        stompSubscription = stompClient.topic("/user/queue/chat")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    try {
                        JSONObject obj = new JSONObject(stompMessage.getPayload());
                        Message msg = new Message();
                        msg.setMessageId(obj.optLong("messageId"));
                        msg.setFromUserId(obj.optLong("fromUserId"));
                        msg.setToUserId(obj.optLong("toUserId"));
                        msg.setContent(obj.optString("content"));
                        msg.setTimestamp(obj.optLong("timestamp"));

                        if ((msg.getFromUserId() == userSimple.getUserId() && msg.getToUserId() == myUserId) ||
                                (msg.getFromUserId() == myUserId && msg.getToUserId() == userSimple.getUserId())) {
                            addMessageAndScroll(msg);
                        }
                    } catch (JSONException e) {
                        // TODO
                    }
                }, throwable -> {
                    // TODO
                });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;
        try {
            JSONObject json = new JSONObject();
            json.put("toUserId", userSimple.getUserId());
            json.put("content", content);
            json.put("type", "text");

            stompClient.send("/app/send-chat", json.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {

                        Message msg = new Message();
                        msg.setFromUserId(myUserId);
                        msg.setToUserId(userSimple.getUserId());
                        msg.setContent(content);
                        msg.setTimestamp(System.currentTimeMillis());
                        addMessageAndScroll(msg);
                        editTextMessage.setText("");
                    }, throwable -> {
                        // fail
                    });
        } catch (JSONException e) {
            // json fail
        }
    }

    private void addMessageAndScroll(Message msg) {
        messages.add(msg);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (stompSubscription != null) stompSubscription.dispose();
        if (stompClient != null) stompClient.disconnect();
    }
}

