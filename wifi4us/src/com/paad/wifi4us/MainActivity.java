package com.paad.wifi4us;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.DeviceInfo;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
import com.paad.wifi4us.utility.SharedPreferenceHelper;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class MainActivity extends ActionBarActivity {
	private FragmentManager fragmentManager;
    
	private Fragment receive_wifi_switcher_button;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_button;
	private Fragment receive_id_start_scan_resultlist;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_scan_text_openwifi;
	private Fragment receive_id_start_wifi_connected_state;

	private Fragment send_id_start_share_button;
	private Fragment send_id_progressbar;
	private Fragment send_id_stop_share_button;
	private Fragment send_id_start_share_text;

	private Fragment send;
	private Fragment receive;
	private OtherFragment other;

	private SharedPreferenceHelper sharedPreference;

	//Receive Service 	
    private ReceiveService receiveService;
	private boolean haveBondService;
	private ServiceConnection sc = new ServiceConnection() {
        @Override  
        public void onServiceDisconnected(ComponentName arg0) {  
        	haveBondService = false;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	receiveService = ((ReceiveService.MyBinder)binder).getService();
        	haveBondService = true;
        }  
    };  
    
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actiongbar_menu, menu);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">一起wifi</font>"));
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_share:
                startShare(this);
                return true;
            case R.id.action_settings:
            	startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void addTab(String label, String titleText, int drawableId, int contentId, TabHost tabHost) {
        TabHost.TabSpec spec = tabHost.newTabSpec(label);
         
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_card, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.tabtitle);
        title.setText(titleText);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tabimage);
        icon.setImageResource(drawableId);
         
        spec.setIndicator(tabIndicator);
        spec.setContent(contentId);
        tabHost.addTab(spec);
    }


	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	Constant.FLAG.STATE_RECEIVE = false;
		Constant.PreventAbuse.DOUBLE_START_SEND = false;

		fragmentManager = this.getSupportFragmentManager();

		//init send mode
		sharedPreference = new SharedPreferenceHelper(getApplicationContext());
		String sendAdMode = sharedPreference.getString("SEND_AD_MODE");
		String sendLimitMode = sharedPreference.getString("SEND_LIMIT_MODE");
		if(sendAdMode.equals(SharedPreferenceHelper.NULL)){
			sharedPreference.putString("SEND_AD_MODE", "NO");
		}
		if(sendLimitMode.equals(SharedPreferenceHelper.NULL)){
			sharedPreference.putString("SEND_LIMIT_MODE", "30");
		}


		Intent intent = new Intent(this, ReceiveService.class);  
		bindService(intent, sc, Context.BIND_AUTO_CREATE); 

    	
		startService(new Intent(this, ReceiveService.class));
		startService(new Intent(this, SendService.class));

    	setContentView(R.layout.activity_main);
    	
    	TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost); 
    	
    	
    	tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {    
            public void onTabChanged(String tabId) {  

            	if(tabId.equals("receive")){
            		if(Constant.FLAG.LAST_TAB.equals("send")){
                		StopSendFragment();
            		}else{
                		StopOtherFragment();
            		}
            		StartReceiveFragment();
            	}else if(tabId.equals("send")){
            		if(Constant.FLAG.LAST_TAB.equals("other")){
                		StopOtherFragment();
            		}else{
                		StopReceiveFragment();
            		}
            		StartSendFragment();
            	}else{
            		if(Constant.FLAG.LAST_TAB.equals("receive")){
                		StopReceiveFragment();
            		}else{
                		StopSendFragment();
            		}
            		StartOtherFragment();
            	}
            }  
        }); 
    	
    	tabHost.setup();
    	
    	addTab("receive", getString(R.string.main_activity_tabwidget_receivetext),  R.drawable.tab_search_selector, R.id.receive, tabHost);
    	addTab("send", getString(R.string.main_activity_tabwidget_sendtext),  R.drawable.tab_share_selector, R.id.send, tabHost);
    	addTab("other", getString(R.string.main_activity_tabwidget_othertext),  R.drawable.tab_settings_selector, R.id.other, tabHost);
    	tabHost.setCurrentTab(0);
    	for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
    	}
    	Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(){

			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				Log.e("uncaught", arg1.getMessage());
			}
    		
    	});
    	UmengUpdateAgent.setUpdateOnlyWifi(false);
    	UmengUpdateAgent.update(this);
    	UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
    	    @Override
    	    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
    	        switch (updateStatus) {
    	        case UpdateStatus.Yes: // has update
    	            UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
    	            break;
    	        case UpdateStatus.No: // has no update
    	            Toast.makeText(MainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
    	            break;
    	        case UpdateStatus.NoneWifi: // none wifi
    	            Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
    	        case UpdateStatus.Timeout: // time out
    	            Toast.makeText(MainActivity.this, "更新失败，请检查网络连接", Toast.LENGTH_SHORT).show();
    	            break;
    	        }
    	    }
    	});
    }

	public void onDestroy(){
		super.onDestroy();
		unbindService(sc);
	}

	public void onPause(){
		super.onPause();
    	if(!haveBondService){
    		return;
    	}
		if(!Constant.FLAG.FINISH_PRECONNNECT){
			Intent intent = new Intent();
			intent.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP_INTERRUPT); 
			sendBroadcast(intent);
			receiveService.WifiDisconnectCompletely();
		}
	}
    
    public void onBackPressed() {  
    	Intent backtoHome = new Intent(Intent.ACTION_MAIN);
        backtoHome.addCategory(Intent.CATEGORY_HOME);
        backtoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backtoHome);  
    }  
    
    private void StartReceiveFragment(){
    	Constant.FLAG.LAST_TAB = "receive";
    	initFragments();
    	if(receive != null){
        	receive.onStart();
        	receive.onResume();
    	}
    	if(receive_wifi_switcher_button != null){
    		receive_wifi_switcher_button.onStart();
    		receive_wifi_switcher_button.onResume();
    	}
    	if(receive_id_switcher_text_on != null){
        	receive_id_switcher_text_on.onStart();
        	receive_id_switcher_text_on.onResume();
    	}
    	if(receive_id_switcher_text_off != null){
        	receive_id_switcher_text_off.onStart();
        	receive_id_switcher_text_off.onResume();
    	}
    	if(receive_id_start_scan_button != null){
        	receive_id_start_scan_button.onStart();
        	receive_id_start_scan_button.onResume();
    	}
    	if(receive_id_start_scan_resultlist != null){
        	receive_id_start_scan_resultlist.onStart();
        	receive_id_start_scan_resultlist.onResume();
    	}
    	if(receive_id_start_scan_progressbar != null){
        	receive_id_start_scan_progressbar.onStart();
        	receive_id_start_scan_progressbar.onResume();
    	}
    	if(receive_id_start_connect_progressbar != null){
        	receive_id_start_connect_progressbar.onStart();
        	receive_id_start_connect_progressbar.onResume();
    	}
    	if(receive_id_start_scan_text_openwifi != null){
        	receive_id_start_scan_text_openwifi.onStart();
        	receive_id_start_scan_text_openwifi.onResume();
    	}
    	if(receive_id_start_wifi_connected_state != null){
        	receive_id_start_wifi_connected_state.onStart();
        	receive_id_start_wifi_connected_state.onResume();
    	}
    }
    
    private void StartSendFragment(){
    	Constant.FLAG.LAST_TAB = "send";
    	initFragments();
       	if(send != null){
       		send.onStart();
       		send.onResume();
    	}
    	if(send_id_start_share_button != null){
    		send_id_start_share_button.onStart();
    		send_id_start_share_button.onResume();
    	}
    	if(send_id_progressbar != null){
    		send_id_progressbar.onStart();
    		send_id_progressbar.onResume();
    	}
    	if(send_id_stop_share_button != null){
    		send_id_stop_share_button.onStart();
    		send_id_stop_share_button.onResume();
    	}
    	if(send_id_start_share_text != null){
    		send_id_start_share_text.onStart();
    		send_id_start_share_text.onResume();
    	}

    }
    
    private void StartOtherFragment(){
    	Constant.FLAG.LAST_TAB = "other";
    	initFragments();
       	if(other != null){
       		other.onStart();
       		other.onResume();
       	}
    }
    
    private void StopReceiveFragment(){
    	initFragments();
    	if(receive != null){
    		receive.onPause();
    		receive.onStop();
    	}
    	if(receive_wifi_switcher_button != null){
    		receive_wifi_switcher_button.onPause();
    		receive_wifi_switcher_button.onStop();
    	}
    	if(receive_id_switcher_text_on != null){
        	receive_id_switcher_text_on.onPause();
        	receive_id_switcher_text_on.onStop();
    	}
    	if(receive_id_switcher_text_off != null){
        	receive_id_switcher_text_off.onPause();
        	receive_id_switcher_text_off.onStop();
    	}
    	if(receive_id_start_scan_button != null){
        	receive_id_start_scan_button.onPause();
        	receive_id_start_scan_button.onStop();
    	}
    	if(receive_id_start_scan_resultlist != null){
        	receive_id_start_scan_resultlist.onPause();
        	receive_id_start_scan_resultlist.onStop();
    	}
    	if(receive_id_start_scan_progressbar != null){
        	receive_id_start_scan_progressbar.onPause();
        	receive_id_start_scan_progressbar.onStop();
    	}
    	if(receive_id_start_connect_progressbar != null){
        	receive_id_start_connect_progressbar.onPause();
        	receive_id_start_connect_progressbar.onStop();
    	}
    	if(receive_id_start_scan_text_openwifi != null){
        	receive_id_start_scan_text_openwifi.onPause();
        	receive_id_start_scan_text_openwifi.onStop();
    	}
    	if(receive_id_start_wifi_connected_state != null){
        	receive_id_start_wifi_connected_state.onPause();
        	receive_id_start_wifi_connected_state.onStop();
    	}
    }
    
    private void StopSendFragment(){
    	initFragments();
    	if(send != null){
    		send.onPause();
    		send.onStop();
    	}
    	if(send_id_start_share_button != null){
    		send_id_start_share_button.onPause();
    		send_id_start_share_button.onStop();
    	}
    	if(send_id_progressbar != null){
    		send_id_progressbar.onPause();
    		send_id_progressbar.onStop();
    	}
    	if(send_id_stop_share_button != null){
    		send_id_stop_share_button.onPause();
    		send_id_stop_share_button.onStop();
    	}
    	if(send_id_start_share_text != null){
    		send_id_start_share_text.onPause();
    		send_id_start_share_text.onStop();
    	}
    }
    
    private void StopOtherFragment(){
    	initFragments();
       	if(other != null){
       		other.onPause();
       		other.onStop();
       	}
    }
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
            if((System.currentTimeMillis()-exitTime) > 2000){  
                Toast.makeText(getApplicationContext(), "qqq再按一次退出程序", Toast.LENGTH_SHORT).show();                                
                exitTime = System.currentTimeMillis();   
            } else {
                finish();
                System.exit(0);
            }
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void initFragments(){
    	receive_wifi_switcher_button = fragmentManager.findFragmentByTag("receivie_wifi_switcher_button");
    	receive_id_switcher_text_on = fragmentManager.findFragmentByTag("receive_id_switcher_text_on");
    	receive_id_switcher_text_off = fragmentManager.findFragmentByTag("receive_id_switcher_text_off");
    	receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
    	receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
    	receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar");
    	receive_id_start_connect_progressbar = fragmentManager.findFragmentByTag("receive_id_start_connect_progressbar");
    	receive_id_start_scan_text_openwifi = fragmentManager.findFragmentByTag("receive_id_start_scan_text_openwifi");
    	receive_id_start_wifi_connected_state = fragmentManager.findFragmentByTag("receive_id_start_wifi_connected_state");

    	send_id_start_share_button = fragmentManager.findFragmentByTag("send_id_start_share_button");
    	send_id_progressbar = fragmentManager.findFragmentByTag("send_id_progressbar");
    	send_id_stop_share_button = fragmentManager.findFragmentByTag("send_id_stop_share_button");
    	send_id_start_share_text = fragmentManager.findFragmentByTag("send_id_start_share_text");

    	send = fragmentManager.findFragmentById(R.id.send);
    	receive = fragmentManager.findFragmentById(R.id.receive);
    	other = (OtherFragment)fragmentManager.findFragmentById(R.id.other);
    }
    
	boolean initShare = false;
	FrontiaSocialShareContent mImageContent;
	FrontiaSocialShare mSocialShare;

    public void startShare(final Context context) {
        if (!initShare) {
            mImageContent = new FrontiaSocialShareContent();
            boolean isInit = Frontia.init(context, "gxLMGxsKv6q3WRAKxBZwuidD");
            if (!isInit) {// Frontia is successfully initialized.
                // Use Frontia
                Toast.makeText(context, "init frontia fail", Toast.LENGTH_SHORT)
                        .show();
            }
            mSocialShare = Frontia.getSocialShare();
            mSocialShare.setContext(context);
            mImageContent.setTitle(context.getResources().getString(
                    R.string.setDiscuss));
            mImageContent
                    .setContent(getString(R.string.main_activity_social_share_content));
            mImageContent.setLinkUrl("http://www.wifitogether.com/");
            mImageContent.setImageData(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.ic_launcher));
            initShare = true;
        }

        mSocialShare.share(mImageContent, MediaType.BATCHSHARE.toString(),
                new CustomListener(new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                Toast.makeText(MainActivity.this, "qqq分享未完成",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "qqq分享失败",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                String timeStr = sharedPreference
                                        .getString("SHARE_TIME");
                                System.out.println("timeStr"+timeStr);
                                if (!timeStr
                                        .equals(SharedPreferenceHelper.NULL)) {
                                    long time = Long.valueOf(timeStr);
                                    long days = time / 3600000 / 24;
                                    long currDay = System.currentTimeMillis() / 3600000 / 24;
                                    System.out.println("currday:"+currDay+"\tdays:"+days);
                                    if (currDay == days) {
                                        Toast.makeText(MainActivity.this, "qqq分享成功，每天只获得一次积分奖励", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                                new AsyncTask<Object, Object, String>() {
                                    
                                    @Override
                                    protected String doInBackground(
                                            Object... params) {
                                        return RemoteInfoFetcher
                                        .addUserCredit(
                                                DeviceInfo
                                                .getInstance(
                                                        MainActivity.this)
                                                        .getIMEI(),
                                                        sharedPreference
                                                        .getString("USER_ID"),
                                                        1);
                                    }
                                    
                                    @Override
                                    protected void onPostExecute(
                                            String result) {
                                        if (result != null) {
                                            Toast.makeText(
                                                    MainActivity.this,
                                                    "qqq分享成功，获得积分奖励",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                            sharedPreference.putString(
                                                    "CREDIT", result);
                                            other.refreshCredit(result);
                                            sharedPreference.putString("SHARE_TIME", String.valueOf(System.currentTimeMillis()));
                                        } else {
                                            Toast.makeText(
                                                    MainActivity.this,
                                                    "qqq服务器错误",
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                        super.onPostExecute(result);
                                    }
                                    
                                }.execute(new Object[] {});
                                break;
                            default:
                                // do nothing;
                                break;
                        }
                        // TODO Auto-generated method stub
                        super.handleMessage(msg);
                    }

                }), true);
    }
    
        public static class CustomListener implements FrontiaSocialShareListener {
    
            Context context;
    
            OtherFragment otherFragment;
    
            SharedPreferenceHelper spf;
            Handler handler;
    
            CustomListener(Handler handler) {
                this.handler = handler;
            }
    
            @Override
            public void onCancel() {
                System.out.println("share cancel");
                handler.obtainMessage(0).sendToTarget();
            }
    
            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                System.out.println("share fail");
                handler.obtainMessage(1).sendToTarget();
            }
    
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                System.out.println("share success");
                handler.obtainMessage(2).sendToTarget();
            }
        }
    
    
}
