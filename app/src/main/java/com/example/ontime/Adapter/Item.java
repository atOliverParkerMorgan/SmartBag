package com.example.ontime.Adapter;

import android.widget.Toast;

public class Item {
    private String itemName;
    private String subjectName;
    public Item(String itemName, String subjectName) {
        this.subjectName = subjectName;
        this.itemName = itemName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getNameInitialsOfSubject() {
        StringBuilder intials = new StringBuilder();
        intials.append(Character.toUpperCase(getSubjectName().charAt(0)));
        for (int i = 1; i < getSubjectName().length() - 1; i++){
            if (getSubjectName().charAt(i) == ' ') {
                intials.append(Character.toUpperCase(getSubjectName().charAt(i + 1)));
            }
            if(intials.length()==2){
                return intials.toString();
            }

        }
        return intials.toString();
    }
}
