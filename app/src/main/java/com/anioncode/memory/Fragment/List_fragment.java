package com.anioncode.memory.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.anioncode.memory.Activity.Login;
import com.anioncode.memory.Activity.MainActivity;
import com.anioncode.memory.Activity.ShowPageActivity;
import com.anioncode.memory.Adapter.Recycle_adapter;
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class List_fragment extends Fragment implements View.OnClickListener {
    private Recycle_adapter recycle_adapter;
    private RecyclerView mRecyclerView;
    FloatingActionButton floatingActionButton;
    View view;
    ListenerRegistration registration;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    ArrayList<Places> adds = new ArrayList<>();

    private Recycle_adapter.OnItemClickListener mListener;

    private CollectionReference noteRef = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("PLACES");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.activity_main,container,false);

        floatingActionButton = view.findViewById(R.id.fab_add);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mListener=new Recycle_adapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Intent detailIntent = new Intent(getActivity(), ShowPageActivity.class);
                Places clickedItem = adds.get(position);
                detailIntent.putExtra("ShowPageActivity", clickedItem.getDescription());
                startActivity(detailIntent);
            }

        };
        recycle_adapter = new Recycle_adapter(adds,mListener);


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
        floatingActionButton.setOnClickListener(List_fragment.this);

    return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Recycle_adapter.OnItemClickListener) {
            //init the listener
            mListener = (Recycle_adapter.OnItemClickListener) context;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        try{
            registration =noteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e == null) {
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
                }
            });
        }catch (Exception e){
            registration.remove();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view1 = inflater2.inflate(R.layout.dialog_layout, null);

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
                                    View parentLayout = view1.findViewById(android.R.id.content);
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


}