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
package io.sarl.sre.extensions.simulation.tests.units.skills;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Event;
import io.sarl.revision.BehaviorGuardEvaluatorRegistry;
import io.sarl.sre.extensions.simulation.skills.SynchronizedEventBus;
import io.sarl.sre.extensions.simulation.skills.TimestampedEvent;
import io.sarl.sre.services.executor.ExecutorService;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import java.util.Iterator;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@SarlSpecification("0.10")
@SarlElementType(10)
public class SynchronizedEventBusTest extends AbstractSarlTest {
  /**
   * @author $Author: sgalland$
   * @version $FullVersion$
   * @mavengroupid $GroupId$
   * @mavenartifactid $ArtifactId$
   */
  @SarlSpecification("0.10")
  @SarlElementType(10)
  public static class TestEvent extends TimestampedEvent {
    public TestEvent(final double time) {
      super(time);
      this.setSource(AbstractSarlTest.<Address>mock(Address.class));
    }
    
    @SyntheticMember
    private static final long serialVersionUID = 1844972199L;
  }
  
  @Nullable
  private ExecutorService executor;
  
  @Nullable
  private BehaviorGuardEvaluatorRegistry dispatcher;
  
  @Nullable
  private SynchronizedEventBus eventBus;
  
  @Nullable
  private Logger logger;
  
  @Before
  public void setUp() {
    this.logger = AbstractSarlTest.<Logger>mock(Logger.class);
    this.executor = AbstractSarlTest.<ExecutorService>mock(ExecutorService.class);
    this.dispatcher = AbstractSarlTest.<BehaviorGuardEvaluatorRegistry>mock(BehaviorGuardEvaluatorRegistry.class);
    SynchronizedEventBus _synchronizedEventBus = new SynchronizedEventBus(this.executor, this.dispatcher);
    this.eventBus = _synchronizedEventBus;
  }
  
  @Test
  public void asyncDispatch_standardEvent() {
    Event event = AbstractSarlTest.<Event>mock(Event.class);
    Mockito.<Address>when(event.getSource()).thenReturn(AbstractSarlTest.<Address>mock(Address.class));
    this.eventBus.asyncDispatch(event, this.logger);
    Iterable<Event> events = this.eventBus.getBufferedEvents();
    Iterator<Event> iterator = events.iterator();
    Assert.assertTrue(iterator.hasNext());
    Assert.assertSame(event, iterator.next());
    Assert.assertFalse(iterator.hasNext());
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void asyncDispatch_timestampEvent() {
    SynchronizedEventBusTest.TestEvent event = AbstractSarlTest.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event, this.logger);
    Iterable<Event> events = this.eventBus.getBufferedEvents();
    Iterator<Event> iterator = events.iterator();
    Assert.assertTrue(iterator.hasNext());
    Assert.assertSame(event, iterator.next());
    Assert.assertFalse(iterator.hasNext());
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void fireBufferedEventsOnBus_emptyBuffer() {
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void fireBufferedEventsOnBus_oneBufferedStandardEvent() {
    Event event0 = AbstractSarlTest.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.only()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent0() {
    SynchronizedEventBusTest.TestEvent event0 = AbstractSarlTest.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.only()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent1() {
    SynchronizedEventBusTest.TestEvent event0 = AbstractSarlTest.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123456.0));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
  }
  
  @Test
  public void fireBufferedEventsOnBus_twoBufferedTimestampEvent0() {
    SynchronizedEventBusTest.TestEvent event0 = AbstractSarlTest.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    Event event1 = AbstractSarlTest.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event1, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    Mockito.<BehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.times(2)).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class));
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
  public SynchronizedEventBusTest() {
    super();
  }
}
