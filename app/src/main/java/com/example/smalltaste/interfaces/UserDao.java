package com.example.smalltaste.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smalltaste.model.Datum;

import java.util.List;

@androidx.room.Dao
public interface UserDao {

    @Insert
    public void insert(Datum userTable);

    @Query("Select * FROM Datum")
    public List<Datum> getUserData();

    @Query("Select * FROM Datum")
    public LiveData<List<Datum>> getUserDataTotal();

    @Query("select *from Datum  limit:i,:j   ")
    public List<Datum> viewData(int i, int j);


    @Query("delete from Datum where mId=:position")
    void delete(long position);

    @Query("update Datum set mFirstName=:name,MEmail=:email,mLastname=:lastName where mId=:id")
    void update(String name, String email, long id, String lastName);

    @Query("select *from Datum  where mId=:i")
    public Datum particularRetrieve(long i);

    @Query("select *from Datum  where mId=:i")
    public LiveData<Datum> LiveDataParticularRetrieve(long i);


}


