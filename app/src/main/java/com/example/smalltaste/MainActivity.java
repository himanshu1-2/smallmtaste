package com.example.smalltaste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltaste.adapter.UserRVAdapter;
import com.example.smalltaste.interfaces.UserDao;
import com.example.smalltaste.model.Datum;
import com.example.smalltaste.model.Repository;
import com.example.smalltaste.model.UserDatabase;
import com.example.smalltaste.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Repository.AsyncComplete {
    RecyclerView rvUserList;
    boolean flagAsyncInComplete;
    LinearLayoutManager linearLayoutManager;
    UserViewModel userViewModel;
    List<Datum> userList;
    ImageView ivAddUser;
    Boolean flagFirstTimeApplaunch;
    UserRVAdapter userRVAdapter;
    final String userdata = "userdata";
    final String firstTimeApplaunch = "FirstTimeApplaunch";
    long id;
    int count;
    int positiondel;
    int position;
    boolean updateFlag;
    UserDao userDao;
    List<Datum> dataList;
    SharedPreferences sp;
    ProgressBar pbLoadBar;
    int currentItems;
    boolean loadtillend;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = new ArrayList<>();
        dataList = new ArrayList<>();
        UserDatabase userDatabase = UserDatabase.getInstance(this);
        userDao = userDatabase.formDao();
        pbLoadBar = findViewById(R.id.pb_activity_main_user_loadbar);
        rvUserList = findViewById(R.id.rv_activity_main_user_list);
        linearLayoutManager = new LinearLayoutManager(this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        rvUserList.setLayoutManager(linearLayoutManager);
        sp = getSharedPreferences(userdata, MODE_PRIVATE);
        editor = sp.edit();
        ivAddUser = findViewById(R.id.iv_activity_main_add_user);
        ivAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putInt("cm", ++count);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);

                startActivityForResult(intent, 1);
            }
        });

        if (!sp.getBoolean(firstTimeApplaunch, false)) {
            Log.d("sharedpredg", "onCreate: ");
            flagFirstTimeApplaunch = true;
            editor.putBoolean(firstTimeApplaunch, flagFirstTimeApplaunch);
            editor.apply();
            userViewModel.init(this);
        } else {
            new RetrieveAsync(0).execute(false);
        }

/*   userViewModel.getRespository(0, 50).observe(this, new Observer<List<Datum>>() {
            @Override
            public void onChanged(final List<Datum> data) {
                userList = data;
                Log.d("ds", "onChanged: " + data.size());
                userRVAdapter.setNote(userList);
                pbLoadBar.setVisibility(View.GONE);
                tempList=data;
                rvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        totalItems = linearLayoutManager.getItemCount();
                        if (data.size() >= 5)
                            ;
                        // fetchData(userList.size());
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

            }
        });

*/


        currentItems = linearLayoutManager.getChildCount();
        rvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!flagAsyncInComplete)
                        if (dataList.size() >= 5) {
                            Log.d("dadaas", "onScrollStateChanged: " + dataList.size());
                            new RetrieveAsync(userList.size()).execute(false);
                        }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {

                //userList.add((Datum) data.getSerializableExtra(getString(R.string.userData)));
                Log.d("sdd", "onActivityResult: " + data.getBooleanExtra(getString(R.string.addflag), false));
                new RetrieveAsync(userList.size()).execute(false);

            } else if (data.getBooleanExtra(getString(R.string.deleteflag), false)) {
                if (resultCode == RESULT_OK) {
                    userList.remove(data.getIntExtra(getString(R.string.positionflag), 0));
                    userRVAdapter.notifyDataSetChanged();
                    Log.d("detle", "onActivityResult: " + data.getBooleanExtra(getString(R.string.deleteflag), false));
                }
            } else if (data.getBooleanExtra(getString(R.string.update), false) || data.getBooleanExtra(getString(R.string.updateflag), false)) {
                if (resultCode == RESULT_OK) {
                    Log.d("ds", "onActivityResult: " + data.getLongExtra(getString(R.string.userDataid), 0));
                    id = data.getLongExtra(getString(R.string.userDataid), 0);
                    position = data.getIntExtra(getString(R.string.positionflags), 0);
                    Log.d("updatecheck", "onActivityResult: " + updateFlag + position + id + data.getBooleanExtra(getString(R.string.updateflags), false));
                    if (id != 0)
                        new RetrieveAsync(0).execute(true);
                }
            }
        }
    }

    @Override
    public void check(boolean flag) {
        new RetrieveAsync(0).execute(false);
    }

    public class RetrieveAsync extends AsyncTask<Boolean, Void, Void> implements UserRVAdapter.OnCardListener, UserRVAdapter.OnButtonClick, Repository.AsyncComplete {
        int size;

        RetrieveAsync(int size) {
            this.size = size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            flagAsyncInComplete = true;
            pbLoadBar.setVisibility(View.VISIBLE);
            if (userRVAdapter == null) {
                userRVAdapter = new UserRVAdapter(getBaseContext(), this, this);
                rvUserList.setAdapter(userRVAdapter);
            }

        }


        @Override
        protected Void doInBackground(Boolean... booleans) {
            try {
                Thread.sleep(1000);
                if (!booleans[0])
                    for (int i = 0; i < userDao.viewData(size, 5).size(); i++) {
                        Log.d("sd", "onPostExecute: " + userDao.viewData(size, 5).get(i).getId());
                        userList.add(userDao.viewData(size, 5).get(i));
                        Log.d("add normal cjheck", "doInBackground: " + userDao.viewData(size, 5).get(i).getFirstName());
                    }

                else {
                    Log.d("updatecheck", "doInBackground: " + userDao.particularRetrieve(id).getFirstName());
                    userList.set(position, userDao.particularRetrieve(id));

                }

                dataList = userDao.viewData(size, 5);
                Log.d("datalistcheck", "doInBackground: " + dataList.size());
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            userRVAdapter.setNote(userList);
            flagAsyncInComplete = false;
            pbLoadBar.setVisibility(View.GONE);


        }

        @Override
        public void onCardClick(int position) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(getString(R.string.userDataid), userList.get(position).getId());
            Log.d("s", "onCardClick: " + userList.get(position).getId());
            intent.putExtra(getString(R.string.position), position);
            startActivityForResult(intent, 2);
        }


        @Override
        public void onUpdateClick(int position) {
            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
            intent.putExtra(getString(R.string.userDataid), userList.get(position).getId());
            intent.putExtra(getString(R.string.position), position);
            startActivityForResult(intent, 3);
        }

        @Override
        public void onDeleteClick(final int position) {
            Log.d("a", "onDeleteClick: " + userList.get(position).getId() + position);

            userViewModel.deleteUser(userList.get(position).getId(), this);
            positiondel = position;
        }

        @Override
        public void check(boolean flag) {
            if (flag) {
                Log.d("ads", "check: ");
                userList.remove(positiondel);
                userRVAdapter.notifyDataSetChanged();
            }
        }
    }
}




