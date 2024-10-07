package com.kewwi.quickmem.ui.fragments.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.databinding.FragmentProfileBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.profile.SettingsActivity;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
//    private FragmentProfileBinding binding;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        UserSharePreferences userSharePreferences = new UserSharePreferences(requireActivity());
//        binding.nameProfileTv.setText(userSharePreferences.getUserName());
//        Picasso.get().load(userSharePreferences.getAvatar()).into(binding.profileIv);
//
//        binding.settingsBtn.setOnClickListener(view12 -> startActivity(new Intent(requireActivity(), SettingsActivity.class)));
//    }
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book, container, false);
    }
}
