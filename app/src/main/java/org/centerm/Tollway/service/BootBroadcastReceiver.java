package org.centerm.Tollway.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.centerm.Tollway.activity.IntroActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public final static String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	
    public BootBroadcastReceiver(){
		super();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("ACTION BOOT COMPLETED");

		if(intent.getAction().equals(ACTION_BOOT_COMPLETED)){
			Intent actIntent = new Intent(context,IntroActivity.class);
			actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(actIntent);
  		}

	}

 }
