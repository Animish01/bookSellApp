package com.example.sellbooks;

import static com.example.sellbooks.MyBooksActivity.SHARED_PREFS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.sellbooks.databinding.ActivityUserPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePageActivity extends AppCompatActivity {

    ActivityUserPageBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String id = auth.getUid();

        ProfileFragment frag2 = new ProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, frag2);
        transaction.commit();

        binding.profile.setTextColor(Color.parseColor("#F48B48"));
        binding.profile.setPaintFlags(binding.profile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateProfileFragment frag1 = new UpdateProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, frag1);
                transaction.commit();


//                Changing the text color of buttons-
                binding.updateProfile.setTextColor(Color.parseColor("#F48B48"));
                binding.profile.setTextColor(Color.parseColor("#707070"));
                binding.phoneChange.setTextColor(Color.parseColor("#707070"));

//                Adding underline to text
                binding.updateProfile.setPaintFlags(binding.updateProfile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                binding.profile.setPaintFlags(binding.profile.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                binding.phoneChange.setPaintFlags(binding.phoneChange.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        });

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProfileFragment frag2 = new ProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, frag2);
                transaction.commit();

//                Changing the text color of buttons-
                binding.profile.setTextColor(Color.parseColor("#F48B48"));
                binding.updateProfile.setTextColor(Color.parseColor("#707070"));
                binding.phoneChange.setTextColor(Color.parseColor("#707070"));


//                Adding underline to text
                binding.profile.setPaintFlags(binding.profile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                binding.updateProfile.setPaintFlags(binding.updateProfile.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                binding.phoneChange.setPaintFlags(binding.phoneChange.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        });

        binding.phoneChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangePhoneFragment frag1 = new ChangePhoneFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, frag1);
                transaction.commit();

                //                Changing the text color of buttons-
                binding.phoneChange.setTextColor(Color.parseColor("#F48B48"));
                binding.profile.setTextColor(Color.parseColor("#707070"));
                binding.updateProfile.setTextColor(Color.parseColor("#707070"));


//                Adding underline to text
                binding.phoneChange.setPaintFlags(binding.phoneChange.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                binding.profile.setPaintFlags(binding.profile.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                binding.updateProfile.setPaintFlags(binding.updateProfile.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        });

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", null);
                editor.apply();

                startActivity(new Intent(ProfilePageActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ProfilePageActivity.this, HomeActivity.class));
            }
        });

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.expandedMenu.setVisibility(View.VISIBLE);
                binding.menu.setVisibility(View.GONE);
            }
        });


        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.expandedMenu.setVisibility(View.GONE);
                binding.menu.setVisibility(View.VISIBLE);
            }
        });
    }

}