/**
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
package io.sarl.sre.extensions.simulation.tests.units.services.lifecycle;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.DynamicSkillProvider;
import io.sarl.sarlspecification.SarlSpecificationChecker;
import io.sarl.sre.extensions.simulation.services.lifecycle.SimulationLifecycleService;
import io.sarl.sre.extensions.simulation.tests.units.services.lifecycle.MockAgent;
import io.sarl.sre.services.context.Context;
import io.sarl.sre.services.lifecycle.AgentCreatorProvider;
import io.sarl.sre.services.lifecycle.SpawnResult;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@SarlSpecification("0.10")
@SarlElementType(10)
public class SimulationLifecycleServiceTest extends AbstractSarlTest {
  /**
   * @author $Author: sgalland$
   * @version $FullVersion$
   * @mavengroupid $GroupId$
   * @mavenartifactid $ArtifactId$
   */
  @SarlSpecification("0.10")
  @SarlElementType(10)
  public static class MockService extends SimulationLifecycleService {
    public MockService(final SarlSpecificationChecker sarlSpecificationChecker, final DynamicSkillProvider skillProvider, final AgentCreatorProvider agentFactoryProvider) {
      super(sarlSpecificationChecker, skillProvider, agentFactoryProvider);
    }
    
    @Override
    public boolean canSpawnAgent() {
      return true;
    }
    
    @Override
    public SpawnResult spawnAgent(final int nbAgents, final UUID spawningAgent, final Context parent, final Class<? extends Agent> agentClazz, final Object[] params, final Function1<? super Integer, ? extends UUID> agentIds) {
      UUID id = agentIds.apply(Integer.valueOf(0));
      Assert.assertNotNull(id);
      Agent agent = AbstractSarlTest.<Agent>mock(Agent.class);
      Mockito.<UUID>when(agent.getID()).thenReturn(id);
      this.onAgentCreated(agent);
      List<UUID> _singletonList = Collections.<UUID>singletonList(id);
      List<Throwable> _emptyList = CollectionLiterals.<Throwable>emptyList();
      return new SpawnResult(_singletonList, _emptyList);
    }
    
    @Override
    public synchronized boolean killAgent(final Agent agent) {
      this.onAgentKilled(agent);
      return true;
    }
  }
  
  @Nullable
  private AgentCreatorProvider agentFactoryProvider;
  
  @Nullable
  private SarlSpecificationChecker checker;
  
  @Nullable
  private DynamicSkillProvider skillProvider;
  
  @Nullable
  private SimulationLifecycleServiceTest.MockService service;
  
  @Before
  public void setUp() {
    this.agentFactoryProvider = AbstractSarlTest.<AgentCreatorProvider>mock(AgentCreatorProvider.class);
    this.checker = AbstractSarlTest.<SarlSpecificationChecker>mock(SarlSpecificationChecker.class);
    Mockito.<Boolean>when(Boolean.valueOf(this.checker.isValidSarlElement(ArgumentMatchers.<Class>any(Class.class)))).thenReturn(Boolean.valueOf(true));
    this.skillProvider = AbstractSarlTest.<DynamicSkillProvider>mock(DynamicSkillProvider.class);
    SimulationLifecycleServiceTest.MockService _mockService = new SimulationLifecycleServiceTest.MockService(this.checker, this.skillProvider, this.agentFactoryProvider);
    this.service = _mockService;
  }
  
  protected void addAgentMock(final UUID id) {
    this.service.spawnAgent(1, UUID.randomUUID(), 
      AbstractSarlTest.<Context>mock(Context.class), id, MockAgent.class);
  }
  
  protected void removeAgentMock(final UUID id) {
    this.service.killAgent(this.service.getAgent(id));
  }
  
  @Test
  @Pure
  public void getAgents_0_noSync() {
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void getAgents_1_noSync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void getAgents_2_noSync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void getAgents_0_sync() {
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void getAgents_1_sync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.service.synchronizeAgentList();
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertTrue(iterator.hasNext());
    Assert.assertNotNull(iterator.next());
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void getAgents_2_sync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Iterable<Agent> iterable = this.service.getAgents();
    Assert.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assert.assertTrue(iterator.hasNext());
    Assert.assertNotNull(iterator.next());
    Assert.assertTrue(iterator.hasNext());
    Assert.assertNotNull(iterator.next());
    Assert.assertFalse(iterator.hasNext());
  }
  
  @Test
  @Pure
  public void hasAgent_0_noSync() {
    Assert.assertFalse(this.service.hasAgent());
  }
  
  @Test
  @Pure
  public void hasAgent_1_noSync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @Pure
  public void hasAgent_2_noSync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @Pure
  public void hasAgent_0_sync() {
    Assert.assertFalse(this.service.hasAgent());
  }
  
  @Test
  @Pure
  public void hasAgent_1_sync() {
    this.addAgentMock(UUID.randomUUID());
    this.service.synchronizeAgentList();
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @Pure
  public void hasAgent_2_sync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_0() {
    this.service.synchronizeAgentList();
    Assert.assertFalse(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_1() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.service.synchronizeAgentList();
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_2() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_3() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id0);
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_4() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id0);
    this.service.synchronizeAgentList();
    Assert.assertTrue(this.service.hasAgent());
  }
  
  @Test
  public void synchronizeAgentLists_5() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id0);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id1);
    this.service.synchronizeAgentList();
    Assert.assertFalse(this.service.hasAgent());
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
  public SimulationLifecycleServiceTest() {
    super();
  }
}
