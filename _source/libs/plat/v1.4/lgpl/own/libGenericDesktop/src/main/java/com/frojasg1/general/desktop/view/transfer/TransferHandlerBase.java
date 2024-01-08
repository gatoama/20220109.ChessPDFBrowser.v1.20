/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer;

import com.frojasg1.general.listeners.filterevents.EventListenerContextBase;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.util.function.Consumer;
import javax.swing.TransferHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *  https://stackoverflow.com/questions/9192371/dragn-drop-files-from-the-os-to-java-application-swing
 * @param <EV>	- Event
 */
public abstract class TransferHandlerBase<EV extends DragAndDropEvent>
	extends TransferHandler
	implements ReleaseResourcesable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferHandlerBase.class);

//	protected List<Consumer<EV>> _listenersList = new ArrayList<>();
	protected ListOfDragAndDropListenersBase<EV> _listeners;

	protected void init()
	{
		_listeners = createListenerContainer();
	}

	protected ListOfDragAndDropListenersBase<EV> createListenerContainer()
	{
		return( new ListOfDragAndDropListenersBase<>() );
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		// we only import FileList
		if (!canImportInternal(info)) {
			return false;
		}

		fireEvent( createCanImportEvent( info ) );

		return true;
	}

	protected abstract boolean canImportInternal( TransferHandler.TransferSupport info );

	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		// Check for FileList flavor
		if (!canImportInternal(info)) {
			LOGGER.info("List doesn't accept a drop of this type.");
			return false;
		}

		EV evt = createDroppedEvent( info );

		return ( fireEvent( evt ) );
	}

	protected abstract EV createCanImportEvent(TransferHandler.TransferSupport info);
	protected abstract EV createDroppedEvent(TransferHandler.TransferSupport info);

	protected boolean fireEvent( EV evt )
	{
		getListeners().notifyEvt(evt);

		return( evt != null );
	}
	
/*
	protected boolean fireEvent( EV evt )
	{
		boolean result = false;
		if( evt != null )
		{
			for(Consumer<EV> listener: getListenersList())
				ExecutionFunctions.instance().safeMethodExecution(
					() -> listener.accept(evt) );

			result = true;
		}

		return( result );
	}

	public synchronized void addListener(Consumer<EV> listener) {
		if( ( listener != null ) && ( ! _listenersList.contains( listener ) ) )
			_listenersList.add( listener );
	}

	public synchronized void removeListener(Consumer<EV> listener) {
		_listenersList.remove( listener );
	}

	protected synchronized List<Consumer<EV>> getListenersList() {
		return new ArrayList<>( _listenersList );
	}
*/

	public synchronized EventListenerContextBase<EV, DragAndDropEventType> addListenerContext(Consumer<EV> listenerConsumer, DragAndDropEventType ... types) {
		return( getListeners().addListenerContext( listenerConsumer, types ) );
	}

	public synchronized EventListenerContextBase<EV, DragAndDropEventType> addListener(Consumer<EV> listenerConsumer) {
		return( addListenerContext( listenerConsumer, DragAndDropEventType.ALL ) );
	}

	public synchronized void removeListener(EventListenerContextBase<EV, DragAndDropEventType> listener) {
		getListeners().remove( listener );
	}

	public synchronized void removeListener(Consumer<EV> listenerConsumer) {
		getListeners().remove( listenerConsumer, DragAndDropEventType.ALL );
	}

	@Override
	public void releaseResources()
	{
		_listeners.clear();
		_listeners = null;
	}

	public ListOfDragAndDropListenersBase<EV> getListeners() {
		return _listeners;
	}
}
