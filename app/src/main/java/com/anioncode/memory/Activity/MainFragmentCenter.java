package com.anioncode.memory.Activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.anioncode.memory.Fragment.Fragment_contenr;
import com.anioncode.memory.Fragment.Maps_fragment;
import com.anioncode.memory.Fragment.Profile_fragment;
import com.anioncode.memory.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainFragmentCenter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainbottom_loyout);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Fragment_contenr()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    String name = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            initSupportActionBar("Lista");
                            selectedFragment = new Fragment_contenr();
                            name="Lista";
                            break;
                        case R.id.nav_maps:
                            initSupportActionBar("Mapa");
                            selectedFragment = new Maps_fragment();
                            name="Mapa";
                            break;
                        case R.id.nav_search:
                            initSupportActionBar("Profil");
                            selectedFragment = new Profile_fragment();
                            name="Profil";
                            break;
                    }

//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            selectedFragment).commit();

                    FragmentManager manager=getSupportFragmentManager();
                    FragmentTransaction transaction=manager.beginTransaction();

                    Slide slide = new Slide();
                    slide.setDuration(1000);
                    slide.setInterpolator(new DecelerateInterpolator());
                    if(name.equals("Lista")){
                        //selectedFragment.setEnterTransition(slide);
                    }
                    else if(name.equals("Mapa")){
                        selectedFragment.setEnterTransition(slide);
                    }
                    else if(name.equals("Profil")){
                       // selectedFragment.setEnterTransition(slide);
                    }
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainFragmentCenter.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSupportActionBar(String nazwa) {
        setTitle(nazwa);
    }
}