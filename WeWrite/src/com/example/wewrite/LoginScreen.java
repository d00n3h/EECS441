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
import android.os.CountDownTimer;
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
  private Button switchButton;
  private CollabrifyUser user;
  private CollabrifyDocument document;
  private Button createSessionButton;
  private Button joinSessionButton;
  private Button leaveSessionButton;
  private Button endSessionButton;
  private TextView sessionText;
  private boolean usingTimer;
  private LinkedList<UserAction> actionQueue;
  private int firstChangedIndex=9999999;
  private Button broadcastButton;
  private Broadcast broadcast = new Broadcast();
  private CountDownTimer countdownTimer;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		
		countdownTimer = new CountDownTimer(1000, 1000)
    {
      
      @Override
      public void onTick(long millisUntilFinished)
      {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void onFinish()
      {
        try
        {
          myClient.broadcast(serialize(document.getText()), "");
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
        actionQueue.clear();
        firstChangedIndex = 9999;
      }
    };
		usingTimer = true;
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
    sessionText = (TextView)findViewById(R.id.textView1);
    broadcastButton = (Button)findViewById(R.id.button5);
    switchButton = (Button)findViewById(R.id.button6);
    broadcastTimer = new Timer();
    actionQueue = new LinkedList<UserAction>();
    
    documentText.setLongClickable(false);
    
    undoButton.setOnClickListener(new OnClickListener() {
      
      public void onClick(View v) {
        UserAction undoAction = user.undoPastAction();
        undoRedo(undoAction);
      }
    });
    
    redoButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        UserAction redoAction = user.redoUndoneAction();
        undoRedo(redoAction);
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
        if(s.length() == 0 || documentText.getSelectionStart() == 0) {
          return;
        }
        delIndex = count + start - 1;
        delChar = s.charAt(delIndex);
      }
      
      @Override
      public void afterTextChanged(Editable s) {
        if(usingTimer){
          countdownTimer.cancel();
        }
        if(s.length() == document.getText().length()) {
          return;
        }
        documentText.removeTextChangedListener(this);
        UserAction action;
        if( s.length() > document.getText().length() ) {
          action = new UserAction("add", addIndex, addChar);
        } else {
          action = new UserAction("del", delIndex, delChar);
        }
        user.addPastAction(action);
        performAction(action);
        documentText.addTextChangedListener(this);
        if(usingTimer) {
          countdownTimer.start();
        }
      }
    });
    
    //SET DOCUMENT SCREEN TO HIDDEN
    documentText.setVisibility(View.INVISIBLE);
    undoButton.setVisibility(View.INVISIBLE);
    redoButton.setVisibility(View.INVISIBLE);
    endSessionButton.setVisibility(View.INVISIBLE);
    leaveSessionButton.setVisibility(View.INVISIBLE);
    sessionText.setVisibility(View.INVISIBLE);
    broadcastButton.setVisibility(View.INVISIBLE);
    createSessionButton.setVisibility(View.VISIBLE);
    joinSessionButton.setVisibility(View.VISIBLE);
    sessionNameText.setVisibility(View.VISIBLE);
    switchButton.setVisibility(View.INVISIBLE);
    //

    
    tags.add(" ");
		CollabrifyListener collabrifyListener = new CollabrifyListener() {
		  
      @Override
      public void onSessionCreated(long id)
      {
        Log.i(CollabrifyClient.class.getSimpleName(), "Session Created, id: " + id );;
        sessionName = new String("Test" + id);
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
            //startTimer();
            setDocumentTextVisible();
            leaveSessionButton.setVisibility(View.VISIBLE);
            sessionText.setText(sessionName);
          }
          
        });
      }

      @Override
      public void onBaseFileChunkReceived(byte[] baseFileChunk)
      {
        
      }

      @Override
      public void onDisconnect()
      {
        Log.i(CollabrifyClient.class.getSimpleName(), "disconnected");
        runOnUiThread(new Runnable() {
          @Override
          public void run()
          {                 
            try
            {
              stopTimer();
            }
            catch( InterruptedException e )
            {
              e.printStackTrace();
            }
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
            int changeIndex = data[0];
            String state = new String("");
            for(int i = 1; data[i] != '\0'; i++ ) {
              state += (char)data[i];
            }
            applyQueuedActions(state, changeIndex);
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
        try
        {
          myClient.broadcast(serialize(document.getText()), "");
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
        actionQueue.clear();
        firstChangedIndex = 9999;
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
		
		broadcastButton.setOnClickListener(new OnClickListener()
    {
      
      @Override
      public void onClick(View v)
      {
        try
        {
          myClient.broadcast(serialize(document.getText()), "");
        }
        catch( CollabrifyException e )
        {
          e.printStackTrace();
        }
        document.setLastBroadcast(document.getText());
        actionQueue.clear();
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
				  sessionText.setText(sessionName);
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
		
		switchButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0)
      {
        // TODO Auto-generated method stub
        if(usingTimer) {
          usingTimer = false;
          countdownTimer.cancel();
          broadcastButton.setVisibility(View.VISIBLE);
        } else {
          usingTimer = true;
          broadcastButton.setVisibility(View.INVISIBLE);
          try
          {
            myClient.broadcast(serialize(document.getText()), "");
          }
          catch( CollabrifyException e )
          {
            e.printStackTrace();
          }
          actionQueue.clear();
          firstChangedIndex = 9999;
          
          
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
	
  private void performAction( UserAction action ) {
    String text = document.getText();
    String part1, part2;
    if( action.getActionType().equals("add")) {
      part1 = text.substring(0, action.getCursorPosition() );
      part2 = text.substring(action.getCursorPosition());
      text = part1 + action.getCharAffected() + part2;
    } else{
      part1 = text.substring(0, action.getCursorPosition() );
      part2 = text.substring(action.getCursorPosition() + 1);
      text = part1 + part2;
    }
    document.setText(text);
    if(action.getCursorPosition() < firstChangedIndex) {
      firstChangedIndex = action.getCursorPosition();
    }
    actionQueue.addLast(action);
  }
 
  private void applyQueuedActions( String text, int changedIndex ) {
    int diff = text.length() - document.getLastBroadcast().length();
    System.out.println("DIFF: " + diff + " CHG INDEX: " + changedIndex);
    String part1, part2;
    int cursorPosition = documentText.getSelectionStart();
    UserAction action;
    while(!actionQueue.isEmpty() ) {
      action = actionQueue.getFirst();
      actionQueue.removeFirst();
      if(changedIndex <= action.getCursorPosition()) {
        action.setCursorPosition(action.getCursorPosition() + diff );
      }
      if( action.getActionType().equals("add")) {
        part1 = text.substring(0, action.getCursorPosition() );
        part2 = text.substring(action.getCursorPosition());
        text = part1 + action.getCharAffected() + part2;   
      } else {
        part1 = text.substring(0, action.getCursorPosition() );
        part2 = text.substring(action.getCursorPosition() + 1);
        text = part1 + part2;
      }
    }
    document.setText(text);
    documentText.setText(document.getText());
    if( cursorPosition > document.getText().length() ) {
      documentText.setSelection( document.getText().length());
    } else {
      documentText.setSelection(cursorPosition);
    }
  }
  
  private void setLoginVisible() {
    documentText.setVisibility(View.INVISIBLE);
    undoButton.setVisibility(View.INVISIBLE);
    redoButton.setVisibility(View.INVISIBLE);
    sessionNameText.setVisibility(View.VISIBLE);
    createSessionButton.setVisibility(View.VISIBLE);
    joinSessionButton.setVisibility(View.VISIBLE);
    sessionText.setVisibility(View.INVISIBLE);
    broadcastButton.setVisibility(View.INVISIBLE);
    switchButton.setVisibility(View.INVISIBLE);
  }
  
  private void setDocumentTextVisible() {
    if(usingTimer) {
      broadcastButton.setVisibility(View.INVISIBLE);
    } else {
      broadcastButton.setVisibility(View.VISIBLE);
    }
    createSessionButton.setVisibility(View.INVISIBLE);
    joinSessionButton.setVisibility(View.INVISIBLE);
    sessionNameText.setVisibility(View.INVISIBLE);
    documentText.setVisibility(View.VISIBLE);
    undoButton.setVisibility(View.VISIBLE);
    redoButton.setVisibility(View.VISIBLE);
    sessionText.setVisibility(View.VISIBLE);
    switchButton.setVisibility(View.VISIBLE);
  }
  
  class Broadcast extends TimerTask {
    @Override
    public void run()
    {
      try
      {
        myClient.broadcast(serialize(document.getText()), "");
      }
      catch( CollabrifyException e )
      {
        e.printStackTrace();
      }
      actionQueue.clear();
      firstChangedIndex = 9999;
      
    }
  }
  
  private void startTimer() {
    broadcastTimer.schedule(broadcast, 1000);
  }
  private void stopTimer() throws InterruptedException {
    broadcastTimer.cancel();
    broadcastTimer.purge();
    //broadcastTimer = new Timer();
    
  }
  
  private void undoRedo(UserAction action) {
    if(action != null) {
      performAction(action);
      documentText.setText(document.getText());
      if(action.getActionType().equals("add")) {
        documentText.setSelection(action.getCursorPosition()+1);
      } else {
        documentText.setSelection(action.getCursorPosition());
      }
    }
  }
  
  private byte[] serialize(String text) {
    byte[] serialized = new byte[text.length()+2];
    serialized[0] = ((Integer)firstChangedIndex).byteValue();
    for(int i = 0; i < text.length(); i++) {
      serialized[i+1] = (byte)text.charAt(i);
    }
    serialized[text.length()+1] = '\0';
    return serialized;
  }
}
