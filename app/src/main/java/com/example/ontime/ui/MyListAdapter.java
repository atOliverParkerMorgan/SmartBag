package com.example.ontime.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ontime.R;

import java.util.List;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private List<Item> Items;

    // RecyclerView recyclerView;
    public MyListAdapter(List<Item> listdata) {
        this.Items = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = Items.get(position);
        holder.textView.setText(Items.get(position).getName());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // remove your item from data base
                Items.remove(item);  // remove the item from list
                notifyItemRemoved(position); // notify the adapter about the removed item
                notifyItemRangeChanged(position, Items.size());

            }
        });
       // holder.imageView.setImageResource(Items.get(position));
        //holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(view.getContext(),"click on item: "+myListData.getName(),Toast.LENGTH_LONG).show();
        //    }
        //});
    }


    @Override
    public int getItemCount() {
        return Items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView textView;
        RelativeLayout relativeLayout;
        ViewHolder(View itemView) {
            super(itemView);
           // this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.imageButton = itemView.findViewById(R.id.layout_list_delete);
            this.textView = itemView.findViewById(R.id.layout_list_name);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


}  