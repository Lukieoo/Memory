package com.anioncode.memory.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anioncode.memory.Activity.Qrscaner;
import com.anioncode.memory.Adapter.ExampleAdapter;
import com.anioncode.memory.Adapter.Profile_adapter;
import com.anioncode.memory.Models.ExampleItem;
import com.anioncode.memory.Models.Friend;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.android.volley.VolleyLog.TAG;

public class Profile_fragment extends Fragment {
    View view;
    List<Friend> lista = new ArrayList<>();
    CircleImageView circleImageView;
    TextView id_text;
    EditText editText;

    private Profile_adapter recycle_adapter;
    private RecyclerView mRecyclerView;
    ArrayList<Friend> friends = new ArrayList<>();
    private Profile_adapter.OnItemClickListener mListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("USERS").document(FirebaseAuth.getInstance().getUid());
    private CollectionReference noteRef = db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("FRIENDS");
    ListenerRegistration registration;

    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_CREATOR = "creatorName";
    public static final String EXTRA_LIKES = "likeCount";

    private RecyclerView mRecyclerViewx;
    private ExampleAdapter mExampleAdapter;
    private ArrayList<ExampleItem> mExampleList;
    private RequestQueue mRequestQueue;

    private ExampleAdapter.OnItemClickListener mListenerx;

    Context contextActivty;

    //Floating action menu
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    boolean isOpen=false;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_layout, container, false);
        contextActivty = getActivity();
        circleImageView = view.findViewById(R.id.profile_image);
        mRecyclerView = view.findViewById(R.id.recycle);
        editText = view.findViewById(R.id.editText);
        //   mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setNestedScrollingEnabled(false);
        mListener = new Profile_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String rodzaj) {
                if (rodzaj.equals("button")) {
                    Friend clickedItem = friends.get(position);
                    Toast.makeText(getActivity(), clickedItem.getFriend_Doc_id(), Toast.LENGTH_LONG).show();
                    noteRef.document(clickedItem.getFriend_Doc_id()).delete();


                }
            }
        };

        fab=(FloatingActionButton) view.findViewById(R.id.fab);
        fab1=(FloatingActionButton) view.findViewById(R.id.fab1);
        fab2=(FloatingActionButton) view.findViewById(R.id.fab2);
        fab3=(FloatingActionButton) view.findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen){
                    openMenu();


                }else {
                    closeMenu();
                }
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view1 = inflater2.inflate(R.layout.qr_barcode, null);
                ImageView imageView=view1.findViewById(R.id.ImageViewButton);
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(FirebaseAuth.getInstance().getUid(), BarcodeFormat.QR_CODE, 400, 400);

                    imageView.setImageBitmap(bitmap);
                } catch(Exception e) {

                }

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();


                            }
                        });
                builder1.setView(view1);
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });


        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody ="Zapraszam cię do aplikacji  \"Zaznacz To\" moje ID :* "+ FirebaseAuth.getInstance().getUid()+ " *  \n https://play.google.com/store/apps/details?id=com.anioncode.memory " ;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Zaznacz To" );
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Zaznacz To "));
            }
        });
//        friends.add(new Friend("TYLE", "MYLE"));
//        friends.add(new Friend("TYLE", "MYLE"));
//        friends.add(new Friend("TYLE", "MYLE"));
        recycle_adapter = new Profile_adapter(getActivity(), friends, mListener);
        mRecyclerView.setAdapter(recycle_adapter);
        recycle_adapter.notifyDataSetChanged();
        id_text = view.findViewById(R.id.textView3);
        id_text.setText(FirebaseAuth.getInstance().getUid());

        init();
        ///DO TEGO PIXABAY
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view1 = inflater2.inflate(R.layout.dialog_edit_url, null);

                final EditText editText = view1.findViewById(R.id.url);
                mRecyclerViewx = view1.findViewById(R.id.recycler_view);
                mRecyclerViewx.setHasFixedSize(true);
                mRecyclerViewx.setLayoutManager(new LinearLayoutManager(getActivity()));
                final EditText get = view1.findViewById(R.id.search);
                FloatingActionButton button = view1.findViewById(R.id.ser);
                mExampleList = new ArrayList<>();
                mListenerx = new ExampleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ExampleItem exampleItem = mExampleList.get(position);
                        editText.setText(exampleItem.getImageUrl());
                        hideSoftKeyboard();
                        docRef.update("avatar", editText.getText().toString().trim());
                        try {
                            Glide.with(getActivity()).load(editText.getText().toString().trim()).into(circleImageView);
                            Snackbar.make(view1,"Prawidłowo zmieniłeś swoje zdjęcie profilowe .", LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Snackbar.make(view1,"Coś poszło nie tak.. O.o Ups ?.", LENGTH_SHORT).show();
                        }

                    }
                };

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExampleList.clear();
                        mRequestQueue = Volley.newRequestQueue(getActivity());
                        parseJSON(get.getText().toString().trim());
                        hideSoftKeyboard();

                    }
                });


                builder1.setView(view1);
                builder1.setNegativeButton(
                        "Anuluj",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        });
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                docRef.update("avatar", editText.getText().toString().trim());
                                try {
                                    Glide.with(getActivity()).load(editText.getText().toString().trim()).into(circleImageView);
                                } catch (Exception e) {

                                }
                                dialog.cancel();


                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), Qrscaner.class);
                startActivity(myIntent);
            }
        });
        return view;

    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void parseJSON(String szukanie) {
        String url = "https://pixabay.com/api/?key=12554467-23ad588a5ecfc9c1865014b5b&q=" + szukanie + "&image_type=true";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);

                                String creatorName = hit.getString("user");
                                String imageUrl = hit.getString("largeImageURL");
                                int likeCount = hit.getInt("likes");
                                //webformatURL
                                mExampleList.add(new ExampleItem(imageUrl, creatorName, likeCount));
                            }

                            mExampleAdapter = new ExampleAdapter(getActivity(), mExampleList);
                            mRecyclerViewx.setAdapter(mExampleAdapter);
                            mExampleAdapter.setOnItemClickListener(mListenerx);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void init() {
        Log.d("UIDLOG", FirebaseAuth.getInstance().getUid());


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {


                if (!editText.getText().toString().trim().equals(FirebaseAuth.getInstance().getUid())) {
                    if (!editText.getText().toString().trim().equals("")) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                                || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                            boolean check = false;
                            for (Friend friend : friends) {

                                if (friend.getFriend_id().equals(editText.getText().toString().trim())) {
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
                                friend.setFriend_id(editText.getText().toString().trim());
                                friend.setFriend_Doc_id(placesref.getId());

                                editText.setText("");
                                placesref.set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        if (task.isSuccessful()) {

                                        } else {

                                            View parentLayout = getActivity().findViewById(android.R.id.content);
                                            Snackbar.make(parentLayout, "Nie mogliśmy tego zapisać", Snackbar.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: " + task.getException());
                                        }
                                    }
                                });
                                recycle_adapter.notifyDataSetChanged();

                            } else {
                                View parentLayout = getActivity().findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Już jest twoim znajomym", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            View parentLayout = getActivity().findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Znajomy został juz dodany", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    View parentLayout = getActivity().findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "To jest twoje ID", Snackbar.LENGTH_SHORT).show();
                }
                return false;
            }


        });

    }


    @Override
    public void onStart() {
        super.onStart();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        User user = task.getResult().toObject(User.class);


                        try {
                            if (!user.getAvatar().equals("") && (user.getAvatar() != null)) {
                                Glide.with(getActivity()).load(user.getAvatar()).into(circleImageView);

                            }

                        } catch (Exception e) {
                            Glide.with(getActivity()).load(R.drawable.ic_person).into(circleImageView);

                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        try {

            registration = noteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e == null) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            friends.clear();

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                Friend p = d.toObject(Friend.class);


                                friends.add(p);

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
        if (registration != null) registration.remove();
    }
    private void openMenu() {
        isOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.stan_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.stan_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.stan_155));
    }
    private void closeMenu() {
        isOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);

    }
}
