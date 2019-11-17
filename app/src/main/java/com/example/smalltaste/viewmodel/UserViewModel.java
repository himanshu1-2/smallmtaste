package com.example.smalltaste.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smalltaste.interfaces.UserDao;
import com.example.smalltaste.interfaces.UserDataGetter;
import com.example.smalltaste.model.Datum;
import com.example.smalltaste.model.Repository;
import com.example.smalltaste.model.UserDatabase;


public class UserViewModel extends AndroidViewModel {
    private UserDao userDao;
    UserDataGetter userDataGetter;
    private Repository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.formDao();


    }

    public void init(Repository.AsyncComplete AsyncComplete) {

        for (int i = 1; i < 3; i++)
            repository.getPage(i, AsyncComplete);
    }


    public void update(long id, String name, String lastName, String email, Repository.AsyncComplete addAsyncComplete) {
        repository.update(id, name, lastName, email, addAsyncComplete);
    }

    public void addUser(String imageUrl, String name, String lastName, String email, Repository.AsyncComplete addAsyncComplete) {
        Datum datum;
        if (!imageUrl.equals("")) {
            datum = new Datum(imageUrl, name, lastName, email);
        } else {
            datum = new Datum("n", name, lastName, email);
        }
        Log.d("d", "addUser: " + (imageUrl));
        repository.addUser(datum, addAsyncComplete);


    }


    public LiveData<Datum> retrieveParticularId(long id) {
        return userDao.LiveDataParticularRetrieve(id);
    }

    public void deleteUser(long id, Repository.AsyncComplete asyncComplete) {
        if (id != 0) {

            repository.deleteUser(id, asyncComplete);
        }

    }

}


