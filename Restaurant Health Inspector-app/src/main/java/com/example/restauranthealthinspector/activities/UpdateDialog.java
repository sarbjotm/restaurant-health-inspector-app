package com.example.restauranthealthinspector.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import com.example.restauranthealthinspector.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Congrats class manages the congrats dialog, shown the game is over
 * The dialog is represented with the congrats layout
 */
public class UpdateDialog extends AppCompatDialogFragment {

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
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("requestData", true);

            }
        };

        // No Button
        DialogInterface.OnClickListener noListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("requestData", false);
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
}
