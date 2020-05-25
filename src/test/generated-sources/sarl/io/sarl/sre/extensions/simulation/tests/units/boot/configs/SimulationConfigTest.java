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
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
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

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: SimulationConfig test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.11")
@SarlElementType(10)
@SuppressWarnings("all")
public class SimulationConfigTest {
  @Nullable
  private SimulationConfig config;
  
  @BeforeEach
  public void setUp() {
    SimulationConfig _simulationConfig = new SimulationConfig();
    this.config = _simulationConfig;
  }
  
  @Test
  @DisplayName("getConfiguration (static)")
  public void getConfiguration() {
    ConfigurationFactory factory = Mockito.<ConfigurationFactory>mock(ConfigurationFactory.class);
    SimulationConfig cfg = Mockito.<SimulationConfig>mock(SimulationConfig.class);
    Mockito.<Object>when(factory.<Object>config(ArgumentMatchers.<Class>any(Class.class), ArgumentMatchers.anyString())).thenReturn(cfg);
    Assertions.assertSame(cfg, SimulationConfig.getConfiguration(factory));
    ArgumentCaptor<Class> arg0 = ArgumentCaptor.<Class, Class>forClass(Class.class);
    ArgumentCaptor<String> arg1 = ArgumentCaptor.<String, String>forClass(String.class);
    Mockito.<ConfigurationFactory>verify(factory, Mockito.only()).<Object>config(arg0.capture(), arg1.capture());
    Assertions.assertEquals(SimulationConfig.class, arg0.getValue());
    Assertions.assertEquals(SimulationConfig.PREFIX, arg1.getValue());
  }
  
  @Test
  @DisplayName("isAutostart")
  public void isAutostart() {
    Assertions.assertTrue(this.config.isAutostart());
  }
  
  @Test
  @DisplayName("setAutostart")
  public void setAutostart() {
    this.config.setAutostart(false);
    Assertions.assertFalse(this.config.isAutostart());
    this.config.setAutostart(true);
    Assertions.assertTrue(this.config.isAutostart());
  }
  
  @Test
  @DisplayName("getLogMessageFormat")
  public void getLogMessageFormat() {
    Assertions.assertEquals(SimulationConfig.LOG_MESSAGE_FORMAT_VALUE, this.config.getLogMessageFormat());
  }
  
  @Test
  @DisplayName("setLogMessageFormat")
  public void setLogMessageFormat() {
    this.config.setLogMessageFormat("a");
    Assertions.assertEquals("a", this.config.getLogMessageFormat());
    this.config.setLogMessageFormat("b");
    Assertions.assertEquals("b", this.config.getLogMessageFormat());
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
  public SimulationConfigTest() {
    super();
  }
}
