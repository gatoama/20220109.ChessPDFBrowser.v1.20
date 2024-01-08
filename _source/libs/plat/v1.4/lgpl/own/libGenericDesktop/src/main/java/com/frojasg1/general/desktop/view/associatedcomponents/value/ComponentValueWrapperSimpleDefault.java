/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtWrapperSimpleBase;
import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComponentValueWrapperSimpleDefault<CC extends Component, VV>
	extends ComponentEvtWrapperSimpleBase<CC, ValueChangedEvent<VV>>
	implements ComponentValueWrapper<VV, ValueChangedEvent<VV>>
{
	public ComponentValueWrapperSimpleDefault( CC component, Component parent )
	{
		super( component, parent );
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected void initChild()
	{
		super.initChild();
	}

	protected ValueChangedEvent<VV> createEvent()
	{
		ValueChangedEvent<VV> result = super.createEvent();
		result.setNewValue( getValue() );

		return( result );
	}

	@Override
	protected ValueChangedEvent<VV> createEmptyEvent() {
		return( new ValueChangedEventBase<>() );
	}

	@Override
	protected boolean canNotify()
	{
		return( getValue() != null );
	}
}
