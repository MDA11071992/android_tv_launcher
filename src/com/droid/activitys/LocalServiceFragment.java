package com.droid.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.droid.R;
import com.droid.activitys.app.BrowserActivity;
import com.droid.application.ClientApplication;
import com.droid.cache.loader.ImageWorker;

import java.util.List;

public class LocalServiceFragment extends WoDouGameBaseFragment implements View.OnClickListener {

    private ImageWorker mImageLoader;
    private static final boolean d = ClientApplication.debug;
    private Context context;
    private List<ContentValues> datas;

    private ImageButton tour;
    private ImageButton tv;
    private ImageButton ad1;
    private ImageButton cate;
    private ImageButton weather;
    private ImageButton ad2;
    private ImageButton news;
    private ImageButton appStore;
    private ImageButton video;
    private Intent JumpIntent;
    private Activity activity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_service, null);
        initView(view);
        setListener();
        return view;
    }

    private void initView(View view) {

        tv = (ImageButton) view.findViewById(R.id.local_tv);
        tour = (ImageButton) view.findViewById(R.id.local_tour);
        ad1 = (ImageButton) view.findViewById(R.id.local_ad1);
        ad2 = (ImageButton) view.findViewById(R.id.local_ad2);
        cate = (ImageButton) view.findViewById(R.id.local_cate);
        weather = (ImageButton) view.findViewById(R.id.local_weather);
        news = (ImageButton) view.findViewById(R.id.local_news);
        appStore = (ImageButton) view.findViewById(R.id.local_app_store);
        video = (ImageButton) view.findViewById(R.id.local_video);

        tv.setOnFocusChangeListener(mFocusChangeListener);
        tour.setOnFocusChangeListener(mFocusChangeListener);
        //ad1.setOnFocusChangeListener(mFocusChangeListener);
        ad2.setOnFocusChangeListener(mFocusChangeListener);
        cate.setOnFocusChangeListener(mFocusChangeListener);
        weather.setOnFocusChangeListener(mFocusChangeListener);
        news.setOnFocusChangeListener(mFocusChangeListener);
        appStore.setOnFocusChangeListener(mFocusChangeListener);
        video.setOnFocusChangeListener(mFocusChangeListener);

        tv.setFocusable(true);
        tv.setFocusableInTouchMode(true);
        tv.requestFocus();
        tv.requestFocusFromTouch();
    }

    private void setListener() {

        tv.setOnClickListener(this);
        tour.setOnClickListener(this);
        ad1.setOnClickListener(this);
        ad2.setOnClickListener(this);
        cate.setOnClickListener(this);
        weather.setOnClickListener(this);
        news.setOnClickListener(this);
        appStore.setOnClickListener(this);
        video.setOnClickListener(this);

    }

    private void showImages() {}

    private void openApplication(Context context, String id) {
        JumpIntent = context.getPackageManager().getLaunchIntentForPackage(id);

        if (JumpIntent != null) {
            startActivity(JumpIntent);
        }

        else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
            }
            catch (android.content.ActivityNotFoundException anthe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=" + id)));
            }
        }
    }

    @Override

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.local_tv:
                openApplication(getActivity(), "com.mstar.tv.tvplayer.ui");
                /*JumpIntent = new Intent("com.mstar.tv.tvplayer.ui.intent.action.SOURCE_INFO");
                JumpIntent.setComponent(ComponentName.unflattenFromString("com.mstar.tv.tvplayer.ui/.channel.SourceInfoActivity"));
                startActivity(JumpIntent);*/
                break;
            case R.id.local_ad1:
                JumpIntent = new Intent(context, BrowserActivity.class);
                JumpIntent.setData(Uri.parse("http://www.worldtimeserver.com/current_time_in_RU-MOW.aspx"));
                //JumpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mvideo.ru/catalog/"));
                startActivity(JumpIntent);
                break;
            case R.id.local_ad2:
                openApplication(getActivity(), "com.firsthash.smartyoutubetv");
                /*JumpIntent = new Intent("android.intent.action.MAIN");
                JumpIntent.setComponent(new ComponentName("com.firsthash.smartyoutubetv", "com.android.browser.BrowserActivity"));
                startActivity(JumpIntent);*/
                break;
            case R.id.local_weather:
                JumpIntent = new Intent(context, BrowserActivity.class);
                JumpIntent.setData(Uri.parse("https://www.gismeteo.ru/"));
                startActivity(JumpIntent);
                break;
            case R.id.local_app_store:
                openApplication(getActivity(), "com.android.vending");
                /*JumpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="));
                startActivity(JumpIntent);*/
                break;
            case R.id.local_cate:
                //openApplication(getActivity(), "ru.ykt.eda");
                JumpIntent = new Intent(context, BrowserActivity.class);
                JumpIntent.setData(Uri.parse("http://studyinrussia.ru/life-in-russia/life-conditions/russian-food/"));
                startActivity(JumpIntent);
                break;
            case R.id.local_news:
                JumpIntent = new Intent(context, BrowserActivity.class);
                JumpIntent.setData(Uri.parse("http://www.vesti.ru/news"));
                startActivity(JumpIntent);
                break;
            case R.id.local_tour:
                JumpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mvideo.ru/catalog/"));
                startActivity(JumpIntent);
                break;
            case R.id.local_video:
                JumpIntent = new Intent(Intent.ACTION_GET_CONTENT);
                JumpIntent.setType("video/*");
                startActivity(JumpIntent);
                break;
        }
    }


}
