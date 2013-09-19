package com.example.wewrite;

public class UserAction
{
  private String actionType;
  private int cursorPosition;
  private char charAffected;
  
  public UserAction(String actionType, int cursorPosition, char charAffected) {
    this.actionType = actionType;
    this.cursorPosition = cursorPosition;
    this.charAffected = charAffected;
  }
  
  public String getActionType() {
    return actionType;
  }
  
  public int getCursorPosition() {
    return cursorPosition;
  }
  
  public char getCharAffected() {
    return charAffected;
  }
  
  public void setActionType( String actionType ) {
    this.actionType = actionType;
  }
  
  public void setCursorPosition( int cursorPosition ) {
    this.cursorPosition = cursorPosition;
  }
  
  public void setCharAffected( char charAffected ) {
    this.charAffected = charAffected;
  }
  
}
