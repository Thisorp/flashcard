package com.kewwi.quickmem.adapter.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kewwi.quickmem.ui.activities.classes.ViewMembersFragment;
import com.kewwi.quickmem.ui.activities.classes.ViewSetsFragment;
//để quản lý các fragment trong giao diện của ứng dụng.
// Class này kế thừa từ FragmentStatePagerAdapter và chịu trách nhiệm cho việc chuyển đổi
// giữa các fragment (ViewSetsFragment và ViewMembersFragment).

//MyViewClassAdapter: Adapter quản lý và chuyển đổi giữa các fragment trong ViewPager.
public class MyViewClassAdapter extends FragmentStatePagerAdapter {

    //hàm khởi tạo adapter với fragment manager và behavior
    public MyViewClassAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    //lấy fragment tương ứng với vị trí
    @NonNull
    @Override
    public Fragment getItem(int position) {
        //trả về fragment tương ứng với vị trí
        return switch (position) {
            case 1 ->
                    new ViewMembersFragment();//nếu vị trí là 1, trả về ViewMembersFragment(hiển thị danh sách thành viên).
            default ->
                    new ViewSetsFragment();//nếu vị trí là 0 hoặc không hợp lệ, trả về ViewSetsFragment(hiển thị danh sách các bộ flashcard).
        };
    }

    //trả về số lượng fragment
    @Override
    public int getCount() {
        return 2;
    }

    //trả về tiêu đề của fragment tương ứng với vị trí
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return switch (position) {
            case 0 -> "SETS";//nếu vị trí là 0, trả về "SETS"(danh sách các bộ flashcard).
            case 1 -> "MEMBERS";//nếu vị trí là 1, trả về "MEMBERS"(danh sách thành viên).
            default -> "";//nếu vị trí không hợp lệ, trả về rỗng
        };
    }
}
