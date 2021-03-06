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

import static ralfstx.mylyn.bugview.test.TestUtil.*;

import org.eclipse.mylyn.tasks.core.ITask;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import ralfstx.mylyn.bugview.TaskMatchers;
import ralfstx.mylyn.bugview.internal.matchers.NameOrId;


@SuppressWarnings( "unchecked" )
public class SearchQueryParser_Test {

  private SearchQueryParser parser;

  @Before
  public void setup() {
    parser = new SearchQueryParser();
  }

  @Test( expected = NullPointerException.class )
  public void parse_withNull() throws Exception {
    parser.parse( null );
  }

  @Test
  public void parse_withEmptyString() throws Exception {
    Matcher<ITask> result = parser.parse( "" );

    Matcher<ITask> expected = CoreMatchers.anything();
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_withSingleString() throws Exception {
    Matcher<ITask> result = parser.parse( "foo" );

    Matcher<ITask> expected = CoreMatchers.allOf( new NameOrId( "foo" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_withTwoStrings() throws Exception {
    Matcher<ITask> result = parser.parse( "foo bar" );

    Matcher<ITask> expected = CoreMatchers.allOf( new NameOrId( "foo" ),
                                                  new NameOrId( "bar" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_stripsWhitespace() throws Exception {
    Matcher<ITask> result = parser.parse( " foo\t  " );

    Matcher<ITask> expected = CoreMatchers.allOf( new NameOrId( "foo" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_incoming() throws Exception {
    Matcher<ITask> result = parser.parse( ":incoming" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.isIncoming() );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_outgoing() throws Exception {
    Matcher<ITask> result = parser.parse( ":outgoing" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.isOutgoing() );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_open() throws Exception {
    Matcher<ITask> result = parser.parse( ":open" );

    Matcher<ITask> expected = CoreMatchers.allOf( CoreMatchers.not( TaskMatchers.isCompleted() ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_defect() throws Exception {
    Matcher<ITask> result = parser.parse( ":defect" );

    Matcher<ITask> expected = CoreMatchers.allOf( CoreMatchers.not( TaskMatchers.isEnhancement() ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_enhancement() throws Exception {
    Matcher<ITask> result = parser.parse( ":enhancement" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.isEnhancement() );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_product() throws Exception {
    Matcher<ITask> result = parser.parse( "product:foo" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.productMatches( "foo" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_productWithoutName() throws Exception {
    Matcher<ITask> result = parser.parse( "product:" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.productMatches( "" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_owner() throws Exception {
    Matcher<ITask> result = parser.parse( "assigned:foo" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.ownerMatches( "foo" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_hashtag() throws Exception {
    Matcher<ITask> result = parser.parse( "#foo" );

    Matcher<ITask> expected = CoreMatchers.allOf( TaskMatchers.containsHashTag( "foo" ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_not() throws Exception {
    Matcher<ITask> result = parser.parse( "!foo" );

    Matcher<ITask> expected
      = CoreMatchers.allOf( CoreMatchers.not( new NameOrId( "foo" ) ) );
    assertMatcherEquals( expected, result );
  }

  @Test
  public void parse_mixed() throws Exception {
    Matcher<ITask> result = parser.parse( "foo :incoming #foo #bar" );

    Matcher<ITask> expected = CoreMatchers.allOf( new NameOrId( "foo" ),
                                                  TaskMatchers.isIncoming(),
                                                  TaskMatchers.containsHashTag( "foo" ),
                                                  TaskMatchers.containsHashTag( "bar" ) );
    assertMatcherEquals( expected, result );
  }

}
