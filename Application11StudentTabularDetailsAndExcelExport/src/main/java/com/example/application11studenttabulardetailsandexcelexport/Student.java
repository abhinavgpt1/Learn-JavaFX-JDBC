package com.example.application11studenttabulardetailsandexcelexport;

import java.time.LocalDate;

public class Student {
    // Since table isn't editable, so I made the fields final.
    private final int rollNumber;
    private final String name;
    private final String percentage; // nullable column
    private final LocalDate dateOfAdmission; // not necessary to be java.sql.Date; better to have as LocalDate since DatePicker gets and set LocalDate.

    // Tip: make constructor, getters, setters etc. automatically (based on code editor used)
    // IntelliJ - Right click > Generate > Getters

    public Student(int rollNumber, String name, String percentage, LocalDate dateOfAdmission) {
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

    public String getPercentage() {
        return percentage;
    }

    public LocalDate getDateOfAdmission() {
        return dateOfAdmission;
    }
}
