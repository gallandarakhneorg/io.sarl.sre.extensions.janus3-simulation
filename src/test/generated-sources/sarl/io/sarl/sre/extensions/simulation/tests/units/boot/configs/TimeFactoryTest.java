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
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import java.util.concurrent.TimeUnit;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@SarlSpecification("0.10")
@SarlElementType(10)
public class TimeFactoryTest extends AbstractSarlTest {
  @Nullable
  private TimeConfig config;
  
  @Before
  public void setUp() {
    TimeConfig _timeConfig = new TimeConfig();
    this.config = _timeConfig;
  }
  
  @Test
  @Pure
  public void getTimeStep() {
    AbstractSarlTest.assertEpsilonEquals(TimeConfig.TIME_STEP_VALUE, this.config.getTimeStep());
  }
  
  @Test
  public void setTimeStep() {
    AbstractSarlTest.assertEpsilonEquals(TimeConfig.TIME_STEP_VALUE, this.config.getTimeStep());
    this.config.setTimeStep(123.456);
    AbstractSarlTest.assertEpsilonEquals(123.456, this.config.getTimeStep());
  }
  
  @Test
  @Pure
  public void getStartTime() {
    AbstractSarlTest.assertEpsilonEquals(TimeConfig.START_TIME_VALUE, this.config.getStartTime());
  }
  
  @Test
  public void setStartTime() {
    AbstractSarlTest.assertEpsilonEquals(TimeConfig.START_TIME_VALUE, this.config.getStartTime());
    this.config.setStartTime(123.456);
    AbstractSarlTest.assertEpsilonEquals(123.456, this.config.getStartTime());
  }
  
  @Test
  @Pure
  public void getSimulationLoopDelay() {
    Assert.assertEquals(TimeConfig.SIMULATION_LOOP_STEP_DELAY_VALUE, this.config.getSimulationLoopDelay());
  }
  
  @Test
  public void setSimulationLoopDelay() {
    Assert.assertEquals(TimeConfig.SIMULATION_LOOP_STEP_DELAY_VALUE, this.config.getSimulationLoopDelay());
    this.config.setSimulationLoopDelay(123456);
    Assert.assertEquals(123456l, this.config.getSimulationLoopDelay());
  }
  
  @Test
  @Pure
  public void getUnit() {
    AbstractSarlTest.assertEquals(TimeUnit.SECONDS, this.config.getUnit());
  }
  
  @Test
  public void setUnit() {
    AbstractSarlTest.assertEquals(TimeUnit.SECONDS, this.config.getUnit());
    TimeUnit[] _values = TimeUnit.values();
    for (final TimeUnit tu : _values) {
      {
        this.config.setUnit(tu);
        Assert.assertSame(tu, this.config.getUnit());
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
  public TimeFactoryTest() {
    super();
  }
}
