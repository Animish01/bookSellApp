package com.example.sellbooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText email;
    EditText userName;
    EditText pw;
    EditText con_pw;
    EditText phone;
    TextView login;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.button_signup);
        login = findViewById(R.id.Login);
        email =  findViewById(R.id.editTextText);
        userName =  findViewById(R.id.username);
        phone =  findViewById(R.id.phoneNo);
        pw = findViewById(R.id.editTextTextPassword);
        con_pw = findViewById(R.id.confirmPw);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = email.getText().toString().trim();
                String user_phone = phone.getText().toString();
                String user_pw = pw.getText().toString();
                String pw_con = con_pw.getText().toString();
                String user_name = userName.getText().toString();




                if(user_email.isEmpty()){

                    Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                else if(user_pw.isEmpty()){

                    Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                else if(user_phone.isEmpty()){

                    Toast.makeText(RegisterActivity.this, "Please enter Phone no.", Toast.LENGTH_SHORT).show();
                }
                else if(user_name.isEmpty()){

                    Toast.makeText(RegisterActivity.this, "Please enter user name", Toast.LENGTH_SHORT).show();
                }
                else if(pw_con==user_pw){

                    Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }

//                progressDialog.cancel();
                else{
                    progressDialog.show();

                    auth.createUserWithEmailAndPassword(user_email, user_pw)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {


                                    firestore.collection("users")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .set(new UserModel(user_email, user_name, user_pw, user_phone));

                                    progressDialog.cancel();

                                    Toast.makeText(RegisterActivity.this, "Verify phone number!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterActivity.this, RegisterPhoneActivity.class);
                                    intent.putExtra("phone", "+91"+phone.getText().toString());
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT);
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}