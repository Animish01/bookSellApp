package com.example.sellbooks;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sellbooks.databinding.ActivityAddBookBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookActivity extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ActivityAddBookBinding binding;
    ImageView camera;
    ImageView gallery;
    byte[] bb;

    final int CAMERA_REQUEST = 100;
    final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];

    Uri imagePath;
    Boolean imgSet = false;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//        final View view1 = getActivity().getLayoutInflater().inflate(R.layout.img_popup, null);

        progressDialog = new ProgressDialog(this);

        binding.addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bookname = binding.bookname.getText().toString();
                String bookdesc = binding.bookdesc.getText().toString();
                String bookprice = binding.bookprice.getText().toString();
                Boolean sold = false;

                if(bookname.isEmpty()){

                    Toast.makeText(AddBookActivity.this, "Please add a book name", Toast.LENGTH_SHORT).show();
                }
                else if(!imgSet){

                    Toast.makeText(AddBookActivity.this, "Please add an image", Toast.LENGTH_SHORT).show();
                }
                else if(bookdesc.isEmpty()){

                    Toast.makeText(AddBookActivity.this, "Please add a short description", Toast.LENGTH_SHORT).show();
                }
                else if(bookprice.isEmpty()){

                    Toast.makeText(AddBookActivity.this, "Please add book price", Toast.LENGTH_SHORT).show();
                }
                else{

                    progressDialog.setMessage("Adding to cloud...");
                    progressDialog.show();

                    String url1 = uploadImage();

                    firestore.collection("Books")
                            .document()
                            .set(new BookModel(bookname, bookdesc, bookprice, FirebaseAuth.getInstance().getUid(), url1, sold, null))

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.cancel();
                                    Toast.makeText(AddBookActivity.this, bookname+" added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddBookActivity.this, HomeActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AddBookActivity.this, HomeActivity.class));
            }
        });

        binding.myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AddBookActivity.this, MyBooksActivity.class));
            }
        });


        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", null);
                editor.apply();

                startActivity(new Intent(AddBookActivity.this, MainActivity.class));
                finish();
            }
        });


        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AddBookActivity.this, ProfilePageActivity.class));
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

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                selectImage();

//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popUpView = inflater.inflate(R.layout.img_popup, null);
//
//                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                boolean focusable = true;  // When clicked outside the box, it closes.
//
//                final PopupWindow popup = new PopupWindow(popUpView, width, height, focusable);
//                popup.showAtLocation(view, Gravity.CENTER, 0, 0);

//                LayoutInflater inflater1 = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                inflater.inflate(R.layout.img_popup, true);

                binding.addImg.setVisibility(View.VISIBLE);
                binding.addbook.setVisibility(View.GONE);


                binding.imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        binding.addImg.setVisibility(View.GONE);
                        binding.addbook.setVisibility(View.VISIBLE);
                    }
                });

                binding.galleryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!checkStoragePermission()) requestStoragePermission();
                        else selectImage();
                    }
                });

                binding.camBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!checkCameraPermission()) requestCameraPermission();
                        else selectImage();
                    }
                });
            }
        });


    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission, CAMERA_REQUEST);
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission, STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        selectImage();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        selectImage();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }


    private String uploadImage() {

        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String filename = date.format(now);

        storageReference = FirebaseStorage.getInstance().getReference("images/"+filename);

        if(imagePath != null)
            storageReference.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        else{

            storageReference.putBytes(bb)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return filename;
    }



    private void selectImage() {

        CropImage.activity().start(AddBookActivity.this);
    }

//    private void captureImage() {
//
////        Intent cam = new Intent();
////        cam.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
////
////        try{
////
////            startActivityForResult(cam, 2);
////        }
////        catch(Exception e){
////
////            Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
////        }
//
//        CropImage.activity()
////                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(AddBookActivity.this);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//
//                Uri img = result.getUri();
//                Picasso.with(this).load(img).into(binding.imageButton);
//            } else Toast.makeText(this, "wrong resultCode", Toast.LENGTH_SHORT).show();
            try{

                Uri img = result.getUri();
                Picasso.with(this).load(img).into(binding.imageButton);
            }
            catch (Exception e){

                Log.d("error", e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}