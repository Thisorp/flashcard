package com.kewwi.quickmem.adapter.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.databinding.ItemOnboardingBinding;

import org.jetbrains.annotations.NotNull;
//hiển thị các trang giới thiệu

//OnboardingAdapter: Adapter quản lý hiển thị các trang giới thiệu.
public class OnboardingAdapter extends PagerAdapter {

    private final Context context;//ngữ cảnh hiện tại

    //hàm khởi tạo adapter
    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    //dữ liệu cho các trang giới thiệu
    private final int[] onBoardingTitles = {
            R.string.onboarding_title_1,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3,
            R.string.onboarding_title_4
    };

    //dữ liệu cho các hình ảnh trang giới thiệu
    private final int[] onBoardingImages = {
            R.drawable.onboarding_1,
            R.drawable.ic_start_svg,
            R.drawable.onboarding_3,
            R.drawable.onboarding_2
    };

    //trả về số lượng trang giới thiệu
    @Override
    public int getCount() {
        return onBoardingTitles.length;
    }

    //xác định xem một view có đại diện cho trang được trả về bởi instantiateItem hay không
    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view.equals(object);//Kiểm tra xem view có khớp với object không
    }

    //tạo view cho mỗi trang giới thiệu
    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        //// Sử dụng View Binding để inflate layout item_onboarding.xml cho từng trang
        final ItemOnboardingBinding binding = ItemOnboardingBinding.inflate(
                // Sử dụng LayoutInflater để inflate layout
                LayoutInflater.from(context), container, false
        );

        //đặt tiêu  đề và hình ảnh cho trang giới thiệu dưa trên vị trí
        binding.onboardingTitleTv.setText(onBoardingTitles[position]);
        binding.onboardingIv.setImageResource(onBoardingImages[position]);

        //thêm view vào container
        container.addView(binding.getRoot());

        //trả về view đã được gắn layout
        return binding.getRoot();
    }

    //hủy view khi không còn hiển thị
    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((View) object);
    }
}
