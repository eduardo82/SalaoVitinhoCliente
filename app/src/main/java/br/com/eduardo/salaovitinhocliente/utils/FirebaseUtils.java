package br.com.eduardo.salaovitinhocliente.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by eduardo.vasconcelos on 30/10/2017.
 */

public class FirebaseUtils {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private FirebaseUtils() {

    }

    public static DatabaseReference getReferenceChild(String... children) {
        DatabaseReference reference = database.getReference();
        for (String child : children) {
            reference = reference.getRef().child(child);
        }
        return reference;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }
}
