package com.laptopfreek0.taskkill;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.dinglisch.tasker.plugin.TaskerPlugin;

import com.laptopfreek0.taskkill.objects.App;
import com.laptopfreek0.taskkill.objects.AppComparer;
import com.laptopfreek0.taskkill.objects.CustomSpinnerAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class EditActivity extends Activity {
	Button button;
	Spinner method;
	Spinner packagelist;
	ProgressBar packagelistprogressbar;
	ArrayList<HashMap<String, Object>> packageData;
	TextView warning;
	TextView Description;
	TextView TaskLabel;
	TextView variabledescription;
	EditText packagename;
	EditText variablename;
	boolean rootaccess = false;
	boolean rootattempted = false;
	boolean userquit = false;
	String previousPackage = null;
	String previousMethod = null;
	int hiddenclickcount = 0;
	CheckBox chkVariableName;
	boolean checked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
    //final Bundle bundle = getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		Bundle bundle = this.getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		if (bundle != null) {
		if(bundle.containsKey("BUNDLE_EXTRA_PACKAGE"))
		  previousPackage = bundle.getString("BUNDLE_EXTRA_PACKAGE");
		  if (previousPackage.contains("%"))
			  checked = true;
		if(bundle.containsKey("BUNDLE_EXTRA_METHOD"))
		  previousMethod = bundle.getString("BUNDLE_EXTRA_METHOD");
		}
    
		packageData = new ArrayList<HashMap<String, Object>>();
    button = (Button) this.findViewById(R.id.button1);
    method = (Spinner) this.findViewById(R.id.method);
    method.setOnItemSelectedListener(methodListener);
    TaskLabel = (TextView) this.findViewById(R.id.TextView01);
    TaskLabel.setOnClickListener(hiddenclicklistener);
    variablename = (EditText) this.findViewById(R.id.variablename);
    packagename = (EditText) this.findViewById(R.id.packagename);
    packagelist = (Spinner) this.findViewById(R.id.packagelist);
    packagelistprogressbar = (ProgressBar) this.findViewById(R.id.packageloadbar);
    Description = (TextView) this.findViewById(R.id.Description);
    variabledescription = (TextView) this.findViewById(R.id.variabledescription);
    warning = (TextView) this.findViewById(R.id.Warning);
    warning.setTextColor(Color.RED);
    warning.setVisibility(View.GONE);
    chkVariableName = (CheckBox) this.findViewById(R.id.chk_variablename);
    if (TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement(this)) {
    	chkVariableName.setVisibility(View.VISIBLE);
    	chkVariableName.setOnClickListener(checkListener);
    	if(checked) {
    		chkVariableName.setChecked(true);
    		variablename.setText(previousPackage);
    		variablename.setVisibility(View.VISIBLE);
            packagelist.setVisibility(View.GONE);
    	}
    		
    }
    
    // Get Package List
    Thread thread = new Thread(new Runnable() {
    	public void run() {
    		 // Lengthy Operation
		    final List<PackageInfo> PackageList = EditActivity.this.getPackageManager().getInstalledPackages(0);
		    ArrayList<App> Apps = new ArrayList<App>();
		    for(PackageInfo packageInfo : PackageList) {
		        // Excludes System apps
		      if ((packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
  		    	App app = new App();
  		    	app.addApp(packageInfo, EditActivity.this);
  		    	Apps.add(app);
		      }
		    }
		    Message msg = new Message();
		    msg.obj = Apps;
		    msg.what = 0;
		    handler.sendMessage(msg);
    	}
    });
    thread.start();
    
    // Find out if device is rooted
    if(isDeviceRooted()) {
    	ArrayAdapter<CharSequence> adapter;
    	if(Build.VERSION.SDK_INT < 14) {
    		adapter = ArrayAdapter.createFromResource(this, R.array.rooted_LT_ICS, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        method.setAdapter(adapter);
    		if(previousMethod != null)
          method.setSelection(adapter.getPosition(previousMethod));
    	} else {
    		adapter = ArrayAdapter.createFromResource(this, R.array.rooted_GT_ICS, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        method.setAdapter(adapter);
    		if(previousMethod != null)
          method.setSelection(adapter.getPosition(previousMethod));
        else
          method.setSelection(adapter.getPosition("Extreme"));
    	}
    } else {
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unrooted, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	method.setAdapter(adapter);
      if(previousMethod != null)
        method.setSelection(adapter.getPosition(previousMethod));
    }
		button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(listener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}
	
	
  @Override
  public void onBackPressed() {
    userquit = true;
    super.onBackPressed();
  }

  @Override
  public void finish()
  {
  	String strMethod = method.getSelectedItem().toString();
  	boolean rootrequired = (strMethod.equals("Advanced") || strMethod.equals("Extreme") || strMethod.equals("Hardcore"));
  	if(userquit) {
  	  super.finish();
  	}else if(rootrequired && rootaccess) {
  	  final Intent resultIntent = new Intent();
		  final Bundle resultBundle = new Bundle();
		  resultBundle.putInt("BUNDLE_EXTRA_INT_VERSION_CODE", 1);
		  String strPackage = "";
		  if(chkVariableName.isChecked()) {
			  strPackage = variablename.getText().toString();
			  if(!strPackage.contains("%"))
				  strPackage = "%" + strPackage;
		  } else {
			  strPackage = packageData.get(packagelist.getSelectedItemPosition()).get("Package").toString();
		  }
		  if (!packagename.getText().toString().equalsIgnoreCase(""))
			  strPackage = packagename.getText().toString();
		  resultBundle.putString("BUNDLE_EXTRA_METHOD", strMethod);
		  resultBundle.putString("BUNDLE_EXTRA_PACKAGE", strPackage);
		    if (TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement(this))
	            TaskerPlugin.Setting.setVariableReplaceKeys(resultBundle, new String [] {  "BUNDLE_EXTRA_PACKAGE" });
		  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, "Method="+strMethod+"; Package="+strPackage);
		  resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
		  setResult(RESULT_OK, resultIntent);
		  super.finish();
  	} else if(rootrequired && !rootaccess && !rootattempted) {
  	    // Root not yet attempted
  		Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// Gain Root
				   try {
					   Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "/system/bin/sh"});
					   DataOutputStream ostream = new DataOutputStream(process.getOutputStream());
					   ostream.writeBytes("exit\n");
					   ostream.close();
				     if (process.waitFor() == 0)
				       handler.sendEmptyMessage(1);
				     else
				       handler.sendEmptyMessage(2);
				   } catch (IOException e) {
				     e.printStackTrace();
				     handler.sendEmptyMessage(2);
				   } catch (InterruptedException e) {
				     e.printStackTrace();
				     handler.sendEmptyMessage(2);
				   }
				}
  		});
  		thread.start();
  	} else if(rootrequired && !rootaccess && rootattempted) {
  	   // Root failed
  	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	  builder.setMessage("Failed to obtain root. Would you like to try again?").setPositiveButton("Yes", dialogClickListener)
  	      .setNegativeButton("No", dialogClickListener).show();
  	} else if(!rootrequired){
	  	final Intent resultIntent = new Intent();
	    final Bundle resultBundle = new Bundle();
	    resultBundle.putInt("BUNDLE_EXTRA_INT_VERSION_CODE", 1);
		  String strPackage = "";
		  if(chkVariableName.isChecked()) {
			  strPackage = variablename.getText().toString();
			  if(!strPackage.contains("%"))
				  strPackage = "%" + strPackage;
		  } else {
			  strPackage = packageData.get(packagelist.getSelectedItemPosition()).get("Package").toString();
		  }
		  if (!packagename.getText().toString().equalsIgnoreCase(""))
			  strPackage = packagename.getText().toString();
	    resultBundle.putString("BUNDLE_EXTRA_METHOD", strMethod);
	    resultBundle.putString("BUNDLE_EXTRA_PACKAGE", strPackage);
	    if (TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement(this))
            TaskerPlugin.Setting.setVariableReplaceKeys(resultBundle, new String [] {  "BUNDLE_EXTRA_PACKAGE" });
	    resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, "Method="+strMethod+"; Package="+strPackage);
	    resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
	    setResult(RESULT_OK, resultIntent);
	    super.finish();
  	} else {
  	  // An Error has occurred
  	  Log.e("Error", "An Unknown Error has Occured");
  	}
  }
  private boolean isDeviceRooted() {
		try {
			File file = new File("/system/bin/su");
			if(file.exists())
				return true;
		} catch (Throwable e) {}
		try {
			File file = new File("/system/xbin/su");
			if(file.exists())
				return true;
		} catch (Throwable e) {}
		return false;
	}
	
	 Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					ArrayList<App> Apps = (ArrayList<App>) msg.obj;
						// Sort the Apps alphabetically 
					Collections.sort(Apps, new AppComparer());
			    for(App app : Apps) {
			    		// Excludes Myself from the list.
			    	if(!app.getAppPkg().equals("com.laptopfreek0.taskkill")){
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("Name", app.getName());
			    		map.put("Icon", app.getIcon());
			    		map.put("Package", app.getPackageName());
			    		packageData.add(map);
			    	}
			    	
			    }
			    // ImageView With Adapter http://stackoverflow.com/questions/3609231/how-is-it-possible-to-create-a-spinner-with-images-instead-of-text
			    packagelistprogressbar.setVisibility(View.GONE);
			    CustomSpinnerAdapter adapter = 
			    		new CustomSpinnerAdapter(EditActivity.this, packageData, R.layout.spinner_view, new String[] { "Name", "Icon" }, 
			    		new int[] { R.id.textviewSpinner, R.id.imageviewSpinner });
			    packagelist.setAdapter(adapter);
			    if(previousPackage != null)
			      packagelist.setSelection(adapter.getPosition(previousPackage));
			      // Hide Spinner
			    packagelistprogressbar.setVisibility(View.GONE);
			      // Enable Button
			    button.setEnabled(true);
			  	break;
				case 1:
				    // Root succeeded
					rootaccess = true;
					rootattempted = true;
					EditActivity.this.finish();
					break;
				case 2:
				    // Root failed
				  rootaccess = false;
				  rootattempted = true;
				  EditActivity.this.finish();
				}
			}
		
		};
	private OnItemSelectedListener methodListener = new OnItemSelectedListener() {

    @Override
    public void onItemSelected(AdapterView<?> parentview, View view, int position, long id) {
      String selectedmethod = method.getSelectedItem().toString();
      if(selectedmethod.equals("Hardcore"))
      {
        warning.setText("Warning: Hardcore method may remove home shortcuts. Only use if other methods fail.");
        warning.setVisibility(View.VISIBLE);
      } else if(warning.getVisibility() == View.VISIBLE) {
        warning.setVisibility(View.GONE);
      }
      
      // Description
      if(selectedmethod.equals("Simple"))
        Description.setText(EditActivity.this.getString(R.string.SimpleDescription));
      else if(selectedmethod.equals("Advanced"))
        Description.setText(EditActivity.this.getString(R.string.AdvancedDescription));
      else if(selectedmethod.equals("Extreme"))
        Description.setText(EditActivity.this.getString(R.string.ExtremeDescription));
      else if(selectedmethod.equals("Hardcore"))
        Description.setText(EditActivity.this.getString(R.string.HardcoreDescription));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
      
    }
	  
	};
	
  private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			EditActivity.this.finish();
		}
  };
  
  private OnClickListener checkListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
		CheckBox chk = (CheckBox) v;
		if (chk.isChecked())
		{
			variablename.setVisibility(View.VISIBLE);
			variablename.setText("");
	        packagelist.setVisibility(View.GONE);
	        packagename.setVisibility(View.GONE);
	        variabledescription.setVisibility(View.VISIBLE);
		} else {
			variablename.setVisibility(View.GONE);
			variablename.setText("");
	        packagelist.setVisibility(View.VISIBLE); 
	        variabledescription.setVisibility(View.GONE);
		}
	}
	  
  };
  
  private OnClickListener hiddenclicklistener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      hiddenclickcount++;
      if(hiddenclickcount > 6) {
    	variablename.setVisibility(View.GONE);
        packagename.setVisibility(View.VISIBLE);
        packagelist.setVisibility(View.GONE);
      }
    }
  };
  
  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
        case DialogInterface.BUTTON_POSITIVE:
            rootattempted = false;
            rootaccess = false;
            EditActivity.this.finish();
            break;

        case DialogInterface.BUTTON_NEGATIVE:
            userquit = true;
            EditActivity.this.finish();
            break;
        }
    }
  };
}
