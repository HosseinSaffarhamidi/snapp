package com.example.adrom.snapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import Lib.Service;

public class ActiveActivity extends AppCompatActivity {

    String baseUrl = "http://192.168.1.117:5000/";
    public  int time= 180;
    String active_code="";
    int[] editText_id={R.id.num1,R.id.num2,R.id.num3,R.id.num4,R.id.num5};
    String token;
    SharedPreferences sp;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        for (int i=0;i<editText_id.length;i++)
        {
            int j=i+1;
            final EditText number1=(EditText)findViewById(editText_id[i]);

            if(editText_id.length-1!=i)
            {
                final EditText number2=(EditText)findViewById(editText_id[j]);
                number1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable)
                    {
                        if(!number1.getText().toString().isEmpty())
                        {
                            number2.requestFocus();
                        }

                    }
                });
            }

        }

        final Handler handler=new Handler();
        Thread thread=new Thread(new Runnable() {
            TextView time_active=(TextView)findViewById(R.id.time_active);
            String t="مدت زمان باقی مانده : ";
            @Override
            public void run()
            {
                while (time>-1)
                {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if(time==0)
                                {
                                    LinearLayout active_box=(LinearLayout)findViewById(R.id.active_box);
                                    active_box.setVisibility(View.GONE);
                                }
                                else
                                {
                                    String t2="";
                                    double a=time/60;
                                    int b=(int)a;
                                    if(b>0)
                                    {
                                        int c=time-(b*60);
                                        t2=b+":"+c;
                                    }
                                    else
                                    {
                                        t2=String.valueOf(time);
                                    }
                                    time_active.setText(t+t2);
                                }

                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time--;
                }

            }
        });
        thread.start();

        Bundle bundle=getIntent().getExtras();
        token=bundle.getString("token");
        String mobile=bundle.getString("mobile");
        TextView mobile_number=(TextView)findViewById(R.id.mobile_number);
        mobile_number.setText(mobile);

    }

    public void send_code(View view)
    {
        boolean send=true;
        active_code="";
        for (int i=0;i<editText_id.length;i++)
        {
            final EditText number1=(EditText)findViewById(editText_id[i]);
            if(number1.getText().toString().trim().isEmpty())
            {
                send=false;
            }
            else
            {
                active_code=active_code+number1.getText().toString().trim();
            }
        }
        if(send)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<ServerData.active> call = apiInterface.active_mobile_number(active_code,token);

            Callback<ServerData.active> callback=new Callback<ServerData.active>() {
                @Override
                public void onResponse(Call<ServerData.active> call, Response<ServerData.active> response) {
                    String status=response.body().getStatus();
                    if(status.equals("ok"))
                    {
                        String token=response.body().getToken();
                        sp=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("token",token);
                        editor.commit();

                        Intent j=new Intent(ActiveActivity.this,MapsActivity.class);
                        startActivity(j);
                    }
                    else if (status.equals("error_code"))
                    {
                        Toast.makeText(ActiveActivity.this,"کد فعال سازی اشتباه می باشد",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Intent j=new Intent(ActiveActivity.this,RegisterActivity.class);
                        startActivity(j);
                    }
                }

                @Override
                public void onFailure(Call<ServerData.active> call, Throwable t) {

                }
            };


            call.enqueue(callback);
        }
    }

}
