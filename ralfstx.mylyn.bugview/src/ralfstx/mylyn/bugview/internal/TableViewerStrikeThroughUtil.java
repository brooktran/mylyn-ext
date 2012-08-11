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

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;


public class TableViewerStrikeThroughUtil {

  public static void attach( StrikeThroughProvider provider, ColumnViewer viewer ) {
    Listener customDrawer = new StrikeThroughListener( provider );
    Control control = viewer.getControl();
    control.addListener( SWT.EraseItem, customDrawer );
    control.addListener( SWT.PaintItem, customDrawer );
  }


  static void strikeThroughTableItem( Item item, GC gc ) {
    
    int columnCount = getColumnCount(item);
    
    for( int column = 0; column < columnCount; column++ ) {
      String text = getItemText(item,column);
      Point extent = gc.textExtent( text );
      Rectangle bounds = getItemTextBounds(item,column);
      int lineY = bounds.y + bounds.height / 2;
      gc.drawLine( bounds.x, lineY, bounds.x + extent.x, lineY );
    }
  }

  private static Rectangle getItemTextBounds( Item item, int column ) {
    if(item instanceof TreeItem){
      return ((TreeItem)item).getTextBounds( column );
    }else if(item instanceof TableItem){
      return ((TableItem)item).getTextBounds( column );
    }
    return null;
  }


  private static String getItemText( Item item, int column ) {
    if(item instanceof TreeItem){
      return ((TreeItem)item).getText( column );
    }else if(item instanceof TableItem){
      return ((TableItem)item).getText( column );
    }
    return item.getText();
  }

  private static int getColumnCount( Item item ) {
    if(item instanceof TreeItem){
      return ((TreeItem)item).getParent().getColumnCount();
    }else if(item instanceof TableItem){
      return ((TableItem)item).getParent().getColumnCount();
    }
    return 0;
  }

  private static class StrikeThroughListener implements Listener {
    private final StrikeThroughProvider provider;

    public StrikeThroughListener( StrikeThroughProvider provider ) {
      this.provider = provider;
    }

    public void handleEvent( Event event ) {
      if( provider.getStrikeThrough( event.item.getData() ) ) {
        strikeThroughTableItem((Item) event.item, event.gc );
      }
    }
  }

  
  

}
