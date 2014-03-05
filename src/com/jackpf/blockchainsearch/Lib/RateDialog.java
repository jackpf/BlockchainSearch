package com.jackpf.blockchainsearch.Lib;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jackpf.blockchainsearch.R;

public class RateDialog
{
    public static void prompt(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        
        if(preferences.getBoolean("rate_dontshowagain", false))
            return;
        
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        
        //increment launch counter
        long rate_launchcount = preferences.getLong("rate_launchcount", 0) + 1;
        preferencesEditor.putLong("rate_launchcount", rate_launchcount);

        //prompt
        if(rate_launchcount >= Integer.parseInt(context.getString(R.string.rate_launches_until_prompt)))
            showDialog(context);
        
        preferencesEditor.apply();
    }
    
    public static void showDialog(final Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor preferencesEditor = preferences.edit();
        
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Rate " + context.getString(context.getApplicationInfo().labelRes));

        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView prompt = new TextView(context);
        prompt.setText(context.getString(R.string.rate_prompt_text));
        prompt.setWidth(240);
        prompt.setPadding(4, 0, 4, 10);
        dialogLayout.addView(prompt);
        
        Button b1 = new Button(context);
        b1.setText(context.getString(R.string.rate_dismiss_text));
        b1.setOnClickListener( new OnClickListener() {
            public void onClick(View v)
            {
                preferencesEditor.putBoolean("rate_dontshowagain", true);
                preferencesEditor.apply();

                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));

                dialog.dismiss();
            }
        });        
        dialogLayout.addView(b1);

        Button b2 = new Button(context);
        b2.setText(context.getString(R.string.rate_remind_text));
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                preferencesEditor.putLong("rate_launchcount", 0);
                preferencesEditor.apply();
                
                dialog.dismiss();
            }
        });
        dialogLayout.addView(b2);

        Button b3 = new Button(context);
        b3.setText(context.getString(R.string.rate_confirm_text));
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                preferencesEditor.putBoolean("rate_dontshowagain", true);
                preferencesEditor.apply();
                
                dialog.dismiss();
            }
        });
        dialogLayout.addView(b3);

        dialog.setContentView(dialogLayout);        
        dialog.show();    
    }
}