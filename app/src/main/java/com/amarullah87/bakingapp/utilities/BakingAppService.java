package com.amarullah87.bakingapp.utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Baking App Project Android Fast Track Nanodegree - Created By Irfan Apandhi, September 2017
 * Service for Update Widget
 */

public class BakingAppService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BakingAppService() {
        super("BakingAppService");
    }

    public static void bakingService(Context context, ArrayList<String> listItem, String recipeName){
        Intent intent = new Intent(context, BakingAppService.class);
        intent.putExtra("extrasItem", listItem);
        intent.putExtra("recipeName", recipeName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            ArrayList<String> itemList = intent.getExtras().getStringArrayList("extrasItem");
            String recipeName = intent.getExtras().getString("recipeName");
            updateWidget(itemList, recipeName);
        }
    }

    private void updateWidget(ArrayList<String> itemList, String recipeName) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.putExtra("extrasItem", itemList);
        intent.putExtra("recipeName", recipeName);
        sendBroadcast(intent);
    }
}
