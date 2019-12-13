package com.junhwa.lecturerecorder.recyclerview.lecture_list;

public class Lecture {
    private String lecture;
    private String professor;

    public Lecture(String lecture, String professor) {
        this.lecture = lecture;
        this.professor = professor;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }
}
