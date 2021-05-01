package com.olivermorgan.ontime.main.Adapter;

import android.content.Context;

import com.olivermorgan.ontime.main.SharedPrefs;

public class Item {
    private String itemName;
    private String subjectName;
    private String type;
    private final boolean isInBag;
    private final Context context;
    private MainTitle mainTitle;

    public Item(String itemName, String subjectName, boolean isInBag, String type, Context context) {
        this.subjectName = subjectName;
        this.itemName = itemName;
        this.isInBag = isInBag;
        this.context = context;
        this.type = type;
    }

    public void setMainTitle(MainTitle mainTitle){
       this.mainTitle = mainTitle;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getItemName() {
        boolean language = SharedPrefs.getBoolean(context, "Language");
        if (itemName.contains("items for ") && language) {
            itemName = itemName.replace("items for ", "pomůcky pro ");
        } else if (itemName.contains("pomůcky pro ") && !language){
            itemName = itemName.replace("pomůcky pro ","items for ");
        }
        return itemName;
    }

    public String getType() {
        return type;
    }

    public String getNameInitialsOfSubject() {
        if(mainTitle !=null) return mainTitle.getShortName();

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
        // specialSchoolCases
        switch (getSubjectName()){

            case "Biologie":
                return "Bi";

            case "Fyzika":
                return "Fy";

            case "Algoritmy":
                return "Alg";

            case "Programování":
                return "Pg";

            case "Chemie":
                return "Ch";

            case "Konverzace ve francouzském jazyce":
                return "FJK";
            case "Konverzace ve španělském jazyce":
                return "ŠJK";
            case "Konverzace ve německém jazyce":
                return "NJK";
            case "Konverzace ve ruském jazyce":
                return "RJK";
            case "Konverzace ve italském jazyce":
                return "IJK";

            case "Základy společenských věd":
                return "ZSV";
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
