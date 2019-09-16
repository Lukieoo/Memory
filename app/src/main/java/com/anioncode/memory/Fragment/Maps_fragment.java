package com.anioncode.memory.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anioncode.memory.Models.Friend;
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.android.volley.VolleyLog.TAG;
import static com.anioncode.memory.Models.StaticClass.USER_CLIENT;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;

public class Maps_fragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private MapView mMapView;
    private FloatingActionButton floatingActionButton;
    private RelativeLayout relativeLayout;
    private GoogleMap mMap;
    ListenerRegistration registration;
    ListenerRegistration registration2;
    private String data_final = "10.15.2019";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private int MY_REQUEST_INT = 1;

    private EditText mSearchText;

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("PLACES");
    private CollectionReference noteRef2 = mDb.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("FRIENDS");

    View view;
    private AdView mAdView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activitymaps_layout, container, false);

//        MobileAds.initialize(getActivity(), "ca-app-pub-3788232558823244/8545263415");
        MobileAds.initialize(getActivity(), "ca-app-pub-3788232558823244/8545263415");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        // setupAutoCompleteFragment();
        mMapView = (MapView) view.findViewById(R.id.map);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mSearchText = (EditText) view.findViewById(R.id.input_search);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout1);


        init();
        floatingActionButton.setOnClickListener(this);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);


        return view;
    }

    private void init() {
        Log.d("", "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();

                    mSearchText.setText("");
                    relativeLayout.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d("", "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e("", "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d("", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 12f,
                    address.getAddressLine(0));
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d("", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        Drawable circleDrawable = getResources().getDrawable(R.drawable.marker);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title).icon(markerIcon);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);

            tvTitle.setText(marker.getTitle());
            tvSubTitle.setText(marker.getSnippet());
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);

            tvTitle.setText(marker.getTitle());
            tvSubTitle.setText(marker.getSnippet());
            return view;
        }
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        try {
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Log.e("", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("", "Can't find style. Error: ", e);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);

            }
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(8)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        // map.animateCamera( CameraUpdateFactory.zoomTo( 6.0f ) );
        //   Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(44, 44)).title("Marker").snippet("Test zawartej treści"));
        //  Drawable circleDrawable = getResources().getDrawable(R.drawable.marker);
        // BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        //   marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

////Długie przytrzymanie palca
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                // Clears the previously touched position

                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(HUE_CYAN));
                // Setting the position for the marker
                markerOptions.position(latLng);


                String name_nzawa = "";
                try {
                    Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Wait", Toast.LENGTH_LONG).show();
                    } else {
                        if (addresses.size() > 0) {
                            if (addresses.get(0).getFeatureName() != null) {
                                name_nzawa += addresses.get(0).getFeatureName() + ", ";
                            }
                            if (addresses.get(0).getLocality() != null) {
                                name_nzawa += addresses.get(0).getLocality() + ", ";
                            }
                            if (addresses.get(0).getAdminArea() != null) {
                                name_nzawa += addresses.get(0).getAdminArea() + ", ";
                            }
                            if (addresses.get(0).getCountryName() != null) {
                                name_nzawa += addresses.get(0).getCountryName();
                            }

                        } else {
                            name_nzawa = "NIe masz połączenia z siecią ";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view1 = inflater2.inflate(R.layout.dialog_layout, null);

                final EditText v = view1.findViewById(R.id.position1);
                final EditText v1 = view1.findViewById(R.id.position2);
                final EditText name = view1.findViewById(R.id.Nazwa);
                final EditText description = view1.findViewById(R.id.Opis);
                final LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.line_hint);

                final DatePicker datePicker = view1.findViewById(R.id.calendarView);

                linearLayout.setVisibility(View.INVISIBLE);
                v.setVisibility(View.INVISIBLE);
                v1.setVisibility(View.INVISIBLE);
                //name.setEnabled(true);

                v.setText(String.valueOf(latLng.latitude));
                v1.setText(String.valueOf(latLng.longitude));
                name.setText(name_nzawa);

                markerOptions.title(name_nzawa);


                builder1.setView(view1);


                final String finalName_nzawa = name_nzawa;
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
                                //map.clear();

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

                                // Animating to the touched position
                                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                                // Placing a marker on the touched position
                                markerOptions.snippet(description.getText().toString());
                                map.addMarker(markerOptions);


                                DocumentReference placesref = mDb
                                        .collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("PLACES").document();
                                String wyraz = placesref.getId();

                                Places places = new Places();
                                //  UserClient userClient=new UserClient();


                                places.setUsername(USER_CLIENT);
                                places.setName(name.getText().toString());
                                places.setPosition1(String.valueOf(latLng.latitude));
                                places.setPosition2(String.valueOf(latLng.longitude));
                                places.setDescription(description.getText().toString());
                                places.setPlaces_id(wyraz);
                                places.setTimestamp(data_final);
                                places.setUser_id(FirebaseAuth.getInstance().getUid());

                                placesref.set(places).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        if (task.isSuccessful()) {

                                        } else {
                                            View parentLayout = getActivity().findViewById(android.R.id.content);
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
        });
///Krótkie kliknięcie
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                // Creating a marker
                final MarkerOptions markerOptions = new MarkerOptions();
                // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(HUE_CYAN));
                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                try {
                    String name_nzawa = "";
                    Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Wait", Toast.LENGTH_LONG).show();
                    } else {
                        if (addresses.size() > 0) {

                            if (addresses.get(0).getFeatureName() != null) {
                                name_nzawa += addresses.get(0).getFeatureName() + ", ";
                            }
                            if (addresses.get(0).getLocality() != null) {
                                name_nzawa += addresses.get(0).getLocality() + ", ";
                            }
                            if (addresses.get(0).getAdminArea() != null) {
                                name_nzawa += addresses.get(0).getAdminArea() + ", ";
                            }
                            if (addresses.get(0).getCountryName() != null) {
                                name_nzawa += addresses.get(0).getCountryName();
                            }
                            Toast.makeText(getActivity().getApplicationContext(),
                                    name_nzawa,
                                    Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), "Włącz transmisje danych lub podłącz się do Wifi", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }

            }

        });

    }

    //Żywotność aplikacji
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
        registration.remove();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    ////////////////////////////////////////
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
//        mMap.clear();
        try {
            registration = noteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!queryDocumentSnapshots.isEmpty()) {


                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Places p = d.toObject(Places.class);

                                final Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getPosition1()), Double.parseDouble(p.getPosition2()))).title(p.getName()).snippet(p.getDescription() + "\n" + p.getTimestamp() + "\n" + p.getUsername()));


                                /// ICONY PROFILU
                                DocumentReference docRef = mDb.collection("USERS").document(p.getUser_id());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                User user = task.getResult().toObject(User.class);
                                                try {
                                                    if (!user.getAvatar().equals("") && (user.getAvatar() != null)) {
                                                        //       Glide.with(getActivity()).load(user.getAvatar()).into(circleImageView);

                                                        //    marker.setIcon(markerIcon);
                                                        loadMarkerIcon(marker, user.getAvatar());

                                                    }
                                                } catch (Exception e) {
                                                    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                                    marker.setIcon(icon);
                                                }


                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                                ///KONIEC ICON PROFILU

                            }


                        }


                    }
                }
            });
        } catch (Exception e) {
            registration.remove();
        }
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
                                                    //      mMap.clear();
                                                    final Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getPosition1()), Double.parseDouble(p.getPosition2()))).title(p.getName()).snippet(p.getDescription() + "\n" + p.getTimestamp() + "\n" + p.getUsername()));

                                                    DocumentReference docRef = mDb.collection("USERS").document(p.getUser_id());
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document != null) {
                                                                    User user = task.getResult().toObject(User.class);
                                                                    try {
                                                                        loadMarkerIcon(marker, user.getAvatar());
                                                                    } catch (Exception e) {
                                                                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                                                        marker.setIcon(icon);
                                                                    }
//                                                                    if (!user.getAvatar().equals("") && (user.getAvatar() != null)) {
//                                                                        //       Glide.with(getActivity()).load(user.getAvatar()).into(circleImageView);
//
//                                                                        //    marker.setIcon(markerIcon);
//                                                                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
//                                                                        marker.setIcon(icon);
//
//                                                                    } else {
//                                                                        //   Glide.with(getActivity()).load(R.drawable.ic_person).into(circleImageView);
//
//                                                                    }


                                                                } else {
                                                                    Log.d(TAG, "No such document");
                                                                }
                                                            } else {
                                                                Log.d(TAG, "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });
                                                }
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
            registration.remove();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        registration.remove();
        registration2.remove();
    }

    ///KONIEC Żywotności aplikacji
    ////Przycisk wyszukiwania
    @Override
    public void onClick(View v) {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.commit();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String txt = sharedPref.getString("dane", "");

        if (txt.equals("tak")) {
            relativeLayout.setVisibility(View.INVISIBLE);
            editor = sharedPref.edit();
            editor.putString("dane", "");
            editor.commit();
        } else {
            relativeLayout.setVisibility(View.VISIBLE);

            editor = sharedPref.edit();
            editor.putString("dane", "tak");
            editor.commit();
        }


    }

    //////////MARKER DESIGN
    private void loadMarkerIcon(final Marker marker, String url) {
        String burlImg = url;
        Glide.with(this).asBitmap().load(burlImg).
                into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (resource != null) {

                            Bitmap mBitmap = getCroppedBitmap(resource);
                            //mBitmap.getConfig();

                            mBitmap = scaleDown(mBitmap, 125, true);
//                            mBitmap = getRoundedCornerBitmap(mBitmap, 125);
                            mBitmap = getborder(mBitmap);
                            mBitmap = getCircledBitmap(mBitmap);

                            //   mBitmap = createRoundedBitmapDrawableWithBorder(mBitmap);
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mBitmap);
                            marker.setIcon(icon);
                        }

                    }

                });

    }

    public Bitmap getborder(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }

    public static Bitmap getCircledBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private Bitmap createRoundedBitmapDrawableWithBorder(Bitmap bitmap) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int borderWidthHalf = 5; // In pixels

        int bitmapRadius = Math.min(bitmapWidth, bitmapHeight) / 2;

        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);
        //Toast.makeText(mContext,""+bitmapMin,Toast.LENGTH_SHORT).show();

        int newBitmapSquareWidth = bitmapSquareWidth + borderWidthHalf;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);


        // Initialize a new Canvas to draw empty bitmap
        Canvas canvas = new Canvas(roundedBitmap);

        // Draw a solid color to canvas
        canvas.drawColor(Color.TRANSPARENT);

        // Calculation to draw bitmap at the circular bitmap center position
        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;


        canvas.drawBitmap(bitmap, x, y, null);

        // Initializing a new Paint instance to draw circular border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf * 2);
        borderPaint.setColor(Color.WHITE);


        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, newBitmapSquareWidth / 2, borderPaint);

        // Create a new RoundedBitmapDrawable


        return roundedBitmap;
    }

    ///Marker places
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

/////////END MAKRER DESIGN
}