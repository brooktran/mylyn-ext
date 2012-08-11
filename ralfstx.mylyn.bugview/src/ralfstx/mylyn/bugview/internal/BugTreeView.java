package ralfstx.mylyn.bugview.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import ralfstx.mylyn.bugview.TaskMatchers;

/**
 * <B>TreeBugView</B>
 * 
 * @author Brook Tran. Email: <a href="mailto:Brook.Tran.C@gmail.com">Brook.Tran.C@gmail.com</a>
 * @since  2012-8-11 created
 */
public class BugTreeView extends AbstractBugView {
  static final int COL_ID = 0;
  static final int COL_TITLE = 1;

  private TreeViewer viewer;
  private Text searchField;
  private IRepositoryQuery activeQuery;
  private Matcher<ITask> toolbarMatcher = CoreMatchers.anything(); //TODO IRepositoryElement / move to new viewPart file.
  private Matcher<ITask> searchMatcher = CoreMatchers.anything();
  private final SearchQueryParser queryParser = new SearchQueryParser();
  private WordProposalProvider proposalProvider;

  @Override
  public void createPartControl( Composite parent ) {
    parent.setLayout( createMainLayout() );
    createQuickFilterArea( parent );
    createSearchTextField( parent );
    createTableViewer( parent );
    addContentProposalToSearchField();
    makeActions();
    refreshViewer();
    refreshAutoSuggestions();
  }

  @Override
  public void setFocus() {
    searchField.setFocus();
  }

  @Override
  public void setActiveQuery( IRepositoryQuery query ) {
    activeQuery = query;
    refreshViewer();
    refreshAutoSuggestions();
  }


  private void createQuickFilterArea( Composite parent ) {
    final QuickFilterArea filterArea = new QuickFilterArea( parent, SWT.NONE );
    filterArea.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    addQuickFilterContributions( filterArea );
    filterArea.setMatcherChangedListener( new Runnable() {
      public void run() {
        toolbarMatcher = filterArea.getMatcher();
        refreshFilter();
      }
    } );
  }

  private static void addQuickFilterContributions( final QuickFilterArea filterArea ) {
    QuickFilterContribution showIncoming = createIncomingContribution();
    QuickFilterContribution showOutgoing = createOutgoingContribution();
    filterArea.createToolBar( showIncoming, showOutgoing );
    QuickFilterContribution showEnhancements = createEnhancementsContribution();
    QuickFilterContribution showDefects = createDefectsContribution();
    filterArea.createToolBar( showEnhancements, showDefects );
    QuickFilterContribution hideCompleted = createHideCompletedContribution();
    filterArea.createToolBar( hideCompleted );
  }

  private static QuickFilterContribution createIncomingContribution() {
    return new QuickFilterContribution( "show only incoming",
                                        Activator.getImageDescriptor( "/icons/incoming.png" ),
                                        TaskMatchers.isIncoming() );
  }

  private static QuickFilterContribution createOutgoingContribution() {
    return new QuickFilterContribution( "show only outgoing",
                                        Activator.getImageDescriptor( "/icons/outgoing.png" ),
                                        TaskMatchers.isOutgoing() );
  }

  private static QuickFilterContribution createEnhancementsContribution() {
    return new QuickFilterContribution( "show only enhancements",
                                        Activator.getImageDescriptor( "/icons/enhancement.png" ),
                                        TaskMatchers.isEnhancement() );
  }

  private static QuickFilterContribution createDefectsContribution() {
    return new QuickFilterContribution( "show only defects",
                                        Activator.getImageDescriptor( "/icons/defect.png" ),
                                        CoreMatchers.not( TaskMatchers.isEnhancement() ) );
  }

  private static QuickFilterContribution createHideCompletedContribution() {
    return new QuickFilterContribution( "hide completed",
                                        Activator.getImageDescriptor( "/icons/hidecompleted.png" ),
                                        CoreMatchers.not( TaskMatchers.isCompleted() ) );
  }

  private void createSearchTextField( Composite parent ) {
    searchField = new Text( parent, SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH | SWT.ICON_CANCEL );
    searchField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    searchField.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        String query = searchField.getText().trim();
        searchMatcher = queryParser.parse( query );
        refreshFilter();
      }
    } );
  }

  private void addContentProposalToSearchField() {
    proposalProvider = new WordProposalProvider();
    TextContentAdapter controlAdapter = new TextContentAdapter();
    ContentProposalAdapter proposalAdapter =
        new ContentProposalAdapter( searchField, controlAdapter, proposalProvider, null, null );
    proposalAdapter.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );
  }

  private void refreshFilter() {
    viewer.refresh( false );
    updateStatusBar();
  }

  private void createTableViewer( Composite parent ) {
    Tree tree = new Tree( parent, SWT.VIRTUAL | SWT.FULL_SELECTION );
    tree.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    tree.setLinesVisible( true );
    tree.setHeaderVisible( true );
    viewer = new TreeViewer( tree );
    TaskTreeLabelProvider taskTreeLabelProvider= new TaskTreeLabelProvider() ;
    viewer.setLabelProvider(taskTreeLabelProvider.getLabelProvider()); 
    viewer.setContentProvider( new TaskTreeContentProvider() );
    viewer.setComparator( new TaskLastModifiedComparator() );
    viewer.addFilter( new TaskViewerFilter() );
    
    
    TreeViewerColumn idColumn = createViewerColumn("summary",SWT.LEFT,200 );
    idColumn.setLabelProvider(taskTreeLabelProvider);
    
    TreeViewerColumn summaryColumn =createViewerColumn("id",SWT.RIGHT,80 );
    summaryColumn.setLabelProvider( new ColumnLabelProvider(){
      @Override
      public String getText( Object element ) {
        if( element instanceof ITask ) {
          ITask task = (ITask)element;
            return task.getTaskId();
          }
        return "";
      }
    });
    
    addDoubleClickBehavior();
  }

  private TreeViewerColumn createViewerColumn(String text, int style, int width ) {
    TreeViewerColumn column = new TreeViewerColumn( viewer, style);
    column.getColumn().setText( text );
    column.getColumn().setWidth( width );
    column.getColumn().setResizable( true );
    column.getColumn().setMoveable( true );
    return column;
  }

  private void addDoubleClickBehavior() {
    viewer.addDoubleClickListener( new IDoubleClickListener() {
      public void doubleClick( DoubleClickEvent event ) {
        ITask selectedTask = getSelectedTask();
        if( selectedTask != null ) {
          MylynBridge.openTaskInEditor( selectedTask );
        }
      }
    } );
  }

  private void makeActions() {
    ImageDescriptor refreshImage = Activator.getImageDescriptor( "/icons/refresh.gif" );
    IAction refreshViewAction = new Action( "Refresh View", refreshImage ) {
      @Override
      public void run() {
        refreshViewer();
        refreshAutoSuggestions();
      }
    };
    ImageDescriptor refreshAllImage =
        Activator.getImageDescriptor( "/icons/repository-synchronize.gif" );
    IAction refreshAllAction = new Action( "Refresh all Repositories", refreshAllImage ) {
      @Override
      public void run() {
        MylynBridge.synchronizeAllRepositories();
      }
    };
    IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
    toolBarManager.add( refreshViewAction );
    toolBarManager.add( refreshAllAction );
    IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
    menuManager.add( new QueryFilterDropDownMenuAction( this ) );
  }

  @SuppressWarnings( "restriction" )
  private void refreshViewer() {
    viewer.setInput( TasksUiPlugin.getTaskList().getRootElements().toArray() );
    updateStatusBar();
  }

  private void refreshAutoSuggestions() {
    ArrayList<String> suggestions = new ArrayList<String>();
    List<String> initialSuggestions = SearchQueryParser.getSuggestions();
    List<String> allTags = findAllTags();
    suggestions.addAll( initialSuggestions );
    suggestions.addAll( allTags );
    proposalProvider.setSuggestions( suggestions );
  }

  private List<String> findAllTags() {
    List<String> allTags = new ArrayList<String>();
    Collection<ITask> tasks = getTasks();
    HashTagParser tagParser = new HashTagParser();
    for( ITask task : tasks ) {
      String notes = MylynBridge.getNotes( task );
      if( notes != null ) {
        List<String> foundTags = tagParser.parse( notes );
        for( String tag : foundTags ) {
          String prefixedTag = "#" + tag;
          if( !allTags.contains( prefixedTag ) ) {
            allTags.add( prefixedTag );
          }
        }
      }
    }
    return allTags;
  }

  private Collection<ITask> getTasks() {
    if( activeQuery != null ) {
      return MylynBridge.getAllTasks( activeQuery );
    }
    return MylynBridge.getAllTasks();
  }

  private ITask getSelectedTask() {
    ISelection selection = viewer.getSelection();
    if( !selection.isEmpty() && selection instanceof StructuredSelection ) {
      StructuredSelection structuredSelection = (StructuredSelection)selection;
      Object element = structuredSelection.getFirstElement();
      if( element instanceof ITask ) {
        return (ITask)element;
      }
    }
    return null;
  }

  private void updateStatusBar() {
    String message = null;
    int items = viewer.getTree().getItemCount();
    message = items + " bugs";
    IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
    statusLineManager.setMessage( message );
  }

  private static GridLayout createMainLayout() {
    GridLayout mainLayout = new GridLayout();
    mainLayout.marginWidth = 0;
    mainLayout.marginHeight = 0;
    mainLayout.verticalSpacing = 0;
    return mainLayout;
  }


  
  private static final class TaskLastModifiedComparator extends ViewerComparator {
    @Override
    public int compare( Viewer viewer, Object element1, Object element2 ) {
      int result = 0;
      if( element1 instanceof ITask && element2 instanceof ITask ) {
        result = compareModificationDate( (ITask)element1, (ITask)element2 ) * -1;
      }
      return result;
    }

    private static int compareModificationDate( ITask task1, ITask task2 ) {
      int result;
      Date modDate1 = task1.getModificationDate();
      Date modDate2 = task2.getModificationDate();
      if( modDate1 == null ) {
        result = modDate2 == null ? 0 : -1;
      } else {
        result = modDate2 == null ? 1 : modDate1.compareTo( modDate2 );
      }
      return result;
    }
  }

  private final class TaskViewerFilter extends ViewerFilter {
    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {
      if( element instanceof ITask ) {
        ITask task = (ITask)element;
        return toolbarMatcher.matches( task ) && searchMatcher.matches( task );
      }
      if(element instanceof ITaskContainer){
        ITaskContainer container = (ITaskContainer)element;
        return toolbarMatcher.matches( container ) && searchMatcher.matches( container );
      }
      return false;
    }
  }}
