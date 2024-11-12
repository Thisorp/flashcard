package com.kewwi.quickmem.ui.activities.profile;

import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.databinding.ActivitySettingsBinding;
import com.kewwi.quickmem.databinding.DialogChangeusernamebyfirebaseBinding;
import com.kewwi.quickmem.ui.activities.auth.signin.SignInActivity;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

public class setting_useFirebase extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private AlertDialog detailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Hiển thị thông tin người dùng từ Firebase
        if (currentUser != null) {
            binding.usernameTv.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "No username");
            binding.emailTv.setText(currentUser.getEmail());
        }

        onClickItemSetting();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void onClickItemSetting() {

        binding.emailCl.setOnClickListener(view -> openDialogChangeUser());
        binding.passwordCl.setVisibility(View.GONE); // Ẩn tùy chọn đổi mật khẩu vì không yêu cầu với đăng nhập Google

        binding.logOutBtn.setOnClickListener(v -> {
            PopupDialog.getInstance(setting_useFirebase.this)
                    .setStyle(Styles.STANDARD)
                    .setHeading("Đăng xuất!")
                    .setDescription("Bạn chắc chắn chứ?")
                    .setPopupDialogIcon(R.drawable.baseline_logout_24)
                    .setCancelable(true)
                    .setPositiveButtonText("OK")
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClicked(Dialog dialog) {
                            super.onPositiveClicked(dialog);
                            mAuth.signOut(); // Đăng xuất Firebase
                            Intent intent = new Intent(setting_useFirebase.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onNegativeClicked(Dialog dialog) {
                            super.onNegativeClicked(dialog);
                            dialog.dismiss();
                        }
                    });
        });
    }

    private void openDialogChangeUser() {
        DialogChangeusernamebyfirebaseBinding changeusernamebyfirebaseBinding = DialogChangeusernamebyfirebaseBinding.inflate(LayoutInflater.from(this));
        View view = changeusernamebyfirebaseBinding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        detailDialog = builder.create();
        detailDialog.setCanceledOnTouchOutside(false);
        detailDialog.show();
        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        changeusernamebyfirebaseBinding.cancelChangeName.setOnClickListener(view1 -> detailDialog.dismiss());

        changeusernamebyfirebaseBinding.submitChangeName.setOnClickListener(v -> {
            String newEmail = changeusernamebyfirebaseBinding.usernameEt.getText().toString().trim();

            if (newEmail.isEmpty()) {
                changeusernamebyfirebaseBinding.textIL.setHelperText("Please enter a new email");
            } else {
                currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(setting_useFirebase.this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                        binding.emailTv.setText(newEmail);
                        detailDialog.dismiss();
                    } else {
                        Toast.makeText(setting_useFirebase.this, "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
