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
import io.sarl.sre.extensions.simulation.services.executor.SynchronousExecutorService;
import io.sarl.sre.services.time.TimeService;
import io.sarl.sre.tests.units.services.executor.AbstractExecutorServiceTest;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
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
@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
public class SynchronousExecutorServiceTest extends AbstractExecutorServiceTest<SynchronousExecutorService> {
  @SarlSpecification("0.10")
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
  
  @SarlSpecification("0.10")
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
  public SynchronousExecutorService newService() {
    return new SynchronousExecutorService();
  }
  
  protected void moveToTime(final long newTime) {
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      TimeUnit tu = ((TimeUnit) _argument);
      double t = tu.convert(newTime, TimeUnit.MILLISECONDS);
      return Double.valueOf(t);
    };
    Mockito.<Double>when(Double.valueOf(this.timeService.getTime(ArgumentMatchers.<TimeUnit>any()))).thenAnswer(_function);
    this.service.timeChanged(this.timeService);
  }
  
  @Override
  public void setUp() {
    this.timeService = AbstractSarlTest.<TimeService>mock(TimeService.class);
    Mockito.<TimeUnit>when(this.timeService.getTimePrecision()).thenReturn(TimeUnit.MILLISECONDS);
    super.setUp();
    this.service.setTimeService(this.timeService);
  }
  
  @Test
  public void execute() {
    Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
    this.service.executeAsap(this.logger, run);
    Mockito.<Runnable>verify(run).run();
    Mockito.verifyZeroInteractions(this.logger);
  }
  
  @Test
  public void execute_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    this.service.executeAsap(this.logger, run);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void submitRunnable() {
    Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
    Future<?> future = this.service.executeAsap(this.logger, run);
    Assert.assertNotNull(future);
    Mockito.<Runnable>verify(run).run();
    Mockito.verifyZeroInteractions(this.logger);
  }
  
  @Test
  public void submitRunnable_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    Future<?> future = this.service.executeAsap(this.logger, run);
    Assert.assertNotNull(future);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void submitRunnableFuture() {
    try {
      UUID value = UUID.randomUUID();
      Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
      Future<UUID> future = this.service.<UUID>executeAsap(this.logger, value, run);
      Assert.assertNotNull(future);
      Assert.assertSame(value, future.get());
      Mockito.<Runnable>verify(run).run();
      Mockito.verifyZeroInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void submitRunnableFuture_exception() {
    try {
      UUID value = UUID.randomUUID();
      SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
      Future<UUID> future = this.service.<UUID>executeAsap(this.logger, value, run);
      Assert.assertNotNull(future);
      Assert.assertSame(value, future.get());
      Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
      ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
      ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
      Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
      Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
      Assert.assertSame(run.getException(), capturedException.getValue());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void submitCallable() {
    try {
      Callable run = AbstractSarlTest.<Callable>mock(Callable.class);
      Future<Object> future = this.service.<Object>executeAsap(this.logger, run);
      Assert.assertNotNull(future);
      Mockito.<Callable>verify(run).call();
      Mockito.verifyZeroInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void submitCallable_exception() {
    SynchronousExecutorServiceTest.FailingCallable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingCallable>spy(new SynchronousExecutorServiceTest.FailingCallable());
    Future<Object> future = this.service.<Object>executeAsap(this.logger, run);
    Assert.assertNotNull(future);
    Mockito.<SynchronousExecutorServiceTest.FailingCallable>verify(run).call();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void scheduleRunnable() {
    Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertTrue(tasks.isEmpty());
    Mockito.verifyZeroInteractions(this.logger);
  }
  
  @Test
  public void scheduleRunnable_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void scheduleCallable() {
    try {
      Callable run = AbstractSarlTest.<Callable>mock(Callable.class);
      ScheduledFuture<Object> future = this.service.<Object>schedule(this.logger, 34, TimeUnit.DAYS, run);
      Assert.assertNotNull(future);
      Mockito.verifyZeroInteractions(run);
      List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
      Assert.assertNotNull(tasks);
      Assert.assertEquals(1, tasks.size());
      Assert.assertSame(future, tasks.get(0));
      double _time = tasks.get(0).getTime();
      Assert.assertEquals(2937600000l, ((long) _time));
      this.moveToTime(2937600000l);
      Mockito.<Callable>verify(run).call();
      tasks = this.service.getScheduledTasks();
      Assert.assertTrue(tasks.isEmpty());
      Mockito.verifyZeroInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void scheduleCallable_exception() {
    SynchronousExecutorServiceTest.FailingCallable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingCallable>spy(new SynchronousExecutorServiceTest.FailingCallable());
    ScheduledFuture<Object> future = this.service.<Object>schedule(this.logger, 34, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingCallable>verify(run).call();
    tasks = this.service.getScheduledTasks();
    Assert.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void scheduleAtFixedRate() {
    Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.scheduleAtFixedRate(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time_1 = tasks.get(0).getTime();
    Assert.assertEquals(3283200000l, ((long) _time_1));
    Mockito.verifyZeroInteractions(this.logger);
  }
  
  @Test
  public void scheduleAtFixedRate_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.scheduleAtFixedRate(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
  }
  
  @Test
  public void scheduleWithFixedDelay() {
    Runnable run = AbstractSarlTest.<Runnable>mock(Runnable.class);
    ScheduledFuture<?> future = this.service.scheduleWithFixedDelay(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<Runnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time_1 = tasks.get(0).getTime();
    Assert.assertEquals(3283200000l, ((long) _time_1));
    Mockito.verifyZeroInteractions(this.logger);
  }
  
  @Test
  public void scheduleWithFixedDelay_exception() {
    SynchronousExecutorServiceTest.FailingRunnable run = AbstractSarlTest.<SynchronousExecutorServiceTest.FailingRunnable>spy(new SynchronousExecutorServiceTest.FailingRunnable());
    ScheduledFuture<?> future = this.service.scheduleWithFixedDelay(this.logger, 34, 4, TimeUnit.DAYS, run);
    Assert.assertNotNull(future);
    Mockito.verifyZeroInteractions(run);
    List<SynchronousExecutorService.SreScheduledFuture<?>> tasks = this.service.getScheduledTasks();
    Assert.assertNotNull(tasks);
    Assert.assertEquals(1, tasks.size());
    Assert.assertSame(future, tasks.get(0));
    double _time = tasks.get(0).getTime();
    Assert.assertEquals(2937600000l, ((long) _time));
    this.moveToTime(2937600000l);
    Mockito.<SynchronousExecutorServiceTest.FailingRunnable>verify(run).run();
    tasks = this.service.getScheduledTasks();
    Assert.assertTrue(tasks.isEmpty());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assert.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assert.assertSame(run.getException(), capturedException.getValue());
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
