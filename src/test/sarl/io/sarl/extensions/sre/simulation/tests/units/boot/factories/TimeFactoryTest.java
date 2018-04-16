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

package io.sarl.extensions.sre.simulation.tests.units.boot.factories;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.sarl.sre.tests.testutils.AbstractSreTest;
import io.bootique.config.ConfigurationFactory;
import io.sarl.extensions.sre.simulation.boot.factories.TimeFactory;
import io.sarl.lang.core.Agent;
import io.sarl.sre.Kernel;
import io.sarl.sre.boot.factories.NoBootAgentNameException;
import io.sarl.sre.boot.factories.RootContextType;
import io.sarl.sre.services.IServiceManager;
import io.sarl.sre.services.context.ContextService;
import io.sarl.sre.services.context.Context;
import io.sarl.sre.services.lifecycle.LifecycleService;
import io.sarl.sre.services.lifecycle.SpawnResult;
import io.sarl.sre.services.logging.LoggerCreator;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.tests.api.Nullable;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class TimeFactoryTest extends AbstractSreTest {

	@Nullable
	private TimeFactory factory;
	
	@Before
	public void setUp() {
		this.factory = new TimeFactory();
	}
	
	@Test
	public void getConfigurationFactory() {
		ConfigurationFactory factory = mock(ConfigurationFactory.class);
		TimeFactory executorFactory = mock(TimeFactory.class);
		when(factory.config(any(Class.class), any(String.class))).thenReturn(executorFactory);
		assertSame(executorFactory, TimeFactory.getConfigurationFactory(factory));
		ArgumentCaptor<Class<?>> arg0 = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		verify(factory, only()).config(arg0.capture(), arg1.capture());
		assertEquals(TimeFactory.class, arg0.getValue());
		assertEquals(TimeFactory.TIME_PREFIX, arg1.getValue());
	}

	@Test
	public void getTimeStep() {
		assertEpsilonEquals(TimeFactory.TIME_STEP_VALUE, this.factory.getTimeStep());
	}

	@Test
	public void setTimeStep() {
		assertEpsilonEquals(TimeFactory.TIME_STEP_VALUE, this.factory.getTimeStep());
		this.factory.setTimeStep(123.456);
		assertEpsilonEquals(123.456, this.factory.getTimeStep());
	}

	@Test
	public void getStartTime() {
		assertEpsilonEquals(TimeFactory.START_TIME_VALUE, this.factory.getStartTime());
	}

	@Test
	public void setStartTime() {
		assertEpsilonEquals(TimeFactory.START_TIME_VALUE, this.factory.getStartTime());
		this.factory.setStartTime(123.456);
		assertEpsilonEquals(123.456, this.factory.getStartTime());
	}

	@Test
	public void getSimulationLoopDelay() {
		assertEquals(TimeFactory.SIMULATION_LOOP_STEP_DELAY_VALUE, this.factory.getSimulationLoopDelay());
	}

	@Test
	public void setSimulationLoopDelay() {
		assertEquals(TimeFactory.SIMULATION_LOOP_STEP_DELAY_VALUE, this.factory.getSimulationLoopDelay());
		this.factory.setSimulationLoopDelay(123456);
		assertEquals(123456l, this.factory.getSimulationLoopDelay());
	}

	@Test
	public void getUnit() {
		assertEquals(TimeUnit.SECONDS, this.factory.getUnit());
	}

	@Test
	public void setUnit() {
		assertEquals(TimeUnit.SECONDS, this.factory.getUnit());
		for (TimeUnit tu : TimeUnit.values()) {
			this.factory.setUnit(tu);
			assertSame(tu, this.factory.getUnit());
		}
	}

}
