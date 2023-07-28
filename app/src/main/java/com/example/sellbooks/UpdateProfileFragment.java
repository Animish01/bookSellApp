package com.example.sellbooks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sellbooks.databinding.FragmentUpdateProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class UpdateProfileFragment extends Fragment {

    FragmentUpdateProfileBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    String originalPw;
    String originalName;
    String originalPhone;
    String originalEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String id = auth.getUid();

        getData(id);

        DocumentReference dr = firestore.collection("users").document(id);
        dr.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        originalPw = documentSnapshot.getString("user_pw");
                        originalEmail = documentSnapshot.getString("user_email");
                        originalName = documentSnapshot.getString("user_name");
                        originalPhone = documentSnapshot.getString("user_phone");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        binding.changeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                                if(binding.username.getText().toString() != null)
                                    originalName = binding.username.getText().toString();


                                firestore.collection("users").document(id)
                                        .set(new UserModel(originalEmail, originalName,  originalPw, originalPhone))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(getActivity(), "Username changed!", Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        });


        binding.changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user;
                user = auth.getCurrentUser();


                String pw = binding.newPw.getText().toString();
                String c_pw = binding.confirmPw.getText().toString();
                String originalEntered = binding.pw.getText().toString();


                if(originalEntered == null){

                    Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
                }
                else if(pw == null){

                    Toast.makeText(getActivity(), "Enter new password!", Toast.LENGTH_SHORT).show();
                }
                else if(c_pw == null){

                    Toast.makeText(getActivity(), "Confirm password!", Toast.LENGTH_SHORT).show();
                }
                else if(originalPw.equals(originalEntered)) {

                    if (pw.equals(c_pw)) {

                    user.updatePassword(pw)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(getActivity(), "Password updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

//                        Toast.makeText(getActivity(), originalPw, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), "Password and confirm password don't match.", Toast.LENGTH_SHORT).show();

                    }
                }
                else{

                    Toast.makeText(getActivity(), "Incorrect password entered.", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void getData(String id) {

        DocumentReference user = firestore.collection("users").document(id);

        user.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        binding.username.setHint("Username: "+documentSnapshot.getString("user_name"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}