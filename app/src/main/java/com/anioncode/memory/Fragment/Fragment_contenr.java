package com.anioncode.memory.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anioncode.memory.Models.Viewpageradapter;
import com.anioncode.memory.R;

public class Fragment_contenr extends Fragment {

    private TabLayout tabLayout;

    private ViewPager viewPager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        tabLayout=(TabLayout) view.findViewById(R.id.tablayout_id);
        viewPager=(ViewPager) view.findViewById(R.id.viewpager_id);

        Viewpageradapter viewpageradapter= new Viewpageradapter(getActivity().getSupportFragmentManager());
//        viewPager.setOffscreenPageLimit(0);
        ///Dodajesz Fragmenty
        viewpageradapter.Addfragment(new List_fragment(),"Moje miejsca");
        viewpageradapter.Addfragment(new freiend_fragment(),"Twoi znajomi");



        viewPager.setAdapter(viewpageradapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
