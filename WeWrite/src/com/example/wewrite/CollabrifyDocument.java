package com.example.wewrite;

public class CollabrifyDocument
{
  private String text;

  public CollabrifyDocument() {
	  this.text = new String("");
  }
  
  public void performAction( CollabrifyUser user, UserAction action ) {
    user.addPastAction(action);
    if(action.getActionType().equals("add")) {
      
    } else if(action.getActionType().equals("del")) {
      
    }
  }
  
  public String getText() {
	return text;
  }

	public void setText(String text) {
		this.text = text;
	}
	
}
