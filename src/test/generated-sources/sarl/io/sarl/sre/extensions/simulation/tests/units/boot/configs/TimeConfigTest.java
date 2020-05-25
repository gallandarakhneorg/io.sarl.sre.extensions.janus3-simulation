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

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.boot.configs.TimeConfig;
import io.sarl.sre.test.framework.extension.PropertyRestoreExtension;
import io.sarl.tests.api.Nullable;
import io.sarl.tests.api.extensions.ContextInitExtension;
import io.sarl.tests.api.extensions.JavaVersionCheckExtension;
import io.sarl.tests.api.tools.TestAssertions;
import java.util.concurrent.TimeUnit;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@ExtendWith({ ContextInitExtension.class, JavaVersionCheckExtension.class, PropertyRestoreExtension.class })
@DisplayName("unit: TimeConfig test")
@Tag("unit")
@Tag("janus")
@Tag("sre-unit")
@Tag("sre-simulation")
@SarlSpecification("0.11")
@SarlElementType(10)
@SuppressWarnings("all")
public class TimeConfigTest {
  @Nullable
  private TimeConfig config;
  
  @BeforeEach
  public void setUp() {
    TimeConfig _timeConfig = new TimeConfig();
    this.config = _timeConfig;
  }
  
  @Test
  @DisplayName("getStartStep")
  public void getTimeStep() {
    TestAssertions.assertEpsilonEquals(TimeConfig.TIME_STEP_VALUE, this.config.getTimeStep());
  }
  
  @Test
  @DisplayName("setStartStep")
  public void setTimeStep() {
    TestAssertions.assertEpsilonEquals(TimeConfig.TIME_STEP_VALUE, this.config.getTimeStep());
    this.config.setTimeStep(123.456);
    TestAssertions.assertEpsilonEquals(123.456, this.config.getTimeStep());
  }
  
  @Test
  @DisplayName("getStartTime")
  public void getStartTime() {
    TestAssertions.assertEpsilonEquals(TimeConfig.START_TIME_VALUE, this.config.getStartTime());
  }
  
  @Test
  @DisplayName("setStartTime")
  public void setStartTime() {
    TestAssertions.assertEpsilonEquals(TimeConfig.START_TIME_VALUE, this.config.getStartTime());
    this.config.setStartTime(123.456);
    TestAssertions.assertEpsilonEquals(123.456, this.config.getStartTime());
  }
  
  @Test
  @DisplayName("getSimulationLoopDelay")
  public void getSimulationLoopDelay() {
    Assertions.assertEquals(TimeConfig.SIMULATION_LOOP_STEP_DELAY_VALUE, this.config.getSimulationLoopDelay());
  }
  
  @Test
  @DisplayName("setSimulationLoopDelay")
  public void setSimulationLoopDelay() {
    Assertions.assertEquals(TimeConfig.SIMULATION_LOOP_STEP_DELAY_VALUE, this.config.getSimulationLoopDelay());
    this.config.setSimulationLoopDelay(123456);
    Assertions.assertEquals(123456l, this.config.getSimulationLoopDelay());
  }
  
  @Test
  @DisplayName("getUnit")
  public void getUnit() {
    Assertions.assertEquals(TimeUnit.SECONDS, this.config.getUnit());
  }
  
  @Test
  @DisplayName("setUnit")
  public void setUnit() {
    Assertions.assertEquals(TimeUnit.SECONDS, this.config.getUnit());
    TimeUnit[] _values = TimeUnit.values();
    for (final TimeUnit tu : _values) {
      {
        this.config.setUnit(tu);
        Assertions.assertSame(tu, this.config.getUnit());
      }
    }
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
  public TimeConfigTest() {
    super();
  }
}
