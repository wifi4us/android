/**
 * @(#)LotteryHistoryFragment.java, 2014-1-23. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us.lottery;

import com.paad.wifi4us.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author yangshi
 *
 */
public class LotteryHistoryFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lottery_history, container, false);
    }


}
