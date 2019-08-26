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
package io.sarl.sre.extensions.simulation.tests.units.boot.configs;

import io.bootique.config.ConfigurationFactory;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.boot.configs.SimulationConfig;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
public class SimulationKernelFactoryTest extends AbstractSarlTest {
  @Nullable
  private SimulationConfig config;
  
  @Before
  public void setUp() {
    SimulationConfig _simulationConfig = new SimulationConfig();
    this.config = _simulationConfig;
  }
  
  @Test
  @Pure
  public void getConfiguration() {
    ConfigurationFactory factory = AbstractSarlTest.<ConfigurationFactory>mock(ConfigurationFactory.class);
    SimulationConfig cfg = AbstractSarlTest.<SimulationConfig>mock(SimulationConfig.class);
    Mockito.<Object>when(factory.<Object>config(ArgumentMatchers.<Class>any(Class.class), ArgumentMatchers.anyString())).thenReturn(cfg);
    Assert.assertSame(cfg, SimulationConfig.getConfiguration(factory));
    ArgumentCaptor<Class> arg0 = ArgumentCaptor.<Class, Class>forClass(Class.class);
    ArgumentCaptor<String> arg1 = ArgumentCaptor.<String, String>forClass(String.class);
    Mockito.<ConfigurationFactory>verify(factory, Mockito.only()).<Object>config(arg0.capture(), arg1.capture());
    AbstractSarlTest.assertEquals(SimulationConfig.class, arg0.getValue());
    AbstractSarlTest.assertEquals(SimulationConfig.PREFIX, arg1.getValue());
  }
  
  @Test
  @Pure
  public void isAutostart() {
    Assert.assertTrue(this.config.isAutostart());
  }
  
  @Test
  public void setAutostart() {
    Assert.assertTrue(this.config.isAutostart());
    this.config.setAutostart(false);
    Assert.assertFalse(this.config.isAutostart());
    this.config.setAutostart(true);
    Assert.assertTrue(this.config.isAutostart());
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
  public SimulationKernelFactoryTest() {
    super();
  }
}
