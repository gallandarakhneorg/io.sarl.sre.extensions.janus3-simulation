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
package io.sarl.sre.extensions.simulation.tests.units.skills;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.sre.extensions.simulation.skills.TimestampedEvent;
import io.sarl.tests.api.AbstractSarlTest;
import io.sarl.tests.api.Nullable;
import org.eclipse.xtext.xbase.lib.Pure;
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
public class TimestampedEventTest extends AbstractSarlTest {
  @Nullable
  private TimestampedEvent event;
  
  @Before
  public void setUp() {
    TimestampedEvent _timestampedEvent = new TimestampedEvent(123.456);
    this.event = _timestampedEvent;
  }
  
  @Test
  @Pure
  public void getTimestamp() {
    AbstractSarlTest.assertEpsilonEquals(123.456, this.event.timestamp);
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
  public TimestampedEventTest() {
    super();
  }
}
