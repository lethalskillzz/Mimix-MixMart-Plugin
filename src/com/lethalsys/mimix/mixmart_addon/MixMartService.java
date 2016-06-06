package com.lethalsys.mimix.mixmart_addon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lethalsys.mimix.aidl.IBinary;

public class MixMartService extends Service {
	static final String LOG_TAG = "MixMartService";
	//private final Binder Binder=new Binder();
	
	static String SERVER;
	static String UserID;
	static String USER;

	public void onStart(Intent intent, int startId) {
		super.onStart( intent, startId );
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
      	return IBinder;
	}

    private final IBinary.Stub IBinder = 
			new IBinary.Stub() {
		public void data( String server, String uid, String user ) {
			
			SERVER = server;
		    UserID = uid;	
		    USER = user;
		}
    };
}

