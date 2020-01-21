package com.anioncode.memory.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
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
import static com.anioncode.memory.Adapter.Recycle_adapter.AVATAR;
import static com.anioncode.memory.Models.StaticClass.USER_CLIENT;

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
    CollectionReference docRef = mDb.collection("USERS");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);

        floatingActionButton = view.findViewById(R.id.fab_add);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mListener = new Recycle_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position, String rodzaj) {
                if (rodzaj.equals("button")) {
                    final Places clickedItem = adds.get(position);
                   // Toast.makeText(getActivity(), clickedItem.getPlaces_id(), Toast.LENGTH_LONG).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Chcesz usunąć ?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ANULUJ",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    noteRef.document(clickedItem.getPlaces_id()).delete();

                                    adds.remove(position);
                                    recycle_adapter.notifyItemRemoved(position);
                                    recycle_adapter.notifyItemRangeChanged(position,adds.size());
                                }
                            });

                    alertDialog.show();
                  //  noteRef.document(clickedItem.getPlaces_id()).delete();


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
                                        Glide.with(getActivity())
                                                .asDrawable()
                                                .load(user.getAvatar())
                                                .thumbnail(Glide.with(getActivity())
                                                        .asDrawable()
                                                        .load(user.getAvatar()))
                                                .listener(new RequestListener<Drawable>() {
                                                    @Override
                                                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        if (isFirstResource) {
                                                            return false; // thumbnail was not shown, do as usual
                                                        }
                                                     return true;
                                                    }
                                                })
                                                .into(circleImageView);

                                       // Glide.with(getActivity()).load(user.getAvatar()).into(circleImageView);
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
                    // Glide.with(getActivity()).load(AVATAR).into(circleImageView);

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

//                    Intent detailIntent = new Intent(getActivity(), ShowPageActivity.class);
//                    Places clickedItem = adds.get(position);
//                    detailIntent.putExtra("ShowPageActivity", clickedItem.getDescription());
//                    startActivity(detailIntent);
                }

            }

        };
        recycle_adapter = new Recycle_adapter(getActivity(), adds, mListener, "TAK");


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
        try {

            registration = noteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        } catch (Exception e) {
            registration.remove();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    String data_final;

    @Override
    public void onClick(final View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view1 = inflater2.inflate(R.layout.dialog_layout, null);

        final EditText position1 = view1.findViewById(R.id.position1);
        final EditText position2 = view1.findViewById(R.id.position2);
        final EditText name = view1.findViewById(R.id.Nazwa);
        final EditText description = view1.findViewById(R.id.Opis);

        final DatePicker datePicker = view1.findViewById(R.id.calendarView);


        builder1.setView(view1);
        builder1.setNegativeButton("Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (!position1.getText().toString().trim().equals("") && !position2.getText().toString().trim().equals("")) {

                            String a = String.valueOf(datePicker.getDayOfMonth());
                            String b = String.valueOf(datePicker.getMonth() + 1);
                            String c = String.valueOf(datePicker.getYear());

                            if (b.length() == 1) {
                                b = "0" + b;
                            }
                            if (a.length() == 1) {
                                a = "0" + a;
                            }
                            data_final = a + "." + b + "." + c;


                            DocumentReference placesref = mDb
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .collection("PLACES").document();
                            String wyraz = placesref.getId();
                            Places places = new Places();

                            places.setUsername(USER_CLIENT);
                            places.setName(name.getText().toString());
                            places.setPosition1(position1.getText().toString());
                            places.setPosition2(position2.getText().toString());
                            places.setDescription(description.getText().toString());
                            places.setPlaces_id(wyraz);
                            places.setUser_id(FirebaseAuth.getInstance().getUid());

//                            Date date= new Date();
//                            System.out.println(datex.getDate());

                            places.setTimestamp(data_final);

                            placesref.set(places).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()) {

                                    } else {
                                        //    View parentLayout =  view.findViewById(android.R.id.content);
                                        //Snackbar.make(parentLayout, "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            dialog.cancel();
                        } else {
//                            View parentLayout =  view.findViewById(android.R.id.content);
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}