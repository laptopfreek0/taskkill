package com.laptopfreek0.taskkill.objects;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class App {
	private String Name;
	//private String Path;
	//private String MainActivity;
	//private ArrayList<String> Activities;
	private String PackageName;
	private Drawable Icon;
	
	public App() {
		Name = "";
/*		Path = "";
		MainActivity = "";
		Activities = new ArrayList<String>();*/
	}
	
	public App(String name, String path, String mainActivity, ArrayList<String> activities, String packageName) {
		Name = name;
/*		Path = path;
		MainActivity = mainActivity;
		Activities = activities;*/
		PackageName = packageName;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

/*	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		Path = path;
	}

	public String getMainActivity() {
		return MainActivity;
	}

	public void setMainActivity(String mainActivity) {
		MainActivity = mainActivity;
	}

	public ArrayList<String> getActivities() {
		return Activities;
	}

	public void setActivities(ArrayList<String> activities) {
		Activities = activities;
	}
	
	public void addActivity(String activity) {
		Activities.add(activity);
	}*/

	public String getPackageName() {
		return PackageName;
	}

	public void setPackageName(String packageName) {
		PackageName = packageName;
	}
	
	/*public String getAppClass() {
		return MainActivity;
	}*/
	
	public String getAppPkg() {
		return PackageName;
	}
	
	public Drawable getIcon() {
		return Icon;
	}
	
	public void addApp(PackageInfo app, Context context) {
		try {
			PackageName = app.packageName;
			ApplicationInfo ai = app.applicationInfo;
			Name = (String) context.getPackageManager().getApplicationLabel(ai);
			//Path = app.applicationInfo.sourceDir;
			//PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(app.applicationInfo.sourceDir, PackageManager.GET_ACTIVITIES);
			//ActivityInfo[] activities = pi.activities;
				// For each Activity in activities
			/*for(ActivityInfo activity : activities) {
				Activities.add(activity.name);
			}*/
			//Intent main = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
			//MainActivity = main.getComponent().getClassName();		
			Icon = context.getPackageManager().getApplicationIcon(PackageName);
		} catch (Exception e) {
			// An error has occured. Oh well.
		}
	}

	@Override
	public String toString() {
		return "Name: " + Name + "\nPackage: " + PackageName;
	}
}
