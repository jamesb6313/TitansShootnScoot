package com.e.titansshootnscoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
//error
//import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.Toast;

public class NextActivity extends AppCompatActivity {

    private TextView tv1;
    private WebView tv;
    private Button saveButton;
    private Button shareButton;
    private String html = "";
    private String path = "";
    private String fn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        tv = findViewById(R.id.tv);
        saveButton = findViewById(R.id.save);
        shareButton = findViewById(R.id.share);
        shareButton.setVisibility(View.INVISIBLE);

        //int numHoles = R.integer.num_holes;
        int totalShoots = 0;

        final StringBuilder sbHtml = new StringBuilder();


        html = "<table border='1' bordercolor='green'><tr><b><th>Hole</th><th>Score</th><th>Time</th><th>Hole</th><th>Score</th><th>Time</th></b></tr>";
//        html += "<tr>";

        sbHtml.append(html);
        sbHtml.append("<tr>");

        for (int i = 0; i < 12; i++) {

            String hole_num1, hole_num2, time1, time2, tempStr;
            int score1, score2;

            hole_num1 = MainActivity.modelArrayList.get(i).getHole();
            score1 = MainActivity.modelArrayList.get(i).getNumber();
            hole_num2 = MainActivity.modelArrayList.get(i + 12).getHole();
            score2 = MainActivity.modelArrayList.get(i + 12).getNumber();

            time1 = MainActivity.modelArrayList.get(i).getElapsedTime();
            if (!time1.equals("-1")) {
                try {
                    int colonPos = time1.indexOf(':');
                    String time1Min = time1.substring(0, colonPos);
                    int t1 = Integer.parseInt(time1Min);
                    time1 = Integer.toString(t1);
                } catch (Exception e) {
                    Log.e("MyInfo", "onCreate() ERROE msg: " + e.getMessage() + ", time1 = " + time1);
                }
            }


            time2 = MainActivity.modelArrayList.get(i + 12).getElapsedTime();
            if (!time2.equals("-1")) {
                try {
                    int colonPos = time2.indexOf(':');
                    String time2Min = time2.substring(0, colonPos);
                    int t2 = Integer.parseInt(time2Min);
                    time2 = Integer.toString(t2);
                } catch (Exception e) {
                    Log.e("MyInfo", "onCreate() ERROE msg: " + e.getMessage() + ", time2 = " + time2);
                }
            }

            tempStr = "<td>" + hole_num1 + "</td>";
            sbHtml.append(tempStr);
            tempStr = "<td>" + score1 + "</td>";
            sbHtml.append(tempStr);
            tempStr = "<td>" + time1 + "</td>";
            sbHtml.append(tempStr);
            tempStr = "<td>" + hole_num2 + "</td>";
            sbHtml.append(tempStr);
            tempStr = "<td>" + score2 + "</td>";
            sbHtml.append(tempStr);
            tempStr = "<td>" + time2 + "</td>";
            sbHtml.append(tempStr);
            sbHtml.append("</tr>");

            totalShoots += (score1 + score2);
        }
        sbHtml.append("</table></br>Shoots = ");
        sbHtml.append(totalShoots);
        sbHtml.append(", Minutes = ");

        String totalTime = MainActivity.timerValue.getText().toString();
        String totalMinutes = "";
        try {
            int colonPos = totalTime.indexOf(':');
            totalMinutes = totalTime.substring(0, colonPos);
            int tt = Integer.parseInt(totalMinutes);
            totalMinutes = Integer.toString(tt);
        } catch (Exception e) {
            Log.e("MyInfo", "onCreate() ERROE msg: " + e.getMessage() + ", time2 = " + totalTime);
        }

        sbHtml.append(totalMinutes);

        int mins = 0;
        try {
            mins = Integer.parseInt(totalMinutes);
        }
        catch(NumberFormatException nfe) {
            // Handle parse error.
            Log.e("MyInfo", "onCreater() Error msg " + nfe.getMessage());
        }

        int tScore = mins + totalShoots;
        String tempstr = ", Score = " + tScore;
        sbHtml.append(tempstr);

        html = sbHtml.toString();

        tv.loadData(html , "text/html; charset=UTF-8", null);

        /*
        see: http://stackoverflow.com/questions/31553402/save-string-as-html-file-android

        in manifest add
        <uses-permission android.name="android.permission:WRITE_EXTERNAL_STORAGE"/>
         */
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                try {
                    saveHtmlFile();
                    saveButton.setVisibility(View.INVISIBLE);
                    shareButton.setVisibility(View.VISIBLE);
                }
                catch (Exception e) {
                    Log.e("Save","saveButton.onClickListener() ERROR " + e.getMessage());
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                try {
                    shareFile(path + "/Documents/" + fn);

                    Toast toast = Toast.makeText(getApplicationContext(), "Scorecard has been shared.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.START, 0, 0);
                    toast.show();
                }
                catch (Exception e) {
                    Log.e("Save","shareButton.onClickListener() ERROR " + e.getMessage());
                }

            }
        });
    }

    // see: https://stackoverflow.com/questions/11254523/android-runonuithread-explanation
    private void saveHtmlFile(){
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    path = Environment.getExternalStorageDirectory().getPath();
                    String timeStr = DateFormat.format("dd_MM_yyyy_hh_mm_ss", System.currentTimeMillis()).toString();
                    fn = "TitanScoreCard_" + timeStr + ".html";
                    File file = new File(path + "/Documents",fn);
                    //String html = "<html><head><title>Title</title></head><body>This is random text<body></html>";

                    html = "<h2>" + fn + "</h2></br>" + html;
                    FileOutputStream out = new FileOutputStream(file);
                    byte[] data = html.getBytes();
                    out.write(data);
                    out.close();
                    Log.i("Save", "File Save : " + file.getPath());
                    Toast toast = Toast.makeText(getApplicationContext(), "Scorecard has been saved. See: " + file.getPath(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.START, 0, 0);
                    toast.show();

                    //shareFile(path + "/Documents/" + fn);

                } catch (FileNotFoundException e) {
                    Log.e("Save", "saveHtmlFile() FILE NOT FOUND ERROR " + e.getMessage());
                } catch (IOException e) {
                    Log.e("Save", "saveHtmlFile() IO ERROR " + e.getMessage());
                }
            }
        });
    }

    /*
    See: https://stackoverflow.com/questions/17985646/android-sharing-files-by-sending-them-via-email-or-other-apps
    and  https://stackoverflow.com/questions/11254523/android-runonuithread-explanation
     */
    private void shareFile(final String filePath) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    File f = new File(filePath);

                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    File fileWithinMyDir = new File(filePath);

                    if (fileWithinMyDir.exists()) {
                        intentShareFile.setType("text/html");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f)); //Uri.parse("file://" + filePath));
                        Log.i("Save", "1 : Uri.parse(file:// ..." + filePath);
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "MyApp File Share: " + f.getName());
                        Log.i("Save", "2 : MyApp File Share  ..." + f.getName());
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "MyApp File Share: " + f.getName());

                        startActivity(Intent.createChooser(intentShareFile, f.getName()));
                    }
                } catch (Exception e){
                    Log.e("Save", "shareFile() ERROR " + e.getMessage());
                }
            }
        });
    }
}

