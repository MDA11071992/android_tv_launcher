package com.droid.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.droid.application.ClientApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class HttpClient {

	public static final String HTTP_POST = "POST";
	
	public static final String HTTP_GET  = "GET";

    private static final boolean d = ClientApplication.debug;


	public static byte[] connect( 
			URL url, 
			String method, 
			String postParam,
			int connectTimeout, 
			int readTimeout)
		throws NullPointerException, IOException, ProtocolException {

		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout( connectTimeout );
		connection.setReadTimeout( readTimeout );
		connection.setDoInput( true );
		connection.setDoOutput( true );
		connection.setRequestMethod( method );
		connection.setRequestProperty( "Accept-Charset", "utf-8" );

		if( method == HttpClient.HTTP_POST && postParam != null ) {
			BufferedOutputStream out = new BufferedOutputStream( connection.getOutputStream(), 8192 );
			if(d)System.out.println( "Разместить свои параметры：" + postParam );
			out.write( postParam.getBytes( "utf-8" ) );
			out.flush();
			out.close();
		}

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int result = connection.getResponseCode();
		if(d)System.out.println( result );
		if ( result == 200 ) {
			BufferedInputStream inStream = new BufferedInputStream( connection.getInputStream(), 8192 );
			byte[] buffer = new byte[1024];
			int len = -1;
			while( ( len = inStream.read( buffer ) ) != -1 ) {
				outStream.write(buffer, 0, len);
			}
			inStream.close();
		}
		outStream.close();
		connection.disconnect();
		return outStream.toByteArray();
	}
	

	public static boolean isConnect( Context context ) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
			if( connectivity != null ) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if( info != null && info.isConnected() && info.isAvailable() ) {
					if( info.getState() == NetworkInfo.State.CONNECTED ) {
						return true;
					}
				}
			}
		} catch ( Exception e ) {
			Log.v( "error", e.toString() );
		}
		return false;
	}
}