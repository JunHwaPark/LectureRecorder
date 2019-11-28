package com.junhwa.lecturerecorder.recyclerview;

import android.view.View;

public interface OnRecFileItemClickListener {
    public void onItemClick(RecFileAdapter.ViewHolder holder, View view, int position);
}
