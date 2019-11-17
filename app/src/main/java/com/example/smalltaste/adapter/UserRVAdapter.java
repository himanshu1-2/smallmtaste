package com.example.smalltaste.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltaste.R;
import com.example.smalltaste.model.Datum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRVAdapter extends RecyclerView.Adapter<UserRVAdapter.MviewHolder> {
    private List<Datum> datumArrayList = new ArrayList<>();
    Context context;
    OnCardListener onCardListner;
    OnButtonClick onButtonClick;

    public UserRVAdapter(Context context, OnCardListener onCardListener, OnButtonClick onButtonClick) {
        this.context = context;
        this.onButtonClick = onButtonClick;
        this.onCardListner = onCardListener;

    }

    @NonNull
    @Override
    public MviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_userdata, viewGroup, false);
        return new MviewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull MviewHolder mviewHolder, final int i) {
        mviewHolder.txtName.setText(i + ":" + datumArrayList.get(i).getFirstName() + " " + datumArrayList.get(i).getLastName());
        mviewHolder.txtEmail.setText(datumArrayList.get(i).getEmail());



    Log.d("Asd", "onBindViewHolder: " + datumArrayList.get(i).getAvatar());


//   Picasso.with(context).load((datumArrayList.get(i).getAvatar()))
  //      .placeholder(R.mipmap.ic_launcher)
    //       .into(mviewHolder.Icon);


       // try {
         //   mviewHolder.Icon.setImageBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(datumArrayList.get(i).getAvatar())));
        //} catch (IOException e) {
          //  e.printStackTrace();
        //}


        mviewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick.onUpdateClick(i);
            }
        });


        mviewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick.onDeleteClick(i);
            }
        });


        mviewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCardListner.onCardClick(i);
            }
        });


    }


    @Override
    public int getItemCount() {
        return datumArrayList.size();
    }

    public void setNote(List<Datum> dataDTO) {
        datumArrayList = dataDTO;
        notifyDataSetChanged();
    }

    public class MviewHolder extends RecyclerView.ViewHolder {
        ImageView Icon;
        TextView txtName, txtEmail;
        RelativeLayout relativeLayout;
        Button btnDelete, btnUpdate;

        public MviewHolder(@NonNull final View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_list_item_user_data_name);
            txtEmail = itemView.findViewById(R.id.txt_list_item_user_data_email);
            Icon = itemView.findViewById(R.id.list_item_user_data_image);
            btnDelete = itemView.findViewById(R.id.btn_list_item_delete);
            btnUpdate = itemView.findViewById(R.id.btn_list_item_user_data_update);
            relativeLayout = itemView.findViewById(R.id.rl_list_item_user_data_layout);
        }
    }

    public interface OnCardListener {
        void onCardClick(int position);
    }


    public interface OnButtonClick {
        void onUpdateClick(int position);

        void onDeleteClick(int position);

    }

}
