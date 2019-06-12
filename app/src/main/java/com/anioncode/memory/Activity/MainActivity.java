package com.anioncode.memory.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anioncode.memory.Adapter.Recycle_adapter;
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Recycle_adapter.OnItemClickListener {

    private Recycle_adapter recycle_adapter;
    private RecyclerView mRecyclerView;
    FloatingActionButton floatingActionButton;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    ArrayList<Places> adds = new ArrayList<>();

    private CollectionReference noteRef = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("PLACES");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.fab_add);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


//        adds.add(new Places(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid(), "t", "a", "Flewe mate bunto i orato musi"));
//        adds.add(new Places(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid(), "t", "a", "Stalone i wia pi 45"));

        recycle_adapter = new Recycle_adapter(adds,this);



//        DocumentReference placesref = mDb
//                .collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("PLACES").document("AJnqvpkbmOCLwVB9v1yL");
//        placesref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot doc = task.getResult();
//                StringBuilder fields = new StringBuilder("");
//                fields.append("username: ").append(doc.get("username"));
//                fields.append("\nname: ").append(doc.get("name"));
//                fields.append("\nposition1: ").append(doc.get("position1"));
//                fields.append("\nposition2: ").append(doc.get("position2"));
//                fields.append("\ndescription: ").append(doc.get("description"));
//                Toast.makeText(MainActivity.this,fields.toString(),Toast.LENGTH_LONG).show();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

//        recycle_adapter.pong.add(new Places(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid(), "t", "a", "Wagi kolirence wia 13 pi 14"));
//        recycle_adapter.pong.add(new Places(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid(), "t", "a", "Stalone i wia pi 45"));
//        recycle_adapter.pong.add(new Places(FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid(), "t", "a", "Gatoot wpia pi 145"));
        mRecyclerView.setAdapter(recycle_adapter);
        recycle_adapter.notifyDataSetChanged();

        mDb.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("PLACES").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    adds.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        Places p = d.toObject(Places.class);

                        adds.add(p);

                    }

                    recycle_adapter.notifyDataSetChanged();

                }

            }
        });
        floatingActionButton.setOnClickListener(this);


    }
    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    adds.clear();

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        Places p = d.toObject(Places.class);

                        adds.add(p);

                    }

                    recycle_adapter.notifyDataSetChanged();

                }

            }
        });
    }
//    @Override
//    protected void onPause() {
//        if(noteRef!=null){
//            noteRef.(this);
//        }
//        super.onPause();
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view1 = inflater2.inflate(R.layout.dialog_layout, null);

        builder1.setView(view1);


        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Places places = new Places();
                        places.setUsername("none1");
                        places.setName("none2");
                        places.setPosition1("none3");
                        places.setPosition2("none4");
                        places.setDescription("none5");
                        DocumentReference placesref = mDb
                                .collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .collection("PLACES").document();
                        placesref.set(places).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {

                                } else {
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onItemClick(int position) {

        Intent detailIntent = new Intent(this, ShowPageActivity.class);
        Places clickedItem = adds.get(position);

        detailIntent.putExtra("ShowPageActivity", clickedItem.getDescription());
        startActivity(detailIntent);
    }
       // Intent intent = new Intent(this, NoteActivity.class);
    //   Toast.makeText(this, String.valueOf(adds.getClass()),Toast.LENGTH_LONG).show();
      //  startActivity(intent);

}
