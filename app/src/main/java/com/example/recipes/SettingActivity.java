package com.example.recipes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipes.adapters.RecipeAdapter;
import com.example.recipes.databinding.ActivitySettingBinding;
import com.example.recipes.models.Recipe;
import com.example.recipes.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.linearLayoutShare.setOnClickListener(view -> changeName(Gravity.CENTER));
        binding.linearLayoutRate.setOnClickListener(view -> changePassword(Gravity.CENTER));
        binding.btnSignout.setOnClickListener(view -> signOut());
    }

    private void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
    }

    private void changeName(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_name);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        EditText edtName = dialog.findViewById(R.id.et_user_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_save);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://recipes-71464-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        reference.child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               user = snapshot.getValue(User.class);
               if (user != null) {
                   edtName.setText(user.getName());
               }
           }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "onCancelled: " + error.getMessage());
            }
        });


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (!newName.isEmpty() && user != null) {
                user.setName(newName);
                reference.child("Users").child(user.getId()).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingActivity.this, "Name Updated Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(SettingActivity.this, "Error in updating name", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SettingActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();

    }
    private void changePassword(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        EditText edtCurPass = dialog.findViewById(R.id.et_cur_pass);
        EditText edtNewPass = dialog.findViewById(R.id.et_new_pass);
        EditText edtConPass = dialog.findViewById(R.id.et_con_pass);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_save);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String curPass = edtCurPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String conPass = edtConPass.getText().toString().trim();

            if (curPass.isEmpty() || newPass.isEmpty() || conPass.isEmpty()) {
                Toast.makeText(SettingActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(conPass)) {
                Toast.makeText(SettingActivity.this, "New Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

            if (email != null) {
                auth.signInWithEmailAndPassword(email, curPass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        auth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance("https://recipes-71464-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("Users").child(Objects.requireNonNull(auth.getUid()));

                                // Update the password field in the Realtime Database
                                reference.child("password").setValue(newPass).addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(SettingActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(SettingActivity.this, "Error in updating password in database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SettingActivity.this, "Error in updating password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(SettingActivity.this, "Current Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }


}