/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.evt;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEventBase;
import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtWrapperSimpleBase;
import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComponentEvtWrapperSimpleDefaultBase<CC extends Component>
	extends ComponentEvtWrapperSimpleBase<CC, ComponentEvtEvent>
{
	public ComponentEvtWrapperSimpleDefaultBase(CC component, Component parent) {
		super(component, parent);
	}

	@Override
	protected ComponentEvtEvent createEmptyEvent()
	{
		return( new ComponentEvtEventBase() );
	}
}
