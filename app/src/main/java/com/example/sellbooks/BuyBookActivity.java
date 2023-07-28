package com.example.sellbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sellbooks.databinding.ActivityBuyBookBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class BuyBookActivity extends AppCompatActivity {

    ActivityBuyBookBinding binding;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    FirebaseAuth auth;
    Boolean sold = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BookModel book = (BookModel) getIntent().getSerializableExtra("BookModel");

        binding = ActivityBuyBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        String id = book.getUserid();
        final String[] number = new String[3];

        number[2] = book.getbookname();

        binding.bookname.setText(number[2]);
        binding.bookdesc.setText(book.getbookdesc());
        binding.bookprice.setText("Rs "+book.getbookprice()+"/-");

        if(book.getImgUrl() != null){

            String url = book.getImgUrl();

            storageReference = FirebaseStorage.getInstance().getReference("images/"+url);

            try{

                File tempfile = File.createTempFile("temp", "jpg");
                storageReference.getFile(tempfile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Bitmap bitmap = BitmapFactory.decodeFile(tempfile.getAbsolutePath());
                                binding.imageButton.setImageBitmap(bitmap);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            } catch(Exception e){


            }
        }


        DocumentReference documentReference = firestore.collection("users").document(id);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                String x = "Seller "+ value.getString("user_name");
                binding.sellerName.setText(x);
                binding.sellerEmail.setText(value.getString("user_email"));

                number[0] = value.getString("user_phone");
                number[1] = value.getString("user_name");
            }
        });

        sold = book.getSold();

        if(id.equals(auth.getUid())) {
            binding.sell.setVisibility(View.GONE);
            binding.sold.setVisibility(View.VISIBLE);

            sold = book.getSold();

            if(sold){

                binding.sold.setBackgroundResource(R.drawable.round_btn);
                binding.sold.setText("Sell agian!");
            }
            else{

                binding.sold.setBackgroundResource(R.drawable.disabled_btn);
                binding.sold.setText("Stop selling!");
            }
        }

//        if(book.getSold() == true){
//
//            binding.sold.setText("Mark unsold!");
//            binding.sold.setBackgroundResource(R.drawable.disabled_btn);
//        }
//        else {
//
//            binding.sold.setText("Mark sold!");
//            binding.sold.setBackgroundResource(R.drawable.round_btn);
//        }

        binding.sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://wa.me/" +"+91" +number[0] + "/?text=" + "Hey "+number[1]+"! I saw your book "+ book.getbookname()+ " on Secondhand-Bookstore. I was interested in buying..."));
                    startActivity(intent);
                }
                catch (Exception e){

                }
//                Toast.makeText(BuyBookActivity.this, id, Toast.LENGTH_SHORT).show();
            }
        });

        binding.sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!book.getSold()){

                    firestore.collection("Books").document(book.getBookId()).set(new BookModel(book.getbookname(), book.getbookdesc(), book.getbookprice(), id, book.getImgUrl(), !book.getSold(), book.getBookId()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    book.setSold(true);
                                    Toast.makeText(BuyBookActivity.this, "Book marked sold!", Toast.LENGTH_SHORT).show();
                                    binding.sold.setBackgroundResource(R.drawable.round_btn);
                                    binding.sold.setText("Sell agian!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BuyBookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else{

                    firestore.collection("Books").document(book.getBookId()).set(new BookModel(book.getbookname(), book.getbookdesc(), book.getbookprice(), id, book.getImgUrl(), !book.getSold(), book.getBookId()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    book.setSold(false);
                                    Toast.makeText(BuyBookActivity.this, "Book marked unsold!", Toast.LENGTH_SHORT).show();
                                    binding.sold.setText("Stop selling!");
                                    binding.sold.setBackgroundResource(R.drawable.disabled_btn);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BuyBookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (BuyBookActivity.this, HomeActivity.class));
            }
        });
    }
}