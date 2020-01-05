package com.example.petime.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.petime.PeTimeApp;


@Database(entities = {Comment.class,Post.class}, version = 7)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract CommentDao CommentDao();
    public abstract PostDao PostDao();
}

public class ModelSql {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(PeTimeApp.getContext(),
                    AppLocalDbRepository.class,
                    "database.db")
                    .fallbackToDestructiveMigration()
                    .build();
}