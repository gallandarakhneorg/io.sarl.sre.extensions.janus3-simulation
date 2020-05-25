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
package io.sarl.sre.extensions.simulation.tests.units.services.executor;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.services.executor.SreScheduledFuture;
import io.sarl.sre.extensions.simulation.services.executor.SynchronousExecutorService;
import io.sarl.sre.extensions.simulation.tests.units.services.executor.AbstractExecutorServiceTest;
import io.sarl.sre.services.time.TimeService;
import io.sarl.tests.api.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
@DisplayName("unit: SynchronousExecutorService test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.11")
@SarlElementType(10)
@SuppressWarnings("all")
public class SynchronousExecutorServiceTest extends AbstractExecutorServiceTest<SynchronousExecutorService> {
  @SarlSpecification("0.11")
  @SarlElementType(10)
  private static class FailingRunnable implements Runnable {
    private final RuntimeException ex = new RuntimeException();
    
    @Override
    public void run() {
      throw this.ex;
    }
    
    @Pure
    public RuntimeException getException() {
      return this.ex;
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
    public FailingRunnable() {
      super();
    }
  }
  
  @SarlSpecification("0.11")
  @SarlElementType(10)
  private static class FailingCallable implements Callable<Object> {
    private final RuntimeException ex = new RuntimeException();
    
    @Override
    public Object call() {
      throw this.ex;
    }
    
    @Pure
    public RuntimeException getException() {
      return this.ex;
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
    public FailingCallable() {
      super();
    }
  }
  
  @Nullable
  private TimeService timeService;
  
  @Override
  public SynchronousExecutorService newService(final ExecutorService executor) {
    return new SynchronousExecutorService(executor, this.timeService);
  }
  
  protected void moveToTime(final long newTime) {
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      TimeUnit tu = ((TimeUnit) _argument);
      double t = tu.convert(newTime, TimeUnit.MILLISECONDS);
      return Double.valueOf(t);
    };
    Mockito.<Double>when(Double.valueOf(this.timeService.getTime(ArgumentMatchers.<TimeUnit>any()))).thenAnswer(_function);
    this.service.runScheduledTasks();
  }
  
  @Override
  public void setUp() {
    this.timeService = Mockito.<TimeService>mock(TimeService.class);
    Mockito.<TimeUnit>when(this.timeService.getTimePrecision()).thenReturn(TimeUnit.MILLISECONDS);
    super.setUp();
  }
  
  @Test
  @DisplayName("executeAsap(Runnable)")
  public void executeAsapRunnable() {
    Runnable run = Mockito.<Runnable>mock(Runnable.class);
    this.service.executeAsap(this.logger, run);
    Mockito.<Runnable>verify(run).run();
    Mockito.verifyNoInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeAsap(Runnable) with exception")
  public void executeAsapRunnable_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = Mockito.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    this.service.executeAsap(this.logger, run);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeAsap(Runnable) with future value")
  public void executeAsapRunnableFuture() {
    try {
      UUID value = UUID.randomUUID();
      Runnable run = Mockito.<Runnable>mock(Runnable.class);
      Future<UUID> future = this.service.<UUID>executeAsap(this.logger, value, run);
      Assertions.assertNotNull(future);
      Assertions.assertSame(value, future.get());
      Mockito.<Runnable>verify(run).run();
      Mockito.verifyNoInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("executeAsap(Runnable) with future value and exception")
  public void executeAsapRunnableFuture_exception() {
    try {
      UUID value = UUID.randomUUID();
      SynchronousExecutorServiceTest.FailingRunnable run = Mockito.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
      Future<UUID> future = this.service.<UUID>executeAsap(this.logger, value, run);
      Assertions.assertNotNull(future);
      Assertions.assertSame(value, future.get());
      Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
      ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
      ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
      Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
      Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
      Assertions.assertSame(run.getException(), capturedException.getValue());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("executeAsap(Callable)")
  public void executeAsapCallable() {
    try {
      Callable run = Mockito.<Callable>mock(Callable.class);
      Future<Object> future = this.service.<Object>executeAsap(this.logger, run);
      Assertions.assertNotNull(future);
      Mockito.<Callable>verify(run).call();
      Mockito.verifyNoInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("executeAsap(Callable) with exception")
  public void executeAsapCallable_exception() {
    SynchronousExecutorServiceTest.FailingCallable run = Mockito.<SynchronousExecutorServiceTest.FailingCallable>spy(new SynchronousExecutorServiceTest.FailingCallable());
    Future<Object> future = this.service.<Object>executeAsap(this.logger, run);
    Assertions.assertNotNull(future);
    Mockito.<SynchronousExecutorServiceTest.FailingCallable>verify(run).call();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  @DisplayName("schedule(Runnable)")
  public void scheduleRunnable() {
    Runnable run = Mockito.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertTrue(tasks.isEmpty());
    Mockito.verifyNoInteractions(this.logger);
  }
  
  @Test
  @DisplayName("schedule(Runnable) with exception")
  public void scheduleRunnable_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = Mockito.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  @DisplayName("schedule(Callable)")
  public void scheduleCallable() {
    try {
      Callable run = Mockito.<Callable>mock(Callable.class);
      ScheduledFuture<Object> future = this.service.<Object>schedule(this.logger, 34, TimeUnit.DAYS, run);
      Assertions.assertNotNull(future);
      Mockito.verifyNoInteractions(run);
      List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
      Assertions.assertNotNull(tasks);
      Assertions.assertEquals(1, tasks.size());
      Assertions.assertSame(future, tasks.get(0));
      double _time = tasks.get(0).getTime();
      Assertions.assertEquals(2937600000l, ((long) _time));
      this.moveToTime(2937600000l);
      Mockito.<Callable>verify(run).call();
      tasks = this.service.getScheduledTasks();
      Assertions.assertTrue(tasks.isEmpty());
      Mockito.verifyNoInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("schedule(Callable) with exception")
  public void scheduleCallable_exception() {
    SynchronousExecutorServiceTest.FailingCallable run = Mockito.<SynchronousExecutorServiceTest.FailingCallable>spy(new SynchronousExecutorServiceTest.FailingCallable());
    ScheduledFuture<Object> future = this.service.<Object>schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingCallable>verify(run).call();
    tasks = this.service.getScheduledTasks();
    Assertions.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  @DisplayName("scheduleAtFixedRate(Runnable)")
  public void scheduleAtFixedRate() {
    Runnable run = Mockito.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.scheduleAtFixedRate(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time_1 = tasks.get(0).getTime();
    Assertions.assertEquals(3283200000l, ((long) _time_1));
    Mockito.verifyNoInteractions(this.logger);
  }
  
  @Test
  @DisplayName("scheduleAtFixedRate(Runnable) with exception")
  public void scheduleAtFixedRate_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = Mockito.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.scheduleAtFixedRate(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  @DisplayName("scheduleAtFixedDelay(Runnable)")
  public void scheduleWithFixedDelay() {
    Runnable run = Mockito.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.scheduleWithFixedDelay(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time_1 = tasks.get(0).getTime();
    Assertions.assertEquals(3283200000l, ((long) _time_1));
    Mockito.verifyNoInteractions(this.logger);
  }
  
  @Test
  @DisplayName("scheduleAtFixedDelay(Runnable) with exception")
  public void scheduleWithFixedDelay_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = Mockito.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.scheduleWithFixedDelay(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assertions.assertNotNull(future);
    Mockito.verifyNoInteractions(run);
    List<SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assertions.assertNotNull(tasks);
    Assertions.assertEquals(1, tasks.size());
    Assertions.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assertions.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assertions.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(run.getException(), capturedException.getValue());
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
  public SynchronousExecutorServiceTest() {
    super();
  }
}
