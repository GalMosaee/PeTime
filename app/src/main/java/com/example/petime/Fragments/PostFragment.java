package com.example.petime.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.petime.Model.Model;
import com.example.petime.Model.Post;
import com.example.petime.R;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {

    private static final int FILE_PERMISSION = 1;
    private static final int PICK_IMAGE = 1000;

    Uri imageUri;
    ImageView close, image_added;
    TextView post;
    EditText description;

    public PostFragment() {   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        close = view.findViewById(R.id.close);
        image_added = view.findViewById(R.id.image_added);
        post = view.findViewById(R.id.post);
        description = view.findViewById(R.id.description);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions();
                //image_added.setImageURI(imageUri);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null){
                    Model.instance.addPost(new Post(Model.instance.getCurrentUser().getUid(),imageUri.toString(),
                            Model.instance.getCurrentUser().getDisplayName(), description.getText().toString()), new Model.AddPostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            Log.d("TAG","post was created");
                            //myContext.getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    });
                } else{
                    Toast.makeText(getActivity(), "Please enter an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null){
            Uri uri = data.getData();
            if(uri == null){
                return;
            }
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), uri);
                image_added.setImageBitmap(bitmap);
            }catch (FileNotFoundException e) {
                if(e.getMessage()!=null) {
                    Log.d("TAG", e.getMessage());
                }
            } catch (IOException e) {
                if(e.getMessage()!=null) {
                    Log.d("TAG", e.getMessage());
                }
            }
            if(bitmap!=null){
                Log.d("TAG","Bitmap created, saveImage() started.");
                Model.instance.saveImage(bitmap,new Model.SaveImageListener(){
                    @Override
                    public void onComplete(String url) {
                        imageUri = Uri.parse(url);
                        //image_added.setImageURI(imageUri);
                    }
                });
            }else{
                Log.d("TAG","bitmap == null");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case FILE_PERMISSION:{
                if(grantResults.length>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        &&grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d("TAG","Entered onRequestPermissionsResult.");
                    openGallery();
                }else {

                }
                return;
            }
        }
    }

    public void verifyStoragePermissions(){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    FILE_PERMISSION
            );
        } else{
            Log.d("TAG","Entered verifyStoragePermissions.");
            openGallery();
        }
    }

    private void openGallery(){
        Log.d("TAG","Create Bitmap.");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
}
