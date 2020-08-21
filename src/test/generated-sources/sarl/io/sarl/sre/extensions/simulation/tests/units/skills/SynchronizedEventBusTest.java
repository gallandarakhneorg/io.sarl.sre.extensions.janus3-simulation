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
import io.sarl.sre.capacities.InternalSchedules;
import io.sarl.sre.extensions.simulation.skills.SynchronizedEventBus;
import io.sarl.sre.extensions.simulation.skills.TimestampedEvent;
import io.sarl.sre.internal.eventguard.IBehaviorGuardEvaluatorRegistry;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: SynchronizedEventBus test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.12")
@SarlElementType(10)
@SuppressWarnings("all")
public class SynchronizedEventBusTest {
  /**
   * @author $Author: sgalland$
   * @version $FullVersion$
   * @mavengroupid $GroupId$
   * @mavenartifactid $ArtifactId$
   */
  @SarlSpecification("0.12")
  @SarlElementType(10)
  public static class TestEvent extends TimestampedEvent {
    public TestEvent(final double time) {
      super(time);
      this.setSource(Mockito.<Address>mock(Address.class));
    }
    
    @SyntheticMember
    private static final long serialVersionUID = 1844972199L;
  }
  
  @Nullable
  private InternalSchedules taskScheduler;
  
  @Nullable
  private IBehaviorGuardEvaluatorRegistry dispatcher;
  
  @Nullable
  private SynchronizedEventBus eventBus;
  
  @Nullable
  private Logger logger;
  
  @BeforeEach
  public void setUp() {
    this.logger = Mockito.<Logger>mock(Logger.class);
    this.taskScheduler = Mockito.<InternalSchedules>mock(InternalSchedules.class);
    this.dispatcher = Mockito.<IBehaviorGuardEvaluatorRegistry>mock(IBehaviorGuardEvaluatorRegistry.class);
    final Supplier<InternalSchedules> _function = () -> {
      return this.taskScheduler;
    };
    SynchronizedEventBus _synchronizedEventBus = new SynchronizedEventBus(_function, this.dispatcher);
    this.eventBus = _synchronizedEventBus;
  }
  
  @Test
  @DisplayName("asyncDispatch standard event w/o move time")
  public void asyncDispatch_standardEvent_noMoveTime() {
    Event event = Mockito.<Event>mock(Event.class);
    Mockito.<Address>when(event.getSource()).thenReturn(Mockito.<Address>mock(Address.class));
    this.eventBus.asyncDispatch(event, this.logger);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("asyncDispatch standard event w/ move before time")
  public void asyncDispatch_standardEvent_moveToTimeBefore() {
    Event event = Mockito.<Event>mock(Event.class);
    Mockito.<Address>when(event.getSource()).thenReturn(Mockito.<Address>mock(Address.class));
    this.eventBus.moveToTime(1000.0);
    this.eventBus.asyncDispatch(event, this.logger);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("asyncDispatch standard event w/ move after time")
  public void asyncDispatch_standardEvent_moveToTimeAfter() {
    Event event = Mockito.<Event>mock(Event.class);
    Mockito.<Address>when(event.getSource()).thenReturn(Mockito.<Address>mock(Address.class));
    this.eventBus.asyncDispatch(event, this.logger);
    this.eventBus.moveToTime(1000.0);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("asyncDispatch stamped event w/o move time")
  public void asyncDispatch_timestampEvent_noMoveToTime() {
    SynchronizedEventBusTest.TestEvent event = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event, this.logger);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("asyncDispatch stamped event w/ move before time")
  public void asyncDispatch_timestampEvent_moveToTimeBefore() {
    SynchronizedEventBusTest.TestEvent event = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.moveToTime(1000.0);
    this.eventBus.asyncDispatch(event, this.logger);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("asyncDispatch stamped event w/ move after time")
  public void asyncDispatch_timestampEvent_moveToTimeAfter() {
    SynchronizedEventBusTest.TestEvent event = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event, this.logger);
    this.eventBus.moveToTime(1000.0);
    List<Event> events = this.eventBus.getTimedEvents();
    Iterator<Event> iterator = events.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertSame(event, iterator.next());
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    events = this.eventBus.getNotImmediatelyFirableEvents();
    iterator = events.iterator();
    Assertions.assertFalse(iterator.hasNext());
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus empty buffer w/o time move")
  public void fireBufferedEventsOnBus_emptyBuffer_noMoveToTime() {
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus empty buffer w/ time move")
  public void fireBufferedEventsOnBus_emptyBuffer_moveToTime() {
    this.eventBus.moveToTime(1000.0);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus 1 buffered standard event w/o time move")
  public void fireBufferedEventsOnBus_oneBufferedStandardEvent_noMoveToTime() {
    Event event0 = Mockito.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus 1 buffered standard event w/ time move")
  public void fireBufferedEventsOnBus_oneBufferedStandardEvent_moveToTime() {
    Event event0 = Mockito.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.moveToTime(1000.0);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.only()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #0, 1 buffered stamped event w/o time move")
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent0_noMoveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.only()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #0, 1 buffered stamped event w/ time move")
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent0_moveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.moveToTime(1000.0);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.only()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #1, 1 buffered stamped event w/o time move")
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent1_noMoveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123456.0));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #1, 1 buffered stamped event w/ time move")
  public void fireBufferedEventsOnBus_oneBufferedTimestampEvent1_moveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123456.0));
    this.eventBus.asyncDispatch(event0, this.logger);
    this.eventBus.moveToTime(1000.0);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.never()).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #0, 2 buffered stamped events w/o time move")
  public void fireBufferedEventsOnBus_twoBufferedTimestampEvent0_noMoveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    Event event1 = Mockito.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event1, this.logger);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.times(1)).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
  }
  
  @Test
  @DisplayName("fireBufferedEventsOnBus #0, 2 buffered stamped events w/ time move")
  public void fireBufferedEventsOnBus_twoBufferedTimestampEvent0_moveToTime() {
    SynchronizedEventBusTest.TestEvent event0 = Mockito.<SynchronizedEventBusTest.TestEvent>spy(new SynchronizedEventBusTest.TestEvent(123.456));
    this.eventBus.asyncDispatch(event0, this.logger);
    Event event1 = Mockito.<Event>mock(Event.class);
    this.eventBus.asyncDispatch(event1, this.logger);
    this.eventBus.moveToTime(1000.0);
    this.eventBus.fireBufferedEventsOnBus(1000.0);
    /* Mockito.<IBehaviorGuardEvaluatorRegistry>verify(this.dispatcher, Mockito.times(2)).getBehaviorGuardEvaluators(ArgumentMatchers.<Event>any(Event.class)); */
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
