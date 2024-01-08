/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.listeners.filterevents.EventListenerContextBase;
import com.frojasg1.general.listeners.filterevents.ListOfDefaultEventContextListenersBase;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EV>	- Event
 */
public class ComponentEvtWrapperListenersContextBase<EV extends ComponentEvtEvent>
			extends ListOfDefaultEventContextListenersBase<EV, Long>
{
	public ComponentEvtWrapperListenersContextBase()
	{
		super( lon -> 1L, ev -> 1L );
	}

	@Override
	protected EventListenerContextBase<EV, Long> createListenerContext( Consumer<EV> consumer,
																	Long ... eventTypesToRegister )
	{
		return( createListenerContextInternal( consumer, -1L ) );
	}
}
