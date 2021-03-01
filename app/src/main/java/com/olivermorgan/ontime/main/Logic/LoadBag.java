package com.olivermorgan.ontime.main.Logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.Adapter.Subject;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.AppSingleton;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.RozvrhAPI;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.RozvrhWrapper;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.Utils;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.Rozvrh;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhDen;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhHodina;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class LoadBag {
    public static final int SUCCESS = 0;
    public static final int LOGIN_FAILED = 1;
    public static final int UNEXPECTED_RESPONSE = 2;
    public static final int UNREACHABLE = 3;
    public static final int NO_CACHE = 4;
    public static final int ERROR = 5;
    public static final int OFFLINE = 5;

    private LiveData<RozvrhWrapper> liveData;

    private static Rozvrh currentRozvrh;


    private LocalDate week = null;
    private boolean offline = false;
    private RozvrhAPI rozvrhAPI = null;

    private final Context context;
    private final LifecycleOwner lifecycleOwner;
     Rozvrh rozvrh;


    public LoadBag(Context context, LifecycleOwner lifecycleOwner){
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;

    }

    public Context getContext() {
        return context;
    }



    public void updateDatabaseWithNewBakalariTimeTable() {
        List<Subject> subjects = new ArrayList<>();
        context.deleteDatabase(FeedReaderDbHelperSubjects.DATABASE_NAME);

        // setup current rozvrh so overview can display it
        currentRozvrh = rozvrh;

        int row = rozvrh.getDny().size();
        for (int i = 0; i < row; i++) {
            RozvrhDen den = rozvrh.getDny().get(i);


            for (int j = 0; j < den.getHodiny().size(); j++) {
                RozvrhHodina item = den.getHodiny().get(j);

                // check if database already has element
                boolean found = false;
                for (Subject s : subjects) {
                    if (s.getName().equals(item.getPr())) {
                        s.setDay(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Subject s = new Subject(item.getPr());
                    s.setDay(i);
                    subjects.add(s);

                }
            }
        }


        for (int i = 0; i < subjects.size(); i++) {
            Intent intent = new Intent();
            if(subjects.get(i).getName().equals("")) i++; // skip free hours

            intent.putExtra("Monday", Boolean.toString(subjects.get(i).getWeek()[0]));
            intent.putExtra("Tuesday", Boolean.toString(subjects.get(i).getWeek()[1]));
            intent.putExtra("Wednesday", Boolean.toString(subjects.get(i).getWeek()[2]));
            intent.putExtra("Thursday", Boolean.toString(subjects.get(i).getWeek()[3]));
            intent.putExtra("Friday", Boolean.toString(subjects.get(i).getWeek()[4]));
            intent.putExtra("Saturday", Boolean.toString(subjects.get(i).getWeek()[5]));
            intent.putExtra("Sunday", Boolean.toString(subjects.get(i).getWeek()[6]));
            intent.putExtra("putInToBag", false);

            if (!FeedReaderDbHelperSubjects.write(context, intent, subjects.get(i).getName())) {
                Toast.makeText(context, R.string.absolute_error,Toast.LENGTH_LONG).show();
            }
        }
        for (Subject subject: subjects) {
            if(FeedReaderDbHelperItems.getContent(context, subject.getName()).size()==0){
                Intent intent = new Intent();
                intent.putExtra("putInToBag", false);
                List<Item> items = new ArrayList<>();
                items.add(new Item(context.getResources().getString(R.string.itemsForSubject)
                        +" "+subject.getName(),subject.getName(),false));

                if(!FeedReaderDbHelperItems.write(context,  intent,  items)){
                    Toast.makeText(context, R.string.databaseError,
                            Toast.LENGTH_LONG).show();
                }
            }
        }




    }

    public void getRozvrh(int weekIndex) {
        //debug timing: Log.d(TAG_TIMER, "displayWeek start " + Utils.getDebugTime());

        //what week is it from now (0: this, 1: next, -1: last, Integer.MAX_VALUE: permanent)
        if (weekIndex == Integer.MAX_VALUE)
            week = null;
        else
            week = Utils.getDisplayWeekMonday(getContext()).plusWeeks(weekIndex);


        //String infoMessage = Utils.getfl10nedWeekString(weekIndex, getContext());
        if (offline) {
            MainActivity.showAlert(context,"OFFLINE");
        }


        rozvrhAPI = AppSingleton.getInstance(context).getRozvrhAPI();

        if (liveData != null)
            liveData.removeObservers(lifecycleOwner);
        liveData = rozvrhAPI.getLiveData(week);
        RozvrhWrapper rw = liveData.getValue();
        Rozvrh item = rw == null ? null : liveData.getValue().getRozvrh();
        if (item != null) {
            // rozvrhLayout.setRozvrh(item);
            if (rw.getSource() == RozvrhWrapper.SOURCE_MEMORY){
                if (offline) {
                    // displayInfo.setLoadingState(DisplayInfo.ERROR);
                } else {
                    // displayInfo.setLoadingState(DisplayInfo.LOADED);
                }
            }
        } else {
            // rozvrhLayout.empty();
        }

        liveData.observe(lifecycleOwner, rozvrhWrapper -> {
            if (rozvrhWrapper.getSource() == RozvrhWrapper.SOURCE_CACHE){
               onCacheResponse(rozvrhWrapper.getCode(), rozvrhWrapper.getRozvrh());

            }else if (rozvrhWrapper.getSource() == RozvrhWrapper.SOURCE_NET){
                onNetResponse(rozvrhWrapper.getCode(), rozvrhWrapper.getRozvrh());
            }
        });

        //debug timing: Log.d(TAG_TIMER, "displayWeek end " + Utils.getDebugTime());
    }

    private void onNetResponse(int code, Rozvrh rozvrh) {
        int r = ERROR;
        //check if fragment was not removed while loading
        if (rozvrh != null) {
            rozvrhAPI.clearMemory();
            this.rozvrh = rozvrh;
            updateDatabaseWithNewBakalariTimeTable();

        }
        //onNetLoaded
        if (code == SUCCESS) {
            if (offline) {
                rozvrhAPI.clearMemory();
                this.rozvrh = rozvrh;
                updateDatabaseWithNewBakalariTimeTable();
                MainActivity.showAlert(context,"OFFLINE");
            }
            offline = false;

        } else {
            offline = true;
            //displayInfo.setLoadingState(DisplayInfo.ERROR);

            if (code == UNREACHABLE) {
                MainActivity.showAlert(context,"UNREACHABLE");
            } else if (code == UNEXPECTED_RESPONSE) {
                MainActivity.showAlert(context,"WTF ERROR");
            } else if (code == LOGIN_FAILED) {
                MainActivity.showAlert(context,"LOGIN FAIL");
            }



        }
    }

    private void onCacheResponse(int code, Rozvrh rozvrh) {
        //check if fragment was not removed while loading
        if (code == SUCCESS) {
            this.rozvrh = rozvrh;
            updateDatabaseWithNewBakalariTimeTable();

        }else {
            MainActivity.showAlert(context,"ERROR");
        }
    }

    public void refresh() {
        // displayInfo.setLoadingState(DisplayInfo.LOADING);

        rozvrhAPI.refresh(week, rw -> {
            /*if (rw.getCode() != SUCCESS){
                displayWeek(weekIndex, false);
            }else {*/
            onNetResponse(rw.getCode(), rw.getRozvrh());
            /*}*/
        });
    }

    public static Rozvrh getCurrentRozvrh(){
        return currentRozvrh;
    }

}


