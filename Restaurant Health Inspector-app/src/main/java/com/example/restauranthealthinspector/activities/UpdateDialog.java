package com.example.restauranthealthinspector.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.online.AppController;
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
    private InputStream inputRestaurant;
    private InputStream inputInspection;
    private ProgressBar progressBar;
    private DownloadManager manager;

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

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            final Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    positiveButton.setEnabled(false);

                    loadData();
                    //getDefaultInput();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                populateRestaurants();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            refreshActivity();
                            d.dismiss();
                        }
                    }, 6000);
                }
            });
        }
    }

    private void loadData()  {
        String restaurantURL = getResources().getString(R.string.restaurantURL);
        getData(restaurantURL, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    inputRestaurant = getInputStream(fileName[0]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);

        String inspectionURL = getResources().getString(R.string.inspectionURL);
        getData(inspectionURL, 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    inputInspection = getInputStream(fileName[1]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 4000);


        SharedPreferences sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("restaurant", fileName[0]);
        editor.putString("inspection", fileName[1]);
        editor.apply();
    }

    private InputStream getInputStream(String fileName) throws FileNotFoundException {
        Log.i("filename", fileName);
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        return new FileInputStream(file);
    }

    private void getDefaultInput()  {
        inputRestaurant = getResources().openRawResource(R.raw.restaurants_itr1);
        inputInspection = getResources().openRawResource(R.raw.inspectionreports_itr1);
    }

    private void downloadData(String dataURL, String lastModified, String name) {
        String fileName = name + ".csv";
        Log.i("downloading", fileName);

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

        downloading();
    }

    private void downloading() {
        boolean downloading = true;
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = null;
        while (downloading) {
            c = manager.query(query);
            if (c.moveToFirst()) {

                int totalSize = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int downloadedSize = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                progressBar.setProgress(downloadedSize/totalSize*100);

                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Log.i("downloading", "done downloading");
                    downloading = false;
                }
            }
        }
        c.close();
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

    private void refreshActivity()  {
        Intent intent = new Intent(getContext(), RestaurantListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", true);
        startActivity(intent);
    }
}
