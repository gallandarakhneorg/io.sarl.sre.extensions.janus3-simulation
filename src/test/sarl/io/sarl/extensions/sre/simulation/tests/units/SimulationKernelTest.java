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

package io.sarl.extensions.sre.simulation.tests.units;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.bootique.config.ConfigurationFactory;
import io.sarl.extensions.sre.simulation.SimulationKernel;
import io.sarl.extensions.sre.simulation.boot.factories.SimulationKernelFactory;
import io.sarl.sre.services.IServiceManager;
import io.sarl.sre.services.context.ContextService;
import io.sarl.sre.services.lifecycle.LifecycleService;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.sre.tests.testutils.AbstractSreTest;
import io.sarl.tests.api.Nullable;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class SimulationKernelTest extends AbstractSreTest {

	@Nullable
	private IServiceManager serviceManager;

	@Nullable
	private UncaughtExceptionHandler exceptionHandler;

	@Nullable
	private Runnable engine;

	@Nullable
	private ExecutorService executorService;

	@Nullable
	private ConfigurationFactory configurationFactory;

	@Nullable
	private SimulationKernel kernel;

	@Nullable
	private LoggingService logging;

	@Nullable
	private LifecycleService lifecycle;

	@Nullable
	private ContextService context;

	@Nullable
	private SimulationKernelFactory simulationKernelFactory;

	@Before
	public void setUp() {
		this.logging = mock(LoggingService.class);
		when(this.logging.getKernelLogger()).thenReturn(mock(Logger.class));
		when(this.logging.getPlatformLogger()).thenReturn(mock(Logger.class));

		this.lifecycle = mock(LifecycleService.class);
		this.context = mock(ContextService.class);

		this.serviceManager = mock(IServiceManager.class);
		when(this.serviceManager.getService(any(Class.class))).thenAnswer((it) -> {
			Class<?> type = it.getArgument(0);
			if (LoggingService.class.equals(type)) {
				return this.logging;
			}
			if (LifecycleService.class.equals(type)) {
				return this.lifecycle;
			}
			if (ContextService.class.equals(type)) {
				return this.context;
			}
			throw new IllegalStateException();
		});

		this.exceptionHandler = mock(UncaughtExceptionHandler.class);

		this.engine = mock(Runnable.class);

		this.executorService = mock(ExecutorService.class);

		this.simulationKernelFactory = mock(SimulationKernelFactory.class);
		
		this.configurationFactory = mock(ConfigurationFactory.class);
		when(this.configurationFactory.config(any(Class.class), any(String.class))).thenReturn(this.simulationKernelFactory);
		
		this.kernel = new SimulationKernel(this.serviceManager, this.exceptionHandler,
				this.engine, this.executorService, this.configurationFactory);
	}

	@Test
	public void startKernelThread() {
		this.kernel.startKernelThread(this.executorService);
		ArgumentCaptor<Runnable> arg = ArgumentCaptor.forClass(Runnable.class);
		verify(this.executorService, times(1)).execute(arg.capture());
		assertSame(this.kernel, arg.getValue());
	}

	@Test
	public void run() {
		this.kernel.run();
		verify(this.engine, times(1)).run();
	}

}
