package com.hackru.beaconapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final Pattern sPattern = Pattern.compile(".*<actualvolume>(\\d+)<///actualvolume>.*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void IncVol(View view) {
        Toast.makeText(MainActivity.this, "Volume Increased!", Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("http://192.168.1.14:8090/volume");

                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        Log.e("aa", "connected");
                        InputStream in = urlConnection.getInputStream();
                        Log.e("aa", "have input");
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        Log.e("ab", total.toString());
                        int a = total.toString().indexOf("actualvolume>") + "actualvolume>".length();
                        int b = total.toString().indexOf("</ac");
                        int c = Integer.parseInt(total.toString().substring(a, b))+1;
                        String newVol = "<volume>" + c + "</volume>";
                        Log.e("volume",newVol);


                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");

                        OutputStream out = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
                        writer.write(newVol);
                        writer.flush();
                        writer.close();
                        out.close();

                        int responseCode=urlConnection.getResponseCode();

                        if (responseCode == HttpsURLConnection.HTTP_OK)
                            Log.e("good?","yes");
                        else
                            Log.e("good?","no");

                        } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void DecVol(View view) {
        Toast.makeText(MainActivity.this, "Volume Decreased!",
                Toast.LENGTH_LONG).show();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("http://192.168.1.14:8090/volume");

                        urlConnection = (HttpURLConnection) url
                                .openConnection();
                        urlConnection.setRequestMethod("GET");
                        Log.e("aa", "connected");
                        InputStream in = urlConnection.getInputStream();
                        Log.e("aa", "have input");
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        Log.e("ab", total.toString());
                        int a = total.toString().indexOf("actualvolume>") + "actualvolume>".length();
                        int b = total.toString().indexOf("</ac");
                        int c = Integer.parseInt(total.toString().substring(a, b))-1;
                        String newVol = "<volume>" + c + "</volume>";
                        Log.e("volume",newVol);


                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");

                        OutputStream out = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
                        writer.write(newVol);
                        writer.flush();
                        writer.close();
                        out.close();

                        int responseCode=urlConnection.getResponseCode();

                        if (responseCode == HttpsURLConnection.HTTP_OK)
                            Log.e("good?","yes");
                        else
                            Log.e("good?","no");

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void a(View view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select music"), 1);
    }
}

