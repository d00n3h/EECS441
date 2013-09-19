package com.example.wewrite;

public class CollabrifyDocument
{
  private String text;
  private int length;

  public CollabrifyDocument(String text, int length) {
    this.text = text;
    this.length = length;
  }
  
  public void performAction( CollabrifyUser user, UserAction action ) {
    user.addPastAction(action);
    if(action.getActionType().equals("add")) {
      
    } else if(action.getActionType().equals("del")) {
      
    }
  }
}
