/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.mylyn.bugview.internal;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskTableLabelProvider;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;


@SuppressWarnings( "restriction" )
public class TaskTreeLabelProvider extends ColumnLabelProvider implements  ITreePathLabelProvider {

  private TaskTableLabelProvider labelProviderProxy =
      new TaskTableLabelProvider( new TaskElementLabelProvider( true ),
                                  PlatformUI.getWorkbench()
                                            .getDecoratorManager()
                                            .getLabelDecorator()
                                            ,
                                            PlatformUI.getWorkbench().getThemeManager()
  .getCurrentTheme().getColorRegistry().get("org.eclipse.mylyn.tasks.ui.colors.category.gradient.end"));

  @Override
  public String getText( Object element ) {
    try {
      Method method = element.getClass().getMethod( "getSummary" );
      String string = (String)method.invoke( element );
      return string;
    } catch( Exception e ) {
      e.printStackTrace();
    }
    return super.getText( element );
  }

  @Override
  public Color getForeground( Object object ) {
    return labelProviderProxy.getForeground( object );
  }
  @Override
  public Color getBackground( Object element ) {
    return labelProviderProxy.getBackground( element );
  }
  @Override
  public Font getFont( Object element ) {
    return labelProviderProxy.getFont( element );
  }

  @Override
  public Image getImage( Object element ) {
    return labelProviderProxy.getImage( element );
  }

  public void updateLabel( ViewerLabel label, TreePath elementPath ) {
    labelProviderProxy.updateLabel( label, elementPath );
  }
   public IBaseLabelProvider getLabelProvider() {
    return labelProviderProxy;
  }
  @Override
  public void dispose() {
    labelProviderProxy.dispose();
    super.dispose();
  }
  
}
