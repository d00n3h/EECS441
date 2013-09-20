package com.example.wewrite;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;

public class DocumentScreen extends Activity {

	CollabrifyUser user;
	CollabrifyDocument document;
	EditText	documentText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_screen);
		
		user = new CollabrifyUser();
		document = new CollabrifyDocument(); 
		documentText = (EditText)findViewById(R.id.editText1);
		documentText.setLongClickable(false);
		//documentText.setText(document.getText());
		
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
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}
	
	private void performAction( UserAction action ) {
		int cursorPosition;
		String text = document.getText();
		String part1, part2;
		if( action.getActionType().equals("add")) {
			part1 = text.substring(0, action.getCursorPosition() );
			part2 = text.substring(action.getCursorPosition());
			text = part1 + action.getCharAffected() + part2;
			cursorPosition = action.getCursorPosition() + 1;
			System.out.println("charAffected: " + action.getCharAffected() + " at index: " + action.getCursorPosition());
		} else{
			part1 = text.substring(0, action.getCursorPosition() );
			part2 = text.substring(action.getCursorPosition() + 1);
			text = part1 + part2;
			cursorPosition = action.getCursorPosition();
		}
		document.setText(text);
		System.out.println(document.getText());
		//documentText.setText(text);
		//documentText.setSelection(cursorPosition);
	}
	
	

}
