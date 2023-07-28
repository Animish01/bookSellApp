package com.example.sellbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.myviewholder>{

    ArrayList<BookModel> books;
    private BookClickInterface listelClick;

    StorageReference storageReference;

    public BooksAdapter(ArrayList<BookModel> books, BookClickInterface listelClick) {
        this.books = books;
        this.listelClick = listelClick;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row_home, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        holder.row_title.setText(books.get(position).getbookname());
        holder.row_desc.setText(books.get(position).getbookdesc());
        holder.row_price.setText("Rs. "+books.get(position).getbookprice());

        if(books.get(position).getImgUrl() != null){

            String imageID = books.get(position).getImgUrl();

            storageReference = FirebaseStorage.getInstance().getReference("images/"+imageID);

            try{

                File localfile = File.createTempFile("tempfile", "jpg");
                storageReference.getFile(localfile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                holder.img_area.setImageBitmap(bitmap);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }catch (Exception e){


            }
        }

        int x = position;

        holder.book_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listelClick.onBookClicked(books.get(x));
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView row_title, row_desc, row_price;
        CardView book_click;
        ImageView img_area;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            row_title = itemView.findViewById(R.id.row_title);
            row_desc = itemView.findViewById(R.id.row_desc);
            row_price = itemView.findViewById(R.id.row_price);
            book_click = itemView.findViewById(R.id.cardView);
            img_area = itemView.findViewById(R.id.imageView2);
        }
    }
}
