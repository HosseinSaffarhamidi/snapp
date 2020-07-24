package Lib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.adrom.snapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by idehpardazanjavan on 2/4/19.
 */

public class ServiceView
{
    Context context;
    RelativeLayout price_box;
    RelativeLayout count_box;
    RelativeLayout request_car_btn;
    TextView toolbar_text;
    TextView car_count;
    LinearLayout hidden_panel;
    public ServiceView(Context context) {
        this.context=context;
        price_box=((Activity) context).findViewById(R.id.price_box);
        count_box=((Activity) context).findViewById(R.id.count_box);
        request_car_btn=((Activity) context).findViewById(R.id.request_car_btn);
        toolbar_text=((Activity) context).findViewById(R.id.toolbar_text);
        car_count=((Activity) context).findViewById(R.id.car_count);
        hidden_panel=((Activity) context).findViewById(R.id.hidden_panel);
    }
    public void hide_price_box() {
        request_car_btn.setVisibility(View.GONE);
        price_box.setVisibility(View.GONE);
        count_box.setVisibility(View.VISIBLE);
    }
    public void show_price_box() {
        count_box.setVisibility(View.GONE);
        request_car_btn.setVisibility(View.VISIBLE);
        price_box.setVisibility(View.VISIBLE);
    }
    public void set_toolbar_text(String text)
    {
        toolbar_text.setText(text);
    }
    public void set_car_count_text(String text)
    {
        car_count.setText(text);
    }
    public void hide_hidden_panel_box()
    {
        if(hidden_panel.getVisibility()==View.VISIBLE)
        {
            Animation up= AnimationUtils.loadAnimation(context,R.anim.down);
            hidden_panel.startAnimation(up);
            hidden_panel.setVisibility(View.GONE);

            ImageView close_hidden_box=((Activity) context).findViewById(R.id.close_hidden_box);
            close_hidden_box.setVisibility(View.GONE);
        }
    }
    public static void show_hidden_panel(final Context context, final int is_run_service, final ServiceView serviceView){

        RelativeLayout request_message=((Activity) context).findViewById(R.id.request_message);
        request_message.setVisibility(View.GONE);

        ImageView close_service=((Activity) context).findViewById(R.id.cancel_service);
        close_service.setVisibility(View.GONE);

        ImageView search=((Activity) context).findViewById(R.id.search);
        search.setVisibility(View.GONE);

        ImageView close_hidden_box=((Activity) context).findViewById(R.id.close_hidden_box);
        close_hidden_box.setVisibility(View.VISIBLE);

        close_hidden_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBox(context,serviceView,is_run_service);
            }
        });

        View price_box_border=((Activity) context).findViewById(R.id.price_box_border);
        price_box_border.setVisibility(View.VISIBLE);

        RelativeLayout driver_info=((Activity) context).findViewById(R.id.driver_info);
        driver_info.setVisibility(View.GONE);

        RelativeLayout count_box=((Activity) context).findViewById(R.id.count_box);
        count_box.setVisibility(View.GONE);
        LinearLayout hidden_panel=((Activity) context).findViewById(R.id.hidden_panel);
        if(is_run_service==1)
        {
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0,App.pxfromdp(context,55),0,0);
            hidden_panel.setLayoutParams(layoutParams);
        }

        Animation up= AnimationUtils.loadAnimation(context,R.anim.up);
        hidden_panel.startAnimation(up);
        hidden_panel.setVisibility(View.VISIBLE);


        if(Service.going_back.equals("ok"))
        {
            RelativeLayout going_round1 = (RelativeLayout) hidden_panel.findViewById(R.id.going_round1);
            RelativeLayout going_round2 = (RelativeLayout) hidden_panel.findViewById(R.id.going_round2);
            going_round1.setVisibility(View.GONE);
            going_round2.setVisibility(View.VISIBLE);
        }
        else{
            RelativeLayout going_round1 = (RelativeLayout) hidden_panel.findViewById(R.id.going_round1);
            RelativeLayout going_round2 = (RelativeLayout) hidden_panel.findViewById(R.id.going_round2);
            going_round1.setVisibility(View.VISIBLE);
            going_round2.setVisibility(View.GONE);
        }

        if(Service.latLng3!=null)
        {
            RelativeLayout second_layout=(RelativeLayout)hidden_panel.findViewById(R.id.second_layout);
            ImageView second_icon=(ImageView)hidden_panel.findViewById(R.id.second_icon);
            TextView second_text=(TextView)hidden_panel.findViewById(R.id.second_text);

            second_layout.setBackgroundResource(R.drawable.itemraduis2);
            second_text.setTextColor(Color.WHITE);
            second_icon.setImageResource(R.drawable.map_marker_plus2);
        }

        //
        if(Service.data!=null && Service.data.has("stop_time")){
            RelativeLayout select_stop_time_layout=((Activity) context).findViewById(R.id.select_stop_time_layout);
            TextView times=((Activity) context).findViewById(R.id.times);
            TextView select_stop_text=((Activity) context).findViewById(R.id.select_stop_text);

            try {
                if(!Service.data.get("stop_time").equals("0"))
                {
                    select_stop_time_layout.setBackgroundResource(R.drawable.itemraduis2);

                    select_stop_text.setTextColor(Color.WHITE);
                    times.setTextColor(Color.WHITE);
                    times.setText(Service.data.get("stop_time").toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }
    public static void hideBox(Context context,ServiceView serviceView,int is_run_service)
    {
        // serviceView.hide_price_box();
        serviceView.hide_hidden_panel_box();

        if(Service.is_running_service==0)
        {
            ImageView close_service=((Activity) context).findViewById(R.id.cancel_service);
            close_service.setVisibility(View.GONE);
        }


        ImageView close_hidden_box=((Activity) context).findViewById(R.id.close_hidden_box);
        close_hidden_box.setVisibility(View.GONE);

        ImageView search=((Activity) context).findViewById(R.id.search);
        search.setVisibility(View.VISIBLE);

        View price_box_border=((Activity) context).findViewById(R.id.price_box_border);
        price_box_border.setVisibility(View.GONE);

        if(Service.is_running_service==1)
        {
            RelativeLayout driver_info=((Activity) context).findViewById(R.id.driver_info);
            driver_info.setVisibility(View.VISIBLE);
        }
    }
    public static void show_view(Context cx,int service_price)
    {

        RelativeLayout price_box=((Activity) cx).findViewById(R.id.price_box);

        price_box.setVisibility(View.VISIBLE);
        if(Service.is_running_service==0)
        {
            RelativeLayout request_car_btn=((Activity) cx).findViewById(R.id.request_car_btn);
            request_car_btn.setVisibility(View.VISIBLE);
        }

        RelativeLayout count_box=((Activity) cx).findViewById(R.id.count_box);
        count_box.setVisibility(View.GONE);

         show_service_price(cx);
    }
    public static void remove_second_direction(Context context)
    {
        LinearLayout hidden_panel=((Activity) context).findViewById(R.id.hidden_panel);

        RelativeLayout second_layout=(RelativeLayout)hidden_panel.findViewById(R.id.second_layout);
        ImageView second_icon=(ImageView)hidden_panel.findViewById(R.id.second_icon);
        TextView second_text=(TextView)hidden_panel.findViewById(R.id.second_text);

        second_layout.setBackgroundResource(R.drawable.itemraduis);
        second_text.setTextColor(Color.BLACK);
        second_icon.setImageResource(R.drawable.map_marker_plus);
    }
    public static void show_service_price(Context cx)
    {
        final DecimalFormat decimalFormat=new DecimalFormat("###,###");
        TextView price_text=((Activity) cx).findViewById(R.id.price);
        String p_string=App.replace_number(decimalFormat.format(OrderPrice.service_price))+" ریال";
        price_text.setText(p_string);
    }
    public static void show_service_view(final Context context, RelativeLayout price_box, JSONObject data, ServiceView serviceView)
    {
        try
        {

            serviceView.set_toolbar_text("سفر سلامت");

            RelativeLayout driver_info=((Activity) context).findViewById(R.id.driver_info);
            driver_info.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            price_box.setLayoutParams(layoutParams);
            price_box.setVisibility(View.VISIBLE);

            RelativeLayout request_car_btn=((Activity) context).findViewById(R.id.request_car_btn);
            request_car_btn.setVisibility(View.GONE);

            View price_box_border=((Activity) context).findViewById(R.id.price_box_border);
            price_box_border.setVisibility(View.VISIBLE);

            String driver_name=data.getString("driver_name");
            String car_type=data.getString("car_type");
            String city_code=data.getString("city_code");
            String city_number=data.getString("city_number");
            String number_plates=data.getString("number_plates");
            String code_number_plates=data.getString("code_number_plates");

            TextView driver_name_text=((Activity) context).findViewById(R.id.driver_name);
            driver_name_text.setText(driver_name);

            TextView car_type_text=((Activity) context).findViewById(R.id.car_type);
            car_type_text.setText(car_type);

            TextView city_number_text=((Activity) context).findViewById(R.id.city_number);
            city_number_text.setText(city_number);

            TextView city_code_text=((Activity) context).findViewById(R.id.city_code);
            city_code_text.setText(city_code);

            TextView number_plates_text=((Activity) context).findViewById(R.id.number_plates);
            number_plates_text.setText(number_plates);

            TextView code_number_plates_text=((Activity) context).findViewById(R.id.code_number_plates);
            code_number_plates_text.setText(code_number_plates);


            if(data.has("price"))
            {
                OrderPrice.service_price=Integer.valueOf(data.getString("price"));
                show_service_price(context);
            }
            if(data.has("service_id"))
            {
                ImageView search=((Activity) context).findViewById(R.id.search);
                search.setVisibility(View.GONE);
                final String service_id=data.getString("service_id");
                ImageView cancel_service=((Activity) context).findViewById(R.id.cancel_service);
                cancel_service.setVisibility(View.VISIBLE);
                cancel_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog=new Dialog(context);
                        View dialog_view= LayoutInflater.from(context).inflate(R.layout.cancel_dialog,null);

                        TextView btn_true=(TextView)dialog_view.findViewById(R.id.btn_true);
                        TextView btn_false=(TextView)dialog_view.findViewById(R.id.btn_false);
                        btn_true.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OrderPrice.request_cancel_service(context,service_id);
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
                });
            }
        }
        catch (JSONException e)
        {

        }
    }
    public static void show_score_view(final Context context, DrawerLayout drawerLayout){

        ScrollView score_layout=((Activity) context).findViewById(R.id.score_layout);
        score_layout.setVisibility(View.VISIBLE);

        try {
            String driver_name=Service.data.getString("driver_name");
            TextView driver_name2=((Activity) context).findViewById(R.id.driver_name2);
            driver_name2.setText(driver_name);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RatingBar ratingBar=((Activity) context).findViewById(R.id.RatingBar);
        Button add_score=((Activity) context).findViewById(R.id.add_score);
        add_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Service.add_score(context,ratingBar.getRating());
            }
        });

        drawerLayout.setVisibility(View.GONE);
    }
}
