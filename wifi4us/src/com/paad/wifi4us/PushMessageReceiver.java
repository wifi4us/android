/**
 * @(#)PushMessageReceiver.java, 2014-3-6. 
 * 
 */
package com.paad.wifi4us;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

/**
 *
 * @author yangshi
 *
 */
public class PushMessageReceiver extends FrontiaPushMessageReceiver{

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onBind(android.content.Context, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void onBind(Context arg0, int arg1, String arg2, String arg3,
            String arg4, String arg5) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onDelTags(android.content.Context, int, java.util.List, java.util.List, java.lang.String)
     */
    @Override
    public void onDelTags(Context arg0, int arg1, List<String> arg2,
            List<String> arg3, String arg4) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onListTags(android.content.Context, int, java.util.List, java.lang.String)
     */
    @Override
    public void onListTags(Context arg0, int arg1, List<String> arg2,
            String arg3) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onMessage(android.content.Context, java.lang.String, java.lang.String)
     */
    @Override
    public void onMessage(Context arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onNotificationClicked(android.content.Context, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void onNotificationClicked(Context context, String title, 
            String description, String customContentString) {
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onSetTags(android.content.Context, int, java.util.List, java.util.List, java.lang.String)
     */
    @Override
    public void onSetTags(Context arg0, int arg1, List<String> arg2,
            List<String> arg3, String arg4) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.baidu.frontia.api.FrontiaPushMessageReceiver#onUnbind(android.content.Context, int, java.lang.String)
     */
    @Override
    public void onUnbind(Context arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

}
