package ralfstx.mylyn.bugview.internal;

import java.util.Collection;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;


public class BugView extends ViewPart {

  static final int COL_ID = 0;
  static final int COL_TITLE = 1;

  private TableViewer viewer;

  @Override
  public void createPartControl( Composite parent ) {
    parent.setLayout( GridLayoutFactory.fillDefaults().create() );
    createTableViewer( parent );
  }

  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  private void createTableViewer( Composite parent ) {
    Table table = new Table( parent, SWT.VIRTUAL );
    table.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    table.setLinesVisible( true );
    new TableColumn( table, SWT.LEFT ).setWidth( 80 );
    new TableColumn( table, SWT.LEFT ).setWidth( 400 );
    viewer = new TableViewer( table );
    viewer.setLabelProvider( new TaskLabelProvider() );
    viewer.setContentProvider( new ArrayContentProvider() );
    Collection<ITask> tasks = MylynBridge.getAllTasks();
    viewer.setInput( tasks );
  }

  static class TaskLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final ImageDescriptor TASK_ICON = Activator.getImageDescriptor( "/icons/task.gif" );

    public Image getColumnImage( Object element, int columnIndex ) {
      Image result = null;
      if( element instanceof ITask ) {
        if( columnIndex == BugView.COL_ID ) {
          result = TASK_ICON.createImage();
        }
      }
      return result;
    }

    public String getColumnText( Object element, int columnIndex ) {
      String result = null;
      if( element instanceof ITask ) {
        ITask task = (ITask)element;
        if( columnIndex == BugView.COL_ID ) {
          result = task.getTaskId();
        } else if( columnIndex == BugView.COL_TITLE ) {
          result = task.getSummary();
        }
      }
      return result;
    }

  }

}
