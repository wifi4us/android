/**
 * @(#)ShareActionProvider.java, 2014-1-9. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

/**
 *
 * @author yangshi
 *
 */
public class ShareActionProvider extends ActionProvider {

    Context mContext;
    
    public ShareActionProvider(Context context){
        super(context);
        mContext = context;
    }
    
    @Override
    public View onCreateActionView(MenuItem forItem) {
        return super.onCreateActionView(forItem);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ActionProvider#onCreateActionView()
     */
    @Override
    public View onCreateActionView() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ActionProvider#onPrepareSubMenu(android.view.SubMenu)
     */
    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        //mContext.getMenuInflater().inflate(R.menu.actionbar_meau, subMenu);
        subMenu.add(0, 0, 0, "back").setIcon(R.drawable.ic_back).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast = Toast.makeText(
                        mContext,
                        "click back sub-menu",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
            
        });
        subMenu.add(0, 0, 0, "more").setIcon(R.drawable.ic_more).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast = Toast.makeText(
                        mContext,
                        "click more sub-menu",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
            
        });
        super.onPrepareSubMenu(subMenu);
    }
    

}
