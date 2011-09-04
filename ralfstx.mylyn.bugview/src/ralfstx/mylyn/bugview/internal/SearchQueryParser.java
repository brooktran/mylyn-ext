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
package ralfstx.mylyn.bugview.internal;

import java.util.ArrayList;

import org.eclipse.mylyn.tasks.core.ITask;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;


public class SearchQueryParser {

  public Matcher<ITask> parse( String query ) {
    if( query == null ) {
      throw new NullPointerException( "null parameter: query" );
    }
    ArrayList<Matcher<? extends ITask>> result = new ArrayList<Matcher<? extends ITask>>();
    String[] parts = query.split( "\\s+" );
    for( String part : parts ) {
      if( part.length() > 0 ) {
        result.add( new NameOrIdMatcher( part ) );
      }
    }
    if( result.size() == 0 ) {
      return CoreMatchers.anything();
    }
    return CoreMatchers.allOf( result );
  }

}