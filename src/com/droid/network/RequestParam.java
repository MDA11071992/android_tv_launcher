package com.droid.network;

import android.util.Log;

import com.droid.application.ClientApplication;

import org.json.JSONArray;
import org.json.JSONObject;


public class RequestParam {

    public static final String USER_NAME = "userName";// 其实是手机号
    public static final String PASSWORD = "password";
    public static final String RANDOM_KEY = "randomKey";
    public static final String REQUEST_TYPE = "requestType";
    public static final String PARAMS = "params";
    public static final String LOCATION_INFO = "locationInfo";
    public static final String MAC_ADDRESS = "macAddress";
    public static final String SEX = "sex";
    public static final String ADDR = "addr";
    public static final String NAME = "name";
    public static final String PHOTO = "photo";

    public static final String USER_PHONE = "userPhone";
    public static final String DEVICE_ID = "deviceID";
    public static final String UNIQUE_ID = "uniqueID";


    public static final String STATUS = "loginStatus";

    public static final int ONLINE = 0;
    public static final int OFFLINE = 1;

    public static final int SEND_TOPIC = 111;


    public final String LOGIN = "Login";


    public static final String LOGOUT = "Logout";


    public static final String UPDATE_INFO = "update_info";


    public static final String GET_PERSONINFO = "GetPersonInfo";


    public static final String GET_APPSTORE_APPS = "GetAllAppstoreApps";


    public static final String TERMINAL_AUTHENTICATION = "TerminalAuthentication";


    public static final String REGISTER = "Register";

    public static final String UPDATE_UI="UpdateUI";

    public static final String SIGNIN = "Signin";

//    public static final String UPDATE = "SoftWareUpdate";
    public static final String UPDATE = "CheckAppUpdate";


    private String userName;


    private String password;


    private String randomKey;


    private String requestType;

    private String macAddress;


    private String locationInfo;


    private Object params[];


    private String deviceID;


    private String userPhone;


    private String uniqueID;

    private static final boolean d = ClientApplication.debug;
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getJSON() {

        JSONObject object = new JSONObject();
        try {
            object.put(RequestParam.USER_NAME, this.userName);
            object.put(RequestParam.PASSWORD, this.password);
            object.put(RequestParam.RANDOM_KEY, this.randomKey);
            object.put(RequestParam.REQUEST_TYPE, this.requestType);
            object.put(RequestParam.DEVICE_ID, this.deviceID);
            object.put(RequestParam.USER_PHONE, this.userPhone);
            object.put(RequestParam.UNIQUE_ID, this.uniqueID);
            object.put(RequestParam.LOCATION_INFO, this.locationInfo);
            object.put(RequestParam.MAC_ADDRESS, this.macAddress);

            JSONArray jsonArray = new JSONArray();

            for (Object param : params) {
                jsonArray.put(param);
            }

            object.put(RequestParam.PARAMS, jsonArray);
            if (d) System.out.println("параметры запроса" + object.toString());
            return object.toString();

        } catch (Exception e) {
            Log.e("RequestParam", "Ошибка параметра запроса", e);
            return "";
        }
    }
}