package com.example.adrom.snapp;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;





import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;
//import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;
import com.github.nkzawa.emitter.Emitter;

import com.google.maps.android.SphericalUtil;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import Lib.App;
import Lib.CedarMapOnBack;
import Lib.OrderPrice;
import Lib.Service;
import Lib.ServiceView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CedarMapActivity extends AppCompatActivity implements LocationListener {

    MapboxMap mMapboxMap;
    String mobile;
    Long m1_id,m2_id,m3_id;
    TextView price,car_count;
    int service_price=0;
    int remover_car_marker=0;
    RelativeLayout count_box,price_box;
    Marker[] car_marker;
    LocationManager locationManager;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Handler handler;
    String stop_time="0";
    int stop_price=0;
    int is_run_service=0;
    ServiceView serviceView;
    BroadcastReceiver broadcastReceiver;
    int inventory=0;
    int g=0;
    com.github.nkzawa.socketio.client.Socket socket;
    DrawerLayout drawer_layout;
    ScrollView search_driver_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        CedarMaps.getInstance()
                .setClientID(CedarMapConfig.CLIENT_ID)
                .setClientSecret(CedarMapConfig.CLIENT_SECRET)
                .setContext(this);


//        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
//            @Override
//            public void onSuccess() {
//
//                Service.show_notification="no";
//                setContentView(R.layout.activity_cedar_map);
//                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//                setSupportActionBar(toolbar);
//
//                serviceView=new ServiceView(CedarMapActivity.this);
//
//
//
//                drawer_layout=(DrawerLayout)findViewById(R.id.drawer_layout);
//                search_driver_layout=(ScrollView)findViewById(R.id.search_driver_layout);
//
//                count_box=(RelativeLayout)findViewById(R.id.count_box);
//                price_box=(RelativeLayout) findViewById(R.id.price_box);
//                car_count=(TextView)findViewById(R.id.car_count);
//                toolbar.setTitleTextColor(Color.BLACK);
//                toolbar.setTitle("");
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//                DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
//                actionBarDrawerToggle=new ActionBarDrawerToggle(CedarMapActivity.this,drawerLayout,R.string.open,R.string.close);
//                drawerLayout.addDrawerListener(actionBarDrawerToggle);
//                actionBarDrawerToggle.syncState();
//                actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.arrow_color));
//
//
//
//                broadcastReceiver=new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//
//                        ServiceView.show_score_view(CedarMapActivity.this,drawerLayout);
//
//                    }
//                };
//
//                registerReceiver(broadcastReceiver,new IntentFilter("finishService"));
//
//
//                if(getIntent().hasExtra("run_service"))
//                {
//                    is_run_service=1;
//                    Service.is_running_service=1;
//                    Bundle bundle=getIntent().getExtras();
//                    try {
//                        Service.data=new JSONObject(bundle.getString("data"));
//                        mobile= Service.data.getString("mobile");
//                        Service.setServiceData(Service.data);
//                        ServiceView.show_service_view(CedarMapActivity.this,price_box,Service.data,serviceView);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                map();
//            }
//
//            @Override
//            public void onFailure(@NonNull String error)
//            {
//                Log.i("errorss","sjdshj");
//            }
//        });




    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public void map()
    {
        MapView mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {


                mMapboxMap = mapboxMap;

                LatLng latLng=new LatLng(38.0412, 46.3993);
                MarkerOptions options=new MarkerOptions().position(latLng)
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.map_marker));


                Service.m1=mMapboxMap.addMarker(options);
                m1_id=Service.m1.getId();

                if(is_run_service==0)
                {
                    check_gps();
                }
                else
                {
                    try {

                        LatLng run_latLng1=new LatLng(Service.data.getDouble("lat1"),Service.data.getDouble("lng1"));
                        LatLng run_latLng2=new LatLng(Service.data.getDouble("lat2"),Service.data.getDouble("lng2"));

                        Service.m1.setPosition(run_latLng1);
                        add_marker2(run_latLng2);
                        mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(run_latLng1,16));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

                mMapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {

                    LatLng position=mapboxMap.getCameraPosition().target;
                    @Override
                    public void onCameraIdle() {
                        if (Service.set_latlng1 == 1){

                            Service.m1.setIcon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.map_marker));
                        }
                        if (Service.m2 != null) {

                            Service.m2.setIcon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.destination_marker));
                        }
                        if(Service.m3!=null)
                        {
                            Service.m3.setIcon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.destination_marker));
                        }
                        if(Service.set_latlng1==1)
                        {
                            get_driver(position.getLatitude(),position.getLongitude());
                        }
                    }
                });
                move_map(mMapboxMap);

                marker_click(mMapboxMap);
            }
        });


    }
    public void move_map(final MapboxMap mapboxMap)
    {
        final int height = 40;
        final int width = 40;
        mMapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {


                LatLng position=new LatLng(mapboxMap.getCameraPosition().target.getLatitude(),mapboxMap.getCameraPosition().target.getLongitude());
                if (Service.set_latlng1 == 1 && is_run_service==0)
                {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.map_marker);
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    Service.m1.setIcon(IconFactory.getInstance(getBaseContext()).fromBitmap(small));
                    Service.m1.setPosition(position);
                }
                else {
                    if (Service.set_latlng2 == 1 && is_run_service==0) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.destination_marker);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        Service.m2.setIcon(IconFactory.getInstance(getBaseContext()).fromBitmap(small));

                        Service.m2.setPosition(position);
                    }
                    else if(Service.set_latlng3==1 && Service.m3!=null)
                    {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.destination_marker);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        Service.m3.setIcon(IconFactory.getInstance(getBaseContext()).fromBitmap(small));
                        Service.m3.setPosition(position);
                    }
                }
            }
        });
    }
    public void marker_click(final MapboxMap mapboxMap) {
        LatLng latLng=new LatLng(38.0412, 46.3993);
        final MarkerOptions options2=new MarkerOptions()
                .setPosition(latLng)
                .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.destination_marker));

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker)
            {
                if(marker.getId()==m1_id && Service.set_latlng1==1 && is_run_service==0)
                {
                    serviceView.set_toolbar_text("کجا می روید ؟ ");
                    Service.latLng1 = marker.getPosition();
                    com.google.android.gms.maps.model.LatLng position1=new com.google.android.gms.maps.model.LatLng(Service.latLng1.getLatitude(),Service.latLng1.getLongitude());
                    com.google.android.gms.maps.model.LatLng m2_position= SphericalUtil.computeOffset(position1,40,220);
                    LatLng position2=new LatLng(m2_position.latitude,m2_position.longitude);
                    add_marker2(position2);
                    Service.set_address(Service.latLng1,1);
                    OrderPrice.get_price_from_server(Service.latLng1.getLatitude(),Service.latLng1.getLongitude());

                }
                if (marker.getId()==m2_id  && Service.set_latlng2==1 && is_run_service==0) {

                    Service.set_latlng2 = 0;
                    Service.latLng2 = marker.getPosition();
                    mapboxMap.setMinZoomPreference(14);
                    mapboxMap.setMaxZoomPreference(14);

                    get_directions();
                    Service.set_address(Service.latLng2,2);
                }
                if(m3_id!=null)
                {
                    if(marker.getId()==m3_id)
                    {
                        Service.set_latlng3=0;
                        Service.latLng3=marker.getPosition();
                        mapboxMap.setMinZoomPreference(14);
                        mapboxMap.setMaxZoomPreference(14);
                        Service.set_address(Service.latLng3,3);
                        get_directions2();
                    }
                }

                return false;
            }
        });
    }
    private void get_directions()
    {
        Service.going_back="no";
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        serviceView.set_toolbar_text("سفر سلامت");
        car_count.setText("در حال محاسبه هزینه ...");
        Service.get_directions(new Service.get_callback() {
            @Override
            public void onResponse(double directions)
            {
                OrderPrice.show_service_info(CedarMapActivity.this,directions);
            }
            @Override
            public void onFailure(String error)
            {
                OrderPrice.set_error_service();
            }
        });
    }
    public void get_driver(double lat,double lng)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<List<ServerData.location_driver>> call = apiInterface.send_location(lat,lng);
        Callback<List<ServerData.location_driver>> callback = new Callback<List<ServerData.location_driver>>() {
            @Override
            public void onResponse(Call<List<ServerData.location_driver>> call, Response<List<ServerData.location_driver>> response) {
                if(response.isSuccessful())
                {

                    if(remover_car_marker==1)
                    {
                        for(int j=0;j<car_marker.length;j++)
                        {
                            Marker c=car_marker[j];
                            c.remove();
                        }
                    }
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.car_icon);
                    Bitmap bitmap=bitmapDrawable.getBitmap();

                    int i=0;
                    car_marker=new Marker[response.body().size()];
                    String s=response.body().size()+" تاکسی موجود ";
                    car_count.setText(s);

                    remover_car_marker=1;
                    for (ServerData.location_driver location_driver : response.body())
                    {
                        Matrix matrix=new Matrix();
                        if(location_driver.getAngle()!=null){
                            matrix.postRotate(Float.valueOf(location_driver.getAngle()));
                        }
                        else {
                            matrix.postRotate(90);
                        }

                        Bitmap b=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                        LatLng sydney=new LatLng(Double.valueOf(location_driver.getLat()),Double.valueOf(location_driver.getLng()));
                        final MarkerOptions markerOptions2 = new MarkerOptions().position(sydney)
                                .icon(IconFactory.getInstance(CedarMapActivity.this).fromBitmap(b));
                        Marker car=mMapboxMap.addMarker(markerOptions2);
                        car_marker[i]=car;
                        i++;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ServerData.location_driver>> call, Throwable t) {

            }
        };

        call.enqueue(callback);

    }
    public void show_hidden_panel(View view){
        ServiceView.show_hidden_panel(CedarMapActivity.this,is_run_service,serviceView);
    }
    public void second_destination(View view) throws JSONException
    {
        if(Service.data==null || (Service.data!=null && !Service.data.has("lat3")))
        {

            if(Service.latLng3!=null)
            {
                ServiceView.remove_second_direction(CedarMapActivity.this);
                OrderPrice.service_price=OrderPrice.service_price-OrderPrice.location2_price;
                OrderPrice.location2_price=0;
                ServiceView.show_service_price(CedarMapActivity.this);
                Service.latLng3=null;
                Service.set_latlng3=1;
                Service.m3.remove();
                Service.m3=null;
            }
            else
            {

                Service.removeGoBack();

                mMapboxMap.setMaxZoomPreference(17);
                mMapboxMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                LinearLayout hidden_panel=(LinearLayout)findViewById(R.id.hidden_panel);
                hidden_panel.setVisibility(View.GONE);


                serviceView.hide_price_box();

                car_count.setText("انتخاب مقصد دوم");
                MarkerOptions markerOptions3 = new MarkerOptions().position(Service.latLng2)
                        .icon(IconFactory.getInstance(CedarMapActivity.this).fromResource(R.drawable.destination_marker));


                Service.m3 = mMapboxMap.addMarker(markerOptions3);
                //m3.setZIndex(1);
                com.google.android.gms.maps.model.LatLng l=new com.google.android.gms.maps.model.LatLng(Service.latLng2.getLatitude(),Service.latLng2.getLongitude());
                com.google.android.gms.maps.model.LatLng m3_p=SphericalUtil.computeOffset(l,70,250);
                LatLng m3_position=new LatLng(m3_p.latitude,m3_p.longitude);
                Service.m3.setPosition(m3_position);
                m3_id =Service.m3.getId();
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m3_position, 16));
            }
        }


    }
    public void get_directions2()
    {
        Service.going_back="no";
        serviceView.set_toolbar_text("سفر سلامت");
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        car_count.setText("در حال محاسبه هزینه ...");
        Service.get_directions(new Service.get_callback() {
            @Override
            public void onResponse(double directions)
            {

                OrderPrice.location2_price=Service.get_price(directions,5000);
                OrderPrice.service_price=OrderPrice.location2_price+OrderPrice.service_price;
                serviceView.show_price_box();

                price=(TextView)findViewById(R.id.price);
                String p_string= App.replace_number(decimalFormat.format(OrderPrice.service_price))+" ریال";
                price.setText(p_string);

                RelativeLayout request_car_btn=(RelativeLayout)findViewById(R.id.request_car_btn);
                if(is_run_service==1){

                    ServiceView.show_hidden_panel(CedarMapActivity.this,is_run_service,serviceView);
                    request_car_btn.setVisibility(View.GONE);
                }
                else{
                    request_car_btn.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(String error)
            {
                Toast.makeText(CedarMapActivity.this, "خطا در محاسبه هزینه ..", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {

        if(location.getProvider().equals("gps"))
        {
            locationManager.removeUpdates(this);
        }
        LatLng user_location=new LatLng(location.getLatitude(),location.getLongitude());
        Service.m1.setPosition(user_location);
        mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 16));
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
    @Override
    public void onProviderEnabled(String s) {

    }
    @Override
    public void onProviderDisabled(String s) {

    }
    public void set_location() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
    }
    public boolean CheckGPS()
    {
        boolean status;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        status=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return  status;
    }
    public  void show_dialog_box()
    {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.location_message);

        TextView btn_true=(TextView)dialog.findViewById(R.id.btn_true);
        btn_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent j=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(j);
            }
        });

        TextView btn_false=(TextView)dialog.findViewById(R.id.btn_false);
        btn_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void request_driver(View view)
    {
        SharedPreferences Taxi_user_datas=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
        String token=Taxi_user_datas.getString("token","null");


         drawer_layout.setVisibility(View.GONE);

         search_driver_layout.setVisibility(View.VISIBLE);

            handler=new Handler();
            if(Service.latLng3!=null)
            {

                Service.get_Socket().emit("add_service",token,Service.address1,Service.address2,OrderPrice.service_price,stop_time,
                        Service.latLng1.getLatitude(), Service.latLng2.getLatitude(),
                        Service.latLng1.getLongitude(), Service.latLng2.getLongitude(),Service.going_back,Service.latLng3.getLatitude(),Service.latLng3.getLongitude(),Service.address3
                );
            }
            else
            {
                Service.get_Socket().emit("add_service",token,Service.address1,Service.address2,OrderPrice.service_price,stop_time,
                        Service.latLng1.getLatitude(), Service.latLng2.getLatitude(),
                        Service.latLng1.getLongitude(), Service.latLng2.getLongitude(),Service.going_back
                );
            }

            Service.get_Socket().on("set_status",StatusHandel);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(is_run_service==0 && OrderPrice.cancel_service==0){
                        search_driver_layout.setVisibility(View.GONE);
                        drawer_layout.setVisibility(View.VISIBLE);
                        Toast.makeText(CedarMapActivity.this, "راننده برای درخواست شما پیدا نشد", Toast.LENGTH_SHORT).show();
                         onBackPressed();
                         onBackPressed();
                    }
                }
            },70000);

    }
    public Emitter.Listener StatusHandel=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final JSONObject data=(JSONObject)args[0];
            handler.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        String Status=data.getString("status");
                        if(Status.equals("1"))
                        {
                            drawer_layout.setVisibility(View.VISIBLE);
                            search_driver_layout.setVisibility(View.GONE);
                            is_run_service=1;
                            Service.is_running_service=1;
                            mobile=data.getString("mobile");
                            Service.data=new JSONObject();
                            Service.data.put("going_back",Service.going_back);
                            Service.data.put("stop_time",stop_time);
                            Service.data.put("service_id",data.getString("service_id"));
                            Service.data.put("driver_name",data.getString("driver_name"));
                            ServiceView.show_service_view(CedarMapActivity.this,price_box,data,serviceView);
                        }
                        else if(Status.equals("2"))
                        {
                            RelativeLayout request_message=(RelativeLayout)findViewById(R.id.request_message);
                            request_message.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    public void select_time1(View view)
    {
        if(is_run_service==1)
        {
            try {
                stop_time=Service.data.getString("stop_time");
                if(stop_time.equals("0"))
                {
                    if(OrderPrice.stop_price==0){
                        select_time();
                    }
                    else {
                        OrderPrice.remover_stop_time(this);
                        stop_time="0";
                    }
                }
                else
                {

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
        else{
            if(OrderPrice.stop_price==0){
                select_time();
            }
            else {
                OrderPrice.remover_stop_time(this);
                stop_time="0";
            }
        }
    }
    public void select_time2(View view)
    {
        select_time();
    }
    public void select_time()
    {
        final RelativeLayout select_stop_time_layout=(RelativeLayout)findViewById(R.id.select_stop_time_layout);
        final TextView select_stop_text=(TextView)findViewById(R.id.select_stop_text);

        final TextView times=(TextView)findViewById(R.id.times);
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");

        final Dialog dialog_time=new Dialog(this);
        dialog_time.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View time_view= LayoutInflater.from(this).inflate(R.layout.time_dioalg,null);

        final NumberPicker time=(NumberPicker)time_view.findViewById(R.id.time);

        final int[] price_time=Service.get_price_time();

        final String[] values=Service.get_time_value();
        time.setMinValue(0);
        time.setMaxValue(10);
        time.setDisplayedValues(values);
        Button select_time=(Button) time_view.findViewById(R.id.select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stop_time=values[time.getValue()];
                int c=price_time[time.getValue()];
                OrderPrice.set_stop_price(CedarMapActivity.this,c);
                times.setText(values[time.getValue()]);
                dialog_time.dismiss();

                select_stop_text.setTextColor(Color.WHITE);
                times.setTextColor(Color.WHITE);
                select_stop_time_layout.setBackgroundResource(R.drawable.itemraduis2);
            }
        });

        dialog_time.setContentView(time_view);
        dialog_time.show();

    }
    public void show_service(View view)
    {
        Intent j=new Intent(CedarMapActivity.this,ServiceListActivity.class);
        startActivity(j);
    }
    public void set_error_final_service_price()
    {
        car_count.setText("انتخاب مبدا");
        Service.set_latlng1=1;
        Service.set_latlng2=1;
        Service.m2.remove();
        LatLng latLng=new LatLng(Service.m1.getPosition().getLatitude(),Service.m1.getPosition().getLongitude());
        mMapboxMap.setMaxZoomPreference(16);
        mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        remover_car_marker=0;
        Toast.makeText(CedarMapActivity.this, "خطا در محاسبه هزینه ..", Toast.LENGTH_SHORT).show();

    }
    public void check_gps()
    {
        if (CheckGPS()) {
            if (ActivityCompat.checkSelfPermission(CedarMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CedarMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(CedarMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                set_location();
            }
        }else {
            show_dialog_box();
        }
    }
    public void add_marker2(LatLng newLatlng)
    {
        final MarkerOptions options2=new MarkerOptions()
                .setPosition(newLatlng)
                .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.destination_marker));
        Service.m2 = mMapboxMap.addMarker(options2);
        m2_id=Service.m2.getId();
        Service.set_latlng1=0;
    }

    public void  going_round(View view)
    {
        RelativeLayout going_round1 = (RelativeLayout) findViewById(R.id.going_round1);
        RelativeLayout going_round2 = (RelativeLayout) findViewById(R.id.going_round2);

        if(g==0 && (is_run_service==0 || (is_run_service==1 && Service.going_back.equals("no")) ))
        {
            g=1;

            car_count.setText("در حال محاسبه هزینه ...");

            if(Service.going_back.equals("no"))
            {
                Service.going_back="ok";
                going_round1.setVisibility(View.GONE);
                going_round2.setVisibility(View.VISIBLE);


                Service.get_directions(new Service.get_callback() {
                    @Override
                    public void onResponse(double directions)
                    {
                        g=0;
                        OrderPrice.show_service_info(CedarMapActivity.this,directions);
                    }
                    @Override
                    public void onFailure(String error)
                    {
                        g=0;
                        OrderPrice.set_error_service();
                    }
                });
            }
            else
            {
                g=0;
                Service.going_back="no";
                going_round1.setVisibility(View.VISIBLE);
                going_round2.setVisibility(View.GONE);
                OrderPrice.remove_back_price(CedarMapActivity.this);
            }

        }
        else if (is_run_service==1 && Service.going_back.equals("ok")){


            try
            {
                if(Service.data.getString("going_back").equals("no"))
                {
                    Service.going_back="no";
                    going_round1.setVisibility(View.VISIBLE);
                    going_round2.setVisibility(View.GONE);
                    OrderPrice.remove_back_price(CedarMapActivity.this);
                }

            }
            catch (JSONException e)
            {

            }

        }

    }
    public void add_service_item(View view)
    {
        Boolean send_item=false;
        SharedPreferences Taxi_user_datas=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
        String token=Taxi_user_datas.getString("token","null");

        try {

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface apiInterface=retrofit.create(ApiInterface.class);
            Call<String> call=null;
            JSONObject jsonObject=new JSONObject();

            if((!Service.data.has("lat3") && Service.latLng3!=null))
            {
                send_item=true;
                jsonObject.put("lat3",Service.latLng3.getLatitude());
                jsonObject.put("lng3",Service.latLng3.getLatitude());
                jsonObject.put("address3",Service.address3);
            }

            if(!Service.data.getString("going_back").equals(Service.going_back))
            {
                send_item=true;
                jsonObject.put("going_back","ok");
            }
            if(!Service.data.getString("stop_time").equals(stop_time))
            {
                send_item=true;
                jsonObject.put("stop_time",stop_time);
            }

            if(send_item){

                call=apiInterface.add_item_service(token,Service.data.getString("service_id"),OrderPrice.service_price,jsonObject);

                if(call!=null){

                    Callback<String> callback = new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful())
                            {
                                if(response.body().equals("ok"))
                                {

                                    is_run_service=1;
                                    try {

                                        if(Service.latLng3!=null){

                                            Service.data.put("lat3",String.valueOf(Service.latLng3.getLatitude()));
                                            Service.data.put("lng3",String.valueOf(Service.latLng3.getLongitude()));
                                        }

                                        if(!Service.data.getString("going_back").equals(Service.going_back)){

                                            Service.data.put("going_back",Service.going_back);
                                        }
                                        Service.data.put("stop_time",stop_time);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    OrderPrice.location2_price=0;
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


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed()
    {
         CedarMapOnBack.back(CedarMapActivity.this,mMapboxMap,serviceView,is_run_service);
    }

    public void PaymentDialog(View view)
    {
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Service.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface apiInterface=retrofit.create(ApiInterface.class);
        Call<String> call=apiInterface.inventory(App.getToken(this));

        Callback<String> callback= new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().equals("error"))
                {
                    Toast.makeText(CedarMapActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();

                }
                else if(response.body().equals("authError")){


                }
                else {

                    inventory=Integer.valueOf(response.body());
                    View payment_box=LayoutInflater.from(CedarMapActivity.this).inflate(R.layout.service_payment_dialog,null);

                    TextView price1=(TextView)payment_box.findViewById(R.id.price1);
                    TextView price2=(TextView)payment_box.findViewById(R.id.price2);
                    TextView price3=(TextView)payment_box.findViewById(R.id.price3);

                    int p=OrderPrice.service_price-inventory;

                    price1.setText(App.replace_number(decimalFormat.format(OrderPrice.service_price)) + " ریال");
                    price2.setText(App.replace_number(decimalFormat.format(inventory))+" ریال");
                    price3.setText(App.replace_number(decimalFormat.format(p))+ " ریال");


                    if(p<=0){
                        TextView payment_message=(TextView)payment_box.findViewById(R.id.payment_message);
                        Button payment_btn=(Button)payment_box.findViewById(R.id.payment_btn);
                        payment_btn.setVisibility(View.GONE);
                        payment_message.setVisibility(View.VISIBLE);

                        price3.setText(App.replace_number("0")+ " ریال");

                    }


                    Dialog dialog=new Dialog(CedarMapActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(payment_box);
                    dialog.show();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CedarMapActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        };


        call.enqueue(callback);

    }
    public void  payment(View view)
    {

        String service_id="";

        try {
            service_id=Service.data.getString("service_id");

            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(Service.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();

            int p=OrderPrice.service_price-inventory;
            ApiInterface apiInterface=retrofit.create(ApiInterface.class);
            Call<ServerData.payment_data> call=apiInterface.payment(App.getToken(this),p,service_id);

            Callback<ServerData.payment_data> callback = new Callback<ServerData.payment_data>() {
                @Override
                public void onResponse(Call<ServerData.payment_data> call, Response<ServerData.payment_data> response) {
                    String url=Service.baseUrl+"user/request_payment/"+response.body().getPayment_code();
                    Intent redirect=new Intent(Intent.ACTION_VIEW);
                    redirect.setData(Uri.parse(url));
                    startActivity(redirect);
                }

                @Override
                public void onFailure(Call<ServerData.payment_data> call, Throwable t) {

                }
            };

            call.enqueue(callback);

        } catch (JSONException e) {
            Toast.makeText(this, "خطا در اتصال به درگاه", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancel_request(View view)
    {
        final Dialog dialog=new Dialog(this);
        View dialog_view= LayoutInflater.from(this).inflate(R.layout.cancel_dialog,null);

        TextView btn_true=(TextView)dialog_view.findViewById(R.id.btn_true);
        TextView btn_false=(TextView)dialog_view.findViewById(R.id.btn_false);
        TextView message_text=(TextView)dialog_view.findViewById(R.id.message_text);
        message_text.setText("در حال پیدا کردن راننده نزدیک به شما هستیم . آیا قصد لغو سفر خود را دارید ؟");
        btn_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderPrice.request_cancel_service(CedarMapActivity.this,"0");
            }
        });
        btn_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialog_view);
        dialog.show();
    }
    public void call(View view)
    {
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+mobile));
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
        Service.show_notification="ok";
    }

    @Override
    protected void onPause() {
        super.onPause();
        Service.show_notification="ok";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Service.show_notification="no";
    }
}
