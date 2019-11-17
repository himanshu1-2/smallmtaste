package com.example.smalltaste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smalltaste.interfaces.UserDao;
import com.example.smalltaste.model.Datum;
import com.example.smalltaste.model.Repository;
import com.example.smalltaste.model.UserDatabase;
import com.example.smalltaste.viewmodel.UserViewModel;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements Repository.AsyncComplete, DialogInterface.OnClickListener {
    TextView etFirstName, etLastName, etEmail;
    ImageView ivUserImage;
    Button btnEdit, btnDelete;
    UserDao userDao;
    long id;
    int position;
    boolean deleteFlag;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activty);
        etFirstName = findViewById(R.id.txt_activity_detail_firstname);
        etLastName = findViewById(R.id.txt_activity_detail_lastname);
        etEmail = findViewById(R.id.txt_activity_detail_email);
        ivUserImage = findViewById(R.id.iv_activity_detail_userimage);
        btnEdit = findViewById(R.id.btn_activity_detail_update);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        Intent intent = getIntent();
        position = intent.getIntExtra(getString(R.string.position), 0);
        btnDelete = findViewById(R.id.btn_activity_detail_delete);
        UserDatabase userDatabase = UserDatabase.getInstance(this);
        userDao = userDatabase.formDao();
        Toolbar toolbar = findViewById(R.id.tb_activity_detail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.detail));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        id = intent.getLongExtra(getString(R.string.userDataid), 0);


        Log.d("s", "onCreate: " + intent.getLongExtra(getString(R.string.userDataid), 0) + position + " " + id);


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog();
            }
        });




        if (id != 0) {
            userViewModel.retrieveParticularId(intent.getLongExtra(getString(R.string.userDataid), 0)).observe(this, new Observer<Datum>() {
                @Override
                public void onChanged(Datum datum) {

                    if (!deleteFlag) {
                        etFirstName.setText(datum.getFirstName());
                        etLastName.setText(datum.getLastName());
                        etEmail.setText(datum.getEmail());
                        Picasso.with(getBaseContext()).load(datum.getAvatar()).error(R.mipmap.ic_launcher)
                                .into(ivUserImage);

                    }
                }
            });
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra(getString(R.string.userDataid), id);
                Log.d("asds", "onClick: " + etFirstName);
                startActivityForResult(intent, 4);


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            if (requestCode == 4)
                if (resultCode == RESULT_OK) {
                    {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.userDataid), id);
                        intent.putExtra(getString(R.string.update), data.getBooleanExtra(getString(R.string.updateflag), false));
                        intent.putExtra(getString(R.string.positionflags), position);
                        Log.d("sa", "onActivityResult:" + id);
                        if (id != 0)
                            userViewModel.retrieveParticularId(intent.getLongExtra(getString(R.string.userDataid), 0)).observe(DetailActivity.this, new Observer<Datum>() {
                                @Override
                                public void onChanged(Datum datum) {

                                    etFirstName.setText(datum.getFirstName());
                                    etLastName.setText(datum.getLastName());
                                    etEmail.setText(datum.getEmail());

                                }
                            });


                        setResult(RESULT_OK, intent);
                    }
                }
        }
    }


    public void confirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.doyouwanttodeleteleave));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", this);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void check(boolean flag) {

        Intent intent = new Intent();
        intent.putExtra(getString(R.string.deleteflag), true);
        intent.putExtra(getString(R.string.positionflag), position);
        setResult(RESULT_OK, intent);
        Toast.makeText(DetailActivity.this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("s", "onClick: " + id);
        deleteFlag = true;
        userViewModel.deleteUser(id, this);
    }
}
