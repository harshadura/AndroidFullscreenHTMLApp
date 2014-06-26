package com.example.malfanitv;

import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SelectionActivity extends Activity {
	private View decorView;
	private ImageButton intMode;
	private ImageButton tvMode;
	
    protected void onCreate(Bundle savedInstanceState) {
    
    	/////////////////
		try {
			Runtime.getRuntime().exec("service call activity 42 s16 com.android.systemui");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////////////
	
		
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);
        
        intMode = (ImageButton) findViewById(R.id.interactive_iptv);
        tvMode = (ImageButton) findViewById(R.id.tv_channels);
        
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		decorView = getWindow().getDecorView();
		hideSystemUI();
		
		//int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE; // <<< for kitkat
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		
		///////////////
		
		intMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				finish();
				startActivity(intent);
			}
		});
		
		tvMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try{
					Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.amlogic.DVBPlayer");
					startActivity(LaunchIntent);
					}catch (Exception e) {
						Toast.makeText(getApplicationContext(), "DVBPlayer Application not available!", Toast.LENGTH_LONG).show();
					}
			}
		});
		
		///////////////
		
		
    }
    
 // This snippet hides the system bars.
 	private void hideSystemUI() {
 	    // Set the IMMERSIVE flag.
 	    // Set the content to appear under the system bars so that the content
 	    // doesn't resize when the system bars hide and show.
 		decorView.setSystemUiVisibility(
 	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
 	              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
 	              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 	              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
 	              | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
 	              | View.SYSTEM_UI_FLAG_IMMERSIVE);
 	}

 	// This snippet shows the system bars. It does this by removing all the flags
 	// except for the ones that make the content appear under the system bars.
 	private void showSystemUI() {
 		decorView.setSystemUiVisibility(
 	               View.SYSTEM_UI_FLAG_LAYOUT_STABLE
 	             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
 	             | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
 	}
 	
 	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	        super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {		
	    	int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
	    	decorView.setSystemUiVisibility(uiOptions);}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			return true;
		}
		
		/**
		 * http://stackoverflow.com/questions/10445157/easy-way-to-hide-system-bar-on-android-ics
		 */
		else if (id == R.id.show_navbar) {
			try {
				showSystemUI();
				Runtime.getRuntime().exec("am startservice --user 0 -n com.android.systemui/.SystemUIService");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (id == R.id.hide_navbar) {
			try {
				hideSystemUI();
				Runtime.getRuntime().exec("service call activity 42 s16 com.android.systemui");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (id == R.id.reset) {
			try {
				AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(this.getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags()));
				android.os.Process.killProcess(android.os.Process.myPid());
				}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (id == R.id.crash) {
			try {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (id == R.id.mode) {
			try {
				Intent intent = new Intent(getBaseContext(), SelectionActivity.class);
				finish();
				startActivity(intent);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return super.onOptionsItemSelected(item);
	}

 	
}


