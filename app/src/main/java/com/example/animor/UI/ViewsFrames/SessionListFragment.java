package com.example.animor.UI.ViewsFrames;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.dto.UserSimple;
import com.example.animor.R;

import java.util.ArrayList;
import java.util.List;

public class SessionListFragment extends Fragment {
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private List<SessionItem> sessionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_message_list, container, false);
        recyclerView = root.findViewById(R.id.recycler_view_sessions);
        adapter = new SessionAdapter(sessionList, userSimple -> {
            // 切换到聊天面板
            ChatFragment chatFragment = ChatFragment.newInstance(userSimple);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }

    public static class SessionItem {
        private final UserSimple userSimple;
        private final String lastMessage;
        public SessionItem(UserSimple userSimple, String lastMessage) {
            this.userSimple = userSimple;
            this.lastMessage = lastMessage;
        }
        public UserSimple getUserSimple() { return userSimple; }
        public String getLastMessage() { return lastMessage; }
    }

    // Adapter
    private static class SessionAdapter extends RecyclerView.Adapter<SessionViewHolder> {
        private final List<SessionItem> list;
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onClick(UserSimple userSimple);
        }

        SessionAdapter(List<SessionItem> list, OnItemClickListener listener) {
            this.list = list; this.listener = listener;
        }

        @NonNull
        @Override
        public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session, parent, false);
            return new SessionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
            SessionItem item = list.get(position);
            holder.bind(item, listener);
        }

        @Override
        public int getItemCount() { return list.size(); }
    }

    // ViewHolder
    private static class SessionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUserPhoto;
        TextView textUserName, textLastMessage;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserPhoto = itemView.findViewById(R.id.image_user_photo);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textLastMessage = itemView.findViewById(R.id.text_last_message);
        }

        void bind(SessionItem item, SessionAdapter.OnItemClickListener listener) {
            textUserName.setText(item.getUserSimple().getUserName());
            textLastMessage.setText(item.getLastMessage());
            // load photo
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL(item.getUserSimple().getUserPhoto());
                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(url.openStream());
                    imageUserPhoto.post(() -> imageUserPhoto.setImageBitmap(bmp));
                } catch (Exception e) {
                    // default photo
                }
            }).start();
            itemView.setOnClickListener(v -> listener.onClick(item.getUserSimple()));
        }
    }
}

