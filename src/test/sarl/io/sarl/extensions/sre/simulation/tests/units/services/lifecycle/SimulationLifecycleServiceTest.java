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

package io.sarl.extensions.sre.simulation.tests.units.services.lifecycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;

import io.sarl.extensions.sre.simulation.services.lifecycle.SimulationLifecycleService;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.DynamicSkillProvider;
import io.sarl.lang.sarl.SarlPackage;
import io.sarl.sarlspecification.SarlSpecificationChecker;
import io.sarl.sre.services.context.Context;
import io.sarl.sre.services.lifecycle.AgentCreatorProvider;
import io.sarl.sre.services.lifecycle.SpawnResult;
import io.sarl.sre.tests.testutils.AbstractSreTest;
import io.sarl.tests.api.Nullable;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class SimulationLifecycleServiceTest extends AbstractSreTest {

	@Nullable
	private AgentCreatorProvider agentFactoryProvider;

	@Nullable
	private SarlSpecificationChecker checker;

	@Nullable
	private DynamicSkillProvider skillProvider;

	@Nullable
	private MockService service;

	@Before
	public void setUp() {
		this.agentFactoryProvider = mock(AgentCreatorProvider.class);
		this.checker = mock(SarlSpecificationChecker.class);
		when(this.checker.isValidSarlElement(any(Class.class))).thenReturn(true);
		this.skillProvider = mock(DynamicSkillProvider.class);
		this.service = new MockService(this.checker, this.skillProvider, this.agentFactoryProvider);
	}

	protected void addAgentMock(UUID id) {
		this.service.spawnAgent(1, UUID.randomUUID(),
				mock(Context.class),
				id, MockAgent.class);
	}

	protected void removeAgentMock(UUID id) {
		this.service.killAgent(this.service.getAgent(id));
	}

	@Test
	public void getAgents_0_noSync() {
		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getAgents_1_noSync() {
		UUID id0 = UUID.randomUUID();
		addAgentMock(id0);

		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getAgents_2_noSync() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);

		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getAgents_0_sync() {
		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getAgents_1_sync() {
		UUID id0 = UUID.randomUUID();
		addAgentMock(id0);
		this.service.synchronizeAgentList();

		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		assertNotNull(iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getAgents_2_sync() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();

		Iterable<Agent> iterable;
		Iterator<Agent> iterator;

		iterable = this.service.getAgents();
		assertNotNull(iterable);
		iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		assertNotNull(iterator.next());
		assertTrue(iterator.hasNext());
		assertNotNull(iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void hasAgent_0_noSync() {
		assertFalse(this.service.hasAgent());
	}

	@Test
	public void hasAgent_1_noSync() {
		UUID id0 = UUID.randomUUID();
		addAgentMock(id0);
		assertFalse(this.service.hasAgent());
	}

	@Test
	public void hasAgent_2_noSync() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		assertFalse(this.service.hasAgent());
	}

	@Test
	public void hasAgent_0_sync() {
		assertFalse(this.service.hasAgent());
	}

	@Test
	public void hasAgent_1_sync() {
		addAgentMock(UUID.randomUUID());
		this.service.synchronizeAgentList();
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void hasAgent_2_sync() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_0() {
		this.service.synchronizeAgentList();
		assertFalse(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_1() {
		UUID id0 = UUID.randomUUID();
		addAgentMock(id0);
		this.service.synchronizeAgentList();
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_2() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_3() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();
		removeAgentMock(id0);
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_4() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();
		removeAgentMock(id0);
		this.service.synchronizeAgentList();
		assertTrue(this.service.hasAgent());
	}

	@Test
	public void synchronizeAgentLists_5() {
		UUID id0 = UUID.randomUUID();
		UUID id1 = UUID.randomUUID();
		addAgentMock(id0);
		addAgentMock(id1);
		this.service.synchronizeAgentList();
		removeAgentMock(id0);
		this.service.synchronizeAgentList();
		removeAgentMock(id1);
		this.service.synchronizeAgentList();
		assertFalse(this.service.hasAgent());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class MockService extends SimulationLifecycleService {

		public MockService(SarlSpecificationChecker sarlSpecificationChecker,
				DynamicSkillProvider skillProvider, AgentCreatorProvider agentFactoryProvider) {
			super(sarlSpecificationChecker, skillProvider, agentFactoryProvider);
		}

		@Override
		public boolean canSpawnAgent() {
			return true;
		}
		
		@Override
		protected SpawnResult spawnAgent(int nbAgents, UUID spawningAgent, Context parent,
				Class<? extends Agent> agentClazz, Object[] params,
				Function1<? super Integer, ? extends UUID> agentIds) {
			UUID id = agentIds.apply(0); 
			assertNotNull(id);
			Agent agent = mock(Agent.class);
			when(agent.getID()).thenReturn(id);
			onAgentCreated(agent);
			return new SpawnResult(Collections.singletonList(id), Collections.emptyList());
		}
		
		@Override
		public synchronized boolean killAgent(Agent agent) {
			onAgentKilled(agent);
			return true;
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SarlSpecification
	@SarlElementType(SarlPackage.SARL_AGENT)
	public static class MockAgent extends Agent {

		public MockAgent(UUID parentID, UUID agentID) {
			super(parentID, agentID);
		}

	}

}
