/**
 * @(#)LotteryPlateFragment.java, 2014-1-23. 
 */
package com.paad.wifi4us.lottery;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paad.wifi4us.R;
import com.paad.wifi4us.utility.Constant;

/**
 * @author yangshi
 */
public class LotteryPlateFragment extends Fragment implements OnClickListener {
	int checkedRedBalls = 0;
	
	int checkedBlueBalls = 0;
	
	TextView tv;
	
	long caipiaoCnt = -1;
	
	long creditPerCaipiao = 2;
	
    int numRedBall = 33;

    int numBlueBall = 16;

    int minRedBall = 6;

    int minBlueBall = 1;

    int col = 7;

    List<CheckBox> redTbs = new ArrayList<CheckBox>();
    
    List<CheckBox> blueTbs = new ArrayList<CheckBox>();
    
    boolean[] blueStatus = new boolean[numBlueBall];
    
    boolean[] redStatus = new boolean[numRedBall];
    
    Button confirmButton;
	SharedPreferences sharedPreference;
    
    void reset(){
    	redTbs.clear();
    	blueTbs.clear();
    	checkedBlueBalls = 0;
    	checkedRedBalls = 0;
    	caipiaoCnt = -1;
    }
    
    
	
	@Override
	public void onStop() {
		for(int i =0;i<blueStatus.length;++i){
			blueStatus[i] = blueTbs.get(i).isChecked();
		}
		for(int i =0;i<redStatus.length;++i){
			redStatus[i] = redTbs.get(i).isChecked();
		}
		super.onStop();
	}



	OnCheckedChangeListener redballListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				++checkedRedBalls;
			} else {
				--checkedRedBalls;
			}
			updatePrice();
			refreshTextView();
		}
		
	};
	
	OnCheckedChangeListener blueballListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				++checkedBlueBalls;
			} else {
				--checkedBlueBalls;
			}
			updatePrice();
			refreshTextView();
		}
		
	};

    LayoutInflater inflater;

    View result;
    
    public void buildBalls(List<CheckBox> cbs, ViewGroup parrent, int ballLayout, OnCheckedChangeListener listener, int size, boolean[] status){
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        while (true) {
            if (cbs.size() >= size) {
                break;
            }
            LinearLayout ll = new LinearLayout(parrent.getContext());
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            parrent.addView(ll, lp);
            for (int j = 0; j < col; ++j) {
                if (cbs.size() >= size) {
                    CheckBox tb = (CheckBox) inflater.inflate(
                    		ballLayout, null);
                    tb.setVisibility(View.INVISIBLE);
                    ll.addView(tb);
                    continue;
                }
                CheckBox tb = (CheckBox) inflater.inflate(
                		ballLayout, null);
                tb.setOnCheckedChangeListener(listener);
                tb.setText((cbs.size() + 1) + "");
                ll.addView(tb);
                cbs.add(tb);
                tb.setChecked(status[cbs.size()-1]);
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        result = inflater.inflate(R.layout.fragment_lottery_plate, container,
                false);
        sharedPreference = getActivity().getSharedPreferences("lottery", Context.MODE_PRIVATE);
        Log.i("lottery", "oncreateview");
        this.inflater = inflater;
        tv = (TextView) result.findViewById(R.id.dlt_text);
        reset();
        buildBalls(redTbs,(LinearLayout) result
                .findViewById(R.id.dlt_red),R.layout.checkbox_redball,redballListener,numRedBall,redStatus);
        buildBalls(blueTbs,(LinearLayout) result
                .findViewById(R.id.dlt_blue),R.layout.checkbox_blueball,blueballListener,numBlueBall, blueStatus);
        confirmButton = (Button) result.findViewById(R.id.dlt_confirm_btn);
        confirmButton.setOnClickListener(this);
        refreshTextView();
        return result;

    }


    protected void updatePrice() {
        if (checkedRedBalls >= 6 && checkedBlueBalls >= 1) {
            caipiaoCnt = cxy(checkedBlueBalls, 1) * cxy(checkedRedBalls, 6);
        } else {
            caipiaoCnt = -1;
        }
    }

    long cxy(int x, int y) {
        long num = 1;
        int n = 0;
        while (n < y) {
            num *= x--;
            ++n;
        }
        while (y > 1) {
            num /= y--;
        }
        return num;
    }

    protected void refreshTextView() {
        if (tv != null) {
            if (caipiaoCnt > 0) {
                tv.setText("已购买" + caipiaoCnt + "注，需"+ caipiaoCnt
                        * creditPerCaipiao + "积分");
                confirmButton.setClickable(true);
            } else {
                tv.setText("选择至少6个红球和一个篮球");
                confirmButton.setClickable(false);
            }
        }
    }

    String buildConfirmInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("您购买的彩票是：\n红球：");
        for (int i = 0; i < redTbs.size(); ++i) {
            if (redTbs.get(i).isChecked()) {
                sb.append(i + 1);
                sb.append(" ");
            }
        }
        sb.append("\n蓝球：");
        for (int i = 0; i < blueTbs.size(); ++i) {
            if (blueTbs.get(i).isChecked()) {
                sb.append(i + 1);
                sb.append(" ");
            }
        }
        sb.append("\n共" + caipiaoCnt + "注, 需要" + caipiaoCnt * creditPerCaipiao
                + "积分");
        return sb.toString();
    }
    
	boolean checkUserInfo() {
		if (sharedPreference.getString(Constant.HttpParas.ALIPAY_ID, null) != null
				&& sharedPreference.getString(Constant.HttpParas.ID_NUM, null) != null
				&& sharedPreference.getString(Constant.HttpParas.PHONE, null) != null
				&& sharedPreference.getString(Constant.HttpParas.NAME, null) != null) {
			return true;
		}
		return false;
	}

    @Override
    public void onClick(View arg0) {
    	
    	if(!checkUserInfo()){
    		Toast.makeText(getActivity(), "请先完善用户信息", Toast.LENGTH_LONG).show();
    		return;
    	}
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(buildConfirmInfo());
        builder.setTitle("购买确认");
        builder.setPositiveButton("购买", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
