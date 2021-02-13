package com.olivermorgan.ontime.main.ui.Overview;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.olivermorgan.ontime.main.Activities.AddSubject;
import com.olivermorgan.ontime.main.Activities.EditSubject;
import com.olivermorgan.ontime.main.Activities.Settings;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class OverviewFragment extends Fragment {
    StorageReference storageReference;
    ProgressBar progressBar;
    TextView codeText;
    TextView codeTextInstructions;
    Button shareBagButton;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        // create view
        final View view = inflater.inflate(R.layout.fragment_overview, parent, false);

        // tutorial
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "recyclerViewerTutorialOverviewLoad");
        sequence.setConfig(config);
        sequence.addSequenceItem(view.findViewById(R.id.editTextCode),
                "Put the code for sharing a bag here", "NEXT");
        sequence.addSequenceItem(view.findViewById(R.id.buttonLoad),
                "Click here to load the shared bag", "GOT IT");
        sequence.start();

        progressBar = view.findViewById(R.id.progress_bar);
        codeText = view.findViewById(R.id.codeText);
        codeTextInstructions = view.findViewById(R.id.codeTextInstructions);
        shareBagButton = view.findViewById(R.id.shareBagButton);
        Button load = view.findViewById(R.id.buttonLoad);

        final TableLayout table = view.findViewById(R.id.mainTable);

        // hide weekend
        boolean weekendOnBoolean = SharedPrefs.getBoolean(getContext(), SharedPrefs.WEEKEND_ON);
        if(weekendOnBoolean){
            View v = view.findViewById(R.id.Sun);
            ((ViewManager)v.getParent()).removeView(v);
            View v1 = view.findViewById(R.id.Sat);
            ((ViewManager)v1.getParent()).removeView(v1);
        }


        // get code if already stored
        String codeName = SharedPrefs.getString(getContext(), SharedPrefs.CODE);

        if(codeName!=null && !codeName.equals("")) {
            codeText.setText(codeName);
            codeTextInstructions.setText(R.string.codeNameInstructions);
            shareBagButton.setText(R.string.updateBag);
        }

        // get different bag via code

        // firebase logic
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // bag code
        final EditText code = view.findViewById(R.id.editTextCode);

        // get path to databases
        String dbnameSubjects = "Subject.db";
        final Uri databaseSubjects = Uri.fromFile(requireContext().getDatabasePath(dbnameSubjects));
        String dbnameItems = "Items.db";
        final Uri databaseItems = Uri.fromFile(requireContext().getDatabasePath(dbnameItems));

        load.setOnClickListener(v -> {
            if(code.getText().toString().length()!=6 || !isStringUpperCase(code.getText().toString())){
                Toast.makeText(getContext(),"Invalid code, must contain six upper-case letters.", Toast.LENGTH_LONG).show();
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Download bag: " + code.getText().toString());
                alert.setMessage("Are you sure you want to download another bag? " +
                        "This is will override all existing subjects and items.");
                alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // for subject
                    StorageReference subjectRef = storageReference.child("subjects/" + code.getText().toString() + ".db");
                    subjectRef.getFile(databaseSubjects).addOnSuccessListener(taskSnapshot -> {
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        Toast.makeText(getContext(), "Error, your code is incorrect or you don't have an internet connection.", Toast.LENGTH_LONG).show();
                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });

                    // for items
                    StorageReference itemRef = storageReference.child("items/" + code.getText().toString() + ".db");
                    itemRef.getFile(databaseItems).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "You have successfully update your bag with the code: " + code.getText().toString(), Toast.LENGTH_LONG).show();
                        updateTable(weekendOnBoolean, table,  view);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 1500);

                    }).addOnFailureListener(exception -> Toast.makeText(getContext(), "Error, your code is incorrect or you don't have an internet connection.", Toast.LENGTH_LONG).show()).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });


                });
                alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // close dialog
                    dialog.cancel();
                });
                alert.show();
            }




        });

        // image button logic add item
        ImageButton imageButtonAddSubject = view.findViewById(R.id.addSubject);
        imageButtonAddSubject.setOnClickListener(v -> {
            // setting add Subject first to falls to avoid error
            AddSubject.firstViewOfActivity = true;
            Intent intent = new Intent(getActivity(), AddSubject.class);
            startActivity(intent);
        });

        // image button logic settings
        ImageButton imageButtonSettings = view.findViewById(R.id.settingsButton);
        imageButtonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Settings.class);
            startActivity(intent);
        });


        updateTable(weekendOnBoolean, table, view);


        // on pressing btnUpload uploadImage() is called
        shareBagButton.setOnClickListener(v -> uploadDatabase());


        return view;
    }


    private void uploadDatabase()
    {
            // get Subject database
            String dbnameSubjects = "Subject.db";
            Uri databaseSubjects = Uri.fromFile(requireContext().getDatabasePath(dbnameSubjects));

            // get Items database
            String dbnameItems = "Items.db";
            Uri databaseItems = Uri.fromFile(requireContext().getDatabasePath(dbnameItems));

            // get code if already stored
            String codeName =  SharedPrefs.getString(getContext(), SharedPrefs.CODE);

            if(codeName==null || codeName.equals("")) {
                // get a UNIQUE name for database
                // Math.random() * (max - min + 1) + min
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    name.append((char) (Math.random() * ((int) 'Z' - (int) 'A' + 1) + (int) 'A'));
                }
                codeName = name.toString();
                SharedPrefs.setString(getContext(), SharedPrefs.CODE, codeName);
            }
            // adding listeners on upload
            // or failure of image

        // Progress Listener for loading
// percentage on the dialog box
        storageReference.child("subjects/"+codeName+".db").putFile(databaseSubjects)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                            })

                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error, check your internet connection.", Toast.LENGTH_LONG).show())
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            });

        final String finalCodeName = codeName;
        // Progress Listener for loading
// percentage on the dialog box
        storageReference.child("items/"+codeName+".db").putFile(databaseItems)
                .addOnSuccessListener(
                        taskSnapshot -> {
                            // stay in place for half a second after completion
                            Handler handler = new Handler();
                            handler.postDelayed(() -> progressBar.setProgress(0), 1500);

                            codeText.setText( finalCodeName);
                            codeTextInstructions.setText(R.string.codeNameInstructions);
                            shareBagButton.setText(R.string.updateBag);


                            Toast.makeText(getContext(), "Success, your bag has been uploaded.", Toast.LENGTH_LONG).show();
                        })

                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error, check your internet connection.", Toast.LENGTH_LONG).show())
                .addOnProgressListener(
                        taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        });

    }
    private void updateTable(boolean weekendOnBoolean, TableLayout table, View view){
        table.removeAllViews();

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
        final boolean donNotShare = max==0;
        max = Math.max(max, 4);
        boolean first = true;

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

                    if(first) {
                        // tutorial
                        ShowcaseConfig config = new ShowcaseConfig();
                        config.setDelay(200); // half second between each showcase view
                        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "recyclerViewerTutorialOverviewTableItem");
                        sequence.setConfig(config);
                        sequence.addSequenceItem(button,
                                "Click the icon to edit this subject", "GOT IT");
                        sequence.start();
                        first = false;
                    }

                    button.setMinimumWidth(0);
                    button.setMinimumHeight(0);
                    button.setBackgroundResource(R.drawable.rounded_textview_padding);
                    button.setGravity(Gravity.CENTER);
                    button.setText(item.getNameInitialsOfSubject());
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setTextSize(30);

                    button.setOnClickListener(v -> {
                        // go back to main activity
                        Intent i1 = new Intent( getActivity(), EditSubject.class);
                        i1.putExtra("subjectName", item.getSubjectName());
                        startActivity(i1);
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
            addSubjectButton.setVisibility(View.VISIBLE);
            view.findViewById(R.id.codeTextInstructions).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.codeText).setVisibility(View.INVISIBLE);
            addSubjectButton.setOnClickListener(v -> {
                // setting add Subject first to falls to avoid error
                AddSubject.firstViewOfActivity = true;
                Intent intent = new Intent(getActivity(), AddSubject.class);
                startActivity(intent);
            });
        }else{
            addSubjectButton.setVisibility(View.GONE);
            // show table
            table.setVisibility(View.VISIBLE);
            view.findViewById(R.id.Table).setVisibility(View.VISIBLE);
            shareBagButton.setVisibility(View.VISIBLE);
        }

    }

    private static boolean isStringUpperCase(String str){

        //convert String to char array
        char[] charArray = str.toCharArray();

        for (char c : charArray) {

            //if any character is not in upper case, return false
            if (!Character.isUpperCase(c))
                return false;
        }

        return true;
    }
}


