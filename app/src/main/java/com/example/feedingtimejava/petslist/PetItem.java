package com.example.feedingtimejava.petslist;

import com.google.firebase.Timestamp;

public class PetItem {
    public String textValue;
    public Timestamp last_fed;

    PetItem(String id, Timestamp mlast_fed) {
        textValue = id;
        last_fed = mlast_fed;
    }
}
