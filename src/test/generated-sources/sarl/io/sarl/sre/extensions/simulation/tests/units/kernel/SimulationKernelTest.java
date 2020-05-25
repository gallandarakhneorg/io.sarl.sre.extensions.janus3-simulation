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

import com.google.common.base.Objects;
import com.google.common.util.concurrent.Service;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.boot.configs.SreConfig;
import io.sarl.sre.extensions.simulation.boot.configs.SimulationConfig;
import io.sarl.sre.extensions.simulation.kernel.SimulationKernel;
import io.sarl.sre.services.IServiceManager;
import io.sarl.sre.services.context.ContextService;
import io.sarl.sre.services.executor.SreKernelRunnable;
import io.sarl.sre.services.lifecycle.LifecycleService;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import io.sarl.tests.api.tools.TestAssertions;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
public class SimulationKernelTest {
  @Nullable
  private IServiceManager serviceManager;
  
  @Nullable
  private Thread.UncaughtExceptionHandler exceptionHandler;
  
  @Nullable
  private Runnable engine;
  
  @Nullable
  private ExecutorService executorService;
  
  @Nullable
  private SimulationKernel kernel;
  
  @Nullable
  private LoggingService logging;
  
  @Nullable
  private LifecycleService lifecycle;
  
  @Nullable
  private ContextService context;
  
  @Nullable
  private SimulationConfig simulationConfig;
  
  @Nullable
  private SreConfig sreConfig;
  
  @BeforeEach
  public void setUp() {
    this.logging = Mockito.<LoggingService>mock(LoggingService.class);
    Mockito.<Logger>when(this.logging.getKernelLogger()).thenReturn(Mockito.<Logger>mock(Logger.class));
    Mockito.<Logger>when(this.logging.getPlatformLogger()).thenReturn(Mockito.<Logger>mock(Logger.class));
    this.lifecycle = Mockito.<LifecycleService>mock(LifecycleService.class);
    this.context = Mockito.<ContextService>mock(ContextService.class);
    this.serviceManager = Mockito.<IServiceManager>mock(IServiceManager.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      Class<?> type = ((Class<?>) _argument);
      boolean _equals = Objects.equal(LoggingService.class, type);
      if (_equals) {
        return this.logging;
      }
      boolean _equals_1 = Objects.equal(LifecycleService.class, type);
      if (_equals_1) {
        return this.lifecycle;
      }
      boolean _equals_2 = Objects.equal(ContextService.class, type);
      if (_equals_2) {
        return this.context;
      }
      throw new IllegalStateException();
    };
    Mockito.<Service>when(this.serviceManager.<Service>getService(ArgumentMatchers.<Class>any(Class.class))).thenAnswer(_function);
    this.exceptionHandler = Mockito.<Thread.UncaughtExceptionHandler>mock(Thread.UncaughtExceptionHandler.class);
    this.engine = Mockito.<Runnable>mock(Runnable.class);
    this.executorService = Mockito.<ExecutorService>mock(ExecutorService.class);
    this.simulationConfig = Mockito.<SimulationConfig>mock(SimulationConfig.class);
    this.sreConfig = Mockito.<SreConfig>mock(SreConfig.class);
    final Provider<IServiceManager> _function_1 = () -> {
      return this.serviceManager;
    };
    final Provider<Thread.UncaughtExceptionHandler> _function_2 = () -> {
      return this.exceptionHandler;
    };
    final Provider<SreConfig> _function_3 = () -> {
      return this.sreConfig;
    };
    final Provider<SimulationConfig> _function_4 = () -> {
      return this.simulationConfig;
    };
    SimulationKernel _simulationKernel = new SimulationKernel(_function_1, _function_2, _function_3, 
      this.engine, 
      this.executorService, _function_4);
    this.kernel = _simulationKernel;
  }
  
  @Test
  @DisplayName("startKernelAsync")
  public void startKernelAsync() {
    this.kernel.startKernelAsync(this.executorService);
    ArgumentCaptor<Runnable> arg = ArgumentCaptor.<Runnable, Runnable>forClass(Runnable.class);
    Mockito.<ExecutorService>verify(this.executorService, Mockito.times(1)).execute(arg.capture());
    Runnable executedTask = arg.getValue();
    TestAssertions.assertInstanceOf(SreKernelRunnable.class, executedTask);
    Assertions.assertSame(this.engine, ((SreKernelRunnable) executedTask).getSource());
  }
  
  @SuppressWarnings("unused_local_variable")
  @Test
  @DisplayName("startKernelSync")
  public void startKernelSync() {
    this.kernel.startKernelSync();
    ArgumentCaptor<Runnable> arg = ArgumentCaptor.<Runnable, Runnable>forClass(Runnable.class);
    Mockito.<Runnable>verify(this.engine, Mockito.times(1)).run();
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
  public SimulationKernelTest() {
    super();
  }
}
