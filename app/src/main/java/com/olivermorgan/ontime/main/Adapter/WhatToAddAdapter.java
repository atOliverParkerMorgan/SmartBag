package com.olivermorgan.ontime.main.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.olivermorgan.ontime.main.Activities.AddSubjectOrOther;
import com.olivermorgan.ontime.main.R;
import java.util.List;

public class WhatToAddAdapter extends RecyclerView.Adapter<WhatToAddAdapter.ViewHolder> {
    private final List<Item> Items;
    boolean goToEditSubject;
    private final Activity mainActivity;

    // RecyclerView recyclerView;
    public WhatToAddAdapter(List<Item> listdata, Activity mainActivity) {
        this.Items = listdata;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_what_to_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Intent intent = new Intent(mainActivity, AddSubjectOrOther.class);
        switch (position) {
            case 0:
                holder.circle.setBackgroundResource(R.drawable.circle_default);
                holder.relativeLayout.setOnClickListener(view -> {
                   gotoAddSubject(intent);
                });
                holder.circle.setOnClickListener(view -> {gotoAddSubject(intent);});
                break;
            case 1:
                holder.circle.setBackgroundResource(R.drawable.square_default);

                holder.relativeLayout.setOnClickListener(view -> {
                   gotoAddSnack(intent);
                });
                holder.circle.setOnClickListener(view -> {gotoAddSnack(intent);});

                break;
            case 2:
                holder.circle.setBackgroundResource(R.drawable.hexagon_default);
                holder.relativeLayout.setOnClickListener(view -> {
                    gotoAddPencilCase(intent);
                });
                holder.circle.setOnClickListener(view -> {gotoAddPencilCase(intent);});
                break;
        }

        holder.circle.setText(Items.get(position).getNameInitialsOfSubject());
        holder.textView.setText(Items.get(position).getItemName());



    }
    public void gotoAddSubject(Intent intent){
        intent.putExtra("name", mainActivity.getString(R.string.title_create_subject));
        intent.putExtra("type", "subject");
        mainActivity.startActivity(intent);
        mainActivity.finish();
        mainActivity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void gotoAddSnack(Intent intent){
        intent.putExtra("name", mainActivity.getString(R.string.title_create_snack));
        intent.putExtra("type", "snack");
        mainActivity.startActivity(intent);
        mainActivity.finish();
        mainActivity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void gotoAddPencilCase(Intent intent){
        intent.putExtra("name", mainActivity.getString(R.string.title_create_pencilCase));
        intent.putExtra("type", "pencilCase");
        mainActivity.startActivity(intent);
        mainActivity.finish();
        mainActivity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Button circle;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            // this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.layout_list_name);
            this.circle = itemView.findViewById(R.id.mark);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }

    }
}
