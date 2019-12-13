package com.junhwa.lecturerecorder.recyclerview.lecture_list;

import android.view.View;

public interface OnLectureItemClickListener {
    void onItemClick(LectureAdapter.ViewHolder holder, View view, int position);
}
