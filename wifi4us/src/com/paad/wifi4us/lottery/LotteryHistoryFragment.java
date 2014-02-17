/**
 * @(#)LotteryHistoryFragment.java, 2014-1-23.
 */
package com.paad.wifi4us.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.paad.wifi4us.R;
import com.paad.wifi4us.utility.DeviceInfo;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
import com.paad.wifi4us.utility.SharedPreferenceHelper;
import com.paad.wifi4us.utility.data.LotteryHistory;

/**
 * for show lottery exchange history
 * 
 * @author yangshi
 */
public class LotteryHistoryFragment extends Fragment {
    ExpandableListView lv;

    private SharedPreferenceHelper sharedPreference;

    List<LotteryHistory> histories = new ArrayList<LotteryHistory>();

    String[] groupkeys = new String[] {
        "period", "state"
    };

    String[] childkeys = new String[] {
        "trade_id", "program"
    };

    int[] groupViews = new int[] {
        R.id.lottery_history_period, R.id.lottery_history_state
    };

    int[] childViews = new int[] {
        R.id.lottery_history_trade_id,
        R.id.lottery_history_program
    };

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery_history,
                container, false);
        sharedPreference = new SharedPreferenceHelper(getActivity());
        lv = (ExpandableListView) view
                .findViewById(R.id.lottery_history_listview);
        textView = (TextView) view.findViewById(R.id.lottery_history_textview);
        textView.setVisibility(View.GONE);
        refreshHistories();
        Button header = new Button(this.getActivity());
        header.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshHistories();
            }
        });
        header.setText(getResources().getString(R.string.lottery_more));
        lv.addHeaderView(header);
        return view;
    }

    void refreshHistories() {
        new AsyncTask<Object, Object, List<LotteryHistory>>() {
            ProgressDialog dialog;
            @Override
            protected List<LotteryHistory> doInBackground(Object... params) {
                return RemoteInfoFetcher
                .fetchLotteryHistories(
                        DeviceInfo.getInstance(getActivity()).getIMEI(),
                        sharedPreference.getString("USER_ID"));
                
            }

            /* (non-Javadoc)
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(List<LotteryHistory> rst) {
                if (rst != null) {
                    histories = rst;
                    updateView();
                } else {
                    textView.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
                super.onPostExecute(rst);
            }

            /* (non-Javadoc)
             * @see android.os.AsyncTask#onPreExecute()
             */
            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(LotteryHistoryFragment.this.getActivity());
                dialog.setTitle("qqq兑换历史");
                dialog.setMessage("qqq正在获取兑换历史，请稍后...");
                dialog.show();
                super.onPreExecute();
            }

        }.execute(new Object[]{new Object()});
    }

    public void updateView() {
        textView.setVisibility(View.GONE);
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        for (LotteryHistory history: histories) {
            groupData.add(buildGroupMap(history));
            List<Map<String, String>> innerList = new ArrayList<Map<String, String>>();
            innerList.add(buildChildMap(history));
            childData.add(innerList);
        }
        lv.setAdapter(new SimpleExpandableListAdapter(getActivity(), groupData,
                R.layout.lottery_history_group, groupkeys, groupViews,
                childData, R.layout.lottery_history_child, childkeys,
                childViews));

    }

    public Map<String, String> buildGroupMap(LotteryHistory history) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(groupkeys[0],
                getResources().getString(R.string.lottery_info_period) + ":"
                        + history.periodNumber);
        map.put(groupkeys[1], getStateString(history.state));
        return map;
    }

    public Map<String, String> buildChildMap(LotteryHistory history) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(childkeys[1],
                getResources().getString(R.string.lottery_info_program) + ":"
                        + buildProgramString(history.program));
        map.put(childkeys[0],
                getResources().getString(R.string.lottery_info_trade_id) + ":"
                        + history.tradeId);
        return map;
    }

    protected String buildProgramString(String raw) {
        try {
            int index = raw.indexOf('|');
            String reds = raw.substring(0, index);
            String blues = raw.substring(index + 1);
            return "\n  " + getResources().getString(R.string.redball) + " :"
                    + reds + "\n  "
                    + getResources().getString(R.string.blueball) + ": "
                    + blues;
        } catch (Exception e) {
            return raw;
        }
    }

    protected String getStateString(int state) {
        switch (state) {
            case 0:
                return getResources().getString(R.string.lottery_state_0);
            case 2:
                return getResources().getString(R.string.lottery_state_2);
            case 3:
                return getResources().getString(R.string.lottery_state_3);
            case 4:
                return getResources().getString(R.string.lottery_state_4);
            case 5:
                return getResources().getString(R.string.lottery_state_5);
            case 6:
                return getResources().getString(R.string.lottery_state_6);
            case 7:
                return getResources().getString(R.string.lottery_state_7);
            default:
                return getResources().getString(R.string.lottery_state_unknow);
        }
    }

}
