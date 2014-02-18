package com.paad.wifi4us;

import java.io.File;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.paad.wifi4us.utility.DeviceInfo;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
import com.paad.wifi4us.utility.SharedPreferenceHelper;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

@SuppressLint("HandlerLeak")
public class OtherFragment extends Fragment implements OnClickListener {
	private TextView other_id_userid_text;

	private TextView other_id_credit_text;

	private String userid;

	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAILURE = 1;
	//"<br><small><font size=\"1\" color=\"#ff3030\">(早九点至晚八点出票)<font></small>";
	private static final String USER_HTML_STR="<font size=\"8\" color=\"#000000\"  >ID   </font><font size=\"8\" color=\"#919191\">xxx</font>"; 
	private static final String CREDIT_HTML_STR="<font size=\"5\" color=\"#B6D268\">积分    </font><font size=\"5\" color=\"#919191\">xxx</font>"; 

	FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();
	FrontiaSocialShare mSocialShare;
	FeedbackAgent agent;

	Context context;

	private SharedPreferenceHelper sharedPreference;

	private Handler useridHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				String tempid = (String) msg.obj;
				userid = String.format("%07d", Integer.parseInt(tempid));
				refreshUserId(userid);
				sharedPreference.putString("USER_ID", userid);
				break;
			case MSG_FAILURE:
				refreshUserId(getResources().getString(R.string.main_activity_otherfragment_useridtext_initfail));
				break;
			}
		}
	};
	
	void refreshUserId(String id){
		other_id_userid_text.setText(Html.fromHtml(USER_HTML_STR.replace("xxx", userid)));
	}
	
	void refreshCredit(String credit){
		other_id_credit_text.setText(Html.fromHtml(CREDIT_HTML_STR.replace("xxx", credit)));
	}

	private Runnable getUserIdRunner = new Runnable() {
		public void run() {
			String imei = DeviceInfo.getInstance(getActivity()).getIMEI();
			String userid = RemoteInfoFetcher.resgisterUserId(imei);
			if (userid != null) {
				useridHandler.obtainMessage(MSG_SUCCESS, userid).sendToTarget();
			} else {
				useridHandler.obtainMessage(MSG_FAILURE).sendToTarget();
			}

		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view_res = inflater.inflate(R.layout.fragment_other, container,
				false);
		context = getActivity();
		sharedPreference = new SharedPreferenceHelper(getActivity()
				.getApplicationContext());
		
		//init userid
		other_id_userid_text = (TextView) view_res
				.findViewById(R.id.other_id_userid);
		userid = sharedPreference.getString("USER_ID");
		if (userid.equals("NULL")) {
			Thread mThread = new Thread(getUserIdRunner);
			mThread.start();
		} else {
			refreshUserId(userid);
		}

		//init credit
		other_id_credit_text = (TextView) view_res
				.findViewById(R.id.other_id_credits);
		String tempcredit = sharedPreference.getString("CREDIT");
		if(tempcredit.equals("NULL")){
			tempcredit = "0";
		}
		refreshCredit(tempcredit);
		sharedPreference.putString("CREDIT", tempcredit);


		
		view_res.findViewById(R.id.btn_quit).setOnClickListener(this);
		view_res.findViewById(R.id.btn_about).setOnClickListener(this);
		view_res.findViewById(R.id.btn_check_update).setOnClickListener(this);
		view_res.findViewById(R.id.btn_feed_back).setOnClickListener(this);
		view_res.findViewById(R.id.btn_help).setOnClickListener(this);
		view_res.findViewById(R.id.btn_Set_Discuss).setOnClickListener(this);
		view_res.findViewById(R.id.btn_settings).setOnClickListener(this);
		view_res.findViewById(R.id.btn_agreement).setOnClickListener(this);
		view_res.findViewById(R.id.btn_exchange).setOnClickListener(this);
		view_res.findViewById(R.id.btn_clear_cache).setOnClickListener(this);
		view_res.findViewById(R.id.btn_binary_code).setOnClickListener(this);
		((TextView)view_res.findViewById(R.id.text_version)).setText(getResources().getString(R.string.version)+"  "+getAppVersion(getActivity()));

		return view_res;
	}
	
	protected String getAppVersion(Context context){
		PackageManager packageManager = context.getPackageManager(); 
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo("com.paad.wifi4us", 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		} 
		 
	}

	protected void showWebViewDialog(String filename) {
		try {
			Builder builder = new AlertDialog.Builder(getActivity());
			WebView webView = new WebView(getActivity());
			InputStream inputstream;
			String s1;
			inputstream = getResources().getAssets().open(filename);
			byte abyte0[] = new byte[inputstream.available()];
			inputstream.read(abyte0);
			s1 = EncodingUtils.getString(abyte0, "UTF-8");
			String s = s1;
			inputstream.close();
			webView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
			builder.setView(webView);
			builder.setPositiveButton(
					getResources().getText(R.string.webview_back), null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quit() {
		getActivity().finish();
	}

	boolean initShare = false;

	public void startUpdate() {
    	Toast.makeText(getActivity(), "正在获取最新版本...", Toast.LENGTH_SHORT).show();
		UmengUpdateAgent.forceUpdate(context);
	}

	boolean initFeedback = false;

	public void startFeedback() {
		if (!initFeedback) {
			agent = new FeedbackAgent(context);
			agent.sync();
			initFeedback = true;
		}
		agent.startFeedbackActivity(); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_about:
			try {
				showWebViewDialog("about.html");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_agreement:
			try {
				showWebViewDialog("agreement.html");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_binary_code:
			ImageView iv = new ImageView(context);
			iv.setImageResource(R.drawable.logo);
			new Builder(getActivity())
					.setTitle("二维码")
					.setIcon(R.drawable.logo)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).setView(iv).create().show();
			break;
		case R.id.btn_clear_cache:
			try {
				new Builder(getActivity())
						.setMessage("清空" + clearCache(false) + "MB缓存?")
						.setTitle("清理缓存")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("清空",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										clearCache(true);
										Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create().show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_exchange:
			try {
				startActivity(new Intent(getActivity(), LotteryActivity.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_quit:
			quit();
			break;
		case R.id.btn_feed_back:
			startFeedback();
			break;
		case R.id.btn_settings:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		case R.id.btn_Set_Discuss:
			((MainActivity)getActivity()).startShare(getActivity());
			break;
		case R.id.btn_help:
	                  try {
                              showWebViewDialog("help.html");
                      } catch (Exception e) {
                              e.printStackTrace();
                      }
                      break;
		case R.id.btn_check_update:
			startUpdate();
			break;
		default:
			break;
		}

	}

	private long clearCache(boolean delete) {
		// TODO Auto-generated method stub
		long all = 0;
		File directory = context.getCacheDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
            	long length = item.length();
            	all+=length;
            	if(delete){
            	    item.delete();
            	}
            }
        }
        return all / (1024*1024);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Process.killProcess(android.os.Process.myPid());
	}

	public void onStart() {
		super.onStart();
		other_id_credit_text = (TextView) getActivity().findViewById(
				R.id.other_id_credits);
		String creditText = sharedPreference.getString("CREDIT");
		refreshCredit(creditText);
		
		other_id_userid_text = (TextView) (TextView) getActivity().findViewById(
				R.id.other_id_userid);
		String userid = sharedPreference.getString("USER_ID");
		if(!userid.equals("NULL")){
			refreshUserId(userid);
		}
	}
}
