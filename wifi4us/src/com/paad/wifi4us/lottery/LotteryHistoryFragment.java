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
import android.os.Handler;
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
	String[] groupkeys = new String[]{"ticket_id","state"};
	String[] childkeys = new String[]{"program", "period", "trade_id"};
	int[] groupViews = new int[]{R.id.lottery_history_ticket_id, R.id.lottery_history_state};
	int[] childViews = new int[]{R.id.lottery_history_program, R.id.lottery_history_period, R.id.lottery_history_trade_id};
	
	Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	lv = (ExpandableListView)inflater.inflate(R.layout.fragment_lottery_history, container, false);
    	refreshHistories();
    	handler = new Handler();
    	return lv;
    }
    
    void refreshHistories(){
    	new Thread(new Runnable(){

			@Override
			public void run() {
				List<LotteryHistory> rst = RemoteInfoFetcher.fetchLotteryHistories(SharedMembers.getInstance().getIMEI(), SharedMembers.getInstance().getUserId());
				
				if(rst!=null){
					histories = rst;
					handler.post(new Runnable(){

						@Override
						public void run() {
							updateView();
						}
						
					});
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
    	map.put(groupkeys[0], getResources().getString(R.string.lottery_info_ticket_id)+":"+history.ticketId);
    	map.put(groupkeys[1], getResources().getString(R.string.lottery_info_state)+":"+ getStateString(history.state));
    	return map;
    }
    public Map<String, String> buildChildMap(LotteryHistory history){
    	Map<String, String> map = new HashMap<String, String>();
    	map.put(childkeys[0], getResources().getString(R.string.lottery_info_program)+":"+buildProgramString(history.program));
    	map.put(childkeys[1], getResources().getString(R.string.lottery_info_period)+":"+history.periodNumber+"");
    	map.put(childkeys[2], getResources().getString(R.string.lottery_info_trade_id)+":"+history.tradeId);
    	return map;
    }
    
    protected String buildProgramString(String raw){
    	try{
    	int index = raw.indexOf('|');
    	String reds = raw.substring(0, index);
    	String blues = raw.substring(index+1);
    	return "\n  "+getResources().getString(R.string.redball)+" :"+reds+"\n  "+getResources().getString(R.string.blueball)+": "+blues;
    	}catch(Exception e){
    		return raw;
    	}
    }
    
    protected String getStateString(int state){
    	switch(state){
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
    		default:
    			return getResources().getString(R.string.lottery_state_unknow);
    	}
    }
    
    
    /**
     * for test
     * @return
     */
    static List<LotteryHistory> getMockHistories(){
    	List<LotteryHistory> rst =new ArrayList<LotteryHistory>();
    	rst.add(new LotteryHistory("1","02,12,22,09,02,12,22,09,22,00,9 |09,05,02,12,22","3","4",0));
    	rst.add(new LotteryHistory("11","12","13","14",1));
    	rst.add(new LotteryHistory("21","22","23","24",2));
    	rst.add(new LotteryHistory("61","62","63","64",0));
    	return rst;

    }
    
}
