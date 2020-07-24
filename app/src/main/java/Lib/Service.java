package Lib;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Build;

import android.widget.RemoteViews;

import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;

import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;

import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting;
import com.example.adrom.snapp.ApiInterface;
import com.example.adrom.snapp.CedarMapActivity;
import com.example.adrom.snapp.Place;
import com.example.adrom.snapp.R;
import com.example.adrom.snapp.ServerData;
import com.github.nkzawa.socketio.client.Socket;
import com.mapbox.mapboxsdk.annotations.Marker;

import com.mapbox.mapboxsdk.geometry.LatLng;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by idehpardazanjavan on 9/14/18.
 */

public class Service
{
    //http://31556.ir:3000/
    //http://192.168.43.210:3000/
    //http://192.168.43.210:8888/Taxi_app/public/
    static int send_score=1;
    public static String show_notification="ok";
    static int i=0;
    public static String baseUrl="http://toosmachinery.com:3000/";
    public static String site_url="http://toosmachinery.com/";
    static double directions;
    static Socket socket;
    public static int is_running_service=0;
    public static String address1,address2,address3;
    static String api_service="Cedar";
    public static LatLng latLng1,latLng2,latLng3;
    public static Marker m1,m2,m3;
    public static int set_latlng1 = 1;
    public static int set_latlng2 = 1;
    public static int set_latlng3=1;
    public static String running_service_id;
    public static String going_back="no";
    public static JSONObject data;
    public static void get_directions(get_callback get_callback) {
        if(api_service.equals("google"))
        {
            get_directions_from_google(get_callback);
        }
        else
        {
            get_directions_from_cedar(get_callback);
        }
    }
    public static void get_directions_from_google(final get_callback get_callback) {
        directions=0;
        String baseUrl="https://maps.googleapis.com/";
        String origin=latLng1.getLatitude()+","+latLng1.getLongitude();
        String destination=latLng2.getLatitude()+","+latLng2.getLongitude();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Place.routes> call = apiInterface.get_directions(origin,destination,"false","driving","AIzaSyDuROZDn60mMZl5vubuGlg_qRRXsjuIoUg");
        Callback<Place.routes> callback = new Callback<Place.routes>() {
            @Override
            public void onResponse(Call<Place.routes> call, Response<Place.routes> response) {
                if(response.isSuccessful())
                {

                    try
                    {
                        directions=response.body().getRoutes().get(0).getLegs().get(0).getDistance().getValue();
                        get_callback.onResponse(directions);
                    }
                    catch (Exception e)
                    {
                        get_callback.onFailure("error");
                    }
                }
            }

            @Override
            public void onFailure(Call<Place.routes> call, Throwable t) {
                get_callback.onFailure("error");
            }
        };

        call.enqueue(callback);
    }
    public static void get_directions_from_cedar(final get_callback get_callback){


        LatLng[] direction=direction_location();

        CedarMaps.getInstance().direction(direction[0],direction[1],
                new GeoRoutingResultListener() {
                    @Override
                    public void onSuccess(@NonNull GeoRouting result) {

                        try {
                            get_callback.onResponse(result.getRoutes().get(0).getDistance().doubleValue());
                        }
                        catch (Exception e)
                        {
                            get_callback.onFailure("error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull String error) {

                        get_callback.onFailure("error");
                    }
       });
    };
    public interface get_callback {
        void onResponse(double directions);
        void onFailure(String error);
    }
    public static void set_address(final LatLng latLng,final int n) {
        CedarMaps.getInstance().reverseGeocode(latLng, new ReverseGeocodeResultListener() {
            @Override
            public void onSuccess(@NonNull ReverseGeocode result)
            {
                String s=result.getCity();
                if(!result.getDistrict().isEmpty())
                {
                    s=s+" "+result.getDistrict();
                }
                if(!result.getLocality().isEmpty())
                {
                    s=s+" "+result.getLocality();
                }
                if(!result.getPlace().isEmpty())
                {
                    s=s+" "+result.getPlace();
                }
                s=s+" "+result.getAddress();

                if(n==1)
                {
                    address1=s;
                }
                else if(n==2)
                {
                    address2=s;
                }
                else if(n==3)
                {
                    address3=s;
                }

            }

            @Override
            public void onFailure(@NonNull String errorMessage) {

                set_address(latLng,n);
            }
        });
    }
    public static int get_price(double directions,int price_km) {
        double c=(directions)/1000;
        c=Math.round(c)*5000;
        int p3=(int)c;
        int p=price_km+p3;
        return  p;
    }

    public static String[] get_time_value(){
         String[] values=new String[11];
        values[0]="۰ تا ۵ دقیقه";
        values[1]="۵ تا ۱۰ دقیقه";
        values[2]="۱۰ تا ۱۵ دقیقه";
        values[3]="۱۵ تا ۲۰ دقیقه";
        values[4]="۲۰ تا ۲۵ دقیقه";
        values[5]="۲۵ تا ۳۰ دقیقه";
        values[6]="۳۰ تا ۴۵ دقیقه";
        values[7]="۴۵ تا ۱ ساعت";
        values[8]="۱ تا ۱.۵ ساعت";
        values[9]="۱.۵ تا ۲ ساعت";
        values[10]="";
        return  values;
    }
    public static int[] get_price_time() {
        final int[] price_time=new int[11];
        price_time[0]=5000;
        price_time[1]=10000;
        price_time[2]=15000;
        price_time[3]=20000;
        price_time[4]=25000;
        price_time[5]=30000;
        price_time[6]=35000;
        price_time[7]=50000;
        price_time[8]=75000;
        price_time[9]=100000;

        return  price_time;
    }
    public static String get_number(String number){
        number=number.replace("0","۰");
        number=number.replace("1","۱");
        number=number.replace("2","۲");
        number=number.replace("3","۳");
        number=number.replace("4","۴");
        number=number.replace("5","۵");
        number=number.replace("6","۶");
        number=number.replace("7","۷");
        number=number.replace("8","۸");
        number=number.replace("9","۹");

        return  number;
    }
    public static String get_service_status(int s){
        Map<Integer,String> map=new HashMap<Integer,String>();

        map.put(-3,"لغو سفر توسط مسافر");
        map.put(-2,"لغو سفر توسط رانند");
        map.put(-1,"در انتظار راننده");
        map.put(1,"قبول درخواست توسط راننده");
        map.put(2,"رسیدن راننده به مبدا");
        map.put(3,"سوار شدن مسافر");
        map.put(4,"رسیدن به مقصد اول");
        map.put(5,"رسیدن به مقصد دوم");
        return  map.get(s);
    }
    public static String get_price(String price) {
        DecimalFormat decimalFormat=new DecimalFormat("###,###");

        String p_string=decimalFormat.format(Integer.valueOf(price))+" ریال";

        p_string=p_string.replace("0","۰");
        p_string=p_string.replace("1","۱");
        p_string=p_string.replace("2","۲");
        p_string=p_string.replace("3","۳");
        p_string=p_string.replace("4","۴");
        p_string=p_string.replace("5","۵");
        p_string=p_string.replace("6","۶");
        p_string=p_string.replace("7","۷");
        p_string=p_string.replace("8","۸");
        p_string=p_string.replace("9","۹");

        return  p_string;
    }
    public static void show_notification(Context context,String title,String text,String class_name,String activity_key,String activity_value) {
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId="user_channel";
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,channelId);
        builder.setSmallIcon(R.mipmap.ic_logo);
        builder.setColor(Color.RED);


        RemoteViews content=new RemoteViews(context.getPackageName(),R.layout.notification);
        content.setTextViewText(R.id.title,title);
        content.setTextViewText(R.id.content,text);
        builder.setCustomContentView(content);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            String channel_name="taxi notification";
            int importance=NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel=new NotificationChannel(channelId,channel_name,importance);
            notificationChannel.setDescription("taxi notification channel");
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            if(notificationManager!=null)
            {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }


        Class<?> myClass=null;

        try {
            myClass=myClass.forName(class_name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent=new Intent(context,myClass);

        if(!activity_key.isEmpty() && !activity_value.isEmpty())
        {
            intent.putExtra(activity_key,activity_value);
        }

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        i++;
        notificationManager.notify(i,builder.build());
    }
    public static void set_Socket(Socket s)
    {
        socket=s;
    }
    public static Socket get_Socket()
    {
        return socket;
    }
    public static void check_order(String token,final  CheckOrderCallback check_order_callback)
    {
        Retrofit retrofit=new Retrofit.Builder().baseUrl(Service.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        ApiInterface apiInterface=retrofit.create(ApiInterface.class);
        Call<ServerData.runing_service> call=apiInterface.runing_service(token);
        Callback<ServerData.runing_service> callback= new Callback<ServerData.runing_service>() {
            @Override
            public void onResponse(Call<ServerData.runing_service> call, Response<ServerData.runing_service> response) {
                if(response.isSuccessful())
                {
                    JSONObject jsonObject=new JSONObject();
                    if(response.body().getDriver_name()!=null){
                        try {
                            jsonObject.put("driver_name",response.body().getDriver_name());
                            jsonObject.put("car_type",response.body().getCar_type());
                            jsonObject.put("mobile",response.body().getMobile());
                            jsonObject.put("city_code",response.body().getCity_code());
                            jsonObject.put("city_number",response.body().getCity_number());
                            jsonObject.put("number_plates",response.body().getNumber_plates());
                            jsonObject.put("code_number_plates",response.body().getCode_number_plates());
                            jsonObject.put("price",response.body().getPrice());
                            jsonObject.put("service_id",response.body().getService_id());
                            jsonObject.put("lat1",response.body().getLat1());
                            jsonObject.put("lat2",response.body().getLat2());
                            jsonObject.put("lat3",response.body().getLat3());
                            jsonObject.put("lng1",response.body().getLng1());
                            jsonObject.put("lng2",response.body().getLng2());
                            jsonObject.put("lng3",response.body().getLng3());
                            jsonObject.put("going_back",response.body().getGoing_back());
                            jsonObject.put("stop_time",response.body().getStop_time());
                            check_order_callback.onResponse(jsonObject.toString());
                        } catch (JSONException e)
                        {
                            check_order_callback.onFailure("error");
                        }
                    }
                    else {
                        check_order_callback.onFailure("error");
                    }

                }
                else {
                    check_order_callback.onFailure("error");
                }
            }

            @Override
            public void onFailure(Call<ServerData.runing_service> call, Throwable t) {
                check_order_callback.onFailure("error");
            }
        };
        call.enqueue(callback);
    }
    public interface CheckOrderCallback {
        void onResponse(String data);
        void onFailure(String error);
    }

    public static LatLng[] direction_location()
    {
        LatLng[] location;
        location=new LatLng[2];
        LatLng departure=null;
        LatLng destination=null;
        if(going_back.equals("no"))
        {
            if(latLng3==null)
            {

                departure=new LatLng(latLng1.getLatitude(),latLng1.getLongitude());
                destination=new LatLng(latLng2.getLatitude(),latLng2.getLongitude());
            }
            else
            {
                departure=new LatLng(latLng2.getLatitude(),latLng2.getLongitude());
                destination=new LatLng(latLng3.getLatitude(),latLng3.getLongitude());
            }
        }
        else
        {
            if(latLng3==null)
            {
                departure=new LatLng(latLng2.getLatitude(),latLng2.getLongitude());
                destination=new LatLng(latLng1.getLatitude(),latLng1.getLongitude());
            }
            else
            {
                departure=new LatLng(latLng3.getLatitude(),latLng3.getLongitude());
                destination=new LatLng(latLng1.getLatitude(),latLng1.getLongitude());
            }
        }
        location[0]=departure;
        location[1]=destination;

        return  location;
    }
    public static void setServiceData(JSONObject data)
    {
        try
        {
            latLng1=new LatLng(Double.valueOf(data.getString("lat1")),Double.valueOf(data.getString("lng1")));
            latLng2=new LatLng(Double.valueOf(data.getString("lat2")),Double.valueOf(data.getString("lng2")));
            if(data.has("lat3"))
            {
                latLng3=new LatLng(Double.valueOf(data.getString("lat3")),Double.valueOf(data.getString("lng3")));
            }
            OrderPrice.service_price=Integer.valueOf(data.getString("price"));
            running_service_id=data.getString("service_id");
            going_back=data.getString("going_back");
        }
        catch (JSONException e)
        {

        }
    }
    public static void removeGoBack()
    {
        try
        {

            if(data!=null){
                going_back="no";
                data.put("going_back","no");
                OrderPrice.service_price=OrderPrice.service_price-OrderPrice.going_back_price;
                OrderPrice.going_back_price=0;
            }
        }
        catch (JSONException j){

        }

    }
    public static void add_score(final Context context, float score){

        if(send_score==1){

            send_score=0;
            try {
                String service_id=data.getString("service_id");

                Retrofit retrofit=new Retrofit.Builder().baseUrl(Service.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
                ApiInterface apiInterface=retrofit.create(ApiInterface.class);
                Call<String> call=apiInterface.add_score(App.getToken(context),service_id,score);
                Callback<String> callback = new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            if(response.body().equals("ok")){
                                Lib.Service.refresh(context);
                            }
                            else{
                                send_score=1;
                                Toast.makeText(context, "خطا ارتباط با سرور", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            send_score=1;
                            Toast.makeText(context, "خطا ارتباط با سرور", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        send_score=1;
                        Toast.makeText(context, "خطا ارتباط با سرور", Toast.LENGTH_SHORT).show();
                    }
                };

                call.enqueue(callback);

            } catch (JSONException e) {
                send_score=1;
                Toast.makeText(context, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public  static  void refresh(Context context){
        set_latlng1=1;
        set_latlng2=1;
        set_latlng3=1;
        is_running_service=0;
        going_back="no";
        data=null;
        address1="";
        address2="";
        address3="";
        latLng1=null;
        latLng2=null;
        latLng3=null;
        Intent refresh=new Intent(context, CedarMapActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(refresh);
    }
}
