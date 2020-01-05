package com.example.petime.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.petime.PeTimeApp;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Model {
    final public static Model instance = new Model();

    ModelFirebase modelFirebase;
    ModelFirebaseAuth modelFirebaseAuth;
    SharedPreferences sharedPreferences;

    private Model() {
        modelFirebase = new ModelFirebase();
        modelFirebaseAuth = new ModelFirebaseAuth();
        sharedPreferences = PeTimeApp.getContext().getSharedPreferences("com.example.petime.preferences", Context.MODE_PRIVATE);
    }

    public long getLastUpdatePost() {
        return sharedPreferences.getLong("LAST_UPDATE_POST", -1);
    }

    public void setLastUpdatePost(Long lastUpdate) {
            sharedPreferences.edit().putLong("LAST_UPDATE_POST", lastUpdate).apply();
    }

    public interface SignInListener{
        void onComplete(boolean status,Exception e);
    }

    public void signIn(String email,String password, SignInListener listener){
        modelFirebaseAuth.signIn(email,password,listener);
    }

    public interface SignUpListener{
        void onComplete(boolean status,Exception e);
    }

    public void signUp(String email, String displayname,String password, SignUpListener listener){
        modelFirebaseAuth.signUp(email,displayname,password,listener);
    }

    public interface UpdateProfileListener{
        void onComplete(boolean status,Exception e);
    }

    public interface OnSignOutCompleteListener {
        void onSignOutComplete();
    }

    public FirebaseUser getCurrentUser(){
        return modelFirebaseAuth.getCurrentUser();
    }

    public void signOut(){
        modelFirebaseAuth.signOut();
    }

    public interface GetAllPostsListener{
        void onComplete(List<Post> data);
    }
    public void getAllPosts(final GetAllPostsListener listener) {
        Log.d("TAG","getAllPosts: Start.");
        PostAsyncDao.getAllPosts(new GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> localData) {
                Log.d("TAG","getAllPosts: Post last update: "+ getLastUpdatePost());
                Log.d("TAG","getAllPosts: LocalDB first fetching.");
                listener.onComplete(localData);
                Log.d("TAG","getAllPosts: Localdb returned by fetching: " + localData.size());
                modelFirebase.getAllPosts(new GetAllPostsListener() {
                    @Override
                    public void onComplete(List<Post> remoteData) {
                        if((remoteData.size()>0)&&(remoteData.get(0).getLastUpdate()!=null)){
                            setLastUpdatePost(remoteData.get(0).getLastUpdate().toDate().getTime());
                            Log.d("TAG","getAllPosts: Post last update: "+ getLastUpdatePost());
                        }
                        Log.d("TAG","getAllPosts: Fetch from Firestore.");
                        Log.d("TAG","getAllPosts: RemoteDB returned by fetching: " + remoteData.size());
                        PostAsyncDao.addPost(new AddPostListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if(success) {
                                    Log.d("TAG","getAllPosts: Add new posts to LocalDB.");
                                    PostAsyncDao.deleteInactivePosts(new DeletePostListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            Log.d("TAG","getAllPosts: Delete inactive posts from LocalDB.");
                                                    PostAsyncDao.getAllPosts(new GetAllPostsListener() {
                                                        @Override
                                                        public void onComplete(List<Post> updatedData) {
                                                         listener.onComplete(updatedData);
                                                        Log.d("TAG","getAllPosts: Localdb returned by fetching: " + updatedData.size());
                                                    }
                                             });
                                        }
                                    });
                                }
                            }

                        },remoteData.toArray(new Post[remoteData.size()]));
                    }
                });
            }
        });
    }

    public void getPostsByUserId(final String userId, final GetAllPostsListener listener) {
        Log.d("TAG","getPostsByUserId: Start.");
        PostAsyncDao.getPostsByUserId(new GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> localData) {
                Log.d("TAG","getPostsByUserId: LocalDB first fetching.");
                listener.onComplete(localData);
                Log.d("TAG","getPostsByUserId: Localdb returned by fetching: " + localData.size());
                //get data from cloud.
                modelFirebase.getPostsByUserId(userId,new GetAllPostsListener() {
                    @Override
                    public void onComplete(List<Post> remoteData) {
                        Log.d("TAG","getPostsByUserId: Fetch from Firestore.");
                        Log.d("TAG","getPostsByUserId: RemoteDB returned by fetching: " + remoteData.size());
                        PostAsyncDao.addPost(new AddPostListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if(success) {
                                    Log.d("TAG","getPostsByUserId: Add new posts to LocalDB.");
                                    PostAsyncDao.deleteInactivePosts(new DeletePostListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            Log.d("TAG","getPostsByUserId: Delete inactive posts from LocalDB.");
                                            PostAsyncDao.getPostsByUserId(new GetAllPostsListener() {
                                                @Override
                                                public void onComplete(List<Post> updatedData) {
                                                    Log.d("TAG","getPostsByUserId: LocalDB last fetching. ");
                                                    listener.onComplete(updatedData);
                                                    Log.d("TAG","getPostsByUserId: Localdb returned by fetching: " + updatedData.size());
                                                }
                                            },userId);
                                        }
                                    });
                                }
                            }

                        },remoteData.toArray(new Post[remoteData.size()]));
                    }
                });
            }
        },userId);
    }

    public interface GetPostListener {
        void onComplete(Post post);
    }

    public void getPost(final String PostId,final GetPostListener listener){
        Log.d("TAG","getPost: Start.");
        PostAsyncDao.getPost(new GetPostListener() {
            @Override
            public void onComplete(Post localData) {
                Log.d("TAG","getPost: LocalDB first fetching.");
                if(localData!=null){
                    listener.onComplete(localData);
                }
                modelFirebase.getPost(PostId,new GetPostListener() {
                    @Override
                    public void onComplete(Post remoteData) {
                        Log.d("TAG","getPost: Fetch from Firestore.");
                        PostAsyncDao.addPost(new AddPostListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if(success) {
                                    Log.d("TAG","getPost: Add new post to LocalDB.");
                                    PostAsyncDao.deleteInactivePosts(new DeletePostListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            Log.d("TAG","getPost: Delete inactive posts from LocalDB.");
                                            PostAsyncDao.getPost(new GetPostListener() {
                                                @Override
                                                public void onComplete(Post updatedData) {
                                                    Log.d("TAG","getPost: LocalDB last fetching. ");
                                                    listener.onComplete(updatedData);
                                                }
                                            },PostId);
                                        }
                                    });
                                }
                            }

                        },remoteData);
                    }
                });
            }
        },PostId);
    }

    public interface AddPostListener{
        void onComplete(boolean success);
    }

    public void addPost(Post post, AddPostListener listener) {
        modelFirebase.addPost(post, listener);
    }

    public interface EditPostListener{
        void onComplete(boolean success);
    }

    public void editPost(String postId,String text,EditPostListener listener) {
        modelFirebase.editPost(postId,text,listener);
    }

    public interface DeletePostListener{
        void onComplete(boolean success);
    }

    public void deletePost(String postId,DeletePostListener listener) {
        modelFirebase.deletePost(postId,listener);
    }

    public interface GetAllCommentsListener{
        void onComplete(List<Comment> data);
    }

    public void getAllComments(final GetAllCommentsListener listener) {
        Log.d("TAG","GetAllComment: Start.");
        CommentAsyncDao.getAllComments(new GetAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> localData) {
                Log.d("TAG","GetAllComment: LocalDB first fetching.");
                listener.onComplete(localData);
                Log.d("TAG","GetAllComment: Localdb returned before fetching: " + localData.size());
                //get data from cloud.
                modelFirebase.getAllComments(new GetAllCommentsListener() {
                    @Override
                    public void onComplete(List<Comment> remoteData) {
                        Log.d("TAG", "GetAllComment: Fetch from Firestore.");
                        Log.d("TAG","GetAllComment: RemoteDB returned by fetching: " + remoteData.size());
                        CommentAsyncDao.addComment(new AddCommentListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if(success) {
                                    Log.d("TAG","GetAllComment: Add new Comments to LocalDB.");
                                    CommentAsyncDao.deleteInactiveComments(new DeleteCommentListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            CommentAsyncDao.getMaxUpdate(new GetCommentListener() {
                                                @Override
                                                public void onComplete(Comment comment) {
                                                    CommentAsyncDao.getAllComments(new GetAllCommentsListener() {
                                                        @Override
                                                        public void onComplete(List<Comment> updatedData) {
                                                            Log.d("TAG","GetAllComment: LocalDB last fetching. ");
                                                            listener.onComplete(updatedData);
                                                            Log.d("TAG","GetAllComment: Localdb returned after fetching: " + updatedData.size());
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                        },remoteData.toArray(new Comment[remoteData.size()]));
                    }
                });
            }
        });
    }

    public void getCommentsByPostId(final String postId,final GetAllCommentsListener listener) {
        Log.d("TAG","getCommentsByPostId: Start.");
        CommentAsyncDao.getCommentsByPostId(new GetAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> localData) {
                Log.d("TAG","getCommentsByPostId: LocalDB first fetching.");
                listener.onComplete(localData);
                Log.d("TAG","getCommentsByPostId: Localdb returned by fetching: " + localData.size());
                //get data from cloud.
                modelFirebase.getCommentsByPostId(postId,new GetAllCommentsListener() {
                    @Override
                    public void onComplete(List<Comment> remoteData) {
                        Log.d("TAG","getCommentsByPostId: Fetch from Firestore.");
                        Log.d("TAG","getCommentsByPostId: RemoteDB returned by fetching: " + remoteData.size());
                        CommentAsyncDao.addComment(new AddCommentListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if(success) {
                                    Log.d("TAG","getCommentsByPostId: Add new Comments to LocalDB.");
                                    CommentAsyncDao.deleteInactiveComments(new DeleteCommentListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            Log.d("TAG","getCommentsByPostId: Delete inactive Comments from LocalDB.");
                                            CommentAsyncDao.getCommentsByPostId(new GetAllCommentsListener() {
                                                @Override
                                                public void onComplete(List<Comment> updatedData) {
                                                    Log.d("TAG","getCommentsByPostId: LocalDB last fetching. ");
                                                    listener.onComplete(updatedData);
                                                    Log.d("TAG","getCommentsByPostId: Localdb returned by fetching: " + updatedData.size());
                                                }
                                            },postId);
                                        }
                                    });
                                }
                            }

                        },remoteData.toArray(new Comment[remoteData.size()]));
                    }
                });
            }
        },postId);
    }

    interface GetCommentListener {
        void onComplete(Comment comment);
    }

    public interface AddCommentListener{
        void onComplete(boolean success);
    }
    public void addComment(Comment comment, AddCommentListener listener) {
        modelFirebase.addComment(comment, listener);
    }

    public interface DeleteCommentListener{
        void onComplete(boolean success);
    }

    public void deleteComment(String commentId,DeleteCommentListener listener) {
        modelFirebase.deleteComment(commentId,listener);
    }

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, listener);
    }
}