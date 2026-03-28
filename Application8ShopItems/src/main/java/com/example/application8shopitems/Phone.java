package com.example.application8shopitems;

import java.util.Arrays;
import java.util.List;

public class Phone extends Item {
    public Phone(String model, double price) {
        super(model, price);
    }
    public static List<Item> getPhoneList(){
        return Arrays.asList(
            new Phone("iPhone 15 Pro Max", 1200),
            new Phone("iPhone 15", 800),
            new Phone("iPhone 14 Plus", 500),
            new Phone("Galaxy S23 FE", 700),
            new Phone("Galaxy A54 5G", 400),
            new Phone("Galaxy A34 5G", 300)
        );
    }
}
