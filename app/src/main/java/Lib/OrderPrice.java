package Lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.adrom.snapp.ApiInterface;
import com.example.adrom.snapp.CedarMapActivity;
import com.example.adrom.snapp.R;
import com.example.adrom.snapp.ServerData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.DecimalFormat;

/**
 * Created by idehpardazanjavan on 1/23/19.
 */

public class OrderPrice
{
    //http://192.168.1.117:5000/
    public static int price=0;
    public static int cancel_service=0;
    static int fixed_price=0;
    static int i=0;
    static double service_lat,service_lng,service_directions;
    static boolean run_fun=false;
    static Context cx;
    public static int service_price;
    public static int location2_price=0;
    public static int going_back_price=0;
    public static int stop_price=0;
    public static void get_price_from_server(double lat,double lng)
    {
        service_lat=lat;
        service_lng=lng;
        i=1;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface=retrofit.create(ApiInterface.class);
        Call<ServerData.Service_Price> call=apiInterface.get_service_price(String.valueOf(lat),String.valueOf(lng));
        Callback<ServerData.Service_Price> callback=new Callback<ServerData.Service_Price>() {
            @Override
            public void onResponse(Call<ServerData.Service_Price> call, Response<ServerData.Service_Price> response) {
                if(response.isSuccessful())
                {
                    price=Integer.valueOf(response.body().getPrice());
                    Log.i("jafeghufj",String.valueOf(price));
                    fixed_price=Integer.valueOf(response.body().getFixed_price());
                    if(run_fun)
                    {
                        run_fun=false;
                        show_service_info(cx,service_directions);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerData.Service_Price> call, Throwable t) {

                    Log.i("jafeghufj","error");
            }
        };

        call.enqueue(callback);
    }

    public static void set_error_service()
    {

    }
    public static void show_service_info(Context context,double directions)
    {

        if(price==0 && i<3)
        {
            service_directions=directions;
            cx=context;
            i++;
            run_fun=true;
            get_price_from_server(service_lat,service_lng);
        }
        else
        {
            service_price=get_final_price(directions);
            ServiceView.show_view(context,service_price);
        }
    }
    public static int get_final_price(double directions)
    {

        double c=(directions)/1000;
        Log.i("cc",String.valueOf(c));
        c=Math.round(c)*price;
        int p3=(int)c;
        int p=0;
        if(Service.going_back.equals("ok"))
        {
            going_back_price=p3*10+5000;
            p=service_price+going_back_price;
        }
        else
        {
             p=(fixed_price+p3)*10;
        }

        return  p;
    }
    public static void remove_back_price(Context cx)
    {
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");

        service_price=service_price-going_back_price;
        going_back_price=0;
        TextView price_text=((Activity) cx).findViewById(R.id.price);
        String p_string=App.replace_number(decimalFormat.format(service_price))+" ریال";
        price_text.setText(p_string);
    }
    public static void set_stop_price(Context cx,int stop_time_price){

        final DecimalFormat decimalFormat=new DecimalFormat("###,###");

        TextView price_text=((Activity) cx).findViewById(R.id.price);
        stop_price=stop_time_price;
        service_price=service_price+stop_time_price;
        String p_string=decimalFormat.format(service_price)+" ریال";
        price_text.setText(App.replace_number(p_string));

    }
    public static void remover_stop_time(Context cx)
    {
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");

        service_price=service_price-stop_price;
        stop_price=0;

        TextView price_text=((Activity) cx).findViewById(R.id.price);
        String p_string=decimalFormat.format(service_price)+" ریال";
        price_text.setText(App.replace_number(p_string));


        RelativeLayout select_stop_time_layout=((Activity) cx).findViewById(R.id.select_stop_time_layout);
        TextView select_stop_text=((Activity) cx).findViewById(R.id.select_stop_text);

        final TextView times=((Activity) cx).findViewById(R.id.times);

        select_stop_text.setTextColor(Color.GRAY);
        times.setTextColor(Color.GRAY);
        select_stop_time_layout.setBackgroundResource(R.drawable.itemraduis);
        times.setText("مجموع توقف");
    }
    public static void request_cancel_service(final Context context, String service_id)
    {
        SharedPreferences sp=context.getSharedPreferences("Taxi_user_datas",Context.MODE_PRIVATE);
        final String token=sp.getString("token","null");

        Retrofit retrofit=new Retrofit.Builder().baseUrl(Service.baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiInterface apiInterface=retrofit.create(ApiInterface.class);
        Call<String> call=apiInterface.cancel_service(token,service_id);
        Callback<String> callback=new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body().equals("ok")){
                        cancel_service=1;
                        Toast.makeText(context, "سفارش شما لغو شد", Toast.LENGTH_LONG).show();
                        Intent refresh=new Intent(context, CedarMapActivity.class);
                        context.startActivity(refresh);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        };

        call.enqueue(callback);
    }

}
