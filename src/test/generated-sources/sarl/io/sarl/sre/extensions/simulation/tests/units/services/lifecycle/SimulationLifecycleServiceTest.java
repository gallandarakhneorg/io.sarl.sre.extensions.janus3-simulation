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
package io.sarl.sre.extensions.simulation.tests.units.services.lifecycle;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Agent;
import io.sarl.sarlspecification.SarlSpecificationChecker;
import io.sarl.sre.extensions.simulation.tests.units.services.lifecycle.mocks.AgentMock;
import io.sarl.sre.extensions.simulation.tests.units.services.lifecycle.mocks.ServiceMock;
import io.sarl.sre.services.context.Context;
import io.sarl.sre.services.lifecycle.AgentCreatorProvider;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import java.util.Iterator;
import java.util.UUID;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: SimulationLifecycleService test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.12")
@SarlElementType(10)
@SuppressWarnings("all")
public class SimulationLifecycleServiceTest {
  @Nullable
  private AgentCreatorProvider agentFactoryProvider;
  
  @Nullable
  private SarlSpecificationChecker checker;
  
  @Nullable
  private ServiceMock service;
  
  @BeforeEach
  public void setUp() {
    this.agentFactoryProvider = Mockito.<AgentCreatorProvider>mock(AgentCreatorProvider.class);
    this.checker = Mockito.<SarlSpecificationChecker>mock(SarlSpecificationChecker.class);
    Mockito.<Boolean>when(Boolean.valueOf(this.checker.isValidSarlElement(ArgumentMatchers.<Class>any(Class.class)))).thenReturn(Boolean.valueOf(true));
    ServiceMock _serviceMock = new ServiceMock(this.checker, this.agentFactoryProvider);
    this.service = _serviceMock;
  }
  
  protected void addAgentMock(final UUID id) {
    this.service.spawnAgent(
      1, 
      UUID.randomUUID(), 
      Mockito.<Context>mock(Context.class), id, 
      AgentMock.class, 
      new Object[] {});
  }
  
  protected void removeAgentMock(final UUID id) {
    this.service.killAgent(this.service.getAgent(id), false);
  }
  
  @Test
  @DisplayName("getAgents w/ 0 agent w/o sync")
  public void getAgents_0_noSync() {
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("getAgents w/ 1 agent w/o sync")
  public void getAgents_1_noSync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("getAgents w/ 2 agents w/o sync")
  public void getAgents_2_noSync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("getAgents w/ 0 agent w/ sync")
  public void getAgents_0_sync() {
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("getAgents w/ 1 agent w/ sync")
  public void getAgents_1_sync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.service.synchronizeAgentList();
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertNotNull(iterator.next());
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("getAgents w/ 2 agents w/ sync")
  public void getAgents_2_sync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Iterable<Agent> iterable = this.service.getAgents();
    Assertions.assertNotNull(iterable);
    Iterator<Agent> iterator = iterable.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertNotNull(iterator.next());
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertNotNull(iterator.next());
    Assertions.assertFalse(iterator.hasNext());
  }
  
  @Test
  @DisplayName("hasAgent w/ 0 agent w/o sync")
  public void hasAgent_0_noSync() {
    Assertions.assertFalse(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("hasAgent w/ 1 agent w/o sync")
  public void hasAgent_1_noSync() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("hasAgent w/ 2 agents w/o sync")
  public void hasAgent_2_noSync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("hasAgent w/ 0 agent w/ sync")
  public void hasAgent_0_sync() {
    Assertions.assertFalse(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("hasAgent w/ 1 agent w/ sync")
  public void hasAgent_1_sync() {
    this.addAgentMock(UUID.randomUUID());
    this.service.synchronizeAgentList();
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("hasAgent w/ 2 agents w/ sync")
  public void hasAgent_2_sync() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 0 agent")
  public void synchronizeAgentLists_0() {
    this.service.synchronizeAgentList();
    Assertions.assertFalse(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 1 agent")
  public void synchronizeAgentLists_1() {
    UUID id0 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.service.synchronizeAgentList();
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 2 agents")
  public void synchronizeAgentLists_2() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 2 agent w/ 1 remove w/o sync")
  public void synchronizeAgentLists_3() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id0);
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 2 agent w/ 1 remove w/ sync")
  public void synchronizeAgentLists_4() {
    UUID id0 = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    this.addAgentMock(id0);
    this.addAgentMock(id1);
    this.service.synchronizeAgentList();
    this.removeAgentMock(id0);
    this.service.synchronizeAgentList();
    Assertions.assertTrue(this.service.hasAgent());
  }
  
  @Test
  @DisplayName("synchronizeAgentLists w/ 2 agent w/ 2 removes")
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
    Assertions.assertFalse(this.service.hasAgent());
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
