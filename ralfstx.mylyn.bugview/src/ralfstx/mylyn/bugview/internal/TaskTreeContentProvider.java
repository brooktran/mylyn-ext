package ralfstx.mylyn.bugview.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * <B>TaskContentProvider</B>
 * 
 * @author Brook Tran. Email: <a href="mailto:Brook.Tran.C@gmail.com">Brook.Tran.C@gmail.com</a>
 * @since  2012-8-11 created
 */
@SuppressWarnings( "restriction" )
public class TaskTreeContentProvider implements ITreeContentProvider, ITreePathContentProvider {
//  protected BugView bugView;

  protected static Object[] EMPTY_ARRRY = new Object[0];

  public TaskTreeContentProvider() {
//    this.bugView = bugView;
  }

  public Object[] getElements( Object parent ) {
    List<AbstractTaskContainer> children = new LinkedList<AbstractTaskContainer>();
    for(Object obj:(Object[] )parent){
      if(! (obj instanceof AbstractTaskContainer)){
        continue;
      }
      AbstractTaskContainer container=(AbstractTaskContainer)obj;
      if(container.getChildren().size()>0){
        children.add( container );
      }
    }
    return children.toArray();
  }
  
  public void dispose() {
  }

  public void inputChanged(final Viewer viewer, Object oldInput, Object newInput ) {
    final ITask task = TasksUi.getTaskActivityManager().getActiveTask();
    if( task != null ) {
      viewer.getControl().getShell().getDisplay().asyncExec( new Runnable() {
        public void run() {
          ( (TreeViewer)viewer ).expandToLevel( task, 0 );
        }
      } );
    }
  }

  public Object[] getChildren( TreePath parentPath ) {//getFilteredRootChildren
    ITaskContainer parent = (ITaskContainer)parentPath.getLastSegment();
    Collection<ITask> parentTasks = parent.getChildren();
    Set<IRepositoryElement> parents = new HashSet<IRepositoryElement>();
    Set<ITask> children = new HashSet<ITask>();
    
    // get all children
    for (ITask element : parentTasks) {
        if (element instanceof ITaskContainer) {
            for (ITask abstractTask : ((ITaskContainer) element).getChildren()) {
                children.add(abstractTask);
            }
        }
    }
    for (ITask task : parentTasks) {
        if (!children.contains(task)) {
            parents.add(task);
        }
    }
    return parents.toArray();
  }

  public TreePath[] getParents( Object element ) {
    return new TreePath[0];
  }

  public boolean hasChildren( TreePath path ) {
    return getChildren( path ).length > 0;
  }

  public Object[] getChildren( Object parent ) {
    return ((AbstractTaskContainer)parent).getChildren().toArray();
  }

 

  public Object getParent( Object child ) {
    // return first parent found, first search within categories then queries
    if( child instanceof ITask ) {
      ITask task = (ITask)child;
//      AbstractTaskCategory parent = TaskCategory.getParentTaskCategory( task );
//      if( parent != null ) {
//        return parent;
//      }

      Set<AbstractTaskContainer> parents = ( (AbstractTask)task ).getParentContainers();
      Iterator<AbstractTaskContainer> it = parents.iterator();
      if( it.hasNext() ) {
        return parents.iterator().next();
      }
    }
    // no parent found
    return null;
  }
  public boolean hasChildren( Object parent ) {
    Collection<ITask> children =( (AbstractTask)parent ).getChildren();
    return children.size() >0; 
  }

}
