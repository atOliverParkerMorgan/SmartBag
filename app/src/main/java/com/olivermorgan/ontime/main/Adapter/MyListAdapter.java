 package com.olivermorgan.ontime.main.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.olivermorgan.ontime.main.Activities.EditSubject;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.R;


import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.view.View.GONE;


 public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private List<Item> Items;
    private byte add;
    private View view;
    private boolean editTextBoolean;
    private boolean showSubjectTitle;
    boolean goToEditSubject;
    private Activity mainActivity;

    // RecyclerView recyclerView;
    public MyListAdapter(List<Item> listdata, byte add, View view, boolean showSubjectTitle, boolean editTextBoolean, boolean goToEditSubject, Activity mainActivity) {
        this.add = add;
        this.Items = listdata;
        this.editTextBoolean = editTextBoolean;
        this.goToEditSubject = goToEditSubject;
        this.showSubjectTitle = showSubjectTitle;
        this.mainActivity = mainActivity;
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
                this.Items.add(indexToAdd.get(i) + i, new Item(subjectsToAdd.get(i), null, false));
            }
        }
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;

        if(add == 1) {
            if(showSubjectTitle){
                if(viewType==0){
                    return new ViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false) , editTextBoolean);
                }
            }
            listItem = layoutInflater.inflate(R.layout.item_add, parent, false);
        }else if(add == 0){
            if(showSubjectTitle){
                if(viewType==0){
                    return new ViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false) , editTextBoolean);
                }
            }
            listItem = layoutInflater.inflate(R.layout.item_remove, parent, false);
        }else if(add==-1){
            if(showSubjectTitle){
                if(viewType==0){
                    return new ViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false) , editTextBoolean);
                }
            }
            listItem = layoutInflater.inflate(R.layout.item_default, parent, false);
        }
        else{
            if(showSubjectTitle){
                if(viewType==0){
                    return new ViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false) , editTextBoolean);
                }
            }
            listItem = layoutInflater.inflate(R.layout.item_edit, parent, false);
        }
        return new ViewHolder(listItem, editTextBoolean);
    }



     @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
         if (holder.getItemViewType() == 0) {
             holder.textView.setText(Items.get(position).getItemName());
         }else{
             if (editTextBoolean) {
                 holder.textView.setVisibility(GONE);
                 holder.editText.setVisibility(View.VISIBLE);
                 holder.editText.setText(Items.get(position).getItemName());
                 holder.editText.addTextChangedListener(new TextWatcher() {

                     @Override
                     public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                     }

                     @Override
                     public void onTextChanged(CharSequence s, int start, int before, int count) {

                     }

                     @Override
                     public void afterTextChanged(Editable s) {
                         Items.get(position).setItemName(s.toString());
                     }
                 });

             }else{
                 // tutorial
                 if(add==1 && position == 1){ // only for the first element
                     // tutorial for add recycler
                     ShowcaseConfig config = new ShowcaseConfig();
                     config.setDelay(200); // half second between each showcase view
                     MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mainActivity, "recyclerViewerTutorialAdd");
                     sequence.setConfig(config);
                     sequence.addSequenceItem(holder.circle,
                             "Click the icon edit this subject", "NEXT");
                     sequence.addSequenceItem(holder.imageButton,
                             "Click here to add this item to your bag", "GOT IT");
                     sequence.start();
                 }else if(add==0 && position == 1){
                     // tutorial for add recycler
                     ShowcaseConfig config = new ShowcaseConfig();
                     config.setDelay(200); // half second between each showcase view
                     MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(mainActivity, "recyclerViewerTutorialRemove");
                     sequence.setConfig(config);
                     sequence.addSequenceItem(holder.imageButton,
                             "Click here to remove this item from your bag", "GOT IT");
                     sequence.start();
                 }
             }



             holder.textView.setText(Items.get(position).getItemName());
             holder.circle.setText(Items.get(position).getNameInitialsOfSubject());
             if(goToEditSubject) {
                 holder.circle.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         // go back to main activity
                         Intent i = new Intent(v.getContext(), EditSubject.class);
                         i.putExtra("subjectName", Items.get(position).getSubjectName());
                         v.getContext().startActivity(i);
                     }
                 });
             }else{
                 holder.circle.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         Toast.makeText(v.getContext(), Items.get(position).getSubjectName(), Toast.LENGTH_SHORT).show();
                     }
                 });
             }
             holder.imageButton.setOnClickListener(new View.OnClickListener() {
                 public void onClick(final View v) {
                     if (add == 1) {

                         FeedReaderDbHelperItems.editBag(v.getContext(), Items.get(position), true);
                         Toast.makeText(v.getContext(), Items.get(position).getItemName() + " has been added to your bag", Toast.LENGTH_SHORT).show();
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

                         // instruction logic
                         TextView noItems = view.findViewById(R.id.noItemsTextAdd);
                         noItems.setAlpha(1.0f);
                         if (Items.size() > 0) {
                             noItems.setAlpha(0.0f);
                         }else {
                             Items.size();
                             noItems.setText(R.string.noItemsInAdd);
                         }

                     }
                     else if(add==-1){
                         Items.remove(Items.get(position));  // remove the itemAdd from list
                         notifyItemRemoved(position); // notify the adapter about the removed itemAdd
                         notifyItemRangeChanged(position, Items.size());
                     }

                     else if (add == 0) {

                         FeedReaderDbHelperItems.editBag(v.getContext(), Items.get(position), false);
                         Toast.makeText(v.getContext(), Items.get(position).getItemName() + " has been removed from your bag", Toast.LENGTH_SHORT).show();
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

                         // instruction logic
                         TextView noItems = view.findViewById(R.id.noItemsTextRemove);
                         noItems.setAlpha(1.0f);
                         if (Items.size() > 0) {
                             noItems.setAlpha(0.0f);
                         }else {
                             Items.size();
                             noItems.setText(R.string.noItemsInRemove);
                         }

                     } else if (add == -10) {
                         AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                         alert.setTitle("Delete Item");
                         alert.setMessage("Are you sure you want to delete " + Items.get(position).getItemName() + "?");
                         alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                             public void onClick(DialogInterface dialog, int which) {
                                 // continue with delete
                                 FeedReaderDbHelperItems.deleteItem(v.getContext(), Items.get(position).getItemName());
                                 Toast.makeText(v.getContext(), Items.get(position).getItemName() + " has been removed from your bag", Toast.LENGTH_SHORT).show();
                                 Items.remove(Items.get(position));  // remove the itemAdd from list
                                 notifyItemRemoved(position); // notify the adapter about the removed itemAdd
                                 notifyItemRangeChanged(position, Items.size());
                             }
                         });
                         alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                                 // close dialog
                                 dialog.cancel();
                             }
                         });
                         alert.show();

                     }


                 }
             });
            }

        }


     public List<Item> getItems() {
         return Items;
     }

     public void add(Item item) {
        Items.add(item);
     }

     @Override
     public int getItemViewType(int position) {
         if(Items.get(position).getSubjectName()==null){
             return 0;
         }
         return 1;
     }


     @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView textView;
        Button circle;
        EditText editText;
        RelativeLayout relativeLayout;
        ViewHolder(View itemView, boolean editTextBoolean) {
            super(itemView);
            // this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            if(editTextBoolean) this.editText = itemView.findViewById(R.id.editTextItem);
            this.imageButton = itemView.findViewById(R.id.layout_list_button);
            this.textView = itemView.findViewById(R.id.layout_list_name);
            this.circle = itemView.findViewById(R.id.mark);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


}  