package com.kewwi.quickmem.ui.activities.profile;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.databinding.ActivitySettingsBinding;
import com.kewwi.quickmem.databinding.DialogChangeusernamebyfirebaseBinding;
import com.kewwi.quickmem.ui.activities.auth.signin.SignInActivity;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

public class FirebaseSettingActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private AlertDialog detailDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_firebase_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Thiết lập ViewBinding
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            navigateToSignIn();
            return;
        }

        // Hiển thị thông tin người dùng
        binding.usernameTv.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "No username");
        binding.emailTv.setText(currentUser.getEmail());

        onClickItemSetting();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(FirebaseSettingActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void onClickItemSetting() {
        binding.usernameCl.setOnClickListener(view -> openDialogChangeUsername());
        //binding.emailCl.setOnClickListener(view -> openDialogChangeEmail());

        binding.logOutBtn.setOnClickListener(v -> {
            PopupDialog.getInstance(FirebaseSettingActivity.this)
                    .setStyle(Styles.STANDARD)
                    .setHeading("Log out!")
                    .setDescription("Are you sure you want to log out?")
                    .setPopupDialogIcon(R.drawable.baseline_logout_24)
                    .setCancelable(true)
                    .setPositiveButtonText("OK")
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClicked(Dialog dialog) {
                            super.onPositiveClicked(dialog);
                            mAuth.signOut();
                            navigateToSignIn();
                        }

                        @Override
                        public void onNegativeClicked(Dialog dialog) {
                            super.onNegativeClicked(dialog);
                            dialog.dismiss();
                        }
                    });
        });


    }
    private void openDialogChangeUsername() {
        DialogChangeusernamebyfirebaseBinding changeUsernameBinding = DialogChangeusernamebyfirebaseBinding.inflate(LayoutInflater.from(FirebaseSettingActivity.this));
        View view = changeUsernameBinding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(FirebaseSettingActivity.this);
        builder.setView(view);
        detailDialog = builder.create();
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.show();
        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        changeUsernameBinding.cancelChangeName.setOnClickListener(view1 -> detailDialog.dismiss());

        changeUsernameBinding.submitChangeName.setOnClickListener(v -> {
            String newUsername = changeUsernameBinding.usernameEt.getText().toString().trim();

            if (newUsername.isEmpty()) {
                changeUsernameBinding.textIL.setHelperText("Please enter a new username");
            } else {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();

                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FirebaseSettingActivity.this, "Username updated successfully!", Toast.LENGTH_SHORT).show();
                        binding.usernameTv.setText(newUsername);
                        detailDialog.dismiss();
                    } else {
                        Toast.makeText(FirebaseSettingActivity.this, "Failed to update username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
  //  private void openDialogChangeEmail() {
////        DialogChangeEmailBinding changeEmailBinding = DialogChangeEmailBinding.inflate(LayoutInflater.from(SettingsActivity.this));
////        View view = changeEmailBinding.getRoot();
////
////        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
////        builder.setView(view);
////        detailDialog = builder.create();
////        detailDialog.setCanceledOnTouchOutside(false);
////        detailDialog.show();
////        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
////
////        changeEmailBinding.cancelChangeEmailBtn.setOnClickListener(view1 -> detailDialog.dismiss());
////
////        changeEmailBinding.submitChangeEmailBtn.setOnClickListener(v -> {
////            String newEmail = changeEmailBinding.emailEt.getText().toString().trim();
////
////            if (newEmail.isEmpty()) {
////                changeEmailBinding.textIL.setHelperText("Please enter a new email");
////            } else {
////                currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
////                    if (task.isSuccessful()) {
////                        Toast.makeText(SettingsActivity.this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
////                        binding.emailTv.setText(newEmail);
////                        detailDialog.dismiss();
////                    } else {
////                        Toast.makeText(SettingsActivity.this, "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////        });
////    }
}