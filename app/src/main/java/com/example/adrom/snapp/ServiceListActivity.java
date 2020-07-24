package com.example.adrom.snapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import Lib.ServiceList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ServiceListActivity extends AppCompatActivity
{
    int scrollHeight=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_service_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        final ServiceList serviceList=new ServiceList(this);

        serviceList.get_data();

        final ScrollView scrollView=(ScrollView)findViewById(R.id.scrollView);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                View view=scrollView.getChildAt((scrollView.getChildCount()-1));
                int y=scrollView.getScrollY();
                int height=scrollView.getHeight();
                int di=(view.getBottom()-(y+height));
                if(di<30 && height>scrollHeight && serviceList.load==1)
                {
                    Toast.makeText(ServiceListActivity.this, "a", Toast.LENGTH_SHORT).show();
                    scrollHeight=height;
                    serviceList.change_page();;
                    serviceList.get_data();

                }
            }
        });


    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
