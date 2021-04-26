package com.olivermorgan.ontime.main.ui.Overview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olivermorgan.ontime.main.Activities.AddSubject;
import com.olivermorgan.ontime.main.Activities.EditSubject;
import com.olivermorgan.ontime.main.Activities.EditSubjectLoggedin;
import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhDen;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhHodina;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.Logic.LoadBag;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class OverviewFragment extends Fragment {
    StorageReference storageReference;
    ProgressBar progressBar;
    ProgressBar progressBarLoadTable;
    TextView codeText;
    TextView codeTextInstructions;
    Button shareBagButton;
    boolean weekendOnBoolean;
    TableLayout table;
    Login login;
    int week;
    private View view;
    // private DisplayInfo displayInfo;

    private static boolean isStringUpperCase(String str) {

        //convert String to char array
        char[] charArray = str.toCharArray();

        for (char c : charArray) {

            //if any character is not in upper case, return false
            if (!Character.isUpperCase(c))
                return false;
        }

        return true;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        // create view
        view = inflater.inflate(R.layout.fragment_overview, parent, false);

        // set title
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_overview);


        //get login
        login = new Login(getContext());

        progressBar = view.findViewById(R.id.progress_bar);
        progressBarLoadTable = view.findViewById(R.id.progressBarLoadTable);

        codeText = view.findViewById(R.id.codeText);
        codeTextInstructions = view.findViewById(R.id.codeTextInstructions);
        shareBagButton = view.findViewById(R.id.shareBagButton);
        Button load = view.findViewById(R.id.buttonLoad);

        table = view.findViewById(R.id.mainTable);

        // login
        ImageButton back = view.findViewById(R.id.imageWeekBack);
        ImageButton front = view.findViewById(R.id.imageWeekFront);
        ImageButton refresh = view.findViewById(R.id.refresh);
        TextView weekDisplay = view.findViewById(R.id.currentWeek);

        weekDisplay.setText("");
        if (login.isLoggedIn()) {
            week = SharedPrefs.getInt(getContext(), "weekIndex");
            setWeekText(week, weekDisplay);
            back.setVisibility(View.VISIBLE);
            front.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            refresh.setClickable(true);
            back.setClickable(true);
            front.setClickable(true);


            refresh.setOnClickListener(v -> {
                week = SharedPrefs.getInt(getContext(), "weekIndex");

                refresh.setClickable(false);
                back.setClickable(false);
                front.setClickable(false);

                refresh.setVisibility(View.INVISIBLE);
                front.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
                progressBarLoadTable.setVisibility(View.VISIBLE);
                MainActivity.loadBag.setDatabasehasBeenUpdated(false);

                MainActivity.loadBag.refresh(week, () -> {
                    setWeekText(week, weekDisplay);

                    updateTable(weekendOnBoolean, table, view);

                    progressBarLoadTable.setVisibility(View.INVISIBLE);
                    front.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);

                    refresh.setClickable(true);
                    back.setClickable(true);
                    front.setClickable(true);
                    MainActivity.loadBag.setDatabasehasBeenUpdated(true);
                });
            });

            back.setOnClickListener(v -> {

                if (week != Integer.MIN_VALUE) {

                    week = SharedPrefs.getInt(getContext(), "weekIndex");

                    refresh.setClickable(false);
                    back.setClickable(false);
                    front.setClickable(false);

                    refresh.setVisibility(View.INVISIBLE);
                    front.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.INVISIBLE);
                    progressBarLoadTable.setVisibility(View.VISIBLE);
                    MainActivity.loadBag.setDatabasehasBeenUpdated(false);

                    MainActivity.loadBag.refresh(week - 1, () -> {
                        SharedPrefs.setInt(getContext(), "weekIndex", week - 1);
                        setWeekText(week - 1, weekDisplay);

                        updateTable(weekendOnBoolean, table, view);

                        progressBarLoadTable.setVisibility(View.INVISIBLE);
                        front.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.VISIBLE);

                        refresh.setClickable(true);
                        back.setClickable(true);
                        front.setClickable(true);
                        MainActivity.loadBag.setDatabasehasBeenUpdated(true);
                    });


                }

            });

            front.setOnClickListener(v -> {
                if (week != Integer.MAX_VALUE) {

                    week = SharedPrefs.getInt(getContext(), "weekIndex");

                    refresh.setClickable(false);
                    back.setClickable(false);
                    front.setClickable(false);

                    refresh.setVisibility(View.INVISIBLE);
                    front.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.INVISIBLE);
                    progressBarLoadTable.setVisibility(View.VISIBLE);
                    MainActivity.loadBag.setDatabasehasBeenUpdated(false);
                    MainActivity.loadBag.refresh(week + 1, () -> {
                        SharedPrefs.setInt(getContext(), "weekIndex", week + 1);
                        setWeekText(week + 1, weekDisplay);

                        updateTable(weekendOnBoolean, table, view);

                        progressBarLoadTable.setVisibility(View.INVISIBLE);
                        front.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.VISIBLE);

                        refresh.setClickable(true);
                        back.setClickable(true);
                        front.setClickable(true);

                        MainActivity.loadBag.setDatabasehasBeenUpdated(true);
                    });
                }
            });
        } else {
            // tutorial
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(200); // half second between each showcase view
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "recyclerViewerTutorialOverviewLoad");
            sequence.setConfig(config);
            sequence.addSequenceItem(view.findViewById(R.id.editTextCode),
                    getActivity().getResources().getString(R.string.bagCode), getActivity().getResources().getString(R.string.Next));
            sequence.addSequenceItem(view.findViewById(R.id.buttonLoad),
                    getActivity().getResources().getString(R.string.bagCodeButton), getActivity().getResources().getString(R.string.gotIt));
            sequence.start();
        }
        // hide weekend
        weekendOnBoolean = SharedPrefs.getBoolean(getContext(), SharedPrefs.WEEKEND_ON);
        if (weekendOnBoolean) {
            View v = view.findViewById(R.id.Sun);
            ((ViewManager) v.getParent()).removeView(v);
            View v1 = view.findViewById(R.id.Sat);
            ((ViewManager) v1.getParent()).removeView(v1);
        }


        // get code if already stored
        String codeName = SharedPrefs.getString(getContext(), SharedPrefs.CODE);

        if (codeName != null && !codeName.equals("")) {
            codeText.setText(codeName);
            codeTextInstructions.setText(R.string.codeNameInstructions);
            shareBagButton.setText(R.string.updateBag);
        }

        // get different bag via code
        String dbnameSubjects = "Subject.db";
        String dbnameItems = "Items.db";
        Uri databaseSubjects = Uri.fromFile(requireContext().getDatabasePath(dbnameSubjects));
        Uri databaseItems = Uri.fromFile(requireContext().getDatabasePath(dbnameItems));
        new Thread(() -> {
            // firebase logic
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            // get path to databases

        }).start();


        // bag code
        final EditText code = view.findViewById(R.id.editTextCode);


        load.setOnClickListener(v -> {
            if (code.getText().toString().length() != 6 || !isStringUpperCase(code.getText().toString())) {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.invalidCode), Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle(getActivity().getResources().getString(R.string.downloadBag) + " " + code.getText().toString());
                Login login = new Login(getContext());
                if (login.isLoggedIn()) {
                    alert.setMessage(getActivity().getResources().getString(R.string.sureAboutDownloadingBagAndLogoutBaka));
                } else {
                    alert.setMessage(getActivity().getResources().getString(R.string.sureAboutDownloadingBag));
                }
                alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // if is logged in => log out
                    if (login.isLoggedIn()) login.logout();
                    // for subject
                    StorageReference subjectRef = storageReference.child("subjects/" + code.getText().toString() + ".db");
                    subjectRef.getFile(databaseSubjects).addOnSuccessListener(taskSnapshot -> {
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        Toast.makeText(getContext(), getActivity().getResources().getString(R.string.incorrectCodeOrNoConnection), Toast.LENGTH_LONG).show();
                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });

                    // for items
                    StorageReference itemRef = storageReference.child("items/" + code.getText().toString() + ".db");
                    itemRef.getFile(databaseItems).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), getActivity().getResources().getString(R.string.successfulUpdateWithCode) + " " + code.getText().toString(), Toast.LENGTH_LONG).show();
                        updateTable(weekendOnBoolean, table, view);

                        progressBarLoadTable.setVisibility(View.INVISIBLE);
                        refresh.setVisibility(View.INVISIBLE);
                        front.setVisibility(View.INVISIBLE);
                        back.setVisibility(View.INVISIBLE);

                        refresh.setClickable(false);
                        front.setClickable(false);
                        back.setClickable(false);

                        weekDisplay.setText("");


                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 1500);

                    }).addOnFailureListener(exception -> Toast.makeText(getContext(), getActivity().getResources().getString(R.string.incorrectCodeOrNoConnection), Toast.LENGTH_LONG).show()).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });


                });
                alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // close dialog

                });
                alert.show();
            }


        });

        //can only be placed into thread if context is valid
        if (SharedPrefs.getBoolean(getContext(), "updateTableInThread")) {
            // create thread to post logic in
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> updateTable(weekendOnBoolean, table, view));
        } else {
            updateTable(weekendOnBoolean, table, view);
        }

        // on pressing btnUpload uploadImage() is called
        shareBagButton.setOnClickListener(v -> uploadDatabase());


        return view;
    }

    private void uploadDatabase() {
        // získat databázi pro předměty

        String dbnameSubjects = "Subject.db";
        Uri databaseSubjects = Uri.fromFile(requireContext().getDatabasePath(dbnameSubjects));

        // získat databázi pro pomůcky
        String dbnameItems = "Items.db";
        Uri databaseItems = Uri.fromFile(requireContext().getDatabasePath(dbnameItems));

        // získat sdílící kód, jestliže je uložen
        String codeName = SharedPrefs.getString(getContext(), SharedPrefs.CODE);

        if (codeName == null || codeName.equals("")) {
            // vygenerovat sdílící kód
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                name.append((char) (Math.random() * ((int) 'Z' - (int) 'A' + 1) + (int) 'A'));
            }
            codeName = name.toString();
            SharedPrefs.setString(getContext(), SharedPrefs.CODE, codeName);
        }
        // přidat posluchač pro nahrávání
        storageReference.child("subjects/" + codeName + ".db").putFile(databaseSubjects)
                .addOnSuccessListener(
                        taskSnapshot -> {
                        })

                .addOnFailureListener(e -> Toast.makeText(getContext(), R.string.internetConnection, Toast.LENGTH_LONG).show())
                .addOnProgressListener(
                        taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        });

        final String finalCodeName = codeName;
        // Posluchač progresu
        // načítací pole ukazuje procenta
        storageReference.child("items/" + codeName + ".db").putFile(databaseItems)
                .addOnSuccessListener(
                        taskSnapshot -> {
                            // když je hotovo progres ukazovaeč se zmrazí na půl vteřiny
                            Handler handler = new Handler();
                            handler.postDelayed(() -> progressBar.setProgress(0), 1500);

                            codeText.setText(finalCodeName);
                            codeTextInstructions.setText(R.string.codeNameInstructions);
                            shareBagButton.setText(R.string.updateBag);


                            Toast.makeText(getContext(), R.string.successUpload, Toast.LENGTH_LONG).show();
                        })

                .addOnFailureListener(e -> Toast.makeText(getContext(), R.string.internetConnection, Toast.LENGTH_LONG).show())
                .addOnProgressListener(
                        taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        });

    }

    @SuppressLint("SetTextI18n")
    private void updateTable(boolean weekendOnBoolean, TableLayout table, View view) {
        // reset table

        table.removeAllViews();

        boolean first = true;
        final boolean donNotShare;
        if (login.isLoggedIn()) {
            int max = Integer.MIN_VALUE;
            int current;

            RozvrhDen rozvrhDenMax = null;
            for (int k = 0; k < LoadBag.getCurrentRozvrh().getDny().size(); k++) {
                RozvrhDen den = LoadBag.getCurrentRozvrh().getDny().get(k);
                current = den.getHodiny().size();
                if (max < current) {
                    rozvrhDenMax = den;
                    max = current;
                }

            }
            donNotShare = max == 0;
            max = Math.max(max, 4);

            // add time

            if (rozvrhDenMax != null) {
                TableRow time = new TableRow(getContext());
                for (int i = 0; i < rozvrhDenMax.getHodiny().size(); i++) {
                    RozvrhHodina rh = rozvrhDenMax.getHodiny().get(i);

                    TextView textTime = new TextView(getContext());
                    textTime.setMinimumWidth(0);
                    textTime.setMinimumHeight(0);
                    textTime.setTextSize(12);
                    textTime.setFontFeatureSettings("bold");
                    textTime.setBackgroundResource(R.drawable.circle_white_half);
                    textTime.setGravity(Gravity.CENTER);
                    textTime.setText(rh.getBegintime() + " - " + rh.getEndtime());
                    time.addView(textTime);
                }
                table.addView(time);
            }


            for (int k = 0; k < LoadBag.getCurrentRozvrh().getDny().size(); k++) {
                RozvrhDen den = LoadBag.getCurrentRozvrh().getDny().get(k);
                TableRow row = new TableRow(getContext());
                row.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 0, 0, 0);

                int addedRows = 0;
                final boolean isDarkMode = SharedPrefs.getDarkMode(getContext());

                for (int j = 0; j < den.getHodiny().size(); j++) {
                    RozvrhHodina item = den.getHodiny().get(j);
                    addedRows++;
                    Button button = new Button(getContext());

                    if (first) {
                        // tutorial
                        ShowcaseConfig config = new ShowcaseConfig();
                        config.setDelay(200); // half second between each showcase view
                        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "recyclerViewerTutorialOverviewTableItem");
                        sequence.setConfig(config);
                        sequence.addSequenceItem(button,
                                getActivity().getResources().getString(R.string.clickIconToEdit), getActivity().getResources().getString(R.string.gotIt));
                        sequence.start();
                        first = false;
                    }

                    button.setMinimumWidth(0);
                    button.setMinimumHeight(0);
                    button.setBackgroundResource(R.drawable.rounded_textview_padding);
                    button.setGravity(Gravity.CENTER);
                    button.setText(item.getZkrpr());
                    if (isDarkMode) {
                        button.setTextColor(getResources().getColor(android.R.color.white));
                    } else {
                        button.setTextColor(getResources().getColor(android.R.color.black));
                    }

                    button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setTextSize(30);

                    button.setOnClickListener(v -> {
                        // go back to main activity
                        if (!item.getPr().equals("")) {
                            Intent i1;
                            i1 = new Intent(getActivity(), EditSubjectLoggedin.class);

                            i1.putExtra("subjectName", item.getPr());
                            i1.putExtra("teacher", item.getUc());
                            i1.putExtra("topic", item.getTema());
                            i1.putExtra("time1", item.getBegintime());
                            i1.putExtra("time2", item.getEndtime());

                            startActivity(i1);
                        }
                    });
                    row.addView(button);
                }
                if (addedRows < max) {
                    for (int j = 0; j < max - addedRows; j++) {
                        Button padding = new Button(getContext());
                        padding.setMinimumWidth(0);
                        padding.setMinimumHeight(0);
                        padding.setBackgroundResource(R.drawable.round_textview_padding_holiday);
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

        } else {

            view.findViewById(R.id.timeRow).setVisibility(View.GONE);
            int max = Integer.MIN_VALUE;
            int current = 0;
            for (int i = 1; i < 8; i++) {
                for (List<String> list : FeedReaderDbHelperSubjects.getContent(getActivity(), true)) {
                    if (list.get(i).equals("true")) {
                        current++;
                    }
                }
                if (max < current) {
                    max = current;
                }
                current = 0;
            }
            donNotShare = max == 0;
            max = Math.max(max, 4);

            for (int i = 1; i < (weekendOnBoolean ? 6 : 8); i++) {
                TableRow row = new TableRow(getContext());
                row.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 0, 0, 0);

                int addedRows = 0;
                final boolean isDarkMode = SharedPrefs.getDarkMode(getContext());

                for (List<String> list : FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
                    final Item item = new Item("null", list.get(0), false, getContext());

                    // days of the week logic

                    if (list.get(i).equals("true")) {
                        addedRows++;
                        Button button = new Button(getContext());

                        if (first) {
                            // tutorial
                            ShowcaseConfig config = new ShowcaseConfig();
                            config.setDelay(200); // half second between each showcase view
                            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "recyclerViewerTutorialOverviewTableItem");
                            sequence.setConfig(config);
                            sequence.addSequenceItem(button,
                                    getActivity().getResources().getString(R.string.clickIconToEdit), getActivity().getResources().getString(R.string.gotIt));
                            sequence.start();
                            first = false;
                        }

                        button.setMinimumWidth(0);
                        button.setMinimumHeight(0);
                        button.setBackgroundResource(R.drawable.rounded_textview_padding);
                        button.setGravity(Gravity.CENTER);
                        button.setText(item.getNameInitialsOfSubject());
                        if (isDarkMode) {
                            button.setTextColor(getResources().getColor(android.R.color.white));
                        } else {
                            button.setTextColor(getResources().getColor(android.R.color.black));
                        }

                        button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        button.setTextSize(30);

                        button.setOnClickListener(v -> {
                            // go back to main activity
                            Intent i1;
                            i1 = new Intent(getActivity(), EditSubject.class);

                            i1.putExtra("subjectName", item.getSubjectName());
                            startActivity(i1);
                        });

                        row.addView(button);


                    }

                }
                if (addedRows < max) {
                    for (int j = 0; j < max - addedRows; j++) {
                        Button padding = new Button(getContext());
                        padding.setMinimumWidth(0);
                        padding.setMinimumHeight(0);
                        padding.setBackgroundResource(R.drawable.round_textview_padding_holiday);
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
        }
        table.requestLayout();     // Not sure if this is needed.
        Button addSubjectButton = view.findViewById(R.id.showButton);
        // hide share if there are zero items
        if (donNotShare) {
            if (login.isLoggedIn()) {
                // holidays
                table.removeAllViews();
                for (int i = 1; i < (weekendOnBoolean ? 6 : 8); i++) {
                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    row.setPadding(0, 0, 0, 0);
                    for (int j = 0; j < 4; j++) {
                        Button padding = new Button(getContext());
                        padding.setMinimumWidth(0);
                        padding.setMinimumHeight(0);
                        padding.setBackgroundResource(R.drawable.round_textview_padding_holiday);
                        padding.setGravity(Gravity.CENTER);
                        padding.setTextColor(Color.WHITE);
                        padding.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        padding.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        padding.setText("");
                        padding.setTextSize(30);
                        row.addView(padding);
                    }
                    table.addView(row);
                }

            } else {

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
            }
        } else {
            addSubjectButton.setVisibility(View.GONE);
            // show table
            table.setVisibility(View.VISIBLE);
            view.findViewById(R.id.Table).setVisibility(View.VISIBLE);
            shareBagButton.setVisibility(View.VISIBLE);
        }
        // hide load bar
        progressBarLoadTable.setVisibility(View.INVISIBLE);

    }

    @SuppressLint("SetTextI18n")
    private void setWeekText(int week, TextView weekDisplay) {
        if (week == 0)
            weekDisplay.setText(getActivity().getResources().getString(R.string.currentWeek));
        else if (week == 1)
            weekDisplay.setText(getActivity().getResources().getString(R.string.nextWeek));
        else if (week == -1)
            weekDisplay.setText(getActivity().getResources().getString(R.string.previousWeek));
        else if (week < -1 && week > -5)
            weekDisplay.setText(-week + " " + getActivity().getResources().getString(R.string.backWeekCZSpecialCase));
        else if (week <= -5)
            weekDisplay.setText(-week + " " + getActivity().getResources().getString(R.string.backWeek));
        else if (week > 1 && week < 5)
            weekDisplay.setText(week + " " + getActivity().getResources().getString(R.string.forwardWeekbackWeekCZSpecialCase));
        else
            weekDisplay.setText(week + " " + getActivity().getResources().getString(R.string.forwardWeek));
    }


}


