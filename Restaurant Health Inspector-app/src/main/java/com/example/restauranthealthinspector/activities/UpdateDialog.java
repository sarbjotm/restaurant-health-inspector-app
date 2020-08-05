package com.example.restauranthealthinspector.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.online.AppController;
import com.example.restauranthealthinspector.model.RestaurantsManager;
import com.example.restauranthealthinspector.model.online.DataLoad;
import com.example.restauranthealthinspector.model.online.DataRequest;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * UpdateDialog class manages the update dialog, shown at the start of the app
 * The dialog is represented with the update layout
 */
public class UpdateDialog extends AppCompatDialogFragment {
    private Context context;
    private View view;
    private ProgressBar progressBar;
    private DownloadManager manager;
    private DataRequest restaurantData;
    private DataRequest inspectionData;
    private DataLoad dataLoad;
    private boolean isAvailable;

    public UpdateDialog(Context context, DataRequest restaurantData, DataRequest inspectionData, DataLoad dataLoad) {
        this.context = context;
        this.restaurantData = restaurantData;
        this.inspectionData = inspectionData;
        this.dataLoad = dataLoad;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    // Refer to Brian Fraser video: AlertDialog via Fragment: Android Programming
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view to show
        view = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_dialog, null);
        progressBar = view.findViewById(R.id.update_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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
                dataLoad.loadData();
                refreshActivity();

            }
        };

        // Build the alert
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.new_update))
                .setView(view)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.no, noListener)
                .setCancelable(false)
                .create();

    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        //Dialog will not close if back button or press outside of dialog box
        d.setCanceledOnTouchOutside(false);
        d.setCancelable(false);
        if (d != null) {
            final Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    positiveButton.setEnabled(false);
                    isNetworkAvailable();
                    if (isAvailable){
                        dataLoad.saveFileName(restaurantData, inspectionData);
                        downloadData(restaurantData);
                        downloadData(inspectionData);
                    }

                    else{
                        String noInternet = getString(R.string.no_internet);
                        Toast.makeText(context,noInternet, Toast.LENGTH_LONG).show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataLoad.loadData();
                            refreshActivity();
                        }
                    }, 5000);
                }
            });
        }

    }

    private void downloadData(DataRequest data) {
            String name = data.getName();
            String dataURL = data.getDataURL();

            String fileName = name + ".csv";
            Log.i("downloading", fileName);

            File oldFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

            if (oldFile.exists()) {
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
            String text = getString(R.string.downloading);
            text += " " + name;
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
                progressBar.setProgress(downloadedSize / totalSize * 100);

                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Log.i("downloading", "done downloading");
                    downloading = false;
                }
            }
        }
        c.close();
    }

    private void isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true;
            }
            else{
                isAvailable = false;
            }
        }
    }

    private void refreshActivity() {
        Intent intent = new Intent(getContext(), RestaurantListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fromDialog", true);
        startActivity(intent);
    }
}
