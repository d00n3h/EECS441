package com.example.wewrite;

import java.util.Stack;

public class CollabrifyUser
{
  private Stack<UserAction> pastActions;
  private Stack<UserAction> undoneActions;
  
  public CollabrifyUser() {   
	  pastActions = new Stack<UserAction>();
	  undoneActions = new Stack<UserAction>();
  }
  
  public void addPastAction( UserAction action ) {
    pastActions.push(flipActionType(action));
  }
  
  public UserAction undoPastAction() {
    if(pastActions.empty()) {
      return null;
    }
    undoneActions.push(flipActionType(pastActions.peek()));
    return pastActions.pop();
  }
  
  public UserAction redoUndoneAction() {
    if(undoneActions.empty()) {
      return null;
    }
    pastActions.push(flipActionType(undoneActions.peek()));
    return undoneActions.pop();
  }
  
  private UserAction flipActionType( UserAction action ) {
    String type = new String();
    if(action.getActionType().equals("add")) {
      type = "del";
    } else {
      type = "add";
    }
    return new UserAction(type, action.getCursorPosition(), action.getCharAffected());
  }

}
