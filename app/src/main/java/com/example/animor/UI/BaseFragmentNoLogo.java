package com.example.animor.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.animor.R;

public abstract class BaseFragmentNoLogo extends Fragment {

    protected abstract int getContentLayoutId();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.fragment_basenologo, container, false);

        FrameLayout containerLayout = baseView.findViewById(R.id.fragmentContainer);
        View contentView = inflater.inflate(getContentLayoutId(), containerLayout, false);
        containerLayout.addView(contentView);

        return baseView;
    }
}

