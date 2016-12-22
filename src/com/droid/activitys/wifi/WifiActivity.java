package com.droid.activitys.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import com.droid.R;
import com.droid.activitys.wifi.util.WiFiAdmin;

import java.util.List;

public class WifiActivity extends Activity implements OnClickListener,OnItemClickListener{
	private ListView WifiListView;
	private WAndB_WifilistAdapter adapter;
	private List<ScanResult> scanResults;
	private WiFiAdmin wiFiAdmin;
	private Switch WifiSwitch;
	private String ConnectSSID="";
	private TextView Wifi_StateDisplay;
	private ImageView Arrowtop;
	private final int WIFI_OPEN_FINISH=1;//Open is complete
	private final int WIFI_FOUND_FINISH=0;//Finished
	private final int WIFI_SCAN=2;
	private final int WIFI_CLOSE=3;
	private final int WIFI_INFO=4;
	private final int WIFI_STATE_INIT=5;
	private Dialog ConnectDialog;
	private int NetId;
	final Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WIFI_FOUND_FINISH:
				scanResults=wiFiAdmin.GetWifilist();
				adapter.notifyDataSetChanged();
				break;
			 case WIFI_STATE_INIT:  
			    	int wifiState=wiFiAdmin.GetWifiState();
			    	if(wifiState==wiFiAdmin.getWifiManager().WIFI_STATE_DISABLED){
				    	Wifi_StateDisplay.setText("WiFi не подключен");
			    	}else if(wifiState==wiFiAdmin.getWifiManager().WIFI_STATE_UNKNOWN){
			    		Wifi_StateDisplay.setText("WiFi статус неизвестен");
			    	}else if(wifiState==wiFiAdmin.getWifiManager().WIFI_STATE_ENABLED){
			        	WifiSwitch.setChecked(true);
			        	wiFiAdmin.StartScan();
			    		scanResults =wiFiAdmin.GetWifilist();
				        handler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);

			    		if(wiFiAdmin.isWifiEnable()){
			               Toast.makeText(WifiActivity.this, "wifi уже включен", Toast.LENGTH_SHORT).show();

			    		}else { 
				             Toast.makeText(WifiActivity.this, "Включите wifi", Toast.LENGTH_SHORT).show();
			    		}		        	
			    	}
			    	
			    break;
			case WIFI_OPEN_FINISH:
				scanResults=wiFiAdmin.GetWifilist();
				adapter=new WAndB_WifilistAdapter(WifiActivity.this, scanResults);
				WifiListView.setAdapter(adapter);
				break;
			case  WIFI_SCAN:
				wiFiAdmin.StartScan();
				scanResults=wiFiAdmin.GetWifilist();
				Wifi_StateDisplay.setText("Поиск WIFI...");
				if(scanResults==null){
					handler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);
				}else if(scanResults.size()==0){
					handler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);
					SetScanResult();
				}else{
					Wifi_StateDisplay.setText("Найден WiFi");
					adapter=new WAndB_WifilistAdapter(WifiActivity.this, scanResults);
					WifiListView.setAdapter(adapter);
				}
				break;
			case WIFI_CLOSE:
				SetScanResult();
				Wifi_StateDisplay.setText("WIFI Закрыт");
				break;
			case WIFI_INFO:
				if(wiFiAdmin.GetSSID().endsWith("<unknown ssid>")||wiFiAdmin.GetSSID().endsWith("NULL")){
					wiFiAdmin.getWifiConnectInfo();
					Wifi_StateDisplay.setText("Нет подключения к WiFi");
					handler.sendEmptyMessageDelayed(WIFI_INFO, 2500);
				}else if(wiFiAdmin.GetSSID().equals("NULL")){
					wiFiAdmin.getWifiConnectInfo();
					Wifi_StateDisplay.setText("Нет подключения, выберите другое WiFi соединения");
		    		handler.sendEmptyMessageDelayed(WIFI_INFO, 2500);
		    	}else{
		    		wiFiAdmin.getWifiConnectInfo();
		    		if(wiFiAdmin.GetIntIp().equals("")){
		    			handler.sendEmptyMessageDelayed(WIFI_INFO, 2500);
		    		}
		    		Wifi_StateDisplay.setText("Подключение к"+wiFiAdmin.GetSSID()+"Если есть проводная сеть, подключите сетевой кабель");
		    		ConnectDialog.dismiss();
		    		ConnectSSID=wiFiAdmin.GetSSID();
		    		Toast.makeText(WifiActivity.this, ConnectSSID, Toast.LENGTH_SHORT).show();
		    		Toast.makeText(WifiActivity.this, "Соединение успешно", Toast.LENGTH_SHORT).show();
		    	}
				break;
			}
			 super.handleMessage(msg);
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wandb_wifipager);
		 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			 }
		 InitData();
		 handler.sendEmptyMessageDelayed(WIFI_STATE_INIT, 1000);
	}
public void InitData(){
	wiFiAdmin=new WiFiAdmin(WifiActivity.this);
	ConnectDialog=new AlertDialog.Builder(WifiActivity.this).create();
	WifiListView=(ListView)findViewById(R.id.wandb_wifi_listview);
	WifiSwitch=(Switch)findViewById(R.id.wifi_switch);
	Arrowtop=(ImageView)findViewById(R.id.wifi_arrowtop);
	Wifi_StateDisplay=(TextView)findViewById(R.id.wifi_statedispaly);
	WifiListView.setOnItemClickListener(this);
	WifiListView.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2==0){
				Arrowtop.setVisibility(View.INVISIBLE);
			}else{
				Arrowtop.setVisibility(View.VISIBLE);	
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	});
	WifiSwitch.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
		if(WifiSwitch.isChecked()){
			wiFiAdmin.OpenWifi();
			wiFiAdmin.StartScan();
			scanResults=wiFiAdmin.GetWifilist();
			handler.sendEmptyMessageDelayed(WIFI_SCAN, 1000);
			 Toast.makeText(WifiActivity.this, "WiFi включен", Toast.LENGTH_SHORT).show();
		}else{
			wiFiAdmin.CloseWifi();
			handler.sendEmptyMessage(WIFI_CLOSE);
			Toast.makeText(WifiActivity.this, "WiFi отключен", Toast.LENGTH_SHORT).show();
		}
		}
	});
}
public void SetScanResult(){
	wiFiAdmin.StartScan();
	scanResults=wiFiAdmin.GetWifilist();
	adapter=new WAndB_WifilistAdapter(WifiActivity.this, scanResults);
	WifiListView.setAdapter(adapter);
}
@Override
public void onClick(View arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	// TODO Auto-generated method stub
	if(GetNowWifiSSID().equals("\""+scanResults.get(arg2).SSID+"\"")){
		Toast.makeText(WifiActivity.this, "В настоящее время вы подключены к этой сети", Toast.LENGTH_SHORT).show();
		}else{
	final int Num=arg2;
	LayoutInflater layoutInflater=LayoutInflater.from(WifiActivity.this);
	View view=(RelativeLayout)layoutInflater.inflate(R.layout.connect_wifidialog, null);
	TextView WifiName=(TextView)view.findViewById(R.id.wifidialog_name);
	WifiName.setText(scanResults.get(arg2).SSID);
	ConnectDialog.show();
	ConnectDialog.getWindow().setContentView(view);
	Window dialogwWindow=ConnectDialog.getWindow();
	WindowManager.LayoutParams params=dialogwWindow.getAttributes();
	dialogwWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
	params.x=0;
	params.y=0;
	params.width=750;//宽
	params.height=400;//高
	params.softInputMode=0;
	dialogwWindow.setAttributes(params);
	ConnectDialog.show();
	Button cancel=(Button)view.findViewById(R.id.wifi_dialog_cancel);
	Button connect=(Button)view.findViewById(R.id.wifi_dialog_connect);
	final EditText password=(EditText)view.findViewById(R.id.wifi_dialog_password);
	cancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ConnectDialog.dismiss();
		}
	});
	connect.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String WifiPassword=password.getText().toString();
			NetId=wiFiAdmin.AddNetwork(wiFiAdmin.CreatConfiguration(scanResults.get(Num).SSID, WifiPassword, 3));
			if(NetId==0){
				Toast.makeText(WifiActivity.this, "Нет доступа в Интернет", Toast.LENGTH_LONG).show();
			}else if(NetId==1){
				Toast.makeText(WifiActivity.this, "Неправильный пароль", Toast.LENGTH_LONG).show();
			}else if(NetId==2){
				Toast.makeText(WifiActivity.this, "соединение", Toast.LENGTH_LONG).show();
                 ConnectDialog.dismiss();
			}else if(NetId==-1){
				Toast.makeText(WifiActivity.this, "Ошибка подключения", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(WifiActivity.this, "соединение", Toast.LENGTH_LONG).show();
                ConnectDialog.dismiss();
			}
			handler.sendEmptyMessageDelayed(WIFI_INFO, 2000);
		}
	});
	password.setOnFocusChangeListener(new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View arg0, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
		        ConnectDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			}else{
				
			}		
		}
	});
		}
}
public String GetNowWifiSSID(){
	WifiManager mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	 WifiInfo wifiInfo = mWifi.getConnectionInfo();
	 String SSID=wifiInfo.getSSID();
	 return SSID;
}
}
