package com.example.ontime.ui;

public class Item {
    private String name;
    public Item(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public String getNameInitials(){
        StringBuilder intials = new StringBuilder();
        intials.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1; i < name.length() - 1; i++)
            if (name.charAt(i) == ' ')
                intials.append(Character.toUpperCase(name.charAt(i + 1)));
        return intials.toString();
    }
}
