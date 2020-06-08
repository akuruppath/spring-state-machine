package com.example.statemachine;

public enum Events {
    
  MARK_READY_FOR_CURRENT_SPRINT,  // BACKLOG -> T0_DO
  
  MARK_IN_PROGRESS, // TO_DO -> IN_PROGRESS
  
  MARK_READY_FOR_REVIEW, // IN_PROGRESS -> REVIEW
  
  MARK_DONE // REVIEW -> DONE
}
