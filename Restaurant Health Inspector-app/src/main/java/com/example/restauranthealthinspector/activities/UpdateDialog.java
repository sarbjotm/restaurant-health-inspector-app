package com.example.restauranthealthinspector.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.AppController;
import com.example.restauranthealthinspector.model.RestaurantsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * UpdateDialog class manages the update dialog, shown at the start of the app
 * The dialog is represented with the update layout
 */
public class UpdateDialog extends AppCompatDialogFragment {
    private Context context;
    private View view;
    private String fileName;
    private InputStream inputRestaurant;
    private InputStream inputInspection;
    private ProgressBar progressBar;
    DownloadManager manager;

    public UpdateDialog (Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    // Refer to Brian Fraser video: AlertDialog via Fragment: Android Programming
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view to show
        view = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_dialog, null);
        progressBar = view.findViewById(R.id.update_progressBar);
        //progressBar.setVisibility(View.INVISIBLE);

        // Ok Button
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressBar.setVisibility(View.VISIBLE);

                loadData();
                getDefaultInput();
                try {
                    populateRestaurants();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                refreshActivity();
            }
        };

        // No Button
        DialogInterface.OnClickListener noListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDefaultInput();

                try {
                    populateRestaurants();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refreshActivity();

            }
        };

        // Build the alert
        return new AlertDialog.Builder(getActivity())
                .setTitle("New Update!")
                .setView(view)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.no, noListener)
                .create();
    }

    private void loadData()  {
        String restaurantURL = getResources().getString(R.string.restaurantURL);
        getData(restaurantURL);
        /*
        try {
            inputRestaurant = getInputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */

        String inspectionURL = getResources().getString(R.string.inspectionURL);
        getData(inspectionURL);
        /*
        try {
            inputInspection = getInputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */
    }

    private InputStream getInputStream() throws FileNotFoundException {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        return new FileInputStream(file);
    }

    private void getDefaultInput()  {
        inputRestaurant = getResources().openRawResource(R.raw.restaurants_itr1);
        inputInspection = getResources().openRawResource(R.raw.inspectionreports_itr1);
    }

    // Code refer from Android JSON parsing using Volley
    // https://www.androidhive.info/2014/09/android-json-parsing-using-volley/
    private void getData(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            JSONArray resourceArray = result.getJSONArray("resources");

                            for (int i = 0; i < resourceArray.length(); i++) {
                                JSONObject resource = (JSONObject) resourceArray.get(i);

                                String format = resource.getString("format");
                                if (format.equals("CSV")) {
                                    String lastModified = resource.getString("last_modified");
                                    String dataURL = resource.getString("url");
                                    String name = resource.getString("name");

                                    Log.i("URL", dataURL);
                                    Log.i("lastModified", lastModified);
                                    downloadData(dataURL, lastModified, name);
                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("data", Objects.requireNonNull(e.getMessage()));
                        }
                    }

                }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("data", Objects.requireNonNull(error.getMessage()));
            }
        } );
        AppController.getInstance().addToRequestQueue(request);
    }

    private void downloadData(String dataURL, String lastModified, String name) {
        fileName = name + ".csv";

        File oldFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if(oldFile.exists()){
            oldFile.delete();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dataURL));
        request.setDescription("Data");
        request.setTitle(name);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        TextView textView = view.findViewById(R.id.update_txtMsg);
        String text = "Downloading " + name;
        textView.setText(text);

    }

    // Code from Brian Fraser videos
    // Read CSV Resource File: Android Programming
    private void populateRestaurants() throws IOException {
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputRestaurant, StandardCharsets.UTF_8)
        );

        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputInspection, StandardCharsets.UTF_8)
        );

        RestaurantsManager.getInstance(readerRestaurants, readerInspections);
    }

    private void refreshActivity() {
        Intent intent = new Intent(getContext(), RestaurantListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", true);
        startActivity(intent);
    }
}
