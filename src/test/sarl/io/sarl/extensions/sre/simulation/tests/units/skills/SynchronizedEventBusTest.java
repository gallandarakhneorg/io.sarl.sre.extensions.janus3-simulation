/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2018 the original authors or authors.
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

package io.sarl.extensions.sre.simulation.tests.units.skills;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import io.sarl.extensions.sre.simulation.skills.SynchronizedEventBus;
import io.sarl.extensions.sre.simulation.skills.TimestampedEvent;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Event;
import io.sarl.revision.BehaviorGuardEvaluatorRegistry;
import io.sarl.sre.services.executor.ExecutorService;
import io.sarl.sre.tests.testutils.AbstractSreTest;
import io.sarl.tests.api.Nullable;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class SynchronizedEventBusTest extends AbstractSreTest {

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
		this.logger = mock(Logger.class);
		this.executor = mock(ExecutorService.class);
		this.dispatcher = mock(BehaviorGuardEvaluatorRegistry.class);
		this.eventBus = new SynchronizedEventBus(this.executor, this.dispatcher);
	}

	@Test
	public void asyncDispatch_standardEvent() {
		Event event = mock(Event.class);
		when(event.getSource()).thenReturn(mock(Address.class));

		this.eventBus.asyncDispatch(event, this.logger);

		Iterable<Event> events = this.eventBus.getBufferedEvents();
		Iterator<Event> iterator = events.iterator();
		assertTrue(iterator.hasNext());
		assertSame(event, iterator.next());
		assertFalse(iterator.hasNext());

		verify(this.dispatcher, never()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void asyncDispatch_timestampEvent() {
		Event event = spy(new TestEvent(123.456));

		this.eventBus.asyncDispatch(event, this.logger);

		Iterable<Event> events = this.eventBus.getBufferedEvents();
		Iterator<Event> iterator = events.iterator();
		assertTrue(iterator.hasNext());
		assertSame(event, iterator.next());
		assertFalse(iterator.hasNext());

		verify(this.dispatcher, never()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void fireBufferedEventsOnBus_emptyBuffer() {
		this.eventBus.fireBufferedEventsOnBus(1000.);
		verify(this.dispatcher, never()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void fireBufferedEventsOnBus_oneBufferedStandardEvent() {
		Event event0 = mock(Event.class);
		this.eventBus.asyncDispatch(event0, this.logger);

		this.eventBus.fireBufferedEventsOnBus(1000.);
		verify(this.dispatcher, only()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void fireBufferedEventsOnBus_oneBufferedTimestampEvent0() {
		Event event0 = spy(new TestEvent(123.456));
		this.eventBus.asyncDispatch(event0, this.logger);

		this.eventBus.fireBufferedEventsOnBus(1000.);
		verify(this.dispatcher, only()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void fireBufferedEventsOnBus_oneBufferedTimestampEvent1() {
		Event event0 = spy(new TestEvent(123456.));
		this.eventBus.asyncDispatch(event0, this.logger);

		this.eventBus.fireBufferedEventsOnBus(1000.);
		verify(this.dispatcher, never()).getBehaviorGuardEvaluators(any(Event.class));
	}

	@Test
	public void fireBufferedEventsOnBus_twoBufferedTimestampEvent0() {
		Event event0 = spy(new TestEvent(123.456));
		this.eventBus.asyncDispatch(event0, this.logger);
		Event event1 = mock(Event.class);
		this.eventBus.asyncDispatch(event1, this.logger);

		this.eventBus.fireBufferedEventsOnBus(1000.);
		verify(this.dispatcher, times(2)).getBehaviorGuardEvaluators(any(Event.class));
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class TestEvent extends TimestampedEvent {

		public TestEvent(double time) {
			super(time);
			setSource(mock(Address.class));
		}

	}

}
