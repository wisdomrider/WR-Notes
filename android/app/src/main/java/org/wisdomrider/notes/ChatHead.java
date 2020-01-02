package org.wisdomrider.notes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.*;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.wisdomrider.Utils.Preferences;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ChatHead extends Service {
    WindowManager windowManager;
    WindowManager.LayoutParams params;
    View chatHeadView;
    public RelativeLayout lay;
    public View add;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 5;
    Preferences preferences;
    Api api;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showFloatingIcon() {
        preferences = new Preferences(this, "Data", 0);
        chatHeadView = LayoutInflater.from(this).inflate(R.layout.chat_head, null);
        add = LayoutInflater.from(this).inflate(R.layout.add, null);
        lay = chatHeadView.findViewById(R.id.head);
        params = getParams("not");
        params.gravity = Gravity.BOTTOM;
        params.x = 400;
        params.y = 0;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHeadView, params);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request x = chain.request();
                Request x1 = x.newBuilder()
                        .method(x.method(), x.body())
                        .addHeader("Authorization", "Bearer " + preferences.getString("token", "notes"))
                        .build();
                return chain.proceed(x1);
            }
        });
        retrofit = new Retrofit.Builder()
                .baseUrl("https://notes.wisdomriderr.shop/api/mobile/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);
    }

    void showNotification() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mBuilder.setChannelId(mChannel.getId());
        }
        mBuilder.setContentTitle("Adding Note")
                .setContentText("Note is Uploading to Server !")
                .setSmallIcon(R.drawable.icon);
        mBuilder.setProgress(100, 0, true);
        mNotifyManager.notify(id, mBuilder.build());

    }

    WindowManager.LayoutParams getParams(String not_focusable) {
        if (preferences.getString("token", "").trim().isEmpty()) {
            onDestroy();
        }
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        if (not_focusable != null)
            return new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        else
            return new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start();
        return START_STICKY;
    }

    String clip;
    Boolean pardaina = false;

    private void start() {


        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay.removeView(add);
                pardaina = true;
                params = getParams(null);
                params.gravity = Gravity.CENTER;
                params.x = 0;
                params.y = 0;
                add = LayoutInflater.from(ChatHead.this).inflate(R.layout.note_1, null);
                windowManager.removeViewImmediate(chatHeadView);
                windowManager.addView(chatHeadView, params);
                lay.addView(add);
                final EditText title1, desc1;
                title1 = add.findViewById(R.id.title);
                desc1 = add.findViewById(R.id.desc);
                desc1.setText(clip);
                add.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lay.removeView(add);
                        params = getParams("not");
                        params.gravity = Gravity.BOTTOM;
                        params.x = 400;
                        params.y = 0;
                        windowManager.removeViewImmediate(chatHeadView);
                        lay.setOnClickListener(null);
                        windowManager.addView(chatHeadView, params);
                    }
                });
                add.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        title = title1.getText().toString();
                        desc = desc1.getText().toString();
                        if (!title.trim().isEmpty() && !desc.trim().isEmpty()) {
                            lay.removeView(add);
                            params = getParams("not");
                            params.gravity = Gravity.BOTTOM;
                            params.x = 400;
                            params.y = 0;
                            windowManager.removeViewImmediate(chatHeadView);
                            windowManager.addView(chatHeadView, params);
                            showNotification();
                            lay.setOnClickListener(null);
                            sendNotificationToServer();
                        }
                    }
                });


            }
        };

        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() {
                clip = clipboard.getText().toString();
                pardaina = false;
                params = getParams("not");
                params.gravity = Gravity.BOTTOM;
                params.x = 400;
                params.y = 0;
                windowManager.removeViewImmediate(chatHeadView);
                windowManager.addView(chatHeadView, params);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!pardaina) {
                            lay.removeView(add);
                            lay.setOnClickListener(null);
                        }
                    }
                }, 7000);
                lay.removeView(add);
                add = LayoutInflater.from(ChatHead.this).inflate(R.layout.add, null);
                lay.addView(add);
                lay.setOnClickListener(listener);
            }
        });

        if (!preferences.getString("cache", "").isEmpty()) {
            String[] data = preferences.getString("cache", "").split("^&&");
            title = data[0];
            desc = data[1];
            showNotification();
            sendNotificationToServer();
            preferences.putString("cache", "").apply();
        }
    }


    Retrofit retrofit;
    String title, desc;

    private void sendNotificationToServer() {
        api.addNote(new LoginPage.Add(title, desc)).enqueue(new Callback<Home.Response>() {
            @Override
            public void onResponse(Call<Home.Response> call, retrofit2.Response<Home.Response> response) {
                if (response.code() == 406) {
                    mBuilder.setContentTitle("Session Expired !")
                            .setContentText("Please open the app to save a note !")
                            .setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    return;
                }
                mBuilder.setContentTitle("Note uploaded succesfully !")
                        .setContentText("Your clipboard is copied  to server !")
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                preferences.putString("cache", "").apply();
            }

            @Override
            public void onFailure(Call<Home.Response> call, Throwable t) {
                mBuilder.setContentText("Unable to connect to server.Note is cached and will be synced once you are connected to internet !")
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                preferences.putString("cache", title + "^&&" + desc).apply();
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                registerReceiver(new NetworkChangeReceiver(), filter);

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHeadView != null) {
            windowManager.removeView(chatHeadView);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showFloatingIcon();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isOnline(context)) {
                    showNotification();
                    sendNotificationToServer();
                    Toast.makeText(context, "back !", Toast.LENGTH_SHORT).show();
                    unregisterReceiver(this);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private boolean isOnline(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return (netInfo != null && netInfo.isConnected());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}

