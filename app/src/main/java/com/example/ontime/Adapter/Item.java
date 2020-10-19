package com.example.ontime.Adapter;

public class Item {
    private String itemName;
    private String subjectName;
    private boolean isInBag;

    public Item(String itemName, String subjectName, boolean isInBag) {
        this.subjectName = subjectName;
        this.itemName = itemName;
        this.isInBag = isInBag;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getNameInitialsOfSubject() {
        StringBuilder initials = new StringBuilder();
        initials.append(Character.toUpperCase(getSubjectName().charAt(0)));
        for (int i = 1; i < getSubjectName().length() - 1; i++) {
            if (getSubjectName().charAt(i) == ' ') {
                initials.append(Character.toUpperCase(getSubjectName().charAt(i + 1)));
            }
            if (initials.length() == 2) {
                return initials.toString();
            }

        }
        return initials.toString();
    }

    public boolean isInBag() {
        return isInBag;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
