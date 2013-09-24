package com.example.wewrite;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import android.util.*;
import android.nfc.*;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.util.*;

public class LoginScreen extends Activity {

  CollabrifyClient myClient;
  String sessionName;
  long sessionId;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		WeWriteCollabrifyListener collabrifyListener = new WeWriteCollabrifyListener();
		try {
		  myClient = new CollabrifyClient(this, "user email", "user display name", 
		      "441fall2013@umich.edu", "XY3721425NoScOpE", false, collabrifyListener);
		}
		catch( CollabrifyException e ) {
		  e.printStackTrace();
		}
		
		Button createSessionButton = (Button)findViewById(R.id.button1);
		createSessionButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
				  Random rand = new Random();
				  sessionName = "Test" + rand.nextInt(Integer.MAX_VALUE );
				  myClient.createSession(sessionName, null, null, 0);
				  Log.i("LoginScreen", "Session name is " + sessionName);
				}
				catch (CollabrifyException e ) {
				  Log.e("LoginScreen", "error", e);
				}
				Intent intent = new Intent(v.getContext(), DocumentScreen.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.document_screen, menu);
		return true;
	}
	public void onSessionCreated(long id) {
	  Log.i("LoginScreen", "Session Created, id: " + id );
	  sessionId = id;
	  runOnUiThread(new Runnable() {

      @Override
      public void run()
      {
        
      }
	    
	  });
	}
}
