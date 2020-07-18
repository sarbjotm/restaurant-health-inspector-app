package com.example.restauranthealthinspector.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restauranthealthinspector.R;
import com.example.restauranthealthinspector.model.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * Congrats class manages the congrats dialog, shown the game is over
 * The dialog is represented with the congrats layout
 */
public class UpdateDialog extends AppCompatDialogFragment {
    Context context;

    public UpdateDialog (Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    // Refer to Brian Fraser video: AlertDialog via Fragment: Android Programming
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_dialog, null);

        // Ok Button
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadData();

            }
        };

        // No Button
        DialogInterface.OnClickListener noListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        };

        // Build the alert
        return new AlertDialog.Builder(getActivity())
                .setTitle("New Update!")
                .setView(v)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.no, noListener)
                .create();
    }

    private void loadData() {
        String restaurantURL = getResources().getString(R.string.restaurantURL);
        getData(restaurantURL);
        String inspectionURL = getResources().getString(R.string.inspectionURL);
        getData(inspectionURL);
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
        String fileName = name + ".csv";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dataURL));
        request.setDescription("Data");
        request.setTitle(name);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
