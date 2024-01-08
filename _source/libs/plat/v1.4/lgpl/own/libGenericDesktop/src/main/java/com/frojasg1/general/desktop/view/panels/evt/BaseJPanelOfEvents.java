/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.evt;

import com.frojasg1.general.desktop.view.panels.evt.map.ButtonEventEntryMultiMap;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJButton;
import com.frojasg1.general.desktop.view.panels.CustomJPanelBase;
import com.frojasg1.general.desktop.view.panels.evt.map.ButtonEventEntry;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.listeners.consumer.ListOfConsumerListenersBase;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <ET>	- Event type
 * @param <EV>	- Event
 */
public abstract class BaseJPanelOfEvents<ET, EV extends PanelEventBase<ET>> extends CustomJPanelBase implements ComposedComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseJPanelOfEvents.class);

	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected ActionListener _actionListener;

	protected Class<ET> _eventTypeClass;
	protected ButtonEventEntryMultiMap<ET> _multiMap;

	protected ListOfConsumerListenersBase<EV> _listenersList;

	/**
	 * Creates new form PlayerNavigatorJPanel
	 */
	public BaseJPanelOfEvents( Class<ET> eventTypeClass )
	{
		_eventTypeClass = eventTypeClass;
	}

	public void init()
	{
		_listenersList = createListenersList();

		initComponentsChild();

		configureButtons();

		setWindowConfiguration();
	}

	protected ButtonEventEntryMultiMap createEmptyMultiMap()
	{
		ButtonEventEntryMultiMap result = new ButtonEventEntryMultiMap( _eventTypeClass );
		result.init();
		return( result );
	}

	protected ListOfConsumerListenersBase<EV> createListenersList()
	{
		return( new ListOfConsumerListenersBase<>() );
	}

	protected abstract void initComponentsChild();

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

	protected abstract JPanel getParentPanel();

	protected Collection<ButtonEventEntry<ET>> getButtonEventEntries()
	{
		return( _multiMap.getMapByName(ButtonEventEntryMultiMap.EVENT_TYPE, _eventTypeClass).values() );
	}

	protected void putResizeRelocateItemParticularItems( MapResizeRelocateComponentItem mapRRCI ) throws InternException
	{
		for( ButtonEventEntry<ET> entry: getButtonEventEntries() )
			mapRRCI.putResizeRelocateComponentItem( entry.getButton(), ResizeRelocateItem.MOVE_ALL_SIDES_PROPORTIONAL );
	}

	protected void setWindowConfiguration()
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
//			boolean postponeInit = true;
//			mapRRCI.putResizeRelocateComponentItem( this, ResizeRelocateItem.FILL_WHOLE_WIDTH, postponeInit );
			mapRRCI.putResizeRelocateComponentItem( getParentPanel(), ResizeRelocateItem.FILL_WHOLE_PARENT );

			putResizeRelocateItemParticularItems( mapRRCI );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( getParentPanel().getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( getParentPanel().getBounds() );
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();

		removeInternalListeners();
	}

	public void addListener( Consumer<EV> listener )
	{
		_listenersList.add( listener );
	}

	public ButtonEventEntryMultiMap<ET> getMultiMap() {
		return _multiMap;
	}

	protected abstract EV createEvent( ET type );

	protected ButtonEventEntry<ET> getButtonEventEntry( ET eventType )
	{
		return( _multiMap.getEntryOfEventType(eventType) );
	}

	public AbstractButton getButton( ET eventType )
	{
		return( getButtonEventEntry( eventType ).getButton() );
	}

	protected EV createEvent( String imageResourceName )
	{
		return( createEvent( _multiMap.getEntryOfImageResource( imageResourceName ).getEventType() ) );
	}

	protected void setNewImageResource( Object button, String imageResourceName )
	{
		if( button instanceof ResizableImageJButton )
			( (ResizableImageJButton) button).setImageResource(imageResourceName);
	}

	protected void enableAllAssociated( ET eventType )
	{
		enableAllAssociated( eventType, true );
	}

	protected void disableAllAssociated( ET eventType )
	{
		enableAllAssociated( eventType, false );
	}

	protected void enableAllAssociated( ET eventType, boolean value )
	{
		ButtonEventEntry<ET> entry = getButtonEventEntry( eventType );
		if( entry != null )
			entry.getAssociatedButtonList().stream().forEach( but -> but.setEnabled(value) );
	}

	protected void actionPerformed( ActionEvent evt )
	{
		Object button = evt.getSource();
		String imageResourceName = getImageResourceName( button );
		EV event = createEvent( imageResourceName );
		if( event != null )
			_listenersList.notifyEvt( event );
	}

	public ActionListener getActionListener() {
		if( _actionListener == null )
			_actionListener = this::actionPerformed;

		return( _actionListener );
	}

	protected void addInternalListeners()
	{
		Set<Component> added = new HashSet<>();
		for( ButtonEventEntry<ET> entry: getButtonEventEntries() )
		{
			AbstractButton button = entry.getButton();
			if( !added.contains( button ))
			{
				addActionListener( button );
				added.add( button );
			}
		}
	}

	protected void addActionListener( AbstractButton button )
	{
		button.addActionListener(getActionListener());
	}

	protected void removeInternalListeners()
	{
		if( _actionListener != null )
		{
			for( ButtonEventEntry<ET> entry: getButtonEventEntries() )
				entry.getButton().removeActionListener(_actionListener);
		}
	}

	protected String getImageResourceName( Object button )
	{
		String result = getDirectImageResourceName( button );
		if( result == null )
			result = getAssociatedImageResourceName( button );

		return( result );
	}

	protected String getAssociatedImageResourceName( Object button )
	{
		String result = null;
		for( ButtonEventEntry<ET> entry: getButtonEventEntries() )
			if( entry.getAssociatedButtonList().contains(button) )
			{
				result = entry.getImageResource();
				break;
			}

		return( result );
	}

	protected String getDirectImageResourceName( Object button )
	{
		String result = null;

		if( button instanceof ResizableImageJButton )
			result = ( (ResizableImageJButton) button ).getResourceName();

		return result;
	}

	public void addAssociatedButton( ET eventType, AbstractButton button )
	{
		ButtonEventEntry<ET> entry = _multiMap.getEntryOfEventType(eventType);
		if( entry != null )
		{
			entry.addAssociatedButton(button);
			addActionListener(button);
		}
	}

	protected abstract void fillMultiMap( ButtonEventEntryMultiMap<ET> result );

	protected ButtonEventEntryMultiMap<ET> createMultiMap()
	{
		ButtonEventEntryMultiMap<ET> result = createEmptyMultiMap();

		fillMultiMap( result );

		return( result );
	}

	protected void configureButtons()
	{
		_multiMap = createMultiMap();

		addInternalListeners();
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		_multiMap.setComponentMapper(compMapper);

		super.setComponentMapper(compMapper);
	}
}