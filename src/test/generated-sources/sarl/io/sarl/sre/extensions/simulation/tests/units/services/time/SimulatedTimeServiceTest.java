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
package io.sarl.sre.extensions.simulation.tests.units.services.time;

import io.sarl.lang.annotation.DefaultValue;
import io.sarl.lang.annotation.DefaultValueSource;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSourceCode;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.boot.configs.SimulationConfig;
import io.sarl.sre.extensions.simulation.boot.configs.TimeConfig;
import io.sarl.sre.extensions.simulation.services.time.SimulatedTimeService;
import io.sarl.sre.internal.SequenceListenerNotifier;
import io.sarl.sre.internal.SmartListenerCollection;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.sre.services.time.TimeListener;
import io.sarl.sre.services.time.TimeService;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import io.sarl.tests.api.tools.TestAssertions;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: SimulatedTimeService test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.11")
@SarlElementType(10)
@SuppressWarnings("all")
public class SimulatedTimeServiceTest {
  @SarlSpecification("0.11")
  @SarlElementType(10)
  private static class MyTimeService extends SimulatedTimeService {
    private long ostime = 1500l;
    
    public long getOSCurrentTime() {
      return this.ostime;
    }
    
    @DefaultValueSource
    public boolean evolveTimeIfPossible(@DefaultValue("io.sarl.sre.extensions.simulation.tests.units.services.time.SimulatedTimeServiceTest$MyTimeService#EVOLVETIMEIFPOSSIBLE_0") final double timeDelta) {
      this.ostime = 1654;
      return super.evolveTimeIfPossible(timeDelta);
    }
    
    /**
     * Default value for the parameter timeDelta
     */
    @SyntheticMember
    @SarlSourceCode("0.0")
    private static final double $DEFAULT_VALUE$EVOLVETIMEIFPOSSIBLE_0 = 0.0;
    
    /**
     * Create a simulated time service.
     * 
     * @param loggingService the logging service.
     * @param config the accessor to the simulation configuration.
     * @param listeners the empty collection of listeners that must be used by this service.
     */
    @SyntheticMember
    @Inject
    public MyTimeService(final LoggingService loggingService, final SimulationConfig config, final SmartListenerCollection<TimeListener> listeners) {
      super(loggingService, config, listeners);
    }
    
    /**
     * Create a simulated time service.
     * 
     * @param loggingService the logging service.
     * @param config the accessor to the time configuration.
     * @param listeners the empty collection of listeners that must be used by this service.
     */
    @SyntheticMember
    public MyTimeService(final LoggingService loggingService, final TimeConfig config, final SmartListenerCollection<TimeListener> listeners) {
      super(loggingService, config, listeners);
    }
  }
  
  @Nullable
  private SimulatedTimeService service;
  
  @Nullable
  private TimeListener listener;
  
  @Nullable
  private LoggingService logger;
  
  @Nullable
  private TimeConfig config;
  
  @BeforeEach
  public void setUp() {
    this.logger = Mockito.<LoggingService>mock(LoggingService.class);
    Mockito.<Logger>when(this.logger.getKernelLogger()).thenReturn(Mockito.<Logger>mock(Logger.class));
    this.config = Mockito.<TimeConfig>mock(TimeConfig.class);
    Mockito.<Boolean>when(Boolean.valueOf(this.config.isTimeProgressionInLogs())).thenReturn(Boolean.valueOf(false));
    Mockito.<TimeUnit>when(this.config.getUnit()).thenReturn(TimeUnit.SECONDS);
    this.listener = Mockito.<TimeListener>mock(TimeListener.class);
    SequenceListenerNotifier _sequenceListenerNotifier = new SequenceListenerNotifier();
    final SmartListenerCollection<TimeListener> coll = new SmartListenerCollection<TimeListener>(_sequenceListenerNotifier);
    SimulatedTimeServiceTest.MyTimeService _myTimeService = new SimulatedTimeServiceTest.MyTimeService(this.logger, this.config, coll);
    this.service = _myTimeService;
    this.service.addTimeListener(this.listener);
  }
  
  @Test
  @DisplayName("getTime before time evolution")
  public void getTime_beforeEvolution() {
    TestAssertions.assertEpsilonEquals(0.0, this.service.getTime(TimeUnit.MINUTES));
    TestAssertions.assertEpsilonEquals(0.0, this.service.getTime(TimeUnit.SECONDS));
    TestAssertions.assertEpsilonEquals(0.0, this.service.getTime(TimeUnit.MILLISECONDS));
    Mockito.verifyNoInteractions(this.listener);
  }
  
  @Test
  @DisplayName("getTime after time evolution")
  public void getTime_afterEvolution() {
    this.service.evolveTimeIfPossible(4);
    TestAssertions.assertEpsilonEquals(0.066, this.service.getTime(TimeUnit.MINUTES));
    TestAssertions.assertEpsilonEquals(4, this.service.getTime(TimeUnit.SECONDS));
    TestAssertions.assertEpsilonEquals(4000.0, this.service.getTime(TimeUnit.MILLISECONDS));
    ArgumentCaptor<TimeService> serviceCaptor = ArgumentCaptor.<TimeService, TimeService>forClass(TimeService.class);
    Mockito.<TimeListener>verify(this.listener, Mockito.times(1)).timeChanged(serviceCaptor.capture());
    Assertions.assertSame(this.service, serviceCaptor.getValue());
  }
  
  @Test
  @DisplayName("getOSTimeFactor before time evolution")
  public void getOSTimeFactor_beforeEvolution() {
    TestAssertions.assertEpsilonEquals(1.0, this.service.getOSTimeFactor());
    Mockito.verifyNoInteractions(this.listener);
  }
  
  @Test
  @DisplayName("evolveTimeIfPossible")
  public void evolveTimeIfPossible() {
    this.service.evolveTimeIfPossible(15);
    ArgumentCaptor<TimeService> serviceCaptor = ArgumentCaptor.<TimeService, TimeService>forClass(TimeService.class);
    Mockito.<TimeListener>verify(this.listener).timeChanged(serviceCaptor.capture());
    Assertions.assertSame(this.service, serviceCaptor.getValue());
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
  public SimulatedTimeServiceTest() {
    super();
  }
}
