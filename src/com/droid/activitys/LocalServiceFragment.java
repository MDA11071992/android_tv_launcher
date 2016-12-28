package com.droid.activitys;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_service, null);
        initView(view);
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
        ad1.setOnFocusChangeListener(mFocusChangeListener);
        ad2.setOnFocusChangeListener(mFocusChangeListener);
        cate.setOnFocusChangeListener(mFocusChangeListener);
        weather.setOnFocusChangeListener(mFocusChangeListener);
        news.setOnFocusChangeListener(mFocusChangeListener);
        appStore.setOnFocusChangeListener(mFocusChangeListener);
        video.setOnFocusChangeListener(mFocusChangeListener);

        tv.setOnClickListener(this);
        video.setOnClickListener(this);

        tv.setFocusable(true);
        tv.setFocusableInTouchMode(true);
        tv.requestFocus();
        tv.requestFocusFromTouch();
    }

    private void showImages() {}

    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_tv:
                JumpIntent = new Intent("com.mstar.tv.tvplayer.ui.intent.action.SOURCE_INFO");
                JumpIntent.setComponent(ComponentName.unflattenFromString("com.mstar.tv.tvplayer.ui/.channel.SourceInfoActivity"));
                startActivity(JumpIntent);
                break;
            case R.id.local_ad1:
                break;
            case R.id.local_ad2:
                break;
            case R.id.local_weather:
                break;
            case R.id.local_app_store:
                JumpIntent = new Intent(Intent.ACTION_VIEW);
                JumpIntent.setData(Uri.parse("market://search?q=pname:com.rovio.angrybirds" ));
                startActivity(JumpIntent);
                break;
            case R.id.local_cate:
                break;
            case R.id.local_news:
                break;
            case R.id.local_tour:
                break;
            case R.id.local_video:
                JumpIntent = new Intent(Intent.ACTION_GET_CONTENT);
                JumpIntent.setType("video/*");
                startActivity(JumpIntent);
                break;
        }
    }


}
