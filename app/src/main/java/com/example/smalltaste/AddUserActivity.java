package com.example.smalltaste;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.example.smalltaste.model.Repository;
import com.example.smalltaste.viewmodel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddUserActivity extends AppCompatActivity implements Repository.AsyncComplete, View.OnClickListener {
    EditText etName, etLastName, etEmail;
    Button btnSubmit;
    String imageUrl;
    UserViewModel userViewModel;
    ImageView ivProfilePic;
    FloatingActionButton fabAddPic;
    Bitmap myBitmap;
    Uri picUri;
    String s;
    int count;
    String yourRealPath;
    SharedPreferences sp;
    public final static int MYREQUESTPERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_user);
        btnSubmit = findViewById(R.id.btn_activity_add_user_submit);
        Toolbar toolbar = findViewById(R.id.tb_activity_add_user_toolbar);
        setSupportActionBar(toolbar);
        etName = findViewById(R.id.et_activity_add_user_name);
        etLastName = findViewById(R.id.et_activity_add_user_lastname);
        etEmail = findViewById(R.id.et_activity_add_user_email);
        ivProfilePic = findViewById(R.id.civ_activity_main_pic);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.adduser));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        btnSubmit.setOnClickListener(this);
        fabAddPic = findViewById(R.id.fab_activity_add_user_add_pic);
        sp = getSharedPreferences("userdata", MODE_PRIVATE);



        fabAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("c", ++count);
                editor.apply();
                startActivityForResult(getPickIntent(), 2);
            }
        });


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
    public void check(boolean flag) {

        if (flag) {
            Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
            Toast.makeText(AddUserActivity.this, getString(R.string.added), Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    public void onClick(View view) {
        if (validate()) {
            Log.d("uritest", "onClick: " + picUri);
            if (picUri != null)
                userViewModel.addUser(s, etName.getText().toString(), etLastName.getText().toString(), etEmail.getText().toString(), this);

        }


    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File file = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {


            Log.d("S", "getCaptureImageOutputUri: " + sp.getInt("c", 0) + sp.getInt("cm", 0));

            file = new File(getExternalCacheDir(), (String.valueOf(sp.getInt("cm", 0))));
        }
        outputFileUri = Uri.fromFile(file);

        return outputFileUri;
    }


    private Intent getPickIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        ////What is use of this
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //What is use of this
        galleryIntent.setType("image/*");
        //What is use of this
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);

        }

        // the main intent is the last in the list  so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.example.smalltaste")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;

    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            picUri=getPickImageResultUri(data);
            if (getPickImageResultUri(data) != null) {
//                s=      getRealPathFromURI(data.getData(), this);
                try {

                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    //myBitmap = rotateImageIfRequired(myBitmap, picUri);


                    Log.d("imageurltest", "onActivityResult: " + imageUrl + "mmm" + picUri+"mmm"+picUri.toString());
                    ivProfilePic.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }
}