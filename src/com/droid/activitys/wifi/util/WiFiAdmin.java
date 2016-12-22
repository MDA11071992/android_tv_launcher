package com.droid.activitys.wifi.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WiFiAdmin {
	// wifimanager
	private WifiManager mWifiManager;
	// wifiInfo
	private WifiInfo mWifiInfo;
	// The list of network connections scanned
	private List<ScanResult> mWifiList;
	// Network Connection List
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mwifiLock;

	public WiFiAdmin(Context context) {
		// wifimannager
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// wifiinfo
		mWifiInfo = mWifiManager.getConnectionInfo();
		mWifiList=new ArrayList<ScanResult>();
		mWifiConfigurations=new ArrayList<WifiConfiguration>();
	}

	public void OpenWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public void CloseWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	public int GetWifiState() {
		return mWifiManager.getWifiState();
	}

	public void AcquireWifiLock() {
		mwifiLock.acquire();
	}

	public void RelaseWifiLock() {
		if (mwifiLock.isHeld()) {
			mwifiLock.release();
		}
	}

	public void CreatWifilock() {
		mwifiLock = mWifiManager.createWifiLock("WIFILOCK");
	}

	public List<WifiConfiguration> getConfigurations() {
		return mWifiConfigurations;
	}

	public void ConnectConfiguration(int index) {
		// If the input index is greater than the configured index, it returns
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// Connect to the specified network
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	public void StartScan() {
		mWifiManager.startScan();
		// Get the scan results
		mWifiList = mWifiManager.getScanResults();
		// Get the configured network connection
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
		Log.v("mWifiManager", mWifiManager+"");
		Log.v("mWifiList", mWifiList+"");
		Log.v("mWifiConfigurations", mWifiConfigurations+"");
		String [] str=new String[mWifiList.size()];
		String tempstring=null;
		for(int i=0;i<mWifiList.size();i++){
			tempstring=mWifiList.get(i).SSID;
			if(null!=mWifiInfo&&tempstring.equals(mWifiInfo.getSSID())){
				tempstring=tempstring+"Подключено";
			}
			str[i]=tempstring;
		}
	}
	public void getWifiConnectInfo(){
		mWifiInfo=mWifiManager.getConnectionInfo();
	}

	public List<ScanResult> GetWifilist() {
		return mWifiList;
	}

	public StringBuilder CheckupScan() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// Convert Scanresult into a string package
			// These include: BSSID SSID capabilities frequency level
			stringBuilder.append(mWifiList.get(i).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	public String GetMacAdress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String GetSSID(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.getSSID();
	}

	public String GetBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int GetIpAdress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int GetNetworkID() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String GetWifiinfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	public int AddNetwork(WifiConfiguration configuration) {
		int configurationId = mWifiManager.addNetwork(configuration);
		boolean b = mWifiManager.enableNetwork(configurationId, true);
		System.out.println("configurationId---------->" + configurationId);
		System.out.println("b---------->" + b);
		return configurationId;
	}

	public void disconnectWifi(int networkid) {
		mWifiManager.disableNetwork(networkid);
		mWifiManager.disconnect();
	}

	public WifiConfiguration CreatConfiguration(String SSID, String Password,
			int Type) {
		WifiConfiguration configuration = new WifiConfiguration();
		configuration.allowedAuthAlgorithms.clear();
		configuration.allowedGroupCiphers.clear();
		configuration.allowedKeyManagement.clear();
		configuration.allowedPairwiseCiphers.clear();
		configuration.allowedProtocols.clear();
		configuration.SSID = "\"" + SSID + "\"";
		WifiConfiguration tempConfiguration = IsExits(SSID, mWifiManager);
		if (tempConfiguration != null) {
			mWifiManager.removeNetwork(tempConfiguration.networkId);
		}

		if (Type == 1) {
			configuration.wepKeys[0] = "";
			configuration.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.NONE);
			configuration.wepTxKeyIndex = 0;
		}

		if (Type == 2) {
			configuration.hiddenSSID = true;
			configuration.wepKeys[0] = "\"" + Password + "\"";
			configuration.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			configuration.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.NONE);
			configuration.wepTxKeyIndex = 0;
		}

		if (Type == 3) {
			configuration.preSharedKey = "\"" + Password + "\"";
			configuration.hiddenSSID = true;
			configuration.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			configuration.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			configuration.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			configuration.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			configuration.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			configuration.status = WifiConfiguration.Status.ENABLED;
		}
		return configuration;
	}

	private static WifiConfiguration IsExits(String SSID, WifiManager manager) {
		List<WifiConfiguration> exitsConfigurations = manager
				.getConfiguredNetworks();
		for (WifiConfiguration configuration : exitsConfigurations) {
			if (configuration.SSID.equals("\"" + SSID + "\"")) {
				return configuration;
			}
		}
		return null;
	}
	  public WifiManager getWifiManager()
	  {
	    return mWifiManager;
	  }
	  public boolean isWifiEnable()
	  {
	    return mWifiManager.isWifiEnabled();
	  }

	 public String GetIntIp()
	 {
		 int i=GetIpAdress();
		 if(i==0){
			 return "";
		 }
	  return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
	    + "." + ((i >> 24) & 0xFF);
	 }
}
