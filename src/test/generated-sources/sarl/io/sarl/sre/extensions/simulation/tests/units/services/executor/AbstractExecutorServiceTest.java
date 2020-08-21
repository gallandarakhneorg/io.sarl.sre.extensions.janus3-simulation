/**
 * $Id$
 * 
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 * 
 * Copyright (C) 2014-2020 the original authors or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
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
import io.sarl.sre.services.executor.EarlyExitException;
import io.sarl.sre.services.executor.ExecutorService;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.sre.test.framework.extension.ServiceManagementExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import io.sarl.tests.api.tools.TestAssertions;
import io.sarl.tests.api.tools.TestMockito;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Copied for Janus.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@SarlSpecification("0.12")
@SarlElementType(10)
@SuppressWarnings("all")
public abstract class AbstractExecutorServiceTest<T extends ExecutorService> {
  @Nullable
  protected java.util.concurrent.ExecutorService executor;
  
  @Nullable
  protected Logger logger;
  
  @Nullable
  protected T service;
  
  @BeforeEach
  public void setUp() {
    this.logger = TestMockito.<Logger>mock(Logger.class);
    this.executor = TestMockito.<java.util.concurrent.ExecutorService>mock(java.util.concurrent.ExecutorService.class);
    this.service = this.newService(this.executor);
  }
  
  protected ThreadPoolExecutor resetToThreadPoolExecutor() {
    final ThreadPoolExecutor tpe = TestMockito.<ThreadPoolExecutor>mock(ThreadPoolExecutor.class);
    this.executor = tpe;
    this.service = this.newService(tpe);
    return tpe;
  }
  
  protected abstract T newService(final java.util.concurrent.ExecutorService executor);
  
  protected void startService() {
    ServiceManagementExtension.startServiceManually(this.service);
  }
  
  @Test
  @DisplayName("neverReturn -> null")
  public void neverReturn_noPostCommand() {
    try {
      final TestAssertions.Code _function = () -> {
        ExecutorService.neverReturn();
      };
      TestAssertions.assertException(EarlyExitException.class, _function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("neverReturn -> []")
  public void neverReturn_postCommand() {
    try {
      final TestAssertions.Code _function = () -> {
        final Runnable _function_1 = () -> {
        };
        ExecutorService.neverReturn(_function_1);
      };
      TestAssertions.assertException(EarlyExitException.class, _function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("applyBlockingConsumer")
  public void applyBlockingConsumer() {
    ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
    for (final Integer i : _doubleDotLessThan) {
      list.add(i);
    }
    TreeSet<Integer> set = CollectionLiterals.<Integer>newTreeSet(null);
    final Consumer<Integer> _function = (Integer it) -> {
      synchronized (set) {
        set.add(Integer.valueOf((((it) == null ? 0 : (it).intValue()) * 10)));
      }
    };
    this.service.<Integer>applyBlockingConsumer(this.logger, list, _function);
    Assertions.assertEquals(100, set.size());
    {
      int i_1 = 0;
      boolean _while = (i_1 < 1000);
      while (_while) {
        Assertions.assertTrue(set.contains(Integer.valueOf(i_1)));
        i_1 = (i_1 + 10);
        _while = (i_1 < 1000);
      }
    }
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("applyBlockingConsumer with exception #1")
  public void applyBlockingConsumer_exception_01() {
    ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
    for (final Integer i : _doubleDotLessThan) {
      list.add(i);
    }
    final Consumer<Integer> _function = (Integer it) -> {
      throw new RuntimeException();
    };
    this.service.<Integer>applyBlockingConsumer(this.logger, list, _function);
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.atLeastOnce()).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    TestAssertions.assertInstanceOf(RuntimeException.class, capturedException.getValue());
  }
  
  @Test
  @DisplayName("applyBlockingConsumer with exception #2")
  public void applyBlockingConsumer_exception_02() {
    ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
    for (final Integer i : _doubleDotLessThan) {
      list.add(i);
    }
    final Consumer<Integer> _function = (Integer it) -> {
      ExecutorService.neverReturn();
    };
    this.service.<Integer>applyBlockingConsumer(this.logger, list, _function);
  }
  
  @Test
  @DisplayName("applyBlockingConsumer with exception #3")
  public void applyBlockingConsumer_exception_03() {
    ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
    for (final Integer i : _doubleDotLessThan) {
      list.add(i);
    }
    final Consumer<Integer> _function = (Integer it) -> {
      IllegalStateException _illegalStateException = new IllegalStateException();
      throw new RuntimeException(_illegalStateException);
    };
    this.service.<Integer>applyBlockingConsumer(this.logger, list, _function);
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.atLeastOnce()).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    TestAssertions.assertInstanceOf(IllegalStateException.class, capturedException.getValue());
  }
  
  @Test
  @DisplayName("applyBlockingConsumer no logger with exception #1")
  public void applyBlockingConsumer_exception_noLogger_01() {
    try {
      ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
      ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
      for (final Integer i : _doubleDotLessThan) {
        list.add(i);
      }
      final TestAssertions.Code _function = () -> {
        final Consumer<Integer> _function_1 = (Integer it) -> {
          throw new RuntimeException();
        };
        this.service.<Integer>applyBlockingConsumer(null, list, _function_1);
      };
      TestAssertions.assertException(RuntimeException.class, _function);
      Mockito.verifyNoInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("applyBlockingConsumer no logger with exception #2")
  public void applyBlockingConsumer_exception_noLogger_02() {
    ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
    for (final Integer i : _doubleDotLessThan) {
      list.add(i);
    }
    final Consumer<Integer> _function = (Integer it) -> {
      ExecutorService.neverReturn();
    };
    this.service.<Integer>applyBlockingConsumer(null, list, _function);
    Mockito.verifyNoInteractions(this.logger);
  }
  
  @Test
  @DisplayName("applyBlockingConsumer no logger with exception #3")
  public void applyBlockingConsumer_exception_noLogger_03() {
    try {
      ArrayList<Integer> list = CollectionLiterals.<Integer>newArrayList();
      ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, 100, true);
      for (final Integer i : _doubleDotLessThan) {
        list.add(i);
      }
      final TestAssertions.Code _function = () -> {
        final Consumer<Integer> _function_1 = (Integer it) -> {
          IllegalStateException _illegalStateException = new IllegalStateException();
          throw new RuntimeException(_illegalStateException);
        };
        this.service.<Integer>applyBlockingConsumer(null, list, _function_1);
      };
      TestAssertions.assertException(IllegalStateException.class, _function);
      Mockito.verifyNoInteractions(this.logger);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  @DisplayName("executeBlockingTasks forwards Exception")
  public void executeBlockingTasks_throws_noException() {
    Runnable run1 = TestMockito.<Runnable>mock(Runnable.class);
    Runnable run2 = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeBlockingTasks(this.logger, true, Collections.<Runnable>unmodifiableList(CollectionLiterals.<Runnable>newArrayList(run1, run2)));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run1, Mockito.only()).run();
    Mockito.<Runnable>verify(run2, Mockito.only()).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTasks forwards Exception with error")
  public void executeBlockingTasks_throws_exception() {
    RuntimeException exception = new RuntimeException();
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run1 = _function;
    Runnable run2 = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    try {
      this.service.executeBlockingTasks(this.logger, true, Collections.<Runnable>unmodifiableList(CollectionLiterals.<Runnable>newArrayList(run1, run2)));
      Assertions.<Object>fail(("Expecting exception: " + exception));
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable ex = (Throwable)_t;
        Assertions.assertSame(exception, ex);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run2, Mockito.only()).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTasks not forwards Exception")
  public void executeBlockingTasks_notThrows_noException() {
    Runnable run1 = TestMockito.<Runnable>mock(Runnable.class);
    Runnable run2 = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeBlockingTasks(this.logger, false, Collections.<Runnable>unmodifiableList(CollectionLiterals.<Runnable>newArrayList(run1, run2)));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run1, Mockito.only()).run();
    Mockito.<Runnable>verify(run2, Mockito.only()).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTasks not forwards Exception with error")
  public void executeBlockingTasks_notThrows_exception() {
    RuntimeException exception = new RuntimeException();
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run1 = _function;
    Runnable run2 = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeBlockingTasks(this.logger, false, Collections.<Runnable>unmodifiableList(CollectionLiterals.<Runnable>newArrayList(run1, run2)));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run2, Mockito.only()).run();
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 1 task")
  public void executeBlockingTask_noException_1task() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(1, this.service.executeBlockingTask(this.logger, 1, 100, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.never()).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(1)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 tasks with 1 task per group")
  public void executeBlockingTask_noException_4tasks_1member() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(4, this.service.executeBlockingTask(this.logger, 4, 1, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(4)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 tasks with 2 tasks per group")
  public void executeBlockingTask_noException_4tasks_2members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(4, this.service.executeBlockingTask(this.logger, 4, 2, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 tasks with 3 tasks per group")
  public void executeBlockingTask_noException_4tasks_3members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(4, this.service.executeBlockingTask(this.logger, 4, 3, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 tasks with 4 tasks per group")
  public void executeBlockingTask_noException_4tasks_4members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(4, this.service.executeBlockingTask(this.logger, 4, 4, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 tasks with 5 tasks per group")
  public void executeBlockingTask_noException_4tasks_5members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(4, this.service.executeBlockingTask(this.logger, 4, 5, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeBlockingTask 1 erroneous task")
  public void executeBlockingTask_exception_1task() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 1, 100, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.never()).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 erroneous task with 1 task per group")
  public void executeBlockingTask_exception_4tasks_1member() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 4, 1, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(4)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 erroneous task with 2 tasks per group")
  public void executeBlockingTask_exception_4tasks_2members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 4, 2, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 erroneous task with 3 tasks per group")
  public void executeBlockingTask_exception_4tasks_3members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 4, 3, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 erroneous task with 3 tasks per group")
  public void executeBlockingTask_exception_4tasks_4members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 4, 4, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeBlockingTask 4 erroneous task with 5 tasks per group")
  public void executeBlockingTask_exception_4tasks_5members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    Assertions.assertEquals(0, this.service.executeBlockingTask(this.logger, 4, 5, run));
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 1 task")
  public void executeNotBlockingTask_noException_1task() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 1, 100, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(1)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 tasks with 1 task per group")
  public void executeNotBlockingTask_noException_4tasks_1member() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 1, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(4)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 tasks with 2 tasks per group")
  public void executeNotBlockingTask_noException_4tasks_2members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 2, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 tasks with 3 tasks per group")
  public void executeNotBlockingTask_noException_4tasks_3members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 3, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 tasks with 4 tasks per group")
  public void executeNotBlockingTask_noException_4tasks_4members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 4, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 tasks with 5 tasks per group")
  public void executeNotBlockingTask_noException_4tasks_5members() {
    Runnable run = TestMockito.<Runnable>mock(Runnable.class);
    final Answer<Object> _function = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeBlockingTask(this.logger, 4, 5, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    Mockito.<Runnable>verify(run, Mockito.times(4)).run();
    Mockito.verifyNoMoreInteractions(this.logger);
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 1 erroneous task")
  public void executeNotBlockingTask_exception_1task() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 1, 100, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(1)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 erroneous task with 1 task per group")
  public void executeNotBlockingTask_exception_4tasks_1member() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 1, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(4)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 erroneous task with 2 tasks per group")
  public void executeNotBlockingTask_exception_4tasks_2members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 2, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 erroneous task with 3 tasks per group")
  public void executeNotBlockingTask_exception_4tasks_3members() {
    RuntimeException exception = new RuntimeException();
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeBlockingTask(this.logger, 4, 3, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(2)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 erroneous task with 3 tasks per group")
  public void executeNotBlockingTask_exception_4tasks_4members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 4, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
  }
  
  @Test
  @DisplayName("executeNotBlockingTask 4 erroneous task with 5 tasks per group")
  public void executeNotBlockingTask_exception_4tasks_5members() {
    RuntimeException exception = TestMockito.<RuntimeException>mock(RuntimeException.class);
    final Runnable _function = () -> {
      throw exception;
    };
    Runnable run = _function;
    final Answer<Object> _function_1 = (InvocationOnMock it) -> {
      Object _argument = it.<Object>getArgument(0);
      ((Runnable) _argument).run();
      return null;
    };
    Mockito.doAnswer(_function_1).<java.util.concurrent.ExecutorService>when(this.executor).execute(ArgumentMatchers.<Runnable>any());
    this.service.executeNotBlockingTask(this.logger, 4, 5, run);
    Mockito.<java.util.concurrent.ExecutorService>verify(this.executor, Mockito.times(1)).execute(ArgumentMatchers.<Runnable>any());
    ArgumentCaptor<Level> capturedLevel = ArgumentCaptor.<Level, Level>forClass(Level.class);
    ArgumentCaptor<Throwable> capturedException = ArgumentCaptor.<Throwable, Throwable>forClass(Throwable.class);
    Mockito.<Logger>verify(this.logger, Mockito.times(4)).log(capturedLevel.capture(), ArgumentMatchers.<String>any(), capturedException.capture());
    Assertions.assertSame(Level.SEVERE, capturedLevel.getValue());
    Assertions.assertSame(exception, capturedException.getValue());
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
  public AbstractExecutorServiceTest() {
    super();
  }
}
