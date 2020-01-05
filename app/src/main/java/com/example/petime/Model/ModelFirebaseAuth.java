package com.example.petime.Model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ModelFirebaseAuth {
    FirebaseAuth modelAuth;

    public ModelFirebaseAuth(){
        modelAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return modelAuth.getCurrentUser();
    }

    public void signIn(String email, String password, final Model.SignInListener listener){
        modelAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //listener.onComplete(task);
                            listener.onComplete(true,null);
                        }
                        else {
                            listener.onComplete(false,task.getException());
                        }
                    }
                });
    }

    public void signUp(String email,String displayname, String password, final Model.SignUpListener listener){
        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname)
                .build();
        modelAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(profileUpdates!=null)
                            modelAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User profile updated.");
                                                listener.onComplete(true, null);
                                            }else {
                                                Log.d("TAG", "User profile update failed.");
                                                listener.onComplete(false, task.getException());
                                            }
                                        }
                                    });
                        }
                        else {
                            Log.d("TAG", "User signup failed.");
                            listener.onComplete(false,task.getException());
                        }
                    }
                });
    }

    public void updateProfile(String displayname, String photo, final Model.UpdateProfileListener listener){
        UserProfileChangeRequest profileUpdates = null;
        if(displayname != null && photo != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname)
                    .setPhotoUri(Uri.parse(photo))
                    .build();
        }
        else if(displayname != null){
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname)
                    .build();
        }
        else if(photo != null){
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(photo))
                    .build();
        }
        if(profileUpdates != null) {
            modelAuth.getCurrentUser().updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User profile updated.");
                                listener.onComplete(true, null);
                            }
                            if (!task.isSuccessful()) {
                                Log.d("TAG", "User profile update failed.");
                                listener.onComplete(false, task.getException());
                            }
                        }
                    });
        }else{
            listener.onComplete(false,null);
        }

    }
    public void signOut(){
        modelAuth.signOut();
    }
}
