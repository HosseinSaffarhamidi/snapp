package Lib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by idehpardazanjavan on 2/1/19.
 */

public class App
{
    public static int pxfromdp(Context context, int dp)
    {
        float a=dp*context.getResources().getDisplayMetrics().density;
        return (int)a;
    }
    public static String replace_number(String number)
    {
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
    public static String getToken(Context context)
    {
        SharedPreferences sp=context.getSharedPreferences("Taxi_user_datas",Context.MODE_PRIVATE);
        return  sp.getString("token","null");
    }

}
