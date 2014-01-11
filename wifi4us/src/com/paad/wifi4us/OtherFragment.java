package com.paad.wifi4us;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.paad.wifi4us.utility.HttpXmlParser;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class OtherFragment extends Fragment implements OnClickListener {
    private TextView other_id_userid_text;

    private static final int MSG_SUCCESS = 0;

    private static final int MSG_FAILURE = 1;

    private static final String REGISTER_BASE_HTTPURL = "http://wifi4us.duapp.com/register.php";

	private SharedPreferenceHelper sharedPreference;

	private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    other_id_userid_text.setText("用户id为 " + (String) msg.obj);
       
                    sharedPreference.putString("USER_ID", (String) msg.obj);
                    break;
                case MSG_FAILURE:
                    other_id_userid_text
                            .setText("请联网后重新打开软件，重新获取id");
                    break;
            }
        }
    };

    Runnable getUserIdRunner = new Runnable() {
        public void run() {

            TelephonyManager tm = (TelephonyManager) getActivity()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String registerUrl;
            if (tm.getDeviceId() == null) {
                registerUrl = REGISTER_BASE_HTTPURL
                        + "?"
                        + "imei="
                        + Secure.getString(getActivity()
                                .getApplicationContext().getContentResolver(),
                                Secure.ANDROID_ID);
            } else {
                registerUrl = REGISTER_BASE_HTTPURL + "?" + "imei="
                        + tm.getDeviceId();
            }
            HttpXmlParser xpp = new HttpXmlParser();
            SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();

            if (xpp.getResultFromURL(registerUrl, result)) {
                String userid = result.get("userid");
                mHandler.obtainMessage(MSG_SUCCESS, userid).sendToTarget();
            } else {
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
            }

        }
    };

    String userid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view_res = inflater.inflate(R.layout.fragment_other, container,
                false);
        
    	sharedPreference = new SharedPreferenceHelper(getActivity().getApplicationContext());

        userid = sharedPreference.getString("USER_ID");
        
        view_res.findViewById(R.id.btn_quit).setOnClickListener(this);
        view_res.findViewById(R.id.btn_about).setOnClickListener(this);
        view_res.findViewById(R.id.btn_check_update).setOnClickListener(this);
        view_res.findViewById(R.id.btn_feed_back).setOnClickListener(this);
        view_res.findViewById(R.id.btn_help).setOnClickListener(this);
        view_res.findViewById(R.id.btn_Set_Discuss).setOnClickListener(this);
        view_res.findViewById(R.id.btn_Set_Score).setOnClickListener(this);
        view_res.findViewById(R.id.btn_settings).setOnClickListener(this);
        view_res.findViewById(R.id.btn_agreement).setOnClickListener(this);
        if (userid.equals("NULL")) {
            Thread mThread = new Thread(getUserIdRunner);
            mThread.start();
        }

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
        Process.killProcess(Process.myPid());
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
            case R.id.btn_quit:
                quit();
                break;
            case R.id.btn_feed_back:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            default:
                break;
        }

    }

}
