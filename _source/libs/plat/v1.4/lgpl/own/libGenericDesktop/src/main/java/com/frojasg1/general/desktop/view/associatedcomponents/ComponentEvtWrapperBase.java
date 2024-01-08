/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComponentEvtWrapperBase<EV extends ComponentEvtEvent>
	implements ComponentEvtWrapper<EV>, ReleaseResourcesable
{
	protected ComponentEvtWrapperListenersContextBase<EV> _listenersContext;

	public void init()
	{
		_listenersContext = createListenersContext();

		initChild();
	}

	protected abstract void initChild();

	protected ComponentEvtWrapperListenersContextBase<EV> createListenersContext()
	{
		return( new ComponentEvtWrapperListenersContextBase<>() );
	}

	protected ComponentEvtWrapperListenersContextBase<EV> getListenersList()
	{
		return( _listenersContext );
	}

	@Override
	public void addComponentEvtListener(Consumer<EV> listener) {
		getListenersList().addListenerContext(listener);
	}

	@Override
	public void removeComponentEvtListener(Consumer<EV> listener) {
		getListenersList().remove(listener);
	}

	protected abstract EV createEmptyEvent();

	protected EV createEvent()
	{
		EV result = createEmptyEvent();

		result.setSource( createSource() );

		return( result );
	}

	protected WrapperEventSource createSource()
	{
		return( createSource(this) );
	}

	protected WrapperEventSource createSource(Object source)
	{
		WrapperEventSource result = new WrapperEventSource();
		result.setSource(source);

		return( result );
	}

	@Override
	public void releaseResources()
	{
		removeListeners();
	}

	protected abstract void removeListeners();

	public abstract void setComponentMapper( ComponentMapper mapper );

	@Override
	public boolean hasBeenAlreadyMapped() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}
}
