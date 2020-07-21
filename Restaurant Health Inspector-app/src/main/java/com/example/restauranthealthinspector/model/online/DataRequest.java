package com.example.restauranthealthinspector.model.online;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DataRequest {
    private String name;
    private String dataURL;
    private String lastModified;

    public DataRequest(String url) {
        getData(url);
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
                                    lastModified = resource.getString("last_modified");
                                    dataURL = resource.getString("url");
                                    name = resource.getString("name");

                                    Log.i("URL", dataURL);
                                    Log.i("lastModified", lastModified);
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

    public String getName() {
        return name;
    }

    public String getDataURL() {
        return dataURL;
    }

    public String getLastModified() {
        return lastModified;
    }
}
