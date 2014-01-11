/**
 * @(#)FeedbackActivity.java, 2014-1-6. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.paad.wifi4us.utility.Constants;

/**
 *
 * @author yangshi
 *
 */
public class FeedbackActivity  extends Activity implements OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViewById(R.id.btn_send_feedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String msg = ((EditText)findViewById(R.id.view_feedback_msg)).toString();
        String contact = ((EditText)findViewById(R.id.view_feedback_contact)).toString();
        postFeedback(msg, contact);
    }
    
    public boolean postFeedback(String feedback, String contact) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(Constants.SERVER_PREFIX);
        List<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
        postData.add(new BasicNameValuePair("msg", feedback));
        postData.add(new BasicNameValuePair("contact", contact));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return false;
        }
        post.setEntity(entity);
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            return true;
        }
        return false;
    }

}
