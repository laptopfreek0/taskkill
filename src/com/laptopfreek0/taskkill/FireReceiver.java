package com.laptopfreek0.taskkill;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public final class FireReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
		{
			final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
			final String packagename = bundle.getString("BUNDLE_EXTRA_PACKAGE");
			String selectedmethod = bundle.getString("BUNDLE_EXTRA_METHOD");
			Log.i("Killing Package", packagename);
			if(selectedmethod.equals("Simple")) {
				Log.i("Selected Method", "Simple");
				// Simple method is just a killbackgroundProcess Handles most simple apps, but nothing persistent.
				
				// May toss this in later (But all logic says it will do nothing ever)
				/*
				 * 				
				ActivityManager am = (ActivityManager) TaskKill.this.getSystemService(ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
				if(list != null){
				 for(int i=0;i<list.size();++i){
				  if("com.dropbox.android".matches(list.get(i).processName)){
				   int pid = list.get(i).pid;
				   Log.i("PID", String.valueOf(pid));
				   android.os.Process.killProcess(pid);
				  }
				 }
				}
				 */
				ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
				am.killBackgroundProcesses(packagename);
				
			} else if(selectedmethod.equals("Advanced")) {
				Log.i("Selected Method", "Advanced");
				// Advanced Method kills the PID. More prone to kill it Asap, but might restart (Requires root)
				ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
				if(list != null){
				 for(int i=0;i<list.size();++i){
				  if(packagename.matches(list.get(i).processName)){
				   final int pid = list.get(i).pid;
				   Log.i("PID", String.valueOf(pid));
				   
				   Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              try {
                Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "/system/bin/sh"});
                DataOutputStream ostream = new DataOutputStream(process.getOutputStream());
                ostream.writeBytes("kill -9 " + String.valueOf(pid) + "\n");
                ostream.writeBytes("exit\n");
			          process.waitFor();
				      } catch (IOException e) {
				        e.printStackTrace();
				      } catch (InterruptedException e) {
				        e.printStackTrace();
				      }
            } 
				   });
				   thread.start();
				   break;
				  }
				 }
				}
				
			} else if(selectedmethod.equals("Extreme")) {
				Log.i("Selected Method", "Extreme");
				// Extreme method kills using the force kill with am (Most prefered)(Requires root & 4.0+)
				Thread thread = new Thread(new Runnable() {
				  @Override
				  public void run() {
				    try {
				       int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				       if (currentapiVersion >= 21) {
				    	   Log.i("API VERSION", "Lollipop or greater");
					       Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "/system/bin/sh"});
					       DataOutputStream ostream = new DataOutputStream(process.getOutputStream());
					       ostream.writeBytes("setenforce 0\n");
					       ostream.writeBytes("am force-stop "+packagename+"\n");
					       ostream.writeBytes("exit\n");
					       process.waitFor();
				       } else {
					       Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "/system/bin/sh"});
					       DataOutputStream ostream = new DataOutputStream(process.getOutputStream());
					       ostream.writeBytes("am force-stop "+packagename+"\n");
					       ostream.writeBytes("exit\n");
					       process.waitFor();
				       }
		           } catch (IOException e) {
		             e.printStackTrace();
		           } catch (InterruptedException e) {
		             e.printStackTrace();
		           }
				  }
				});
				thread.start();
				
			} else if(selectedmethod.equals("Hardcore")) {
				Log.i("Selected Method", "Hardcore");
			  Thread thread = new Thread(new Runnable() {
			    @Override
			    public void run() {
			      try {
			        Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "/system/bin/sh"});
              DataOutputStream ostream = new DataOutputStream(process.getOutputStream());
              ostream.writeBytes("pm disable " + packagename+"\n");
              ostream.writeBytes("pm enable " + packagename+"\n");
              ostream.writeBytes("exit\n");
              process.waitFor();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
			    }
			  });
			  thread.start();
			  
			} else {
				Log.e("Selected Method", "Unknown");
			}
		    
		}
		
	}

}
