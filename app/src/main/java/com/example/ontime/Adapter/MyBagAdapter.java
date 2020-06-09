package com.example.ontime.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperMyBag;
import com.example.ontime.R;

import java.util.List;


public class MyBagAdapter extends RecyclerView.Adapter<MyBagAdapter.ViewHolder>{
    private List<Item> Items;

    // RecyclerView recyclerView;
    public MyBagAdapter(List<Item> listdata) {
        this.Items = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;

        listItem = layoutInflater.inflate(R.layout.item_default, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(Items.get(position).getItemName());
        holder.circle.setText(Items.get(position).getNameInitialsOfSubject());
        holder.circle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(v.getContext(),Items.get(position).getSubjectName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if(FeedReaderDbHelperMyBag.delete(v.getContext(), Items.get(position))){
                   Toast.makeText(v.getContext(), Items.get(position).getItemName()+" has been removed from your bag", Toast.LENGTH_LONG).show();
                   Items.remove(Items.get(position));  // remove the itemAdd from list
                   notifyItemRemoved(position); // notify the adapter about the removed itemAdd
                   notifyItemRangeChanged(position, Items.size());
                }else{
                   Toast.makeText(v.getContext(), "Oops an error has occurred", Toast.LENGTH_LONG).show();
               }




            }
        });
    }


    @Override
    public int getItemCount() {
        return Items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView textView;
        Button circle;
        RelativeLayout relativeLayout;
        ViewHolder(View itemView) {
            super(itemView);
           // this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.imageButton = itemView.findViewById(R.id.layout_list_button);
            this.textView = itemView.findViewById(R.id.layout_list_name);
            this.circle = itemView.findViewById(R.id.mark);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


}  