package com.example.ontime.ui.Overview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.ontime.Activities.AddSubject;
import com.example.ontime.Activities.EditSubject;
import com.example.ontime.Activities.Settings;
import com.example.ontime.Adapter.Item;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.List;
import java.util.Objects;


public class OverviewFragment extends Fragment {
    StorageReference storageReference;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {


        // get most subjects in a day
        int max = Integer.MIN_VALUE;
        int current = 0;
        for (int i = 1; i < 8; i++) {
            for (List<String> list : FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
                if (list.get(i).equals("true")) {
                    current++;
                }
            }
            if(max<current){
                max = current;
            }
            current = 0;
        }
        boolean donNotShare = max==0;
        max = Math.max(max, 4);

        // create view
        final View view = inflater.inflate(R.layout.fragment_overview, parent, false);

        //bag code
        final EditText code = view.findViewById(R.id.editTextNumberCode);



        TableLayout table = view.findViewById(R.id.mainTable);
        // views for button
        Button shareBagButton = view.findViewById(R.id.shareBagButton);
        // hide weekend
        SharedPreferences preferencesWeekendOn = Objects.requireNonNull(requireActivity().getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE));
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);
        if(weekendOnBoolean){
            View v = view.findViewById(R.id.Sun);
            ((ViewManager)v.getParent()).removeView(v);
            View v1 = view.findViewById(R.id.Sat);
            ((ViewManager)v1.getParent()).removeView(v1);
        }

        // image button logic add item
        ImageButton imageButtonAddSubject = view.findViewById(R.id.addSubject);
        imageButtonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting add Subject first to falls to avoid error
                AddSubject.firstViewOfActivity = true;
                Intent intent = new Intent(getActivity(), AddSubject.class);
                startActivity(intent);
            }
        });

        // image button logic settings
        ImageButton imageButtonSettings = view.findViewById(R.id.settingsButton);
        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });


        for (int i = 1; i < (weekendOnBoolean?6:8); i++) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0,0,0,0);

            int addedRows = 0;
            for (List<String> list : FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
                final Item item = new Item("null", list.get(0), false);

                // days of the week logic

                if (list.get(i).equals("true")) {
                    addedRows++;
                    Button button = new Button(getContext());
                    button.setMinimumWidth(0);
                    button.setMinimumHeight(0);
                    button.setBackgroundResource(R.drawable.rounded_textview_default_borders);
                    button.setGravity(Gravity.CENTER);
                    button.setText(item.getNameInitialsOfSubject());
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setTextSize(30);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // go back to main activity
                            Intent i = new Intent( getActivity(), EditSubject.class);
                            i.putExtra("subjectName", item.getSubjectName());
                            startActivity(i);
                        }
                    });

                    row.addView(button);


                }

            }
            if(addedRows<max){
                for(int j=0;j<max-addedRows;j++) {
                    Button padding = new Button(getContext());
                    padding.setMinimumWidth(0);
                    padding.setMinimumHeight(0);
                    padding.setBackgroundResource(R.drawable.rounded_textview_padding);
                    padding.setGravity(Gravity.CENTER);
                    padding.setTextColor(Color.WHITE);
                    padding.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    padding.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    padding.setText("");
                    padding.setTextSize(30);
                    row.addView(padding);
                }

            }
            table.addView(row);

        }
        table.requestLayout();     // Not sure if this is needed.

        Button addSubjectButton =  view.findViewById(R.id.showButton);
        // hide share if there are zero items
        if(donNotShare){
            // hide table
            table.setVisibility(View.GONE);
            view.findViewById(R.id.Table).setVisibility(View.GONE);
            // hide share button
            shareBagButton.setVisibility(View.GONE);
            addSubjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // setting add Subject first to falls to avoid error
                    AddSubject.firstViewOfActivity = true;
                    Intent intent = new Intent(getActivity(), AddSubject.class);
                    startActivity(intent);
                }
            });
        }else{
            addSubjectButton.setVisibility(View.GONE);
        }

        // firebase logic
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // on pressing btnUpload uploadImage() is called
        shareBagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadDatabase();
            }
        });


        return view;
    }


    private void uploadDatabase()
    {
            String dbname = "Subject.db";
            Uri file = Uri.fromFile(requireContext().getDatabasePath(dbname));




            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();



            // get name for database
            // Math.random() * (max - min + 1) + min
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                name.append((char) (Math.random() * ((int) 'Z' - (int) 'A' + 1) + (int)'A'));
            }
            // adding listeners on upload
            // or failure of image

            storageReference.child("databases/"+"PASV"+".db").putFile(file)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getContext(),
                                                    "Your bag has been Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        @NonNull UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });

    }
}


