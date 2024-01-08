/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class AssociatedComponentEvtBase<EV extends ComponentEvtEvent,
												WW extends ComponentEvtWrapper<EV>>
	extends ComponentEvtWrapperBase<EV>
	implements AssociatedComponentEvt<EV, WW>
{
	protected List<WW> _componentValueWrapperList;

	protected Consumer<EV> _internalListener;

	protected BiFunction<Component, Component, WW> _wrapperBuilder;

	public AssociatedComponentEvtBase( BiFunction<Component, Component, WW> wrapperBuilder )
	{
		_wrapperBuilder = wrapperBuilder;
	}

	public BiFunction<Component, Component, WW> getWrapperBuilder() {
		return _wrapperBuilder;
	}

	protected <C> List<C> createList()
	{
		return( new ArrayList<>() );
	}

	@Override
	protected void initChild()
	{
		_componentValueWrapperList = createList();
		_internalListener = createInternalListener();
	}

	public List<WW> getComponentValueWrapperList() {
		return _componentValueWrapperList;
	}

	protected Consumer<EV> createInternalListener()
	{
		return( this::internalValueChangedListener );
	}

	protected void internalValueChangedListener( EV evt )
	{
		EV newEvt = createEvent( evt );

		getListenersList().notifyEvt( newEvt );
	}

	protected abstract EV createEmptyEvent();

	protected EV createEvent( EV parentEvt )
	{
		EV result = null;

		if( parentEvt != null )
		{
			result = createEmptyEvent();

			result.setSource( createSource( parentEvt.getSource() ) );
		}

		return( result );
	}

	protected WrapperEventSource createSource( WrapperEventSource parentSource )
	{
		WrapperEventSource result = new WrapperEventSource();
		result.setSource(this);
		result.setParentSource(parentSource);

		return( result );
	}

	protected WW createWrapper( Component component, Component parent )
	{
		return( getWrapperBuilder().apply(component, parent) );
	}

	@Override
	public void addComponentToWrap( Component component )
	{
		addComponentToWrap( component, null );
	}

	@Override
	public void addComponentToWrap( Component component, Component parent )
	{
		addComponentEvtWrapper( createWrapper( component, parent ) );
	}

	@Override
	public synchronized void addComponentEvtWrapper(WW wrapper)
	{
		_componentValueWrapperList.add( wrapper );

		addListener( wrapper );
	}

	protected void addListener(ComponentEvtWrapper<EV> wrapper)
	{
		wrapper.addComponentEvtListener(_internalListener);
	}

	protected void removeListener(ComponentEvtWrapper<EV> wrapper)
	{
		wrapper.removeComponentEvtListener(_internalListener);
	}

	protected void removeListeners()
	{
		for( ComponentEvtWrapper<EV> wrapper: _componentValueWrapperList )
			removeListener(wrapper);
	}

	@Override
	public void setComponentMapper( ComponentMapper mapper )
	{
		for( WW wrapper: _componentValueWrapperList )
			wrapper.setComponentMapper( mapper );
	}

	@Override
	public void setEnabled( boolean value )
	{
		for( WW wrapper: _componentValueWrapperList )
			wrapper.setEnabled( value );
	}
}
