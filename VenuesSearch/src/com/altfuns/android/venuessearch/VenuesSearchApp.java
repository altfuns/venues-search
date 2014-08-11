package com.altfuns.android.venuessearch;

import com.altfuns.android.venuessearch.core.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Application;

public class VenuesSearchApp extends Application {

    private static VenuesSearchApp instance;

    private DatabaseHelper databaseHelper = null;

    public static VenuesSearchApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this,
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
