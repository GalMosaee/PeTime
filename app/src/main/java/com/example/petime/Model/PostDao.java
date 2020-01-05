package com.example.petime.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {
    String POSTS_TABLE_NAME = "posts";

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Query("DELETE FROM posts WHERE isActive = 0")
    void deleteInactivePosts();

    @Query("DELETE FROM posts")
    void deleteAllPosts();

    @Delete
    void deletePost(Post post);

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY lastUpdate DESC")
    List<Post> getPostsByUserId(String userId);

    @Query("SELECT * FROM posts ORDER BY lastUpdate DESC")
    List<Post> getAllPosts();

    @Query("SELECT * FROM posts WHERE lastUpdate =(SELECT MAX(lastUpdate) FROM posts)")
    Post getMaxUpdate();

    @Query("SELECT * FROM posts WHERE Id = :postId")
    Post getPost(String postId);
}