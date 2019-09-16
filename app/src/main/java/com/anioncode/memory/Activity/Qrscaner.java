package com.anioncode.memory.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anioncode.memory.Models.Friend;
import com.anioncode.memory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class Qrscaner extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("USERS").document(FirebaseAuth.getInstance().getUid());
    private CollectionReference noteRef = db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("FRIENDS");
    ArrayList<Friend> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscaner);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Zeskanuj QR znajomego");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                addFriend( result.getContents());
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void addFriend(String result_qr){
        if (!result_qr.trim().equals(FirebaseAuth.getInstance().getUid())) {
            if (!result_qr.trim().equals("")) {


                boolean check = false;
                for (Friend friend : friends) {

                    if (friend.getFriend_id().equals(result_qr.trim())) {
                        check = true;
                        break;
                    } else {
                        check = false;
                    }
                }
                if (!check) {


                    DocumentReference placesref = db
                            .collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("FRIENDS").document();

                    Friend friend = new Friend();
                    friend.setFriend_id(result_qr.trim());
                    friend.setFriend_Doc_id(placesref.getId());


                    placesref.set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {

                            } else {

                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: " + task.getException());
                            }
                        }
                    });
               //     recycle_adapter.notifyDataSetChanged();

                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Już jest twoim znajomym", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                View parentLayout =findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Znajomy został juz dodany", Snackbar.LENGTH_SHORT).show();
            }

        } else {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "To jest twoje ID", Snackbar.LENGTH_SHORT).show();
        }
    }
}
