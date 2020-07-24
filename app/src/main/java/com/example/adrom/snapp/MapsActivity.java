package com.example.adrom.snapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.DecimalFormat;
import java.util.List;


import Lib.OrderPrice;
import Lib.Service;
import Lib.ServiceView;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    String baseUrl="http://192.168.43.210:3000/";
    private GoogleMap mMap;
    Marker m2;
    LatLng latLng1,latLng2,latLng3;
    int set_latlng1 = 1;
    int set_latlng2 = 1;
    int set_latlng3=1;
    String m2_id;
    String m3_id="null";
    Marker m1,m3;
    Marker[] car_marker;
    int remover_car_marker=0;
    LocationManager locationManager;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView car_count;
    RelativeLayout count_box,price_box;
    TextView toolbar_text;
    int service_price=0;
    TextView price;
    int stop_price=0;
    int service_price2=0;
    String address1,address2,address3;
    String stop_time="0";
    Handler handler;
    int is_run_service=0;
    ServiceView serviceView;
    com.github.nkzawa.socketio.client.Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.arrow_color));


        car_count=(TextView)findViewById(R.id.car_count);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        serviceView=new ServiceView(MapsActivity.this);

        count_box=(RelativeLayout)findViewById(R.id.count_box);
        price_box=(RelativeLayout) findViewById(R.id.price_box);
        toolbar_text=(TextView)findViewById(R.id.toolbar_text);
        if (CheckGPS()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                set_location();
            }
        } else
        {
            show_dialog_box();
        }


        CedarMaps.getInstance()
                .setClientID(CedarMapConfig.CLIENT_ID)
                .setClientSecret(CedarMapConfig.CLIENT_SECRET)
                .setContext(this);

    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng sydney = new LatLng(38.0412, 46.3993);


        MarkerOptions markerOptions1 = new MarkerOptions().position(sydney)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

        final MarkerOptions markerOptions2 = new MarkerOptions().position(sydney)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));


        m1 = mMap.addMarker(markerOptions1);
        final String m1_id = m1.getId();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        move();
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng position = googleMap.getCameraPosition().target;
                m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                if (m2 != null) {

                    if(remover_car_marker==1)
                    {
                        for(int j=0;j<car_marker.length;j++)
                        {
                            Marker c=car_marker[j];
                            c.remove();
                        }
                    }
                    m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));
                }
                if(m3!=null)
                {

                    m3.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));

                }
                if(set_latlng1==1)
                {
                    get_driver(position.latitude,position.longitude);
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getId().equals(m1_id)  && set_latlng1==1) {
                    latLng1 = marker.getPosition();
                    m2 = mMap.addMarker(markerOptions2);
                    m2.setZIndex(1);
                    LatLng m2_position=SphericalUtil.computeOffset(marker.getPosition(),40,220);
                    m2.setPosition(m2_position);
                    m2_id = m2.getId();
                    set_latlng1 = 0;
                    set_address(latLng1,1);
                }
                if (marker.getId().equals(m2_id) && set_latlng2==1) {
                    set_latlng2 = 0;
                    latLng2 = marker.getPosition();
                    mMap.setMinZoomPreference(14);
                    mMap.setMaxZoomPreference(14);
                    get_directions();
                    set_address(latLng2,2);
                }
                if(marker.getId().equals(m3_id))
                {
                    latLng3=marker.getPosition();
                    mMap.setMinZoomPreference(14);
                    mMap.setMaxZoomPreference(14);
                    get_directions2();
                    set_address(latLng3,3);
                }

                return true;
            }
        });


        FloatingActionButton my_location = (FloatingActionButton) findViewById(R.id.my_location);
        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckGPS()) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location last_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng user_location=new LatLng(last_location.getLatitude(),last_location.getLongitude());
                    if(user_location!=null)
                    {
                        m1.setPosition(user_location);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 16));
                    }
                    else
                    {
                        set_location();
                    }
                }
                else
                {
                    show_dialog_box();
                }
            }
        });

    }
    public void move()
    {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng position = mMap.getCameraPosition().target;
                int height = 40;
                int width = 40;
                if (set_latlng1 == 1) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.map_marker);
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    m1.setIcon(BitmapDescriptorFactory.fromBitmap(small));
                    m1.setPosition(position);
                } else {
                    if (set_latlng2 == 1) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.destination_marker);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        m2.setIcon(BitmapDescriptorFactory.fromBitmap(small));

                        m2.setPosition(position);
                    }
                    else if(set_latlng3==1 && !m3_id.equals("null"))
                    {


                        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.destination_marker);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        m3.setIcon(BitmapDescriptorFactory.fromBitmap(small));
                        m3.setPosition(position);

                    }
                }
            }
        });
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
    @Override
    public void onLocationChanged(Location location)
    {
        if(location.getProvider().equals("gps"))
        {
            locationManager.removeUpdates(this);
        }
        LatLng user_location=new LatLng(location.getLatitude(),location.getLongitude());
        m1.setPosition(user_location);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 16));
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void get_driver(double lat,double lng)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<List<ServerData.location_driver>> call = apiInterface.send_location(lat,lng);

        Callback<List<ServerData.location_driver>> callback= new Callback<List<ServerData.location_driver>>() {
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
                        matrix.postRotate(Float.valueOf(location_driver.getAngle()));
                        Bitmap b=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                        LatLng sydney=new LatLng(Double.valueOf(location_driver.getLat()),Double.valueOf(location_driver.getLng()));
                        final MarkerOptions markerOptions2 = new MarkerOptions().position(sydney)
                                .icon(BitmapDescriptorFactory.fromBitmap(b));
                        Marker car=mMap.addMarker(markerOptions2);
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
    //public boolean on

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void get_directions()
    {
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        toolbar_text.setText("سفر سلامت");
        car_count.setText("در حال محاسبه هزینه ...");
        Service.get_directions(new Service.get_callback() {
            @Override
            public void onResponse(double directions)
            {
                OrderPrice.show_service_info(MapsActivity.this,directions);
            }
            @Override
            public void onFailure(String error)
            {
                OrderPrice.set_error_service();
            }
        });
    }
    public void get_directions2()
    {
        toolbar_text.setText("سفر سلامت");
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        car_count.setText("در حال محاسبه هزینه ...");
        Service.get_directions(new Service.get_callback() {
            @Override
            public void onResponse(double directions)
            {
                int p1=5000;
                double p2=(directions*5000)/1000;
                double c=p2/5000;
                c=Math.round(c)*5000;
                int p3=(int)c;
                int p=p3;

                p=p+service_price;
                count_box.setVisibility(View.GONE);
                price_box.setVisibility(View.VISIBLE);
                service_price=p;
                price=(TextView)findViewById(R.id.price);
                String p_string=decimalFormat.format(p)+" ریال";
                price.setText(p_string);


                RelativeLayout request_car_btn=(RelativeLayout)findViewById(R.id.request_car_btn);
                request_car_btn.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(String error)
            {
                Toast.makeText(MapsActivity.this, "خطا در محاسبه هزینه ..", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void request_driver(View view)
    {
        SharedPreferences Taxi_user_datas=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
        String token=Taxi_user_datas.getString("token","null");


        if(latLng3!=null)
        {
            Service.get_Socket().emit("add_service",token,address1,address2,address3,service_price,stop_time,
                    latLng1.latitude, latLng2.latitude,
                    latLng1.longitude, latLng2.longitude,Service.going_back,latLng3.latitude,latLng3.longitude
            );
        }
        else
        {
            Service.get_Socket().emit("add_service",token,address1,address2,address3,service_price,stop_time,
                    latLng1.latitude, latLng2.latitude,
                    latLng1.longitude, latLng2.longitude,Service.going_back
            );
        }
        Service.get_Socket().on("set_status",StatusHandel);
    }
    public void show_hidden_panel(View view)
    {
        ServiceView.show_hidden_panel(MapsActivity.this,is_run_service,serviceView);
    }
    public void select_time1(View view)
    {
        if(stop_price==0)
        {
            select_time();
        }
        else
        {
            final DecimalFormat decimalFormat=new DecimalFormat("###,###");
            final TextView times=(TextView)findViewById(R.id.times);
            times.setText("مجموع توقف");
            service_price=service_price-stop_price;
            String p_string=decimalFormat.format(service_price)+" ریال";
            price.setText(p_string);
            stop_price=0;
            stop_time="0";
        }
    }
    public void select_time2(View view)
    {
        select_time();
    }
    public void select_time()
    {
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
                service_price=service_price+c;
                dialog_time.dismiss();


                stop_price=c;
                times.setText(values[time.getValue()]);
                String p_string=decimalFormat.format(service_price)+" ریال";
                price.setText(p_string);
            }
        });

        dialog_time.setContentView(time_view);
        dialog_time.show();

    }
    public void going_round(View view) {
        final DecimalFormat decimalFormat = new DecimalFormat("###,###");


        RelativeLayout going_round1 = (RelativeLayout) findViewById(R.id.going_round1);
        RelativeLayout going_round2 = (RelativeLayout) findViewById(R.id.going_round2);

        if (service_price2 == 0) {

            going_round1.setVisibility(View.GONE);
            going_round2.setVisibility(View.VISIBLE);

            price.setText("در حال محاسبه هزینه ...");
            Service.get_directions(new Service.get_callback() {
                @Override
                public void onResponse(double directions) {
                    Service.going_back = "ok";

                    int p1 = 15000;
                    double p2 = (directions * 5000) / 1000;
                    double c = p2 / 5000;
                    c = Math.round(c) * 5000;
                    int p3 = (int) c;
                    int p = p1 + p3;
                    service_price2 = p;
                    service_price = service_price + service_price2;
                    price = (TextView) findViewById(R.id.price);
                    String p_string = decimalFormat.format(service_price) + " ریال";
                    price.setText(p_string);

                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(MapsActivity.this, "خطا در محاسبه هزینه ..", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
    public void second_destination(View view)
    {
        LinearLayout hidden_panel=(LinearLayout)findViewById(R.id.hidden_panel);
        hidden_panel.setVisibility(View.GONE);

        RelativeLayout request_car_btn=(RelativeLayout)findViewById(R.id.request_car_btn);
        request_car_btn.setVisibility(View.GONE);


        RelativeLayout price_box=(RelativeLayout)findViewById(R.id.price_box);
        price_box.setVisibility(View.GONE);

        RelativeLayout count_box=(RelativeLayout)findViewById(R.id.count_box);
        count_box.setVisibility(View.VISIBLE);

        car_count.setText("انتخاب مقصد دوم");


        MarkerOptions markerOptions3 = new MarkerOptions().position(latLng2)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));


        m3 = mMap.addMarker(markerOptions3);
        m3.setZIndex(1);
        LatLng m3_position=SphericalUtil.computeOffset(latLng2,70,250);
        m3.setPosition(m3_position);
        m3_id = m3.getId();

        mMap.resetMinMaxZoomPreference();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m3_position, 16));

    }
    public void set_address(final LatLng latLng1, final int r)
    {
        String url="http://maps.google.com/";
        String location=latLng1.latitude+","+latLng1.longitude;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LocationAddress.results> call = apiInterface.get_directions(location,"true","fa","AIzaSyDuROZDn60mMZl5vubuGlg_qRRXsjuIoUg");

        Callback<LocationAddress.results> callback=new Callback<LocationAddress.results>() {
            @Override
            public void onResponse(Call<LocationAddress.results> call, Response<LocationAddress.results> response) {
                if(response.isSuccessful())
                {
                    String a1 = "";
                    String a2 = "";
                    String a3 = "";
                    String a4 = "";
                    if (response.body().getResults().size()> 0)
                    {
                        a1 = response.body().getResults().get(0).getAddress_components().get(0).getShort_name();
                        a2 = response.body().getResults().get(1).getAddress_components().get(0).getShort_name();
                        a3 = response.body().getResults().get(1).getAddress_components().get(1).getShort_name();
                        a4 = response.body().getResults().get(0).getAddress_components().get(1).getShort_name();

                        if (r == 1)
                        {
                            address1 = a4 + " " + a3 + " " + a2 + " " + a1;
                        }
                        else if (r == 2)
                        {
                            address2 = a4 + " " + a3 + " " + a2 + " " + a1;
                        }
                        else if (r == 3)
                        {
                            address3 = a4 + " " + a3 + " " + a2 + " " + a1;
                        }

                    }
                    else
                    {
                        set_address(latLng1, r);

                    }
                }
            }

            @Override
            public void onFailure(Call<LocationAddress.results> call, Throwable t) {

            }
        };

        call.enqueue(callback);
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
                            RelativeLayout driver_info=(RelativeLayout)findViewById(R.id.driver_info);
                            driver_info.setVisibility(View.VISIBLE);

                            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            price_box.setLayoutParams(layoutParams);
                            price_box.setVisibility(View.VISIBLE);

                            RelativeLayout request_car_btn=(RelativeLayout)findViewById(R.id.request_car_btn);
                            request_car_btn.setVisibility(View.GONE);

                            View price_box_border=(View)findViewById(R.id.price_box_border);
                            price_box_border.setVisibility(View.VISIBLE);

                            String driver_name=data.getString("driver_name");
                            String car_type=data.getString("car_type");
                            String city_code=data.getString("city_code");
                            String city_number=data.getString("city_number");
                            String number_plates=data.getString("number_plates");
                            String code_number_plates=data.getString("code_number_plates");

                            TextView driver_name_text=(TextView)findViewById(R.id.driver_name);
                            driver_name_text.setText(driver_name);

                            TextView car_type_text=(TextView)findViewById(R.id.car_type);
                            car_type_text.setText(car_type);

                            TextView city_number_text=(TextView)findViewById(R.id.city_number);
                            city_number_text.setText(city_number);

                            TextView city_code_text=(TextView)findViewById(R.id.city_code);
                            city_code_text.setText(city_code);

                            TextView number_plates_text=(TextView)findViewById(R.id.number_plates);
                            number_plates_text.setText(number_plates);

                            TextView code_number_plates_text=(TextView)findViewById(R.id.code_number_plates);
                            code_number_plates_text.setText(code_number_plates);
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
}
