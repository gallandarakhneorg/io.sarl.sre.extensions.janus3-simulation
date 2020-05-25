/**
 * $Id$
 * 
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 * 
 * Copyright (C) 2014-2019 the original authors or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.sre.extensions.simulation.tests.units.kernel;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Agent;
import io.sarl.sre.extensions.simulation.boot.configs.TimeConfig;
import io.sarl.sre.extensions.simulation.kernel.RunnableSynchronousEngine;
import io.sarl.sre.extensions.simulation.kernel.SynchronousEngineExternalController;
import io.sarl.sre.extensions.simulation.schedule.AgentScheduler;
import io.sarl.sre.extensions.simulation.services.executor.SynchronousExecutorService;
import io.sarl.sre.extensions.simulation.services.lifecycle.SimulationLifecycleService;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.sre.services.time.TimeService;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.sre.test.framework.mockito.NativeDoubleArgumentCaptor;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import io.sarl.tests.api.tools.TestAssertions;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: RunnableSynchronousEngine test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.11")
@SarlElementType(10)
@SuppressWarnings("all")
public class RunnableSynchronousEngineTest {
  @Nullable
  private RunnableSynchronousEngine engine;
  
  @Nullable
  private TimeConfig timeConfig;
  
  @Nullable
  private AgentScheduler agentScheduler;
  
  @Nullable
  private SimulationLifecycleService lifecycleService;
  
  @Nullable
  private TimeService timeService;
  
  @Nullable
  private LoggingService loggingService;
  
  @Nullable
  private SynchronousEngineExternalController externalController;
  
  @Nullable
  private Iterable<Agent> agents;
  
  @BeforeEach
  public void setUp() {
    this.agentScheduler = Mockito.<AgentScheduler>mock(AgentScheduler.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      return ((Iterable<? extends Agent>) _argument).iterator();
    };
    Mockito.<Iterator<Agent>>when(this.agentScheduler.schedule(ArgumentMatchers.<Iterable>any(Iterable.class))).thenAnswer(_function);
    Iterator iterator = Mockito.<Iterator>mock(Iterator.class);
    Mockito.<Boolean>when(Boolean.valueOf(iterator.hasNext())).thenReturn(Boolean.valueOf(false));
    this.agents = Mockito.<Iterable>mock(Iterable.class);
    Mockito.<Iterator<Agent>>when(this.agents.iterator()).thenReturn(iterator);
    this.timeConfig = Mockito.<TimeConfig>mock(TimeConfig.class);
    Mockito.<Double>when(Double.valueOf(this.timeConfig.getStartTime())).thenReturn(Double.valueOf(12.34));
    Mockito.<Double>when(Double.valueOf(this.timeConfig.getTimeStep())).thenReturn(Double.valueOf(90.12));
    this.lifecycleService = Mockito.<SimulationLifecycleService>mock(SimulationLifecycleService.class);
    final AtomicBoolean bool = new AtomicBoolean(true);
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      return Boolean.valueOf(bool.getAndSet(false));
    };
    Mockito.<Boolean>when(Boolean.valueOf(this.lifecycleService.hasAgent())).thenAnswer(_function_1);
    Mockito.<Iterable<Agent>>when(this.lifecycleService.getAgents()).thenReturn(this.agents);
    this.timeService = Mockito.<TimeService>mock(TimeService.class);
    Logger logger = Mockito.<Logger>mock(Logger.class);
    this.loggingService = Mockito.<LoggingService>mock(LoggingService.class);
    Mockito.<Logger>when(this.loggingService.getKernelLogger()).thenReturn(logger);
    this.externalController = Mockito.<SynchronousEngineExternalController>mock(SynchronousEngineExternalController.class);
    Mockito.<Boolean>when(Boolean.valueOf(this.externalController.isRunning())).thenReturn(Boolean.valueOf(true));
    Mockito.<Boolean>when(Boolean.valueOf(this.externalController.isStopped())).thenReturn(Boolean.valueOf(false));
    SynchronousExecutorService _mock = Mockito.<SynchronousExecutorService>mock(SynchronousExecutorService.class);
    RunnableSynchronousEngine _runnableSynchronousEngine = new RunnableSynchronousEngine(
      this.agentScheduler, this.timeService, this.timeConfig, 
      this.lifecycleService, _mock, 
      this.loggingService, 
      this.externalController);
    this.engine = _runnableSynchronousEngine;
  }
  
  protected OngoingStubbing<Long> applyDelay() {
    return Mockito.<Long>when(Long.valueOf(this.timeConfig.getSimulationLoopDelay())).thenReturn(Long.valueOf(1l));
  }
  
  protected OngoingStubbing<Long> applyNoDelay() {
    return Mockito.<Long>when(Long.valueOf(this.timeConfig.getSimulationLoopDelay())).thenReturn(Long.valueOf(0l));
  }
  
  @Test
  @DisplayName("run with delay")
  public void run_delay() {
    this.applyDelay();
    this.engine.run();
    Mockito.<SimulationLifecycleService>verify(this.lifecycleService, Mockito.times(1)).synchronizeAgentList();
    NativeDoubleArgumentCaptor doubleArg = NativeDoubleArgumentCaptor.forPrimitive();
    Mockito.<TimeService>verify(this.timeService, Mockito.times(1)).evolveTimeIfPossible(doubleArg.capture());
    TestAssertions.assertEpsilonEquals(90.12, doubleArg.getValue());
    doubleArg = NativeDoubleArgumentCaptor.forPrimitive();
    Mockito.<TimeService>verify(this.timeService, Mockito.times(1)).setTimeIfPossible(doubleArg.capture());
    double _value = doubleArg.getValue();
    TestAssertions.assertEpsilonEquals(12.34, _value);
  }
  
  @SuppressWarnings("unused_local_variable")
  @Test
  @DisplayName("run without delay")
  public void run_noDelay() {
    this.applyNoDelay();
    this.engine.run();
    Mockito.<SimulationLifecycleService>verify(this.lifecycleService, Mockito.times(1)).synchronizeAgentList();
    NativeDoubleArgumentCaptor doubleArg = NativeDoubleArgumentCaptor.forPrimitive();
    boolean r = Mockito.<TimeService>verify(this.timeService, Mockito.times(1)).setTimeIfPossible(doubleArg.capture());
    TestAssertions.assertEpsilonEquals(12.34, doubleArg.getValue());
    doubleArg = NativeDoubleArgumentCaptor.forPrimitive();
    Mockito.<TimeService>verify(this.timeService, Mockito.times(1)).evolveTimeIfPossible(doubleArg.capture());
    TestAssertions.assertEpsilonEquals(90.12, doubleArg.getValue());
  }
  
  @Override
  @Pure
  @SyntheticMember
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    return result;
  }
  
  @SyntheticMember
  public RunnableSynchronousEngineTest() {
    super();
  }
}
