package com.example.smalltaste.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smalltaste.interfaces.UserDao;

@Database(entities = Datum.class, version = 10)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase instance;

    public abstract UserDao formDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, UserDatabase.class, "userdatabase").fallbackToDestructiveMigration().build();
        return instance;
    }
}




