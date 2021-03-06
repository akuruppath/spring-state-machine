package com.example.statemachine;

import java.util.EnumSet;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<States, Events> {


  @Override
  public void configure(StateMachineConfigurationConfigurer<States, Events> config)
      throws Exception {
    config.withConfiguration().listener(listener()).autoStartup(true);
  }

  @Override
  public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
    states.withStates().initial(States.BACKLOG).states(EnumSet.allOf(States.class));
  }


  @Override
  public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
      throws Exception {

    /* @formatter:off */
    transitions.withExternal()
    
        .source(States.BACKLOG)
        .target(States.TO_DO)
        .event(Events.MARK_READY_FOR_CURRENT_SPRINT)
        
        .and().withExternal()
        
        .source(States.TO_DO)
        .target(States.IN_PROGRESS)
        .event(Events.MARK_IN_PROGRESS)
        
        .and().withExternal()
        
        .source(States.IN_PROGRESS)
        .target(States.REVIEW)
        .event(Events.MARK_READY_FOR_REVIEW)
        .action(reviewAction())
        
        .and().withExternal()
        
        .source(States.REVIEW)
        .target(States.DONE)
        .event(Events.MARK_DONE)
        .guard(checkReviewGuard())
        .action(mergeAction())
        
        .and().withExternal()
        
        .source(States.DONE)
        .target(States.IN_PROGRESS)
        .event(Events.MARK_IN_PROGRESS)
        .action(undoReviewAction())
        .action(inProgressAction())
        
        .and().withExternal()
        .source(States.REVIEW)
        .target(States.IN_PROGRESS)
        .event(Events.MARK_IN_PROGRESS)
        .action(undoReviewAction())
        .action(inProgressAction());
    
    /* @formatter:on */
  }


  private StateMachineListener<States, Events> listener() {

    return new StateMachineListenerAdapter<States, Events>() {

      @Override
      public void eventNotAccepted(Message<Events> event) {
        log.error("Event [{}] not acceptable.", event);
      }

      @Override
      public void transition(Transition<States, Events> transition) {
        log.warn("Transition from:[{}] to:[{}]", ofNullableState(transition.getSource()),
            ofNullableState(transition.getTarget()));
      }

      private States ofNullableState(State<States, Events> s) {
        return Optional.ofNullable(s).map(State::getId).orElse(null);
      }
    };
  }

  private Action<States, Events> reviewAction() {
    log.warn("APPROVED FOR MERGE.");
    return context -> context.getExtendedState().getVariables().put("approved", true);
  }

  private Action<States, Events> undoReviewAction() {
    log.warn("NEEDS RE-REVIEW.");
    return context -> context.getExtendedState().getVariables().put("approved", false);
  }

  private Action<States, Events> mergeAction() {
    return context -> log.warn("MERGING INTO THE MASTER BRANCH: {}", context.getEvent());
  }

  private Action<States, Events> inProgressAction() {
    return context -> log.warn("MORE WORK REQUIRED: {}", context.getEvent());
  }

  private Guard<States, Events> checkReviewGuard() {
    log.warn("Checking whether a review was performed ... ");
    return context -> {
      Boolean reviewed = (Boolean) context.getExtendedState().getVariables().get("approved");
      return reviewed == null ? false : reviewed;
    };
  }

}
