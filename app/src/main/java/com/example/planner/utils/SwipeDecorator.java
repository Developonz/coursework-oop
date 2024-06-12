package com.example.planner.utils;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeDecorator {
    public static void createDecorator(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, String text, int color) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), color))
                .setSwipeLeftLabelColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white))
                .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16)
                .setSwipeLeftLabelTypeface(Typeface.DEFAULT_BOLD)
                .addSwipeLeftLabel(text)
                .create()
                .decorate();
    }
}
