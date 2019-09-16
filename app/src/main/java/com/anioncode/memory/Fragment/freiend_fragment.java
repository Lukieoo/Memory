package com.anioncode.memory.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyLog;
import com.anioncode.memory.Adapter.Recycle_adapter;
import com.anioncode.memory.Models.Friend;
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.android.volley.VolleyLog.TAG;
import static com.anioncode.memory.Adapter.Recycle_adapter.AVATAR;
import static com.anioncode.memory.Models.StaticClass.USER_CLIENT;

public class freiend_fragment extends Fragment {
    private Recycle_adapter recycle_adapter;
    private RecyclerView mRecyclerView;
    FloatingActionButton floatingActionButton;
    View view;
    ListenerRegistration registration;
    ListenerRegistration registration2;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    ArrayList<Places> adds = new ArrayList<>();

    private Recycle_adapter.OnItemClickListener mListener;

    private CollectionReference noteRef = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("PLACES");
    private CollectionReference noteRef2 = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("FRIENDS");
    CollectionReference docRef = mDb.collection("USERS");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);

        floatingActionButton = view.findViewById(R.id.fab_add);
        floatingActionButton.hide();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mListener = new Recycle_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String rodzaj) {
                if (rodzaj.equals("button")) {
                    Places clickedItem = adds.get(position);
                    Toast.makeText(getActivity(), clickedItem.getPlaces_id(), Toast.LENGTH_LONG).show();
                    noteRef.document(clickedItem.getPlaces_id()).delete();

                } else {
                    Places clickedItem = adds.get(position);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view1 = inflater2.inflate(R.layout.show_memory, null);

                    TextView tvTitle = (TextView) view1.findViewById(R.id.tv_title2);
                    TextView tvSubTitle = (TextView) view1.findViewById(R.id.tv_subtitle2);
                    final CircleImageView circleImageView = (CircleImageView) view1.findViewById(R.id.profile_image3);

                    docRef.document(clickedItem.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    User user = task.getResult().toObject(User.class);

                                    try {
                                        Glide.with(getActivity()).load(user.getAvatar()).into(circleImageView);
                                        AVATAR = user.getAvatar();
                                    } catch (Exception e) {
                                        Glide.with(getActivity()).load(R.drawable.ic_person).into(circleImageView);


                                    }


                                } else {
                                    Log.d(VolleyLog.TAG, "No such document");
                                }
                            } else {
                                Log.d(VolleyLog.TAG, "get failed with ", task.getException());
                            }
                        }
                    });



                    tvTitle.setText(clickedItem.getName());
                    tvSubTitle.setText(clickedItem.getDescription() + "\n\n" + clickedItem.getTimestamp() + "\n" + clickedItem.getUsername());


                    builder1.setView(view1);
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();


                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                }

            }

        };
        recycle_adapter = new Recycle_adapter(getActivity(), adds, mListener, "NIE");


        mRecyclerView.setAdapter(recycle_adapter);
        recycle_adapter.notifyDataSetChanged();


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

        try {
            registration2 = noteRef2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!queryDocumentSnapshots.isEmpty()) {


                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Friend px = d.toObject(Friend.class);


                                CollectionReference docRef2 = mDb.collection("USERS").document(px.getFriend_id()).collection("PLACES");

                                docRef2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                        if (e == null) {
                                            if (!queryDocumentSnapshots.isEmpty()) {


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

                            }


                        }


                    }
                }
            });
        } catch (Exception e) {
            registration2.remove();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        registration2.remove();
    }


}