package com.paad.wifi4us;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   		Editor sharedata = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE).edit(); 
		sharedata.putBoolean("FINISH_VIDEO", true);
		sharedata.commit();
        setContentView(R.layout.activity_first);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void StartMainActivity(View view)
    {
    	Intent intent = new Intent();
    	intent.setClass(this, MainActivity.class);
    	startActivity(intent);
    }
    
}
