package com.droid.utils;

import android.content.*;
import android.os.Handler;
import android.os.Message;
import com.droid.db.SharedPreferencesUtil;
import org.json.JSONArray;
import org.json.JSONException;


public class UpdateManager {
	private static final String TAG = "UpdateManager";
	private Context mContext;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private String packageVersion;
	private String packageDownloadPath;
	private String packageMD5;
	private JSONArray appList;
	private boolean d = true;// debug flag
	private SharedPreferencesUtil sp;
	private FileCache fileCache;

	public UpdateManager(Context context) {
		this.mContext = context;
		fileCache = new FileCache(mContext);
		sp = SharedPreferencesUtil.getInstance(mContext);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				break;
			case DOWN_OVER:
				break;
			default:
				break;
			}
		}
	};


	public void checkUpdateInfo() {

    }

	public String getCacheDir() {
		return fileCache.getCacheDir();
	}


	private void downloadApk() throws JSONException {

    }


	private void downloadZip() {}

}
