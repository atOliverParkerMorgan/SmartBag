package com.olivermorgan.ontime.main.Adapter;

public class Subject {
    private final boolean[] week;
    private final String name;
    private final String shortName;



    public Subject(String name, String shortName) {
        this.week = new boolean[7];
        this.name = name;
        this.shortName = shortName;
    }

    public boolean[] getWeek() {
        return week;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setDay(int index){
        week[index] = true;
    }
}
