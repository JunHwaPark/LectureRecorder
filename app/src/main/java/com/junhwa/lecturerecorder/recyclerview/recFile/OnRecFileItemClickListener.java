package com.junhwa.lecturerecorder.recyclerview.recFile;

import android.view.View;

public interface OnRecFileItemClickListener {
    void onItemClick(RecFileAdapter.ViewHolder holder, View view, int position);
}
