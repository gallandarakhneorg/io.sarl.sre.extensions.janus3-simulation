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
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.sarl.extensions.sre.simulation.boot.factories.TimeFactory;
import io.sarl.extensions.sre.simulation.engine.RunnableSynchronousEngine;
import io.sarl.extensions.sre.simulation.services.lifecycle.SimulationLifecycleService;
import io.sarl.extensions.sre.simulation.skills.TimestampedEvent;
import io.sarl.lang.core.Agent;
import io.sarl.sre.services.lifecycle.LifecycleService;
import io.sarl.sre.services.time.TimeService;
import io.sarl.sre.tests.testutils.AbstractSreTest;
import io.sarl.tests.api.Nullable;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class TimestampedEventTest extends AbstractSreTest {

	@Nullable
	private TimestampedEvent event;

	@Before
	public void setUp() {
		this.event = new TimestampedEvent(123.456);
	}

	@Test
	public void getTimestamp() {
		assertEpsilonEquals(123.456, this.event.timestamp);
	}
	
}
