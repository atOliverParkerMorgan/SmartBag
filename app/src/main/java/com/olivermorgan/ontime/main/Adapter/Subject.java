package com.olivermorgan.ontime.main.Adapter;

public class Subject {
    private boolean[] week;
    private String name;

    public Subject(String name) {
        this.week = new boolean[7];
        this.name = name;
    }

    public boolean[] getWeek() {
        return week;
    }

    public String getName() {
        return name;
    }
    public void setDay(int index){
        week[index] = true;
    }
}
