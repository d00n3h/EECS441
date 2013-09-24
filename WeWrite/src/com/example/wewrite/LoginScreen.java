package com.example.wewrite;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import android.util.*;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.util.*;

public class LoginScreen extends Activity {

  private CollabrifyClient myClient;
  private String sessionName;
  private long sessionId;
  private List<String> tags = new ArrayList<String>();
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
    tags.add("LALALAL");
		CollabrifyListener collabrifyListener = new CollabrifyListener() {
		  
      @Override
      public void onSessionCreated(long id)
      {
        Log.i("LoginScreen", "Session Created, id: " + id );
        sessionId = id;
        System.out.println("SMACK DAT");
        runOnUiThread(new Runnable() {
          @Override
          public void run()
          {
            
          }
          
        });
      }

      @Override
      public byte[] onBaseFileChunkRequested(long currentBaseFileSize)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void onBaseFileUploadComplete(long baseFileSize)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onSessionJoined(long maxOrderId, long baseFileSize)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onBaseFileChunkReceived(byte[] baseFileChunk)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onDisconnect()
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onReceiveEvent(long orderId, int submissionRegistrationId,
          String eventType, byte[] data)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onReceiveSessionList(final List<CollabrifySession> sessionList)
      {
        System.out.println("SMACK DAT");
        if( sessionList.isEmpty() ) {
          Log.i(CollabrifyClient.class.getSimpleName(), "No session available");
          return;
        }
        
        List<String> sessionNames = new ArrayList<String>();
        for(CollabrifySession s : sessionList) {
          sessionNames.add(s.name());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        builder.setTitle("Choose Session").setItems(sessionNames.toArray(new String[sessionList.size()]), new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            try {
              sessionId = sessionList.get(which).id();
              sessionName = sessionList.get(which).name();
              myClient.joinSession(sessionId, null);
            }
            catch(CollabrifyException e) {
              Log.e(CollabrifyClient.class.getSimpleName(), "error", e);
            }
          } 
        });
        runOnUiThread(new Runnable() {

          @Override
          public void run()
          {
            builder.show();
          }
          
        });
      }

      @Override
      public void onParticipantJoined(CollabrifyParticipant p)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onParticipantLeft(CollabrifyParticipant p)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onError(CollabrifyException e)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onSessionEnd(long id)
      {
        // TODO Auto-generated method stub
        
      }
		  
		};
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
				  Random rand = new Random();
				  sessionName = "Test" + rand.nextInt(Integer.MAX_VALUE );
				  try {
				    myClient.createSession(sessionName, tags, null, 0);
				  }
				  catch (CollabrifyException e) {
				    e.printStackTrace();
				  };
				  Log.i(CollabrifyClient.class.getSimpleName(), "Session name is " + sessionName);
				Intent intent = new Intent(v.getContext(), DocumentScreen.class);
				startActivity(intent);
			}
		});
		
		Button joinSessionButton = (Button)findViewById(R.id.button2);
		joinSessionButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0)
      {
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        try
        {
          myClient.requestSessionList(tags);
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
        
      }
		  
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.document_screen, menu);
		return true;
	}
}
