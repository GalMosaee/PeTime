package com.example.petime.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.Nullable;

//import android.support.annotation.NonNull;

public class ModelFirebase {

    FirebaseFirestore db;

    public ModelFirebase() {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
    }

    public void getAllPosts(final Model.GetAllPostsListener listener) {
        Timestamp lastUpdateTimestamp = new Timestamp(new Date(Model.instance.getLastUpdatePost()));
        db.collection("posts").whereGreaterThanOrEqualTo("lastUpdate",lastUpdateTimestamp)
                .orderBy("lastUpdate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Post> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    Log.d("TAG","postId: " + post);
                    data.add(post);
                }
                listener.onComplete(data);
            }
        });
    }

    public void getPostsByUserId(String userId, final Model.GetAllPostsListener listener) {
        db.collection("posts").whereEqualTo("userId", userId)
                .orderBy("lastUpdate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Post> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    data.add(post);
                }
                listener.onComplete(data);
            }
        });
    }

    public void getPost(String id, final Model.GetPostListener listener) {
        db.collection("posts").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Post post = snapshot.toObject(Post.class);
                            listener.onComplete(post);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void addPost(Post post, final Model.AddPostListener listener) {
        Log.d("TAG","ModelFirebaseAddPost: Start.");
        final DocumentReference newRef = db.collection("posts").document();
        Map<String,Object> newPost = new HashMap<String,Object>();
        newPost.put("id",newRef.getId());
        newPost.put("userId",post.getUserId());
        newPost.put("image",post.getImage());
        newPost.put("userName",post.getUserName());
        newPost.put("text",post.getText());
        newPost.put("lastUpdate",FieldValue.serverTimestamp());
        //newPost.put("isActive",true);
        newPost.put("isActive",1);
        db.collection("posts").document(newRef.getId())
                .set(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("TAG","ModelFirebaseAddPost: "+newRef.getId()+" Created in Firestore.");
                }else{
                    Log.d("TAG","ModelFirebaseAddPost: "+newRef.getId()+" Creation in Firestore failed.");
                }
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void editPost(final String postId,String text ,final Model.EditPostListener listener) {
        DocumentReference deletedRef = db.collection("posts").document(postId);
        deletedRef.update("text",text,"lastUpdate", FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("TAG", "ModelFirebaseEditPost: "+postId+" Edited successfully.");
                        } else{
                            Log.d("TAG", "ModelFirebaseEditPost: "+postId+" Edited failed.");
                        }
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void deletePost(final String postId, final Model.DeletePostListener listener) {
        db.collection("comments").whereEqualTo("postId",postId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deleteComment(document.getId(), new Model.DeleteCommentListener() {
                                    @Override
                                    public void onComplete(boolean success) {
                                        if(!success)
                                        {
                                            Log.d("TAG", "Some of comment deletion failed.");
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        DocumentReference deletedRef = db.collection("posts").document(postId);
        deletedRef.update("isActive",0,"lastUpdate", FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("TAG", "ModelFirebaseDeletePost: "+postId+" deleted successfully.");
                        } else{
                            Log.d("TAG", "ModelFirebaseDeletePost: "+postId+" deletion failed.");
                        }
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void getAllComments(final Model.GetAllCommentsListener listener) {
        db.collection("comments").orderBy("lastUpdate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Comment> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Comment comment = doc.toObject(Comment.class);
                    data.add(comment);
                }
                listener.onComplete(data);
            }
        });
    }

    public void getCommentsByPostId(String postId, final Model.GetAllCommentsListener listener) {
        db.collection("comments").whereEqualTo("postId", postId)
                .orderBy("lastUpdate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<Comment> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Comment comment = doc.toObject(Comment.class);
                    data.add(comment);
                }
                listener.onComplete(data);
            }
        });
    }

    public void getComment(String id, final Model.GetCommentListener listener) {
        db.collection("comments").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Comment comment = snapshot.toObject(Comment.class);
                            listener.onComplete(comment);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void addComment(Comment comment, final Model.AddCommentListener listener) {
        Log.d("TAG","ModelFirebaseAddComment: Start.");
        final DocumentReference newRef = db.collection("comments").document();
        Map<String,Object> newComment = new HashMap<String,Object>();
        newComment.put("id",newRef.getId());
        newComment.put("postId",comment.getPostId());
        newComment.put("userId",comment.getUserId());
        newComment.put("userName",comment.getUserName());
        newComment.put("text",comment.getText());
        newComment.put("lastUpdate",FieldValue.serverTimestamp());
        newComment.put("isActive",1);
        db.collection("comments").document(newRef.getId())
                .set(newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("TAG","ModelFirebaseAddComment: "+newRef.getId()+" Created in Firestore.");
                }else{
                    Log.d("TAG","ModelFirebaseAddComment: "+newRef.getId()+" Creation in Firestore failed.");
                }
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void deleteComment(final String commentId, final Model.DeleteCommentListener listener) {
        DocumentReference deletedRef = db.collection("comments").document(commentId);
        deletedRef.update("isActive",0,"lastUpdate", FieldValue.serverTimestamp())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("TAG", "ModelFirebaseDeleteComment: "+commentId+" deleted successfully.");
                        } else{
                            Log.d("TAG", "ModelFirebaseDeleteComment: "+commentId+" deletion failed.");
                        }
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }


    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        Log.d("TAG","Entered saveImage().");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Date d = new Date();
        // Create a reference to "mountains.jpg"
        final StorageReference imageStorageRef = storageRef.child("image_" + d.getTime() + ".jpg");
        Log.d("TAG","Compressing.");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        Log.d("TAG","UploadTask start.");
        UploadTask uploadTask = imageStorageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("TAG","Upload successfully completed.");
                    Log.d("TAG","Image URL: " + downloadUri.toString());
                    listener.onComplete(downloadUri.toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }
}
