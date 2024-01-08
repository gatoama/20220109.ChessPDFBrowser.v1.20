/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper;

import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventType;
import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventBuilder;
import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEvent;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.CallStackFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.edt.EventDispatchThreadFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.transfer.file.FileTransferHandler;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponentBase;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import com.frojasg1.general.update.Updateable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JViewport;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class JComponentWrapper<JC extends JComponent, UI extends ComponentUI,
										EV extends JComponentWrapperEvent<JC>,
										CC>
	extends InternallyMappedComponentBase
	implements ReleaseResourcesable, Updateable//, Blockable
{
	protected JC _jComponent;
	protected FileTransferHandler<CC> _fileTransferHandler;
	protected UI _compUI;

	protected BaseJPopupMenu _basePopupMenu = null;

	protected ListOfJComponentWrapperEventContextListenersBase<JC, EV> _listenersList;

	protected JComponentWrapperEventBuilder<EV> _eventBuilder;

	protected boolean _hasBeenAlreadyMapped;

	public JComponentWrapper( JC jComponent, JComponentWrapperEventBuilder<EV> eventBuilder )
	{
		super( jComponent );

		_jComponent = jComponent;
		_eventBuilder = eventBuilder;
	}

	protected void init()
	{
		setUI();

		setFileTransfer();

		_basePopupMenu = createBasePopupMenu();
		_listenersList = createListenersList();

		addListeners();

//		changeViewportUIToBlockable();
	}

	protected JViewport getViewport()
	{
		return( ComponentFunctions.instance().getViewport( getJComponent() ) );
	}
/*
	protected void changeViewportUIToBlockable()
	{
		JViewport vp = getViewport();
		if( vp != null )
		{
			BasicViewportBlockableUI viewportUI = new BasicViewportBlockableUI().init();
			vp.setUI(viewportUI);
		}
	}

	@Override
	public void block()
	{
		setBlocked(true);
	}

	@Override
	public void unblock()
	{
		setBlocked(false);
	}

	protected BasicViewportBlockableUI getBlockableViewportUI()
	{
		return( (BasicViewportBlockableUI) ComponentFunctions.instance().getUI( getViewport() ) );
	}

	@Override
	public void setBlocked(boolean value)
	{
		getBlockableViewportUI().setBlocked( value );
	}

	@Override
	public boolean isBlocked() {
		return( getBlockableViewportUI().isBlocked() );
	}
*/
	public JComponentWrapperEventBuilder<EV> getEventBuilder() {
		return _eventBuilder;
	}

	protected ListOfJComponentWrapperEventContextListenersBase<JC, EV> createListenersList()
	{
		return( new ListOfJComponentWrapperEventContextListenersBase<>() );
	}

	protected ListOfJComponentWrapperEventContextListenersBase<JC, EV> getListenersList()
	{
		return( _listenersList );
	}

	public void addWrapperListener( Consumer<EV> listener, Long ... eventTypes )
	{
		getListenersList().addListenerContext(listener, eventTypes);
	}

	public void removeWrapperListener( Consumer<EV> listener )
	{
		getListenersList().remove(listener);
	}

	public void notifyEvt( EV evt )
	{
		getListenersList().notifyEvt(evt);
	}

	protected BaseJPopupMenu getBasePopupMenu()
	{
		return( _basePopupMenu );
	}

	protected abstract BaseJPopupMenu createBasePopupMenu();

	protected void doPopup( MouseEvent evt )
	{
		BaseJPopupMenu popupMenu = getBasePopupMenu();
		if( popupMenu != null )
			popupMenu.doPopup(evt);
	}

	public JC getJComponent()
	{
		return( _jComponent );
	}

	protected abstract FileTransferHandler<CC> createFileTransferHandler();

	protected void setFileTransfer()
	{
		_fileTransferHandler = createFileTransferHandler();
		if( _fileTransferHandler != null )
			getJComponent().setTransferHandler(_fileTransferHandler);
	}

	protected UI createUI()
	{
		return( null );
	}

	public UI getCompUI() {
		return _compUI;
	}

	protected abstract void installUI( UI compUI );

	protected synchronized void setCompUI(UI compUI) {
		this._compUI = compUI;
		if( compUI != null )
			installUI( compUI );
	}

	protected synchronized void setUI()
	{
		UI compUI = createUI();
		setCompUI( compUI );
		updateUI(compUI);
	}

	protected abstract void updateUI( UI compUI );

	public FileTransferHandler getFileTransferHandler() {
		return _fileTransferHandler;
	}

	protected void setFileTransferHandler(FileTransferHandler _fileTransferHandler) {
		this._fileTransferHandler = _fileTransferHandler;
	}

	@Override
	public void releaseResources()
	{
		releaseResources( getFileTransferHandler() );
		releaseResources( getCompUI() );

		getListenersList().clear();

		removeListeners();
	}

	protected abstract void addListeners();
	protected abstract void removeListeners();

	protected void releaseResources( Object obj )
	{
		if( obj instanceof ReleaseResourcesable )
			ExecutionFunctions.instance().safeMethodExecution( () -> ( (ReleaseResourcesable) obj).releaseResources() );
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor( getJComponent() ) );
	}

	protected Font getFont()
	{
		return( getJComponent().getFont() );
	}

	protected int zoomValue( int value )
	{
		return( zoomValue( value, getZoomFactor() ) );
	}

	protected int unzoomValue( int value )
	{
		return( unzoomValue( value, getZoomFactor() ) );
	}

	protected int zoomValue( int value, double factor )
	{
		return( IntegerFunctions.zoomValueFloor( value, factor ) );
	}

	protected int unzoomValue( int value, double factor )
	{
		return( IntegerFunctions.zoomValueCeil( value, 1 / factor ) );
	}

	protected double getZoomFactor()
	{
		return( FrameworkComponentFunctions.instance().getZoomFactor( getJComponent() ) );
	}

	protected EV createEvt( long eventType )
	{
		return( getEventBuilder().buildEvent(eventType) );
	}

	public <EVT extends EV> EVT createEvt( Class<EVT> clazz )
	{
		return( getEventBuilder().buildEvent(clazz) );
	}

	protected void notifyHasBeenModified()
	{
		notifyEvt( createEvt( JComponentWrapperEventType.HAS_BEEN_MODIFIED ) );
	}

	protected boolean wasLatestModeDark()
	{
		return( FrameworkComponentFunctions.instance().wasLatestModeDark( getJComponent() ) );
	}

	public JScrollPane getScrollPane( Component component )
	{
		return( ComponentFunctions.instance().getScrollPane( component ) );
	}

	public Locale getOutputLocale()
	{
		return( FrameworkComponentFunctions.instance().getOutputLocale( getJComponent() ) );
	}

	@Override
	public void setComponentMapper( ComponentMapper mapper )
	{
		_jComponent = mapper.mapComponent( _jComponent );

		_hasBeenAlreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _hasBeenAlreadyMapped );
	}

	public abstract void updateInternal();

	@Override
	public void update()
	{
		EventDispatchThreadFunctions.instance().edtAsinchronousMethodExecution(this::updateInternal);
	}

	protected InternationalizedWindow getWindow()
	{
		return( FrameworkComponentFunctions.instance().getInternationalizedWindow( getJComponent() ) );
	}

	protected void taskWithClock( Runnable runnable )
	{
		getWindow().changeToWaitCursor();
		try
		{
			runnable.run();
		}
		finally
		{
			getWindow().revertChangeToWaitCursor();
		}
	}

	protected List<Exception> createCallStacksList()
	{
		return( new ArrayList<>() );
	}

	protected CallStackFunctions getCallStackFun()
	{
		return( CallStackFunctions.instance() );
	}

	public void addCallStack( String message, List<Exception> callStacks )
	{
		getCallStackFun().addCallStack( message, callStacks );
	}

	public void logCallStacks( String message, List<Exception> callStacks )
	{
		getCallStackFun().logCallStacks( message, callStacks );
	}

	protected boolean isEmpty( int[] array )
	{
		return( ArrayFunctions.instance().isEmpty( array ) );
	}
}
