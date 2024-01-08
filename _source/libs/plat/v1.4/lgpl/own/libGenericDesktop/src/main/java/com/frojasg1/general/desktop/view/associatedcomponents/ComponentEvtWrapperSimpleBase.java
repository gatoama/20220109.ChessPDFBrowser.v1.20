/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComponentEvtWrapperSimpleBase<CC extends Component, EV extends ComponentEvtEvent>
	extends ComponentEvtWrapperBase<EV>
	implements ComponentEvtSimpleWrapper<CC, EV>
{
	protected CC _component;
	protected Component _parent;

	public ComponentEvtWrapperSimpleBase( CC component, Component parent )
	{
		_component = component;
		if( parent != null )
			_parent = parent;
		else
			_parent = component;
	}

	@Override
	public void init()
	{
		super.init();
	}

	protected Component getParent()
	{
		return( _parent );
	}

	@Override
	protected void initChild()
	{
		addListener();

		addInternallyMappedComponentToParentWindow();
	}

	@Override
	public void setEnabled( boolean value )
	{
		getComponent().setEnabled(value);
	}

	protected void addInternallyMappedComponentToParentWindow()
	{
		FrameworkComponentFunctions.instance().addInternallyMappedComponentToParent( this, getParent() );
	}

	@Override
	protected EV createEvent()
	{
		EV result = super.createEvent();

		return( result );
	}

	protected boolean canNotify()
	{
		return( true );
	}

	protected void internalValueChangedListener()
	{
		if( canNotify() )
		{
			EV evt = createEvent();

			getListenersList().notifyEvt( evt );
		}
	}

	@Override
	public CC getComponent() {
		return _component;
	}

	@Override
	protected WrapperEventSource createSource()
	{
		WrapperEventSource result = super.createSource();
		result.setParentSource( createSource( getComponent() ) );

		return( result );
	}

	protected abstract void addListener();

	@Override
	protected abstract EV createEmptyEvent();

	@Override
	public void setComponentMapper( ComponentMapper mapper )
	{
		_component = mapper.mapComponent(_component);
	}
}
