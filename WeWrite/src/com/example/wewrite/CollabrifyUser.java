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
    pastActions.push(action);
  }
  
  public UserAction undoPastAction() {
    undoneActions.push(pastActions.peek());
    return pastActions.pop();
  }
  
  public UserAction redoUndoneAction() {
    pastActions.push(undoneActions.peek());
    return undoneActions.pop();
  }

}
