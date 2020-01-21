package com.anioncode.memory.Models;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Viewpageradapter extends FragmentStatePagerAdapter {

    ///Tworze listy
    private final List<Fragment>fragmentsList=new ArrayList<>();
    private final List<String>FragmentListTitles=new ArrayList<>();

    public Viewpageradapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsList.get(i);
    }

    @Override
    public int getCount() {
        return FragmentListTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentListTitles.get(position);
    }
    public void Addfragment(Fragment fragment, String title){
        fragmentsList.add(fragment);
        FragmentListTitles.add(title);

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

    }
}
