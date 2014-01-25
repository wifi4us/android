/**
 * @(#)UserInformationFragment.java, 2014-1-23. 
 * 
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
public class UserInformationFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lottery_info, container, false);
    }
}
