package com.paad.wifi4us;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.HttpXmlParser;
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
	private static final int CREDIT_INIT = 2;

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
				other_id_userid_text.setText("�û��ţ�" + userid);
				sharedPreference.putString("USER_ID", userid);
				break;
			case MSG_FAILURE:
				other_id_userid_text.setText("���˳������������״̬�����»�ȡid");
				break;
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler creditHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				String tempcredit = (String) msg.obj;
				other_id_credit_text.setText("��֣�" + tempcredit);
				sharedPreference.putString("CREDIT", tempcredit);
				break;
			case MSG_FAILURE:
				break;
			case CREDIT_INIT:
				other_id_userid_text.setText("��֣�0");
				break;
			}
		}
	};

	private Runnable getUserIdRunner = new Runnable() {
		public void run() {

			TelephonyManager tm = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String registerUrl;
			if (tm.getDeviceId() == null) {
				registerUrl = Constant.Networks.REGISTER_BASE_HTTPURL
						+ "?"
						+ "imei="
						+ Secure.getString(getActivity()
								.getApplicationContext().getContentResolver(),
								Secure.ANDROID_ID);
			} else {
				registerUrl = Constant.Networks.REGISTER_BASE_HTTPURL + "?"
						+ "imei=" + tm.getDeviceId();
			}
			HttpXmlParser xpp = new HttpXmlParser();
			SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();

			if (xpp.getResultFromURL(registerUrl, result)) {
				String userid = result.get("userid");
				useridHandler.obtainMessage(MSG_SUCCESS, userid).sendToTarget();
			} else {
				useridHandler.obtainMessage(MSG_FAILURE).sendToTarget();
			}

		}
	};

	private Runnable getCreditRunner = new Runnable() {
		public void run() {
			if (userid.equals("NULL")) {
				creditHandler.obtainMessage(CREDIT_INIT).sendToTarget();
				return;
			}
			TelephonyManager tm = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String registerUrl = Constant.Networks.GET_BASE_HTTPURL
					+ "?userid=" + userid;
			if (tm.getDeviceId() == null) {
				registerUrl = registerUrl
						+ "&"
						+ "imei="
						+ Secure.getString(getActivity()
								.getApplicationContext().getContentResolver(),
								Secure.ANDROID_ID);
			} else {
				registerUrl = registerUrl + "&" + "imei=" + tm.getDeviceId();
			}
			HttpXmlParser xpp = new HttpXmlParser();
			SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();

			if (xpp.getResultFromURL(registerUrl, result)) {
				String account = result.get("account");
				creditHandler.obtainMessage(MSG_SUCCESS, account)
						.sendToTarget();
			} else {
				creditHandler.obtainMessage(MSG_FAILURE).sendToTarget();
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

		other_id_userid_text = (TextView) view_res
				.findViewById(R.id.other_id_userid);
		userid = sharedPreference.getString("USER_ID");
		if (userid.equals("NULL")) {
			Thread mThread = new Thread(getUserIdRunner);
			mThread.start();
		} else {
			other_id_userid_text.setText("�û��ţ�" + userid);
		}

		other_id_credit_text = (TextView) view_res
				.findViewById(R.id.other_id_credits);
		Thread mThread = new Thread(getCreditRunner);
		mThread.start();

		view_res.findViewById(R.id.btn_quit).setOnClickListener(this);
		view_res.findViewById(R.id.btn_about).setOnClickListener(this);
		view_res.findViewById(R.id.btn_check_update).setOnClickListener(this);
		view_res.findViewById(R.id.btn_feed_back).setOnClickListener(this);
		view_res.findViewById(R.id.btn_help).setOnClickListener(this);
		view_res.findViewById(R.id.btn_Set_Discuss).setOnClickListener(this);
		view_res.findViewById(R.id.btn_Set_Score).setOnClickListener(this);
		view_res.findViewById(R.id.btn_settings).setOnClickListener(this);
		view_res.findViewById(R.id.btn_agreement).setOnClickListener(this);
		view_res.findViewById(R.id.btn_exchange).setOnClickListener(this);

		return view_res;
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

	public void startShare() {
		if (!initShare) {
			boolean isInit = Frontia.init(
					getActivity().getApplicationContext(),
					"gxLMGxsKv6q3WRAKxBZwuidD");
			if (!isInit) {// Frontia is successfully initialized.
				// Use Frontia
				Toast.makeText(context, "init frontia fail", Toast.LENGTH_SHORT)
						.show();
			}
			mSocialShare = Frontia.getSocialShare();
			mSocialShare.setContext(context);
			mImageContent.setTitle(getResources()
					.getString(R.string.setDiscuss));
			mImageContent.setContent("文案");
			mImageContent.setLinkUrl("http://wifi4us.paad.com/");
			mImageContent.setImageData(BitmapFactory.decodeResource(
					getResources(), R.drawable.ic_launcher));
			initShare = true;
		}
		mSocialShare.share(mImageContent, MediaType.BATCHSHARE.toString(),
				new FrontiaSocialShareListener() {

					@Override
					public void onCancel() {
						Log.d("Test", "share cancel");

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.d("Test", "share fail");
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d("Test", "share success");
					}

				}, true);
	}

	boolean initUpdate = false;

	public void startUpdate() {
		if (!initUpdate) {
			UmengUpdateAgent.update(context);
			initUpdate = true;
		}
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
		case R.id.btn_exchange:
			try {
				startActivity(new Intent(getActivity(), DltActivity.class));
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
			startShare();
		case R.id.btn_check_update:
			startUpdate();
		default:
			break;
		}

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
		other_id_credit_text.setText(creditText);
	}

}
