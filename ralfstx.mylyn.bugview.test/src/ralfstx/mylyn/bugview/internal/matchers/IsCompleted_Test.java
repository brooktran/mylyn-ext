/*******************************************************************************
 * Copyright (c) 2011 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.mylyn.bugview.internal.matchers;

import static org.junit.Assert.*;
import static ralfstx.mylyn.bugview.test.TestUtil.*;

import org.hamcrest.StringDescription;
import org.junit.Test;

import ralfstx.mylyn.bugview.TaskMatcher;


public class IsCompleted_Test {

  @Test
  public void matches() throws Exception {
    TaskMatcher matcher = new IsCompleted();

    assertTrue( matcher.matches( mockCompletedTask() ) );
    assertFalse( matcher.matches( mockUncompletedTask() ) );
  }

  @Test
  public void description() throws Exception {
    assertEquals( "isCompleted", StringDescription.toString( new IsCompleted() ) );
  }

}
