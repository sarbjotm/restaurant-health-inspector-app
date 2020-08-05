package com.example.restauranthealthinspector.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import com.example.restauranthealthinspector.R;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static androidx.core.content.ContextCompat.getSystemService;

public class TranslationManager {

    private String originalText;
    private String translatedText;
    Translate translate;


    public TranslationManager(String originalText, Translate translate) {
        this.originalText = originalText;
        this.translatedText = "";
        this.translate = translate;
    }

    public String getTranslatedText() {
        if (Locale.getDefault().getLanguage().contentEquals("fr")) {
            com.google.cloud.translate.Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("fr"), Translate.TranslateOption.model("base"));
            translatedText = translation.getTranslatedText();
        }

        else{
            translatedText = originalText;
        }

        return translatedText;
    }

}


