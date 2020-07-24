package com.example.adrom.snapp;


import android.app.Service;

import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;



import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import Lib.App;
import androidx.annotation.Nullable;

/**
 * Created by Hossein Saffarhamidi.
 */

public class MyService extends Service {
    Handler handler;
    Socket socket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //http://192.168.1.117:5000
    @Override
    public void onCreate() {
        handler = new Handler();
        try {

            String token= App.getToken(MyService.this);
            IO.Options options = new IO.Options();
            options.query = "group=user&token="+token;
            options.reconnection = true;
            socket = IO.socket("http://toosmachinery.com:3000", options);

            socket.connect();
            Lib.Service.set_Socket(socket);
            socket.on("notification", notificationHandle);
            socket.on("set_status", StatusHandel);
            socket.on("cancel_service", CancelServiceHandel);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Lib.Service.get_Socket().connect();
        return START_STICKY;
    }

    public Emitter.Listener notificationHandle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final JSONObject data = (JSONObject) args[0];
            handler.post(new Runnable() {
                @Override
                public void run() {

                    try {

                        String title = data.getString("title");
                        String content = data.getString("content");
                        String activity_key = data.getString("activity_key");
                        String activity_value = data.getString("activity_value");

                        String class_name = "com.example.adrom.snapp.ServiceListActivity";

                        if (!data.getString("activity").equals("null")) {
                            class_name = data.getString("activity");
                        }

                        Lib.Service.show_notification(MyService.this, title,
                                content, class_name, activity_key, activity_value);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public Emitter.Listener StatusHandel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final JSONObject data = (JSONObject) args[0];
            handler.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        String Status = data.getString("status");
                        if (Status.equals("2") && Lib.Service.show_notification.equals("ok"))
                        {
                            Lib.Service.show_notification(MyService.this,
                                    "تاکسی ", "تاکسی شما رسید",
                                    "com.example.adrom.snapp.CedarMapActivity", "", "");
                        }
                        else if(Status.equals("finish")){
                            Intent finish=new Intent("finishService");
                            sendBroadcast(finish);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            });
        }
    };

    public Emitter.Listener CancelServiceHandel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(Lib.Service.show_notification.equals("ok")){

                        Lib.Service.show_notification(MyService.this,
                                "تاکسی ", "درخواست شما توسط راننده لغو شد",
                                "com.example.adrom.snapp.CedarMapActivity", "", "");
                    }
                    else {
                        Toast.makeText(MyService.this, "سفارش شما توسط راننده لغو شد", Toast.LENGTH_SHORT).show();
                        Lib.Service.refresh(MyService.this);
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
