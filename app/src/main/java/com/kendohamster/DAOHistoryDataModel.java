package com.kendohamster;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOHistoryDataModel {
    private DatabaseReference databaseReference;

    public DAOHistoryDataModel(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(HistoryDataModel.class.getSimpleName());
    }

    public Task<Void> add(HistoryDataModel rdm){
        return databaseReference.push().setValue(rdm);
    }
}
