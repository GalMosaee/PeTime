package com.example.petime.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentDao {
    String COMMENTS_TABLE_NAME = "comments";

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Comment... comments);

    @Query("DELETE FROM comments WHERE isActive = 0")
    void deleteInactiveComments();

    @Query("DELETE FROM comments")
    void deleteAllComments();

    @Delete
    void deleteComment(Comment comment);

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY lastUpdate DESC")
    List<Comment> getCommentsByPostId(String postId);

    @Query("SELECT * FROM comments ORDER BY lastUpdate DESC")
    List<Comment> getAllComments();

    @Query("SELECT * FROM comments WHERE lastUpdate =(SELECT MAX(lastUpdate) FROM comments)")
    Comment getMaxUpdate();
}