package com.example.statemachine;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

@SpringBootTest
class StateMachineTest {

  @Autowired
  private StateMachine<States, Events> stateMachine;
  
  @Test
  void testInitStateMachine() {
    
     assertThat(stateMachine).isNotNull();
     assertThat(stateMachine.getInitialState().getId()).isEqualTo(States.BACKLOG);
  }

  @Test
  void testStateMachineTransitions() {
    
    stateMachine.sendEvent(Events.MARK_READY_FOR_CURRENT_SPRINT);
    stateMachine.sendEvent(Events.MARK_IN_PROGRESS);
    stateMachine.sendEvent(Events.MARK_READY_FOR_REVIEW);
    stateMachine.sendEvent(Events.MARK_DONE);
    
    assertThat(stateMachine.getState().getId()).isEqualTo(States.DONE);
  }

}
