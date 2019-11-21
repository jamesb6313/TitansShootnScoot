/*
MAIN source for this is: https://demonuts.com/listview-button/

FOR Gson
See: https://www.youtube.com/watch?v=jqv3Qkgop88
See - http://www.dev2qa.com/android-sharedpreferences-save-load-java-object-example/
 */
package com.e.titansshootnscoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
/*
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

 */
//new
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//FIXME: all the gson code causing error - was using for temp saving?
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
*/
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    public static ArrayList<Model> modelArrayList;
    public static TextView timerValue;

    private ListView lv;
    private CustomAdapter customAdapter;
    private Button btnnext;
    private Button startButton;
    private Button pauseButton;
    private Handler customHandler = new Handler();
    public static final String TAG = "MyInfo";

    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    //Gson gson;
    SimpleDateFormat simpleDateFormat;
    String start_time;
    Calendar calendar;
    long startTimeInMillis = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        //gson = new Gson();

        lv = findViewById(R.id.lv);
        btnnext = findViewById(R.id.next);

        timerValue = findViewById(R.id.timerValue);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);

        try {
            Long l_value = mpref.getLong("data2", 0);
            startTimeInMillis = l_value;
            modelArrayList = getModel();

            Log.i(TAG , "init: mpref str_value = " + l_value);
            if (l_value == 0) {
                startButton.setEnabled(true);
                customAdapter = new CustomAdapter(this);
                lv.setAdapter(customAdapter);

                //////
//                for (int i = 0; i < lv.getAdapter().getCount(); i++) {
//                    View view = lv.getAdapter().getView(i, null, null);
//                    view.getChildAt(i).setEnabled(false);
//                    //view.getContext(
//                    view.setClickable(false);
//                    Log.i(TAG , "enabled false " + i);
//                }
                ///////////

            } else {
                startButton.setEnabled(false);
                String str_value = mpref.getString("data1", "");
                setTitle("Titan's ScootnShoot: " + str_value);

                modelArrayList.clear();
                String json = mpref.getString("data3", "");
                /*
                Type type = new TypeToken<ArrayList<Model>>() {}.getType();

                modelArrayList = gson.fromJson(json, type);
                */

                customAdapter = new CustomAdapter(this);
                lv.setAdapter(customAdapter);
                Log.i(TAG,"onCreate() json " + json);

                customHandler.removeCallbacks(updateTimerThread);
                customHandler.postDelayed(updateTimerThread, 1000); // delay 1 second
            }
        } catch (Exception e) {
            Log.e(TAG,"onCreate() Error msg " + e.getMessage());
            mEditor.clear();
            customHandler.removeCallbacks(updateTimerThread);
        }

        // see: https://stackoverflow.com/questions/10201201/android-code-not-being-accepted-onitemclicklistener-must-implement-the
        // see: https://stackoverflow.com/questions/9596663/how-to-make-items-clickable-in-list-view
        // can use this lv.setClickable(true) or in xml make ListView clickable note: buttons must not be focusable

        // see: https://stackoverflow.com/questions/8955270/listview-items-are-not-clickable-why
        // activity_main.xml - make ListView Clickable & Focusable
        // lv_item.xml - make buttons not focusable
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!startButton.isEnabled()) {
                    Button btnPlus = view.findViewById(R.id.plus);
                    Button btnMinus = view.findViewById(R.id.minus);

                    btnMinus.setEnabled(true);
                    btnPlus.setEnabled(true);
                }
                String curHole = modelArrayList.get(position).getHole();
                Toast.makeText(MainActivity.this, "Current Hole = " + curHole, Toast.LENGTH_SHORT).show();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NextActivity.class);
                startActivity(intent);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                try {
                    startButton.setEnabled(false);

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    start_time = simpleDateFormat.format(calendar.getTime());
                    setTitle("Titan's ScootnShoot: " + start_time);

                    startTimeInMillis = calendar.getTimeInMillis();     //System.currentTimeMillis();

                    mEditor.putString("data1", start_time).apply();//commit();

                    mEditor.putLong("data2", startTimeInMillis).apply();

                    Log.i(TAG,"startButton.onClickListener() Start Time = " + start_time);

                    customHandler.removeCallbacks(updateTimerThread);
                    customHandler.postDelayed(updateTimerThread, 1000); // delay 1 second
                } catch (Exception e) {
                    Log.e(TAG,"startButton.onClickListener() Error msg " + e.getMessage());
                    customHandler.removeCallbacks(updateTimerThread);
                }
            }
        });
//FIXME: fix pauseButton - AlertDialog is main issue (new imports - solved??)

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Stopping Timer will end round. Continue?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customHandler.removeCallbacks(updateTimerThread);
                                    mEditor.clear().commit();
                                    Log.i(TAG,"stopButton.onClickListener() Editor Cleared");

                                    dialog.cancel();
                                }
                            });
                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG,"stopButton.onClickListener() Do not close");
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert1 = builder1.create();
                    alert1.show();


                } catch (Exception e) {
                    Log.e(TAG,"stopButton.onClickListener() Error msg " + e.getMessage());
                }

            }
        });
    }

    private long calcTimeDiff() {
        long elapsedTime = 0L;

        try {
            calendar = Calendar.getInstance();
            elapsedTime = calendar.getTimeInMillis() - startTimeInMillis;

//            int secs = (int) (elapsedTime / 1000);
//            int mins = secs / 60;
//            secs = secs % 60;
        } catch (Exception e) {
            Log.e(TAG,"calcTimeDiff() error msg " + e.getMessage());
        }
        return elapsedTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {

            String str_value = mpref.getString("data1", "");
            setTitle("Titan's ScootnShoot: " + str_value);

            /*String json = gson.toJson(modelArrayList);
            mEditor.putString("data3", json);
            Log.i(TAG,"onDestroy() json " + json);*/
            mEditor.apply();

            customHandler.removeCallbacks(updateTimerThread);
        } catch (Exception e) {
            Log.e(TAG,"onDestroy() error msg " + e.getMessage());
            mEditor.clear();
            customHandler.removeCallbacks(updateTimerThread);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            String str_value = mpref.getString("data1", "");
            setTitle("Titan's ScootnShoot: " + str_value);

            /*String json = gson.toJson(modelArrayList);
            mEditor.putString("data3", json);
            Log.i(TAG,"onPause() json " + json);*/
            mEditor.apply();

            customHandler.removeCallbacks(updateTimerThread);
        } catch (Exception e) {
            Log.e(TAG,"onPause() Error msg " + e.getMessage());
            mEditor.clear();
            customHandler.removeCallbacks(updateTimerThread);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Long l_value = mpref.getLong("data2", 0);

            startTimeInMillis = l_value;
            Log.i(TAG , "onResume() mpref l_value = " + l_value.toString());
            if (l_value != 0) {
                customHandler.removeCallbacks(updateTimerThread);
                customHandler.postDelayed(updateTimerThread, 1000); // delay 1 second
            }
        } catch (Exception e) {
            Log.e(TAG,"onResume() error msg " + e.getMessage());
            customHandler.removeCallbacks(updateTimerThread);
        }
    }

    private ArrayList<Model> getModel(){
        ArrayList<Model> list = new ArrayList<>();

        //int numHoles = R.integer.num_holes;
        for(int i = 0; i < 24; i++){

            Model model = new Model();
            model.setNumber(0);
            model.setHole("Hole" + (i + 1));
            model.setElapsedTime("-1");
            list.add(model);
        }
        return list;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            Long eTime;

            eTime = calcTimeDiff();
            int secs = (int) (eTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
//            Log.i(TAG, "Elapsed time " + String.format("%03d", mins) + ":"
//                    + String.format("%02d", secs));
            String value = String.format("%03d", mins) + ":" + String.format("%02d", secs);
            timerValue.setText(value);
            customHandler.postDelayed(this, 1000);
        }
    };

}
