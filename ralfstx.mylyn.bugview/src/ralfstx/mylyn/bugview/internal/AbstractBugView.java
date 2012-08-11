package ralfstx.mylyn.bugview.internal;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.ui.part.ViewPart;

/**
 * <B>AbstractBugView</B>
 * 
 * @author Brook Tran. Email: <a href="mailto:Brook.Tran.C@gmail.com">Brook.Tran.C@gmail.com</a>
 * @since  2012-8-11 created
 */
public abstract class AbstractBugView extends ViewPart implements IBugView{

  private IRepositoryQuery activeQuery;

  public void setActiveQuery( IRepositoryQuery query ) {
    activeQuery = query;
  }

  public IRepositoryQuery getActiveQuery() {
    return activeQuery;
  }
  

  
} 