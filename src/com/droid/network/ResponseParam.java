package com.droid.network;

import org.json.JSONException;
import org.json.JSONObject;


public class ResponseParam {

	private static final String RESULT = "result";
	private static final String RESPONSE_TYPE = "requestType";
	protected static final String CONTENT = "content";

	

	public static final int START_REQUEST = -1;
	

	public static final int NET_WORK_ERROR = -2;
	

	public static final int REQUEST_FAIL = -3;
	

	public static final int RESULT_SUCCESS = 0;

	public static final int RESULT_USER_LOGIN_NAME_ERROR = 1;

	public static final int RESULT_PASSWORD_ERROR = 2;	
	

	public static final int RESULT_SERVER_ERROR = 3;
	
	
	protected JSONObject jsonObject;
		
	
	public ResponseParam( String responseJson ) throws JSONException {
		
		try {
			this.jsonObject = new JSONObject( responseJson );
		} catch ( JSONException e ) {
			throw e;
		}
	}
	
	

	public int getResult() {
		try {
			return this.jsonObject.getInt( RESULT );
		} catch (JSONException e) {
			e.printStackTrace();
			return ResponseParam.RESULT_SERVER_ERROR;
		}
	}

	public String getRequestType() {
		try {
			return this.jsonObject.getString( RESPONSE_TYPE );
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getContent() {
		try {
			return this.jsonObject.getString( CONTENT );
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}
