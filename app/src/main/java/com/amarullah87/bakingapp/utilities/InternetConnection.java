package com.amarullah87.bakingapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

/**
 * Baking App Project Android Fast Track Nanodegree - Created By Irfan Apandhi, September 2017
 * You can use this class to call before using any service that need Internet Connection
 */

public class InternetConnection {
    public static boolean checkConnection(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
