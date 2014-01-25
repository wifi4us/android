/**
 * @(#)LotteryHistoryFragment.java, 2014-1-23. 
 * 
 */
package com.paad.wifi4us.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.paad.wifi4us.R;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
import com.paad.wifi4us.utility.SharedMembers;
import com.paad.wifi4us.utility.data.LotteryHistory;

/**
 * for show lottery exchange history
 * @author yangshi
 *
 */
public class LotteryHistoryFragment extends Fragment {
	ExpandableListView lv;
	List<LotteryHistory> histories = new ArrayList<LotteryHistory>();
	String[] groupkeys = new String[]{"period","state"};
	String[] childkeys = new String[]{"program", "ticket_id", "trade_id"};
	int[] groupViews = new int[]{R.id.lottery_history_period, R.id.lottery_history_state};
	int[] childViews = new int[]{R.id.lottery_history_program, R.id.lottery_history_ticket_id, R.id.lottery_history_trade_id};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	lv = (ExpandableListView)inflater.inflate(R.layout.fragment_lottery_history, container, false);
    	refreshHistories();
    	return lv;
    }
    
    void refreshHistories(){
    	new Thread(new Runnable(){

			@Override
			public void run() {
				List<LotteryHistory> rst = RemoteInfoFetcher.fetchLotteryHistories(SharedMembers.getInstance().getIMEI(), SharedMembers.getInstance().getUserId());
				if(rst!=null){
					histories = rst;
					updateView();
				}
			}
    	}).start();;
    }
    public void updateView(){
    	List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
    	List<List<Map<String,String>>> childData = new ArrayList<List<Map<String,String>>>();
    	for(LotteryHistory history:histories){
    		groupData.add(buildGroupMap(history));
    		List<Map<String,String>> innerList = new ArrayList<Map<String,String>>();
    		innerList.add(buildChildMap(history));
			childData.add(innerList);
		}
		lv.setAdapter(new SimpleExpandableListAdapter(getActivity(), groupData,
				R.layout.lottery_history_group, groupkeys, groupViews,
				childData, R.layout.lottery_history_child, childkeys,
				childViews));
		
	}
    
    public Map<String, String> buildGroupMap(LotteryHistory history){
    	Map<String, String> map = new HashMap<String, String>();
    	map.put(groupkeys[0], history.periodNumber+"");
    	map.put(groupkeys[1], history.state+"");
    	return map;
    }
    public Map<String, String> buildChildMap(LotteryHistory history){
    	Map<String, String> map = new HashMap<String, String>();
    	map.put(childkeys[0], history.program);
    	map.put(childkeys[1], history.ticketId+"");
    	map.put(childkeys[2], history.tradeId+"");
    	return map;
    }
    
}
