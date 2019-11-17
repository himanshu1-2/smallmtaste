package com.example.smalltaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smalltaste.model.Datum;
import com.example.smalltaste.model.Repository;
import com.example.smalltaste.viewmodel.UserViewModel;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, Repository.AsyncComplete {
    UserViewModel userViewModel;
    EditText etName, etLastName, etEmail;
    Button btnSubmit;
    int position;
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        etName = findViewById(R.id.et_activity_upadte_name);
        etLastName = findViewById(R.id.et_activity_upadte_lastname);
        etEmail = findViewById(R.id.et_activity_upadte_email);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        Toolbar toolbar = findViewById(R.id.tb_activity_upadte_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.detail));
        toolbar.setTitle(getString(R.string.updateuser));
        btnSubmit = findViewById(R.id.btn_activity_upadte_submit);
        Intent intent = getIntent();
        userId = intent.getLongExtra(getString(R.string.userDataid), 0);
        intent.getStringExtra(getString(R.string.lastname));
        Log.d("sd", "onCreate: " + intent.getStringExtra(getString(R.string.firstname)));
        Log.d("update a", "onCreate: " + userId + intent);
        position = intent.getIntExtra(getString(R.string.position), 0);
        Log.d("d", "onCreate: " + userId);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.d("ad", "onCreate: " + userId);
        if (userId != 0) {
            userViewModel.retrieveParticularId(userId).observe(this, new Observer<Datum>() {
                @Override
                public void onChanged(Datum datum) {
                    etName.setText(datum.getFirstName());
                    etLastName.setText(datum.getLastName());
                    etEmail.setText(datum.getEmail());

                }
            });

        }
        btnSubmit.setOnClickListener(this);
    }

    private boolean validate() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (etName.getText().toString().trim().equals(""))
            Toast.makeText(this, getString(R.string.enterpropername), Toast.LENGTH_SHORT).show();
        else if (etLastName.getText().toString().trim().equals(""))
            Toast.makeText(this, getString(R.string.enterproperlastname), Toast.LENGTH_SHORT).show();
        else if (!etEmail.getText().toString().matches(emailPattern))
            Toast.makeText(this, getString(R.string.enterproperemail), Toast.LENGTH_SHORT).show();

        else
            return true;

        return false;
    }

    @Override
    public void onClick(View view) {
        userViewModel.update(userId, etName.getText().toString(), etLastName.getText().toString(), etEmail.getText().toString(), this);

    }

    @Override
    public void check(boolean flag) {
        if (validate()) {
            Log.d("A", "check: ");
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.updateflag), true);
            intent.putExtra(getString(R.string.userDataid), userId);
            intent.putExtra(getString(R.string.positionflags), position);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
