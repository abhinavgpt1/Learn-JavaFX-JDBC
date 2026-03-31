package com.example.application11studenttabulardetailsandexcelexport;

public class Student {
    // since table isn't editable, so made the fields final.
    private final int rollNumber;
    private final String name;
    private final float percentage;
    private final String dateOfAdmission; // not necessary to be java.sql.Date

    // Tip: make constructor, getters, setters etc. automatically (based on code editor used)
    // IntelliJ - Right click > Generate > Getters

    public Student(int rollNumber, String name, float percentage, String dateOfAdmission) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.percentage = percentage;
        this.dateOfAdmission = dateOfAdmission;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getName() {
        return name;
    }

    public float getPercentage() {
        return percentage;
    }

    public String getDateOfAdmission() {
        return dateOfAdmission;
    }
}
