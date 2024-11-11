package com.kewwi.quickmem.adapter.viewpager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kewwi.quickmem.ui.fragments.library.FoldersFragment;
import com.kewwi.quickmem.ui.fragments.library.MyClassesFragment;
import com.kewwi.quickmem.ui.fragments.library.StudySetsFragment;
//phục vụ cho việc chuyển đổi giữa các fragment

//MyViewPagerAdapter: Adapter quản lý hiển thị các fragment trong ViewPager.
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    //hàm khởi tạo adapter
    public MyViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    //lấy fragment tương ứng với vị trí
    @NonNull
    @Override
    public Fragment getItem(int position) {
        //trả về fragment tương ứng với vị trí
        return switch (position) {
            case 1 -> new FoldersFragment();//hiển thị fragment FoldersFragment
            case 2 -> new MyClassesFragment();//hiển thị fragment MyClassesFragment
            default -> new StudySetsFragment();//hiển thị fragment StudySetsFragment
        };
    }

    //trả về số lượng fragment
    @Override
    public int getCount() {
        return 3;
    }

    //trả về tiêu đề cho mỗi fragment
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //trả về tiêu đề tương ứng với vị trí
        return switch (position) {
            case 0 -> "Study sets";//hiển thị tiêu đề "Study sets"
            case 1 -> "Folders";//hiển thị tiêu đề "Folders"
            case 2 -> "Classes";//hiển thị tiêu đề "Classes"
            default -> "";//hiển thị tiêu đề rỗng
        };
    }
}