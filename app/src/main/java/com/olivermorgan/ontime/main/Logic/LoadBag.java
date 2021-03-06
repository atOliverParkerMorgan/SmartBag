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
import com.olivermorgan.ontime.main.Adapter.Subject;
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
    Thread updateThread;
    Thread updateThread2;



    public LoadBag(Context context, LifecycleOwner lifecycleOwner){
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;

    }

    public Context getContext() {
        return context;
    }



    public void updateDatabaseWithNewBakalariTimeTable() {

       new Thread(()-> {
            List<Subject> subjects = new ArrayList<>();
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
                if (subjects.get(i).getName().equals("")) i++; // skip free hours

                intent.putExtra("Monday", Boolean.toString(subjects.get(i).getWeek()[0]));
                intent.putExtra("Tuesday", Boolean.toString(subjects.get(i).getWeek()[1]));
                intent.putExtra("Wednesday", Boolean.toString(subjects.get(i).getWeek()[2]));
                intent.putExtra("Thursday", Boolean.toString(subjects.get(i).getWeek()[3]));
                intent.putExtra("Friday", Boolean.toString(subjects.get(i).getWeek()[4]));
                intent.putExtra("Saturday", Boolean.toString(subjects.get(i).getWeek()[5]));
                intent.putExtra("Sunday", Boolean.toString(subjects.get(i).getWeek()[6]));
                intent.putExtra("putInToBag", false);

                try {
                    Log.d("what are you doing here", "here");
                    FeedReaderDbHelperSubjects.write(context, intent, subjects.get(i).getName());
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
            for (Subject subject : subjects) {
                // set default item name => items/pomůcky
                if (FeedReaderDbHelperItems.getContent(context, subject.getName()).size() == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("putInToBag", false);
                    List<Item> items = new ArrayList<>();
                    items.add(new Item("items", subject.getName(), false, context));
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
        updateThread = new Thread(()-> {
            List<Subject> subjects = new ArrayList<>();
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

            Log.e("Subject: ", "NEWWW");
            for (int i = 0; i < subjects.size(); i++) {
                Intent intent = new Intent();
                if (subjects.get(i).getName().equals("")) i++; // skip free hours

                intent.putExtra("Monday", Boolean.toString(subjects.get(i).getWeek()[0]));
                intent.putExtra("Tuesday", Boolean.toString(subjects.get(i).getWeek()[1]));
                intent.putExtra("Wednesday", Boolean.toString(subjects.get(i).getWeek()[2]));
                intent.putExtra("Thursday", Boolean.toString(subjects.get(i).getWeek()[3]));
                intent.putExtra("Friday", Boolean.toString(subjects.get(i).getWeek()[4]));
                intent.putExtra("Saturday", Boolean.toString(subjects.get(i).getWeek()[5]));
                intent.putExtra("Sunday", Boolean.toString(subjects.get(i).getWeek()[6]));
                intent.putExtra("putInToBag", false);

                try {
                    FeedReaderDbHelperSubjects.write(context, intent, subjects.get(i).getName());
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
            for (Subject subject : subjects) {
                // set default item name => items/pomůcky
                if (FeedReaderDbHelperItems.getContent(context.getApplicationContext(), subject.getName()).size() == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("putInToBag", false);
                    List<Item> items = new ArrayList<>();
                    items.add(new Item("items", subject.getName(), false, context));
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
        //debug timing: Log.d(TAG_TIMER, "displayWeek start " + Utils.getDebugTime());

        //what week is it from now (0: this, 1: next, -1: last, Integer.MAX_VALUE: permanent)

        updateThread2 = new Thread(()-> {
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
                if (item == null) {
                    // rozvrhLayout.empty();
                } else {
                    // rozvrhLayout.setRozvrh(item);
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
        });
        //debug timing: Log.d(TAG_TIMER, "displayWeek end " + Utils.getDebugTime());
    }

    private void onNetResponse(int code, Rozvrh rozvrh) {
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
        if (rozvrh != null) {
            Log.e("1","1");
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
            Log.e("4","4");
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
            Log.d("HERE1","HERE");
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



}


