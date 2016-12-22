package com.droid.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("DefaultLocale")
public class Tools {

	private static boolean d = true;
	private static String TAG = "Tools";

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}


	public static String md5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}


	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,3,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "-telnum-");
		return m.matches();
	}


	private static boolean matchingText(String expression, String text) {
		Pattern p = Pattern.compile(expression);
		Matcher m = p.matcher(text);
		boolean b = m.matches();
		return b;
	}


	public static boolean isZipcode(String zipcode) {
		Pattern p = Pattern.compile("[0-9]\\d{5}");
		Matcher m = p.matcher(zipcode);
		System.out.println(m.matches() + "-zipcode-");
		return m.matches();
	}


	public static boolean isValidEmail(String email) {
		Pattern p = Pattern
				.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher m = p.matcher(email);
		System.out.println(m.matches() + "-email-");
		return m.matches();
	}

	public static boolean isTelfix(String telfix) {
		Pattern p = Pattern.compile("d{3}-d{8}|d{4}-d{7}");
		Matcher m = p.matcher(telfix);
		System.out.println(m.matches() + "-telfix-");
		return m.matches();
	}


	public static boolean isCorrectUserName(String name) {
		Pattern p = Pattern.compile("([A-Za-z0-9]){2,10}");
		Matcher m = p.matcher(name);
		System.out.println(m.matches() + "-name-");
		return m.matches();
	}


	public static boolean isCorrectUserPwd(String pwd) {
		Pattern p = Pattern.compile("\\w{6,18}");
		Matcher m = p.matcher(pwd);
		System.out.println(m.matches() + "-pwd-");
		return m.matches();
	}


	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}


	public static String calculationRemainTime(String endTime, long countDown) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date now = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			Date endData = df.parse(endTime);
			long l = endData.getTime() - countDown - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			return "year" + day + "day" + hour + "hour" + min + "min" + s + "sec";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void showLongToast(Context act, String pMsg) {
		Toast toast = Toast.makeText(act, pMsg, Toast.LENGTH_LONG);
		toast.show();
	}

	public static void showShortToast(Context act, String pMsg) {
		Toast toast = Toast.makeText(act, pMsg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static String getImeiCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}


	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static PackageInfo getAPKVersionInfo(Context context,
			String packageName) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo;
	}

	public static void download(String url, String path,
			final IOAuthCallBack iOAuthCallBack) {
		HttpUtils http = new HttpUtils();
		HttpHandler<File> handler = http.download(url, path, false, // If the target file exists, the unfinished portion continues downloading.
				// RANGE will be downloaded when the server does not support RANGE.
				false, // If the file name is retrieved from the request return information,
				// the download is automatically renamed.
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						if (d)
							Log.d("-----downfile-----", "success");
						iOAuthCallBack.getIOAuthCallBack(arg0.result.getName(),
								0);
					}

                    @Override
                    public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                        if (d)
                            Log.d("-----downfile-----", "fail");
                        iOAuthCallBack.getIOAuthCallBack(e.toString(), 1);
                    }
                });
	}

	public static void installApk(Context context, File apk, String md5) {

		// Verify the file MD5 value
		try {
			Log.d(TAG,
					"md5" + MD5Util.getFileMD5String(apk).equalsIgnoreCase(md5));
			if (!apk.exists()
					|| !MD5Util.getFileMD5String(apk).equalsIgnoreCase(md5)) {
				Log.d(TAG, "md5 check error");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtils.modifyFile(apk);// 修改文件权限之可执行
		Log.i(TAG, "===========install apk =========" + apk.getAbsolutePath());
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apk.getAbsolutePath()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}
	

	public static final boolean ping() {

        String result = null;
        try {
                String ip = "www.google.com";// Unless Baidu hung up, otherwise it should be no problem with this
			// (can also be replaced by the server to connect their address)
                Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping3次
                // Read the contents of the ping
                InputStream input = p.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                StringBuffer stringBuffer = new StringBuffer();
                String content = "";
                while ((content = in.readLine()) != null) {
                        stringBuffer.append(content);
                }
                Log.i("TTT", "result content : " + stringBuffer.toString());
                int status = p.waitFor();
                if (status == 0) {
                        result = "successful~";
                        return true;
                } else {
                        result = "failed~ cannot reach the IP address";
                }
        } catch (IOException e) {
                result = "failed~ IOException";
        } catch (InterruptedException e) {
                result = "failed~ InterruptedException";
        } finally {
                Log.i("TTT", "result = " + result);
        }
        return false;
}


    public static  long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks/1024;
    }


    public static boolean isJson(String value) {
        try {
            new JSONObject(value);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    static public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

}
