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
import io.bootique.config.ConfigurationFactory;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.boot.configs.SimulationConfig;
import io.sarl.sre.extensions.simulation.kernel.SimulationKernel;
import io.sarl.sre.services.IServiceManager;
import io.sarl.sre.services.context.ContextService;
import io.sarl.sre.services.lifecycle.LifecycleService;
import io.sarl.sre.services.logging.LoggingService;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
@SuppressWarnings("all")
@SarlSpecification("0.10")
@SarlElementType(10)
public class SimulationKernelTest extends AbstractSarlTest {
  @Nullable
  private IServiceManager serviceManager;
  
  @Nullable
  private Thread.UncaughtExceptionHandler exceptionHandler;
  
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
  private SimulationConfig simulationConfig;
  
  @Before
  public void setUp() {
    this.logging = AbstractSarlTest.<LoggingService>mock(LoggingService.class);
    Mockito.<Logger>when(this.logging.getKernelLogger()).thenReturn(AbstractSarlTest.<Logger>mock(Logger.class));
    Mockito.<Logger>when(this.logging.getPlatformLogger()).thenReturn(AbstractSarlTest.<Logger>mock(Logger.class));
    this.lifecycle = AbstractSarlTest.<LifecycleService>mock(LifecycleService.class);
    this.context = AbstractSarlTest.<ContextService>mock(ContextService.class);
    this.serviceManager = AbstractSarlTest.<IServiceManager>mock(IServiceManager.class);
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
    this.exceptionHandler = AbstractSarlTest.<Thread.UncaughtExceptionHandler>mock(Thread.UncaughtExceptionHandler.class);
    this.engine = AbstractSarlTest.<Runnable>mock(Runnable.class);
    this.executorService = AbstractSarlTest.<ExecutorService>mock(ExecutorService.class);
    this.simulationConfig = AbstractSarlTest.<SimulationConfig>mock(SimulationConfig.class);
    this.configurationFactory = AbstractSarlTest.<ConfigurationFactory>mock(ConfigurationFactory.class);
    Mockito.<Object>when(this.configurationFactory.<Object>config(ArgumentMatchers.<Class>any(Class.class), ArgumentMatchers.anyString())).thenReturn(this.simulationConfig);
    SimulationKernel _simulationKernel = new SimulationKernel(this.serviceManager, this.exceptionHandler, 
      this.engine, this.executorService, this.configurationFactory);
    this.kernel = _simulationKernel;
  }
  
  @Test
  public void startKernelThread() {
    this.kernel.startKernelThread(this.executorService);
    ArgumentCaptor<Runnable> arg = ArgumentCaptor.<Runnable, Runnable>forClass(Runnable.class);
    Mockito.<ExecutorService>verify(this.executorService, Mockito.times(1)).execute(arg.capture());
    Assert.assertSame(this.kernel, arg.getValue());
  }
  
  @Test
  public void run() {
    this.kernel.run();
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
