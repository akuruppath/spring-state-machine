package com.example.statemachine;

public enum States {
  
  BACKLOG, // task still in backlog
  
  TO_DO, // work to be started
  
  IN_PROGRESS, // work in progress
  
  REVIEW, // work to be reviewed
  
  DONE // work finished

}
