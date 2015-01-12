package com.example.notificationapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Notification;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity {

    private static final String LOG_TAG = "EmergencyAlertApp";
    private Boolean currentAlert = false;
    private boolean looking = true;
    private Timer timer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Alert();
            }
        }, 0, 1000);


    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void Alert() {
        try {

            if (!currentAlert) {
                CallApi task = new CallApi();
                final URL url = new URL("http://platform-core.herokuapp.com/police");
                task.execute(url);
                Log.e(LOG_TAG, "No Alert");
            }
            else {
                Log.e(LOG_TAG, "ALERT");
                final TextView newView = (TextView) findViewById(R.id.alerttext);
                final Button button = (Button) findViewById(R.id.button);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final LinearLayout layout = (LinearLayout) findViewById(R.id.mainlayout);

                        newView.setText("ALERT");
                        newView.setTextColor(Color.RED);
                        newView.setTextSize(20);
                        button.setEnabled(true);
                        currentAlert = false;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setEnabled(false);
                                newView.setText("No Alert");
                                newView.setTextColor(Color.WHITE);
                                try {
                                    DeleteAlert deleteAlert = new DeleteAlert();
                                    final URL url = new URL("http://platform-core.herokuapp.com/police");

                                    deleteAlert.execute(url);

                                }
                                catch (MalformedURLException e) {
                                    Log.e(LOG_TAG, "Couldn't delete alert");
                                }
                            }
                        });

                    }
                });

//                timer.cancel();
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL is Bad");
        }
    }


    private class CallApi extends AsyncTask<URL, Void, Boolean> {

        @Override
        protected Boolean doInBackground(URL... urls) {

            Boolean alert = false;
            for (URL url : urls) {

                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                try {


                    conn = (HttpURLConnection) url.openConnection();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        alert = true;
                    }



                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Error processing Places API URL", e);
                    return false;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error connecting to Places API", e);
                    return false;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }


            }
            return alert;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            currentAlert = result;
        }

    }


    private class DeleteAlert extends AsyncTask<URL, Void, Boolean> {

        @Override
        protected Boolean doInBackground(URL... urls) {

            Boolean alert = false;
            for (URL url : urls) {

                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                try {

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");


                    if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                        alert = true;
                    }
                    else {
                        Log.e(LOG_TAG, "DELETE Didn't work");
                    }



                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Error processing Places API URL", e);
                    return false;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error connecting to Places API", e);
                    return false;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }


            }
            return alert;
        }

        @Override
        protected void onPostExecute(Boolean result) {
//             = result;
        }

    }
}
