package com.example.petime.Model;

import android.os.AsyncTask;

import java.util.List;

public class CommentAsyncDao {

    public static void getAllComments(final Model.GetAllCommentsListener listener) {
        new AsyncTask<String, String, List<Comment>>() {
            @Override
            protected List<Comment> doInBackground(String... strings) {
                List<Comment> list = ModelSql.db.CommentDao().getAllComments();
                return list;
            }

            @Override
            protected void onPostExecute(List<Comment> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute();
    }

    public static void getCommentsByPostId(final Model.GetAllCommentsListener listener,String ... strings) {
        new AsyncTask<String, String, List<Comment>>() {
            @Override
            protected List<Comment> doInBackground(String... strings) {
                List<Comment> list = ModelSql.db.CommentDao().getCommentsByPostId(strings[0]);
                return list;
            }

            @Override
            protected void onPostExecute(List<Comment> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute(strings);
    }

    public static void addComment(final Model.AddCommentListener listener, Comment... comments) {
        new AsyncTask<Comment, String, String>() {
            @Override
            protected String doInBackground(Comment... comments) {
                ModelSql.db.CommentDao().insertAll(comments);
                return "";
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(true);
                }
            }
        }.execute(comments);
    }

    public static void addCommentsAndGetPostList(final Model.GetAllCommentsListener listener, final Comment ...comments) {
        new AsyncTask<Comment, String, List<Comment>>() {
            @Override
            protected List<Comment> doInBackground(Comment... comments) {
                ModelSql.db.CommentDao().insertAll(comments);
                List<Comment> list = ModelSql.db.CommentDao().getAllComments();
                return list;
            }

            @Override
            protected void onPostExecute(List<Comment> data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute(comments);
    }
    public static void deleteInactiveComments(final Model.DeleteCommentListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                ModelSql.db.CommentDao().deleteInactiveComments();
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

    public static void getMaxUpdate(final Model.GetCommentListener listener) {
        new AsyncTask<String, String, Comment>() {
            @Override
            protected Comment doInBackground(String... strings) {
                Comment comment = ModelSql.db.CommentDao().getMaxUpdate();
                return comment;
            }

            @Override
            protected void onPostExecute(Comment data) {
                super.onPostExecute(data);
                if (listener != null) {
                    listener.onComplete(data);
                }
            }
        }.execute();
    }
}