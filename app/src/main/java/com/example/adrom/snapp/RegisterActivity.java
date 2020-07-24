package com.example.adrom.snapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {

    EditText name;
    EditText mobile;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_register);
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public void add_user(View view)
    {
        name=(EditText)findViewById(R.id.name);
        mobile=(EditText)findViewById(R.id.mobile);
        password=(EditText)findViewById(R.id.password);
        if(validate())
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<ServerData.register> call = apiInterface.register(name.getText().toString(), mobile.getText().toString(), password.getText().toString());

            Callback<ServerData.register> callback = new Callback<ServerData.register>() {
                @Override
                public void onResponse(Call<ServerData.register> call, Response<ServerData.register> response) {
                    if (response.isSuccessful())
                    {
                        if(response.body().getStatus().equals("no"))
                        {

                            Toast.makeText(RegisterActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                        }
                        else if(response.body().getStatus().equals("ok"))
                        {
                            String token=response.body().getToken();
                            Intent j=new Intent(RegisterActivity.this,ActiveActivity.class);
                            j.putExtra("token",token);
                            j.putExtra("mobile",mobile.getText().toString());
                            startActivity(j);
                            overridePendingTransition(R.anim.enter,R.anim.exit);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerData.register> call, Throwable t) {
                    Log.i("response_data", "error");
                }
            };


            call.enqueue(callback);
        }
    }
    public boolean validate()
    {
        boolean a=validateName();
        boolean b=validateMobile();
        boolean c=validatePassword();
        if(a && b && c)
        {

            return true;
        }
        else
        {
            return  false;
        }

    }
    public boolean validateName()
    {
        boolean a=true;
        if(name.getText().toString().trim().isEmpty())
        {
            a=false;
            name.setBackgroundResource(R.drawable.validateborder);
        }
        else
        {
            a=true;
            name.setBackgroundResource(R.drawable.editborder);
        }
        return  a;
    }

    public boolean validateMobile()
    {
        boolean a=true;
        if(mobile.getText().toString().trim().isEmpty())
        {
            a=false;
            mobile.setBackgroundResource(R.drawable.validateborder);
        }
        else
        {
            String numberPattern="[0-9]+";
            if(mobile.getText().toString().matches(numberPattern))
            {
                if(mobile.getText().toString().length()==11)
                {
                    if(mobile.getText().toString().substring(0,2).equals("09"))
                    {
                        a=true;
                        mobile.setBackgroundResource(R.drawable.editborder);
                    }
                    else
                    {
                        a=false;
                        Toast.makeText(RegisterActivity.this,"شماره موبایل وارد شده معتبر نمی باشد",Toast.LENGTH_LONG).show();
                        mobile.setBackgroundResource(R.drawable.validateborder);
                    }

                }
                else if (mobile.getText().toString().length()==10)
                {
                    if(mobile.getText().toString().substring(0,1).equals("9"))
                    {
                        a=true;
                        mobile.setBackgroundResource(R.drawable.editborder);
                    }
                    else
                    {
                        a=false;
                        Toast.makeText(RegisterActivity.this,"شماره موبایل وارد شده معتبر نمی باشد",Toast.LENGTH_LONG).show();
                        mobile.setBackgroundResource(R.drawable.validateborder);
                    }
                }
                else
                {
                    a=false;
                    Toast.makeText(RegisterActivity.this,"شماره موبایل وارد شده معتبر نمی باشد",Toast.LENGTH_LONG).show();
                    mobile.setBackgroundResource(R.drawable.validateborder);
                }

            }
            else
            {
                a=false;
                Toast.makeText(RegisterActivity.this,"شماره موبایل وارد شده معتبر نمی باشد",Toast.LENGTH_LONG).show();
                mobile.setBackgroundResource(R.drawable.validateborder);
            }
        }
        return  a;
    }

    public boolean validatePassword()
    {
        boolean a=true;
        if(password.getText().toString().trim().isEmpty())
        {
            a=false;
            password.setBackgroundResource(R.drawable.validateborder);
        }
        else
        {
            if(password.getText().toString().length()<6)
            {
                a=false;
                password.setBackgroundResource(R.drawable.validateborder);
                Toast.makeText(RegisterActivity.this,"کلمه عبور حداقل باید شامل 6 کاراکتر باشد",Toast.LENGTH_LONG).show();
            }
            else
            {
                a=true;
                password.setBackgroundResource(R.drawable.editborder);
            }
        }

        return  a;
    }

}
