package Lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;


import com.example.adrom.snapp.R;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;



/**
 * Created by idehpardazanjavan on 2/2/19.
 */

public class CedarMapOnBack
{
    public static void  back(Context context, MapboxMap mMapboxMap,ServiceView serviceView,int is_run_service)
    {
        serviceView.hide_hidden_panel_box();
        if(is_run_service==1){

            RelativeLayout driver_info=((Activity) context).findViewById(R.id.driver_info);
            driver_info.setVisibility(View.VISIBLE);
        }

        if(Service.m2==null)
        {
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
        }
        else
        {
            if(Service.latLng2==null && Service.latLng1!=null && is_run_service==0)
            {
                Service.set_latlng1=1;
                Service.m2.remove();
                Service.m2=null;
                serviceView.set_toolbar_text("کجا هستید؟");
                serviceView.set_car_count_text("انتخاب مبدا");
                mMapboxMap.setMaxZoomPreference(17);
                mMapboxMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
            else if(Service.latLng2!=null && Service.latLng1!=null && is_run_service==0)
            {
                Service.set_latlng2=1;
                Service.latLng2=null;
                serviceView.hide_price_box();
                serviceView.set_toolbar_text("به کجا می روید؟");
                serviceView.set_car_count_text("انتخاب مقصد");
                if(Service.m3!=null)
                {
                    //serviceView.show_price_box();
                    Service.set_latlng3=1;
                    Service.m3.remove();
                    Service.m3=null;
                }
                mMapboxMap.setMaxZoomPreference(17);
                mMapboxMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            }
        }
    }
}
