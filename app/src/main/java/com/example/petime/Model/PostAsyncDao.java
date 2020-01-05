package com.example.petime.Model;

import android.os.AsyncTask;

import java.util.List;

public class PostAsyncDao {

    public static void getAllPosts(final Model.GetAllPostsListener listener) {
        new AsyncTask<String, String, List<Post>>() {
            @Override
            protected List<Post> doInBackground(String... strings) {
                List<Post> list = ModelSql.db.PostDao().getAllPosts();
                return list;
            }

            @Override
            protected void onPostExecute(List<Post> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute();
    }

    public static void getPostsByUserId(final Model.GetAllPostsListener listener,String ... strings) {
        new AsyncTask<String, String, List<Post>>() {
            @Override
            protected List<Post> doInBackground(String... strings) {
                List<Post> list = ModelSql.db.PostDao().getPostsByUserId(strings[0]);
                return list;
            }

            @Override
            protected void onPostExecute(List<Post> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute(strings);
    }

    public static void addPost(final Model.AddPostListener listener, Post... posts) {
        new AsyncTask<Post, String, String>() {
            @Override
            protected String doInBackground(Post... posts) {
                ModelSql.db.PostDao().insertAll(posts);
                return "";
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(true);
                }
            }
        }.execute(posts);
    }
    public static void addPostsAndGetPostList(final Model.GetAllPostsListener listener, final Post ...posts) {
        new AsyncTask<Post, String, List<Post>>() {
            @Override
            protected List<Post> doInBackground(Post... posts) {
                ModelSql.db.PostDao().insertAll(posts);
                //List<Post> list =ModelSql.db.PostDao().getPostsByUserId(posts[0].getUserId());
                List<Post> list = ModelSql.db.PostDao().getAllPosts();
                return list;
            }

            @Override
            protected void onPostExecute(List<Post> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute(posts);
    }
    public static void deleteInactivePosts(final Model.DeletePostListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                ModelSql.db.PostDao().deleteInactivePosts();
                return "";
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(true);
                }
            }
        }.execute();
    }

    public static void getMaxUpdate(final Model.GetPostListener listener) {
        new AsyncTask<String, String, Post>() {
            @Override
            protected Post doInBackground(String... strings) {
                Post post = ModelSql.db.PostDao().getMaxUpdate();
                return post;
            }

            @Override
            protected void onPostExecute(Post data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute();
    }

    public static void getPost(final Model.GetPostListener listener, final String string) {
        new AsyncTask<String, String, Post>() {
            @Override
            protected Post doInBackground(String ... strings) {
                Post post = ModelSql.db.PostDao().getPost(strings[0]);
                return post;
            }

            @Override
            protected void onPostExecute(Post data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute(string);
    }
}