package com.example.printref;

/**
 * This is a reference application for printing with Hammermill Print
 * It displays test content and then allows to print it with the PRINT NOW button
 * if the HammermillPrint is installed, or prompts to install it otherwise.
 * Developers can use the same technique to embed printing into their apps
 * for more details visit http://printhand.com/integration.php.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

	// Package name of the printing app 
	private final String print_package = "com.hammermill.premium";
	
	// Install URL of the printing app
	private final String installUri = "https://play.google.com/store/apps/details?id="+print_package;

	// Internal variables
	private final String filename = "image.jpg";
	private File file;
	private boolean printAppInstalled;
	private ImageView iv;
	private int currentImageId = R.drawable.promo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Content selector
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		rg.setOnCheckedChangeListener(this);

		iv = (ImageView)findViewById(R.id.imageView1);
		file = new File(getExternalCacheDir(), filename);
	}

	@Override
	protected void onResume() {
		super.onResume();
		printAppInstalled = isPrintAppInstalled();
		invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);

	    MenuItem item = menu.getItem(0);
	    if (printAppInstalled)
	    	item.setTitle("Print NOW");
	    else
	    	item.setTitle("Hammermill Print");
	    
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Display toolbar label depending on whether printing app is installed
		// This could be in any other form such as menu, button, etc. 
	    switch (item.getItemId()) {
	        case R.id.action_print:
	        	if (printAppInstalled)
	        		doPrint();
	        	else
	        		doInstall();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Selecting the content. In this reference app the for display is a set of 
	 * pre-rendeded images, however the actual app can render content on the screen any 
	 * way possible.
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
    	switch(checkedId) {
        	case R.id.radio_promo:
        		setImage(R.drawable.promo);
        		break;
        	case R.id.radio_business:
        		setImage(R.drawable.business);
        		break;
        	case R.id.radio_maps:
        		setImage(R.drawable.maps);
        		break;
	    }
	}
	
	private void setImage(int res) {
		currentImageId = res;
		iv.setImageResource(res);
	}
	
	boolean isPrintAppInstalled() {
		PackageManager mPm = getPackageManager();
		PackageInfo info = null;
		try {
			info = mPm.getPackageInfo(print_package, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info != null;
	}
	
	void doInstall() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 		alertDialogBuilder.setTitle("User action needed");
 		alertDialogBuilder
			.setMessage("You need to install Print Hammermill app in order to print. Do you want to do it now?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent i = new Intent(android.content.Intent.ACTION_VIEW);
					i.setData(Uri.parse(installUri));
					startActivity(i);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
 		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	/** 
	 * Launching printing app, passing the data to it 
	 * as a temporary file. There are other methods of communicating with the app
	 * such as Intent API and stand-alone library. 
	 * For more details visit http://printhand.com/integration.php
	 * 
	 */
	private void doPrint() {
		writeImageFile();
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setPackage(print_package);
		Uri uri = Uri.fromFile(file);
		i.setDataAndType(uri, "image/jpg");
		startActivity(i);
	}
	
	/**
	 * Writing content to a temporary file
	 */
	private void writeImageFile() {
		try
	    {
		    InputStream inputStream = getResources().openRawResource(currentImageId);
		    OutputStream out = new FileOutputStream(file);
		    byte buf[] = new byte[1024];
		    int len;
		    while ((len = inputStream.read(buf)) > 0)
		    	out.write(buf, 0, len);
		    out.close();
		    inputStream.close();
	    } catch (IOException e) {
			e.printStackTrace();
	    }
		
	}
	
}
