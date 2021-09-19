package com.example.memorygame.pagerTabs;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.memorygame.R;
import com.example.memorygame.interfaces.ClickInterface;

public class CandylandTab extends Fragment {
    private ClickInterface clickInterface;
    private ViewGroup rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.candyland_tab,container,false);

        setInterface((ClickInterface) getActivity());

        Button forestBtn = rootView.findViewById(R.id.candyland_select);
        forestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInterface.buttonClicked(1);

            }
        });



        return rootView;
    }

    public void setInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isVisible() && !isHidden())
        {

            setMenuVisibility(true);
        }


    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            final ImageView swipeAnim = rootView.findViewById(R.id.swipe_anim);
            swipeAnim.setVisibility(View.VISIBLE);
            AnimationDrawable animationDrawable = (AnimationDrawable)swipeAnim.getDrawable();
            animationDrawable.start();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeAnim.setVisibility(View.GONE);
                }
            },1500);
        }
    }
}
