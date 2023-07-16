package com.example.shortease;

import androidx.appcompat.app.AppCompatDelegate;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
