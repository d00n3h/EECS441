package com.example.wewrite;

import edu.umich.imlc.android.common.Utils;
import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.*;
import java.io.ByteArrayOutputStream;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import edu.umich.imlc.collabrify.client.exceptions.LeaveException;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.*;

public class LoginScreen extends Activity {

  private CollabrifyClient myClient;
  private String sessionName;
  private long sessionId;
  private List<String> tags = new ArrayList<String>();
  private ByteArrayOutputStream baseFileReceiveBuffer;
  private Timer broadcastTimer;
  private EditText sessionNameText;
  private EditText documentText;
  private Button redoButton;
  private Button undoButton;
  private CollabrifyUser user;
  private CollabrifyDocument document;
  private Button createSessionButton;
  private Button joinSessionButton;
  private Button leaveSessionButton;
  private Button endSessionButton;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		
		user = new CollabrifyUser();
    document = new CollabrifyDocument(); 
    documentText = (EditText)findViewById(R.id.EditText01);
    sessionNameText = (EditText)findViewById(R.id.editText1);
    redoButton = (Button)findViewById(R.id.redoButton);
    undoButton = (Button)findViewById(R.id.undoButton);
    createSessionButton = (Button)findViewById(R.id.button1);
    joinSessionButton = (Button)findViewById(R.id.button2);
    leaveSessionButton = (Button)findViewById(R.id.button3);
    endSessionButton = (Button)findViewById(R.id.button4);


    documentText.setLongClickable(false);
    
    
    undoButton.setOnClickListener(new OnClickListener() {
      
      public void onClick(View v) {
        UserAction undoAction = user.undoPastAction();
        if(undoAction != null) {
            performAction(undoAction);
            documentText.setText(document.getText());
        }
      }
    });
    
    redoButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        UserAction redoAction = user.redoUndoneAction();
        if(redoAction != null) {
            performAction(redoAction);
            documentText.setText(document.getText());
        }
      }
    });
    
    
    documentText.addTextChangedListener(new TextWatcher() {
      int addIndex;
      int delIndex;
      char addChar;
      char delChar;
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        addIndex = count + start - 1;
        System.out.println("In On text" + " count: " + count + " start: " + start + "before: " + before);
        if(addIndex >= 0) {
          addChar = s.charAt(addIndex);
        }
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
        if(s.length() == 0) {
          return;
        }
        delIndex = count + start - 1;
        delChar = s.charAt(delIndex);
        System.out.println("In Before Text");
      }
      
      @Override
      public void afterTextChanged(Editable s) {
        if(s.length() == document.getText().length()) {
          System.out.println("The ODDONE");
          return;
        }
        documentText.removeTextChangedListener(this);
        UserAction action;
        UserAction undoAction;
        if( s.length() > document.getText().length() ) {
          action = new UserAction("add", addIndex, addChar);
        } else {
          action = new UserAction("del", delIndex, delChar);
        }
        user.addPastAction(action);
        performAction(action);
        documentText.addTextChangedListener(this);
      }
    });
    
    //SET DOCUMENT SCREEN TO HIDDEN
    documentText.setVisibility(View.INVISIBLE);
    undoButton.setVisibility(View.INVISIBLE);
    redoButton.setVisibility(View.INVISIBLE);
    endSessionButton.setVisibility(View.INVISIBLE);
    leaveSessionButton.setVisibility(View.INVISIBLE);
    createSessionButton.setVisibility(View.VISIBLE);
    joinSessionButton.setVisibility(View.VISIBLE);
    sessionNameText.setVisibility(View.VISIBLE);
    //

    
    tags.add(" ");
		CollabrifyListener collabrifyListener = new CollabrifyListener() {
		  
      @Override
      public void onSessionCreated(long id)
      {
        Log.i(CollabrifyClient.class.getSimpleName(), "Session Created, id: " + id );
        sessionId = id;
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
        Log.i(CollabrifyClient.class.getSimpleName(), "session Joined");
        if (baseFileSize > 0) {
          baseFileReceiveBuffer = new ByteArrayOutputStream((int) baseFileSize);
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run()
          {
            setDocumentTextVisible();
            leaveSessionButton.setVisibility(View.VISIBLE);
          }
          
        });
      }

      @Override
      public void onBaseFileChunkReceived(byte[] baseFileChunk)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onDisconnect()
      {
        Log.i(CollabrifyClient.class.getSimpleName(), "disconnected");
        runOnUiThread(new Runnable() {
          @Override
          public void run()
          {                      
          }          
        });        
      }

      @Override
      public void onReceiveEvent(long orderId, int submissionRegistrationId,
          String eventType, final byte[] data)
      {
        Utils.printMethodName(CollabrifyClient.class.getSimpleName());
        Log.d(CollabrifyClient.class.getSimpleName(), "RECEIVED SUB ID " + submissionRegistrationId);
        runOnUiThread(new Runnable() {

          @Override
          public void run()
          {
            Utils.printMethodName(CollabrifyClient.class.getSimpleName());
            String message = new String(data);
            //broadcastedText.setText(message);
            
          }

        });
      }

      @Override
      public void onReceiveSessionList(final List<CollabrifySession> sessionList)
      {
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
        
      }

      @Override
      public void onError(CollabrifyException e)
      {
        
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
		
		endSessionButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v)
      {
        setLoginVisible();
        endSessionButton.setVisibility(View.INVISIBLE);
        try
        {
          if( myClient.inSession()) {
            myClient.leaveSession(true);
          }
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
      }
		  
		});
		
		leaveSessionButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0)
      {
        setLoginVisible();
        leaveSessionButton.setVisibility(View.INVISIBLE);
        try
        {
          if( myClient.inSession()) {
            myClient.leaveSession(false);
          }
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
      } 
		});
		
		
		
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
				  setDocumentTextVisible();
				  endSessionButton.setVisibility(View.VISIBLE);
			}
		});
		
		joinSessionButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0)
      {
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
	

	class Broadcast extends TimerTask {

    @Override
    public void run()
    {
      System.out.println("ERMAHGERD");
      
    }
	  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.document_screen, menu);
		return true;
	}
	
  private void performAction( UserAction action ) {
    String text = document.getText();
    String part1, part2;
    if( action.getActionType().equals("add")) {
      part1 = text.substring(0, action.getCursorPosition() );
      part2 = text.substring(action.getCursorPosition());
      text = part1 + action.getCharAffected() + part2;
      System.out.println("charAffected: " + action.getCharAffected() + " at index: " + action.getCursorPosition());
    } else{
      part1 = text.substring(0, action.getCursorPosition() );
      part2 = text.substring(action.getCursorPosition() + 1);
      text = part1 + part2;
    }
    document.setText(text);
    System.out.println(document.getText());
  }
  
  private void setLoginVisible() {
    documentText.setVisibility(View.INVISIBLE);
    undoButton.setVisibility(View.INVISIBLE);
    redoButton.setVisibility(View.INVISIBLE);
    createSessionButton.setVisibility(View.VISIBLE);
    joinSessionButton.setVisibility(View.VISIBLE);
    sessionNameText.setVisibility(View.VISIBLE);
  }
  
  private void setDocumentTextVisible() {
    createSessionButton.setVisibility(View.INVISIBLE);
    joinSessionButton.setVisibility(View.INVISIBLE);
    sessionNameText.setVisibility(View.INVISIBLE);
    documentText.setVisibility(View.VISIBLE);
    undoButton.setVisibility(View.VISIBLE);
    redoButton.setVisibility(View.VISIBLE);
  }
}
