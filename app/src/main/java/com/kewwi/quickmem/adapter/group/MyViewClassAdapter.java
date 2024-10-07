package com.kewwi.quickmem.adapter.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kewwi.quickmem.ui.activities.classes.ViewMembersFragment;
import com.kewwi.quickmem.ui.activities.classes.ViewSetsFragment;

public class MyViewClassAdapter extends FragmentStatePagerAdapter {

    public MyViewClassAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return switch (position) {
            case 1 -> new ViewMembersFragment();
            default -> new ViewSetsFragment();
        };
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return switch (position) {
            case 0 -> "SETS";
            case 1 -> "MEMBERS";
            default -> "";
        };
    }
}
