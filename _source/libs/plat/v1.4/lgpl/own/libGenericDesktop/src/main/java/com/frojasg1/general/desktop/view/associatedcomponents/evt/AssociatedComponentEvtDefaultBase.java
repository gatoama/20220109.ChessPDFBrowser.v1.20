/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.evt;

import com.frojasg1.general.desktop.view.associatedcomponents.AssociatedComponentEvtBase;
import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEventBase;
import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtWrapper;
import com.frojasg1.general.desktop.view.associatedcomponents.evt.impl.ComponentEvtWrapperBuilder;
import java.awt.Component;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AssociatedComponentEvtDefaultBase
	extends AssociatedComponentEvtBase<ComponentEvtEvent, ComponentEvtWrapper<ComponentEvtEvent>>
{
	public AssociatedComponentEvtDefaultBase() {
		this( ComponentEvtWrapperBuilder.instance()::apply );
	}

	public AssociatedComponentEvtDefaultBase(BiFunction<Component, Component, ComponentEvtWrapper<ComponentEvtEvent>> wrapperBuilder) {
		super(wrapperBuilder);
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected ComponentEvtEvent createEmptyEvent()
	{
		return( new ComponentEvtEventBase() );
	}
}
