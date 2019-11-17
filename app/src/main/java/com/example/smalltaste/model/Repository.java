package com.example.smalltaste.model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.smalltaste.interfaces.UserDao;
import com.example.smalltaste.interfaces.UserDataGetter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private UserDao userDao;
    private UserDataGetter userDataGetter;

    public Repository(Application application) {
        userDataGetter = RetrofitRespone.createService(UserDataGetter.class);
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.formDao();


    }


    public void getPage(int page, final AsyncComplete addAsyncComplete) {
        Log.d("dfs", "getPage: ");
        userDataGetter.getUserData(page).enqueue(new Callback<Model>() {

            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.body() != null)
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        Datum datum = new Datum(response.body().getData().get(i).getAvatar(), response.body().getData().get(i).getFirstName(), response.body().getData().get(i).getLastName(), response.body().getData().get(i).getEmail());
                        new InsertAsyncTask(response.body().getData().size(), addAsyncComplete, response.body().getTotalPages()).execute(datum);
                        Log.d("ds", "onResponse: " + response.body().getData().get(i).getLastName());
                    }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {             //show toast "User List could not be loaded"
            }
        });
    }

    public void addUser(final Datum datum, final AsyncComplete checkx) {
        userDataGetter.addUserData(1, datum).enqueue(new Callback<Datum>() {
            @Override
            public void onResponse(Call<Datum> call, Response<Datum> response) {
                new AddAsyncTask(checkx).execute(datum);
            }

            @Override
            public void onFailure(Call<Datum> call, Throwable t) {

            }
        });
    }

    public void update(final long id, final String name, final String lastName, final String email, final AsyncComplete addAsyncComplete) {


        userDataGetter.updateUserData(11, new Datum(name, lastName, email, "")).enqueue(new Callback<Datum>() {
            @Override
            public void onResponse(Call<Datum> call, Response<Datum> response) {
                if (response.isSuccessful())
                    new UpdateAsync(name, email, id, lastName, addAsyncComplete).execute();
            }

            @Override
            public void onFailure(Call<Datum> call, Throwable t) {

            }
        });


    }

    public void deleteUser(final Long id, AsyncComplete asyncComplete) {

        Log.d("asd", "deleteUser: " + id);


        new DeleteUserAsync(id, asyncComplete).execute();


    }


    public class InsertAsyncTask extends AsyncTask<Datum, Void, List<Datum>> {
        private int listSize;
        AsyncComplete addAsyncComplete;
        long totalnoPages;


        InsertAsyncTask(int listSize, AsyncComplete addAsyncComplete, Long totalPages) {
            this.listSize = listSize;

            this.totalnoPages = totalPages;
            this.addAsyncComplete = addAsyncComplete;
        }

        @Override
        protected List<Datum> doInBackground(Datum... userTables) {
            if (userDao.getUserData().size() != listSize * totalnoPages) {
                Log.d("nbv", "doInBackground: " + userDao.getUserData().size());
                userDao.insert(userTables[0]);
            }
            return userDao.getUserData();
        }


        @Override
        protected void onPostExecute(List<Datum> data) {
            super.onPostExecute(data);
            if (listSize * totalnoPages == data.size()) {
                addAsyncComplete.check(true);
                Log.d("size check", "onPostExecute: " + data.size());
            }
        }
    }


    public class AddAsyncTask extends AsyncTask<Datum, Void, Void> {
        AsyncComplete addAsyncComplete;


        AddAsyncTask(AsyncComplete addAsyncComplete) {
            this.addAsyncComplete = addAsyncComplete;

        }

        @Override
        protected Void doInBackground(Datum... userTables) {
            userDao.insert(userTables[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            addAsyncComplete.check(true);

        }
    }


    public class UpdateAsync extends AsyncTask<Void, Void, Void> {
        String name;
        String email;
        AsyncComplete addAsyncComplete;
        String lastName;

        long id;

        UpdateAsync(String name, String email, long id, String lastName, AsyncComplete addAsyncComplete) {
            this.email = email;
            this.name = name;
            this.id = id;
            this.addAsyncComplete = addAsyncComplete;
            this.lastName = lastName;


        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.update(name, email, id, lastName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            addAsyncComplete.check(true);

        }
    }

    public class DeleteUserAsync extends AsyncTask<Void, Void, String> {

        long position;
        AsyncComplete asyncComplete;


        DeleteUserAsync(long position, AsyncComplete asyncComplete) {
            this.position = position;
            this.asyncComplete = asyncComplete;
        }

        @Override
        protected String doInBackground(Void... voids) {
            userDao.delete(position);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            asyncComplete.check(true);

        }
    }

    public interface AsyncComplete {
        void check(boolean flag);
    }

}





