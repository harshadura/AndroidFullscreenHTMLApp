package com.example.malfanitv;

import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	private String webUrl;
	private View decorView;
	private SharedPreferences pref;

	private WebView engine;

	private String defaultURL = "http://192.168.1.120/hotel123/index.jsp";

	@Override
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
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		decorView = getWindow().getDecorView();
		
		//int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE; // <<< for kitkat
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		
//		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		// Hide both the navigation bar and the status bar.
		// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
		// a general rule, you should design your app to hide the status bar whenever you
		// hide the navigation bar.
//		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//		decorView.setSystemUiVisibility(uiOptions);

//		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//			@Override
//			public void onSystemUiVisibilityChange(int visibility) {
//				
//				View decorView2 = MainActivity.this.getWindow().getDecorView();
//				int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//				decorView2.setSystemUiVisibility(uiOptions);
//
//				// Note that system bars will only be "visible" if none of the
//				// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
//				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//					// TODO: The system bars are visible. Make any desired
//					// adjustments to your UI, such as showing the action bar or
//					// other navigational controls.
//
//				}
//				else {
//					// TODO: The system bars are NOT visible. Make any desired
//					// adjustments to your UI, such as hiding the action bar or
//					// other navigational controls.
//				}
//			}
//		});

		//////////////
		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getBoolean("first_time", false)) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean("first_time", true);
			editor.putString("WEB_URL", defaultURL);
			editor.commit();
		}

		engine = (WebView) findViewById(R.id.web_engine);

		//////////Clearing Cache/////
		engine.clearCache(true);
		engine.getSettings().setAppCacheEnabled(false);

		this.deleteDatabase("webview.db");
		this.deleteDatabase("webviewCache.db");
		
		// Progress bar.
		// With full screen app, window progress bar (FEATURE_PROGRESS) doesn't seem to show,
		// so we add an explicit one.
//		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);

		engine.setWebChromeClient(new WebChromeClient() {
			
			public void onProgressChanged(WebView view, int progress) {
//				progressBar.setProgress(progress);
			}
			
			public boolean onConsoleMessage(ConsoleMessage cm) {
			    Log.d("MalfaniTV", cm.message() + " -- From line "
			                         + cm.lineNumber() + " of "
			                         + cm.sourceId() );
			    return true;
			  }

		});

		engine.setWebViewClient(new FixedWebViewClient() {
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				progressBar.setVisibility(View.VISIBLE);
			}

			public void onPageFinished(WebView view, String url) {
//				progressBar.setVisibility(View.GONE);
				
				//JS for auto play and auto loop for Android
				engine.loadUrl("javascript:(function() { var video = document.getElementsByTagName('video')[0]; video.loop = false; "
						+ "video.addEventListener('ended', function() { video.currentTime=0.1; video.play(); }, false); video.play(); })()");
			}
		});
		engine.getSettings().setJavaScriptEnabled(true);
		engine.getSettings().setAllowFileAccess(true);
		engine.getSettings().setPluginState(PluginState.ON);
//		engine.getSettings().setPluginsEnabled(true); 
		
		webUrl = pref.getString("WEB_URL", defaultURL);
		engine.loadUrl(webUrl);
	
	}
	
	

	public void onBackPressed() {
		WebView engine = (WebView) findViewById(R.id.web_engine);
		String url = engine.getUrl();

		if (url.equals(webUrl)) {
			// do nothing -- stuck at there.
		}
		else {
			// go back a page, like normal browser
			engine.goBack();
		}
	}

	private class FixedWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
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

			// public boolean onLongClick(View v) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setMessage("Please enter the new URL (Eg: http://www.google.com)");
			// Seting an EditText view to get user input
			final EditText input = new EditText(this);
			input.setText(pref.getString("WEB_URL", defaultURL));

			alert.setView(input);
			alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String newUrl = input.getText().toString();
					if (newUrl != null) {
						SharedPreferences.Editor editor = pref.edit();
						editor.putString("WEB_URL", newUrl);
						editor.commit();
						MainActivity.this.engine.loadUrl(newUrl);
						webUrl = newUrl;
					}
				}
			});
			AlertDialog build = alert.create();
			build.show();
			// }

			return true;
		}
		
		/**
		 * http://stackoverflow.com/questions/10445157/easy-way-to-hide-system-bar-on-android-ics
		 */
		else if (id == R.id.show_navbar) {
			try {
				Runtime.getRuntime().exec("am startservice --user 0 -n com.android.systemui/.SystemUIService");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (id == R.id.hide_navbar) {
			try {
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
		
		return super.onOptionsItemSelected(item);
	}

}
