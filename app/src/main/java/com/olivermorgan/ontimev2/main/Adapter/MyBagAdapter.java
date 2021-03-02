package com.olivermorgan.ontimev2.main.Adapter;

import android.app.Activity;
import android.content.Intent;
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


import com.olivermorgan.ontimev2.main.Activities.EditSubject;
import com.olivermorgan.ontimev2.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontimev2.main.R;


import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MyBagAdapter extends RecyclerView.Adapter<MyBagAdapter.ViewHolder>{
    private final List<Item> Items;
    private final Activity mainActivity;

    // RecyclerView recyclerView;
    public MyBagAdapter(List<Item> listdata, boolean showSubjectTitle, Activity mainActivity) {
        this.mainActivity = mainActivity;

        this.Items = listdata;
        List<Integer> indexToAdd = new ArrayList<>();
        List<String> subjectsToAdd = new ArrayList<>();
        if(showSubjectTitle) {
            // add title
            String currentSubjectName = null;            for (int i = 0; i < this.Items.size(); i++) {
                if (currentSubjectName == null || !currentSubjectName.equals(this.Items.get(i).getSubjectName())) {
                    currentSubjectName = this.Items.get(i).getSubjectName();
                    indexToAdd.add(i);
                    subjectsToAdd.add(currentSubjectName);
                }
            }
            for (int i = 0; i < indexToAdd.size(); i++) {
                this.Items.add(indexToAdd.get(i) + i, new Item(subjectsToAdd.get(i), null, false, mainActivity));
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType==0) return new ViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false));
        return new ViewHolder(layoutInflater.inflate(R.layout.item_default, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            holder.textView.setText(Items.get(position).getItemName());
        }else {
            // tutorial
            if(position == 1) {
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(200); // half second between each showcase view
                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mainActivity, "recyclerViewerTutorialRemoveFromBag");
                sequence.setConfig(config);
                sequence.addSequenceItem(holder.imageButton,
                        mainActivity.getResources().getString(R.string.clickHereToRemoveItemFromBag),  mainActivity.getResources().getString(R.string.gotIt));
                sequence.start();
            }

            holder.textView.setText(Items.get(position).getItemName());
            holder.circle.setText(Items.get(position).getNameInitialsOfSubject());
            holder.circle.setOnClickListener(v -> {
                // go back to main activity
                Intent i = new Intent( v.getContext(), EditSubject.class);
                i.putExtra("subjectName", Items.get(position).getSubjectName());
                v.getContext().startActivity(i);
            });
            holder.imageButton.setOnClickListener(v -> {
                // logic
                    FeedReaderDbHelperItems.editBag(v.getContext(), Items.get(position), false);
                    Toast.makeText(v.getContext(), Items.get(position).getItemName() + " "+mainActivity.getResources().getString(R.string.hasBeenRemoved), Toast.LENGTH_SHORT).show();
                    Items.remove(Items.get(position));  // remove the itemAdd from list
                    notifyItemRemoved(position); // notify the adapter about the removed itemAdd
                    notifyItemRangeChanged(position, Items.size());
                    if(Items.get(position-1).getSubjectName()==null){
                        if(position - 1 == Items.size()-1){
                            Items.remove(Items.get(position-1));  // remove the Title from list
                            notifyItemRemoved(position-1);
                        }else if(Items.get(position).getSubjectName()==null){
                            Items.remove(Items.get(position-1));  // remove the Title from list
                            notifyItemRemoved(position-1);
                        }
                    }
                    notifyItemRangeChanged(position-1, Items.size());


                });
        }
    }


    @Override
    public int getItemCount() {
        return Items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(Items.get(position).getSubjectName()==null){
            return 0;
        }
        return 1;
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