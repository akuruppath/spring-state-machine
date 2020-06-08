package com.example.statemachine;

import java.util.EnumSet;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<States, Events> {


  @Override
  public void configure(StateMachineConfigurationConfigurer<States, Events> config)
      throws Exception {
    config.withConfiguration().autoStartup(true);
  }

  @Override
  public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
    states.withStates().initial(States.BACKLOG).states(EnumSet.allOf(States.class));
  }


  @Override
  public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
      throws Exception {
    transitions.withExternal().source(States.BACKLOG).target(States.TO_DO)
        .event(Events.MARK_READY_FOR_CURRENT_SPRINT).and().withExternal().source(States.TO_DO)
        .target(States.IN_PROGRESS).event(Events.MARK_IN_PROGRESS).and().withExternal()
        .source(States.IN_PROGRESS).target(States.REVIEW).event(Events.MARK_READY_FOR_REVIEW).and()
        .withExternal().source(States.REVIEW).target(States.DONE).event(Events.MARK_DONE);
  }

}
