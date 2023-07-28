package com.example.sellbooks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sellbooks.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements BookClickInterface{

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    ActivityHomeBinding binding;

    BooksAdapter bookadapter;

    String username;


    ArrayList<BookModel>books;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        binding.bookList.setLayoutManager(new LinearLayoutManager(this));

        progressDialog.setMessage("Fetching books from cloud...");
        progressDialog.show();


        books = new ArrayList<>();
        bookadapter = new BooksAdapter(books, this);
        binding.bookList.setAdapter(bookadapter);


        firestore.collection("Books").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list =queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot d:list){

                            String id1 = d.getId();

                            BookModel model = d.toObject(BookModel.class);
                            if(model.getSold() == true || model.getUserid().equals(auth.getUid())) continue;
                            model.setBookId(id1);
                            books.add(model);
                        }

                        progressDialog.cancel();
                        bookadapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.cancel();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {


                firestore.collection("Books")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<DocumentSnapshot>list = queryDocumentSnapshots.getDocuments();

                                if(editable.equals("")) books.clear();

                                books.clear();

                                for(DocumentSnapshot d:list){

                                    String name = d.getString("bookname").toLowerCase(Locale.ROOT);
                                    String desc = d.getString("bookdesc").toLowerCase(Locale.ROOT);

                                    if(name.contains(editable) || desc.contains(editable)){

                                        BookModel model2 = d.toObject(BookModel.class);

                                        if(!books.contains(model2)) {

                                            String id1 = d.getId();

                                            BookModel model = d.toObject(BookModel.class);
                                            model2.setBookId(id1);

                                            books.add(model2);
                                        }
                                    }
                                }

                                bookadapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        String userId = FirebaseAuth.getInstance().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userId);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                username = value.getString("user_name");
                binding.user.setText("Welcome " + username+"!");
            }
        });


        binding.addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });


        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", null);
                editor.apply();

                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, ProfilePageActivity.class));
            }
        });

         binding.myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, MyBooksActivity.class));
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

    public void onBookClicked(BookModel bookModel){

        Intent intent = new Intent(getApplicationContext(), BuyBookActivity.class);
        intent.putExtra("BookModel", bookModel);
        startActivity(intent);
    }
}