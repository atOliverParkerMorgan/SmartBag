package com.olivermorgan.ontime.main.Logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MainTitle;
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
    // messy code should be re-written !!!
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
    private boolean databasehasBeenUpdated = true;

    private final Context context;

    public Rozvrh getRozvrhVariable() {
        return rozvrh;
    }

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

       new Thread(()-> {
            List<MainTitle> mainTitles = new ArrayList<>();
            FeedReaderDbHelperSubjects.deleteAllSubject(context);
            // setup current rozvrh so overview can display it
            currentRozvrh = rozvrh;

            int row = rozvrh.getDny().size();
            for (int i = 0; i < row; i++) {
                RozvrhDen den = rozvrh.getDny().get(i);


                for (int j = 0; j < den.getHodiny().size(); j++) {
                    RozvrhHodina item = den.getHodiny().get(j);

                    // check if database already has element
                    boolean found = false;
                    for (MainTitle s : mainTitles) {
                        if (s.getName().equals(item.getPr())) {
                            s.setDay(i);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        MainTitle s = new MainTitle(item.getPr(), item.getZkrpr());
                        s.setDay(i);
                        mainTitles.add(s);

                    }
                }
            }


            for (int i = 0; i < mainTitles.size(); i++) {
                Intent intent = new Intent();
                if (mainTitles.get(i).getName().equals("")) i++; // skip free hours

                intent.putExtra("Monday", Boolean.toString(mainTitles.get(i).getWeek()[0]));
                intent.putExtra("Tuesday", Boolean.toString(mainTitles.get(i).getWeek()[1]));
                intent.putExtra("Wednesday", Boolean.toString(mainTitles.get(i).getWeek()[2]));
                intent.putExtra("Thursday", Boolean.toString(mainTitles.get(i).getWeek()[3]));
                intent.putExtra("Friday", Boolean.toString(mainTitles.get(i).getWeek()[4]));
                intent.putExtra("Saturday", Boolean.toString(mainTitles.get(i).getWeek()[5]));
                intent.putExtra("Sunday", Boolean.toString(mainTitles.get(i).getWeek()[6]));
                intent.putExtra("putInToBag", false);

                try {
                    FeedReaderDbHelperSubjects.write(context, intent, mainTitles.get(i).getName(), "subject");
                }catch (Exception e){
                    ((Activity) getContext()).runOnUiThread(() -> {
                        if (SharedPrefs.getBoolean(getContext(), "Languages")) {
                            Toast.makeText(context, "Jejda něco se pokazilo v databázi.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Oops something went work in the database.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            for (MainTitle mainTitle : mainTitles) {
                // set default item name => items/pomůcky
                if (FeedReaderDbHelperItems.getContent(context, mainTitle.getName()).size() == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("putInToBag", false);
                    List<Item> items = new ArrayList<>();
                    Item i = new Item("items for "+ mainTitle.getShortName(), mainTitle.getName(),false,"subject", context);
                    i.setMainTitle(mainTitle);
                    items.add(i);
                    if (!FeedReaderDbHelperItems.write(context, intent, items)) {
                        Toast.makeText(context, context.getResources().getString(R.string.databaseError),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).start();






    }

    public void updateDatabaseWithNewBakalariTimeTable(Runnable runnable) {
        FeedReaderDbHelperSubjects.deleteAllSubject(context);
        Thread updateThread = new Thread(()-> {
            List<MainTitle> mainTitles = new ArrayList<>();
            // setup current rozvrh so overview can display it
            currentRozvrh = rozvrh;

            int row = rozvrh.getDny().size();
            for (int i = 0; i < row; i++) {
                RozvrhDen den = rozvrh.getDny().get(i);


                for (int j = 0; j < den.getHodiny().size(); j++) {
                    RozvrhHodina item = den.getHodiny().get(j);

                    // check if database already has element
                    boolean found = false;
                    for (MainTitle s : mainTitles) {
                        if (s.getName().equals(item.getPr())) {
                            s.setDay(i);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        MainTitle s = new MainTitle(item.getPr(), item.getZkrpr());
                        s.setDay(i);
                        mainTitles.add(s);

                    }
                }
            }

            Log.e("Subject: ", "NEWWW");
            for (int i = 0; i < mainTitles.size(); i++) {
                Intent intent = new Intent();
                if (mainTitles.get(i).getName().equals("")) i++; // skip free hours

                intent.putExtra("Monday", Boolean.toString(mainTitles.get(i).getWeek()[0]));
                intent.putExtra("Tuesday", Boolean.toString(mainTitles.get(i).getWeek()[1]));
                intent.putExtra("Wednesday", Boolean.toString(mainTitles.get(i).getWeek()[2]));
                intent.putExtra("Thursday", Boolean.toString(mainTitles.get(i).getWeek()[3]));
                intent.putExtra("Friday", Boolean.toString(mainTitles.get(i).getWeek()[4]));
                intent.putExtra("Saturday", Boolean.toString(mainTitles.get(i).getWeek()[5]));
                intent.putExtra("Sunday", Boolean.toString(mainTitles.get(i).getWeek()[6]));
                intent.putExtra("putInToBag", false);

                try {
                    FeedReaderDbHelperSubjects.write(context, intent, mainTitles.get(i).getName(), "subject");
                }catch (Exception e){
                    ((Activity) getContext()).runOnUiThread(() -> {
                        if (SharedPrefs.getBoolean(getContext(), "Languages")) {
                            Toast.makeText(context, "Jejda něco se pokazilo v databázi.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Oops something went work in the database.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            for (MainTitle subject : mainTitles) {
                // set default item name => items/pomůcky
                if (FeedReaderDbHelperItems.getContent(context.getApplicationContext(), subject.getName()).size() == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("putInToBag", false);
                    List<Item> items = new ArrayList<>();
                    Item i = new Item("items for "+subject.getShortName(), subject.getName(),false,"subject", context);
                    i.setMainTitle(subject);
                    items.add(i);
                    if (!FeedReaderDbHelperItems.write(context, intent, items)) {
                        Toast.makeText(context, context.getResources().getString(R.string.databaseError),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
            ((Activity)getContext()).runOnUiThread(runnable);
        });

        updateThread.start();




    }


    public void getRozvrh(int weekIndex) {

        //what week is it from now (0: this, 1: next, -1: last, Integer.MAX_VALUE: permanent)

        new Thread(()-> {
            if (weekIndex == Integer.MAX_VALUE)
                week = null;
            else
                week = Utils.getDisplayWeekMonday(getContext()).plusWeeks(weekIndex);


            //String infoMessage = Utils.getfl10nedWeekString(weekIndex, getContext());
            if (offline) {
                ((Activity)getContext()).runOnUiThread(()->MainActivity.showAlert(context, context.getResources().getString(R.string.OFFLINE), context.getResources().getString(R.string.OFFLINEsubtext)));
            }


            rozvrhAPI = AppSingleton.getInstance(context).getRozvrhAPI();

            if (liveData != null)
                ((Activity)getContext()).runOnUiThread(()->liveData.removeObservers(lifecycleOwner));
            ((Activity)getContext()).runOnUiThread(()-> {
                liveData = rozvrhAPI.getLiveData(week);
                RozvrhWrapper rw = liveData.getValue();
                Rozvrh item = rw == null ? null : liveData.getValue().getRozvrh();
                if (item != null) {
                    if (rw.getSource() == RozvrhWrapper.SOURCE_MEMORY) {
                        if (offline) {
                            ((Activity) getContext()).runOnUiThread(() -> MainActivity.showAlert(context, context.getResources().getString(R.string.OFFLINE), context.getResources().getString(R.string.OFFLINEsubtext)));
                        }
                    }
                }

                liveData.observe(lifecycleOwner, rozvrhWrapper -> {
                    if (rozvrhWrapper.getSource() == RozvrhWrapper.SOURCE_CACHE) {
                        onCacheResponse(rozvrhWrapper.getCode(), rozvrhWrapper.getRozvrh());

                    } else if (rozvrhWrapper.getSource() == RozvrhWrapper.SOURCE_NET) {
                        onNetResponse(rozvrhWrapper.getCode(), rozvrhWrapper.getRozvrh());
                    }
                });
            });
        }).start();


    }

    private void onNetResponse(int code, Rozvrh rozvrh) {
        //check if fragment was not removed while loading
        if (rozvrh != null) {
            rozvrhAPI.clearMemory();
            this.rozvrh = rozvrh;
            if(databasehasBeenUpdated) {
                updateDatabaseWithNewBakalariTimeTable();
                databasehasBeenUpdated = false;
            }

        }
        //onNetLoaded
        if (code == SUCCESS) {
            if (offline) {
                rozvrhAPI.clearMemory();
                this.rozvrh = rozvrh;
                MainActivity.showAlert(context,context.getResources().getString(R.string.OFFLINE), context.getResources().getString(R.string.OFFLINEsubtext));
            }
            offline = false;

        } else {
            offline = true;
            //displayInfo.setLoadingState(DisplayInfo.ERROR);

            if (code == UNREACHABLE) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.UNREACHABLE),context.getResources().getString(R.string.UNREACHABLEsubtext));
            }
            else if (code == UNEXPECTED_RESPONSE) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.ERRORsubtext));
            }
            else if (code == LOGIN_FAILED) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.LOGINFAIL),context.getResources().getString(R.string.LOGINFAIL));
            }



        }
    }

    private void onNetResponse(int code, Rozvrh rozvrh, Runnable runnable) {
        //check if fragment was not removed while loading
        //check if fragment was not removed while loading
        Log.e("HERE","HERE");
        if (rozvrh != null) {
            rozvrhAPI.clearMemory();
            this.rozvrh = rozvrh;
            updateDatabaseWithNewBakalariTimeTable(runnable);
        }
        //onNetLoaded
        if (code == SUCCESS) {
            if (offline) {
                rozvrhAPI.clearMemory();
                this.rozvrh = rozvrh;
                MainActivity.showAlert(context,context.getResources().getString(R.string.OFFLINE), context.getResources().getString(R.string.OFFLINEsubtext));
                runnable.run();
            }
            else {
                if (rozvrh == null){
                    runnable.run();
                }
                offline = false;
            }

        } else {
            offline = true;
            //displayInfo.setLoadingState(DisplayInfo.ERROR);

            if (code == UNREACHABLE) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.UNREACHABLE),context.getResources().getString(R.string.UNREACHABLEsubtext));
            }
            else if (code == UNEXPECTED_RESPONSE) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.ERRORsubtext));
            }
            else if (code == LOGIN_FAILED) {
                MainActivity.showAlert(context,context.getResources().getString(R.string.LOGINFAIL),context.getResources().getString(R.string.LOGINFAIL));
            }
            if(rozvrh==null) runnable.run();



        }

    }

    private void onCacheResponse(int code, Rozvrh rozvrh) {
        //check if fragment was not removed while loading
        if (code == SUCCESS) {
            this.rozvrh = rozvrh;
            updateDatabaseWithNewBakalariTimeTable();

        }
    }



    public void refresh(int weekIndex, Runnable runnable) {
        // displayInfo.setLoadingState(DisplayInfo.LOADING);
        if (weekIndex == Integer.MAX_VALUE)
            week = null;
        else
            week = Utils.getDisplayWeekMonday(getContext()).plusWeeks(weekIndex);

        rozvrhAPI.refresh(week, rw -> {

            onNetResponse(rw.getCode(), rw.getRozvrh(),runnable);
            /*}*/
        });
    }

    public static Rozvrh getCurrentRozvrh(){
        return currentRozvrh;
    }

    public static RozvrhHodina getRozvrhHodinaFromRozvrh(String name){
        int row = getCurrentRozvrh().getDny().size();
        for (int i = 0; i < row; i++) {
            RozvrhDen den = getCurrentRozvrh().getDny().get(i);

            for (int j = 0; j < den.getHodiny().size(); j++) {
                RozvrhHodina item = den.getHodiny().get(j);
                if (item.getPr().equals(name)) {
                    return item;
                }
            }

        }
        return null;
    }

    public void setDatabasehasBeenUpdated(boolean databasehasBeenUpdated) {
        this.databasehasBeenUpdated = databasehasBeenUpdated;
    }
}


