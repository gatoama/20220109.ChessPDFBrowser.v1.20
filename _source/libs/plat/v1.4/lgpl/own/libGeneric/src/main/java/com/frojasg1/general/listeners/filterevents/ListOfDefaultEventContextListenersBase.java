/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.listeners.filterevents;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EV>	- Event
 * @param <ET>	- Event type
 */
public class ListOfDefaultEventContextListenersBase<EV, ET>
	extends ListOfEventContextListenersBase<EV, ET, EventListenerContextBase<EV, ET>> {

	public ListOfDefaultEventContextListenersBase( Function<ET, Long> eventTypeToLongFunction,
													Function<EV, ET> eventToEventTypeFunction )
	{
		super( eventTypeToLongFunction,	eventToEventTypeFunction );
	}

	@Override
	protected EventListenerContextBase<EV, ET> createListenerContextInternal( Consumer<EV> consumer,
																	ET ... eventTypesToRegister )
	{
		return( new EventListenerContextBase( consumer, getEventTypeToLongFunction(),
												eventTypesToRegister) );
	}
}
