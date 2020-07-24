package com.example.adrom.snapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import Lib.Service;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;
    int show_password=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final EditText password=(EditText)findViewById(R.id.password);
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(motionEvent.getAction()==motionEvent.ACTION_UP)
                {
                    if((motionEvent.getRawX()-password.getCompoundDrawables()[0].getBounds().width())<=password.getCompoundDrawables()[0].getBounds().width())
                    {
                        if(show_password==0)
                        {
                            show_password=1;
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.eye,0);
                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        else
                        {
                            show_password=0;
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.eye_off,0);
                            password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    }

                }
                return false;
            }
        });
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void user_login(View view)
    {
        EditText mobile=(EditText)findViewById(R.id.mobile);
        EditText password=(EditText)findViewById(R.id.password);

        if(mobile.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "اطلاعات لازم برای ورود را وارد نمایید", Toast.LENGTH_LONG).show();
        }
        else
        {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<ServerData.login> call = apiInterface.login(mobile.getText().toString(), password.getText().toString());


            Callback<ServerData.login> callback = new Callback<ServerData.login>() {
                @Override
                public void onResponse(Call<ServerData.login> call, Response<ServerData.login> response) {
                    if (response.isSuccessful())
                    {
                        if(response.body().auth.equals("true"))
                        {
                            String token=response.body().token;
                            sp=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("token",token);
                            editor.commit();

                            Intent j=new Intent(LoginActivity.this,CedarMapActivity.class);
                            startActivity(j);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"شماره موبایل یا کلمه عبور وارد شده اشتباه می باشد",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerData.login> call, Throwable t) {

                }
            };

            call.enqueue(callback);

        }
    }

}

