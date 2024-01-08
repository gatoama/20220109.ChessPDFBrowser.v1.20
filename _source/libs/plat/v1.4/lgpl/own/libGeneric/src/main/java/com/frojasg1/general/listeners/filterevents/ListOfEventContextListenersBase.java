/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.listeners.filterevents;

import com.frojasg1.general.listeners.ListOfListenersNotifyEvtBase;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EV>	- Event
 * @param <ET>	- Event type
 * @param <LT>	- Listener
 */
public abstract class ListOfEventContextListenersBase<EV, ET, LT extends EventListenerContextBase<EV, ET>>
	extends ListOfListenersNotifyEvtBase<LT, EV> {

	protected Function<ET, Long> _eventTypeToLongFunction;
	protected Function<EV, ET> _eventToEventTypeFunction;

	public ListOfEventContextListenersBase( Function<ET, Long> eventTypeToLongFunction,
											Function<EV, ET> eventToEventTypeFunction)
	{
		super( EventListenerContextBase::getListenerFunction );

		_eventTypeToLongFunction = eventTypeToLongFunction;
		_eventToEventTypeFunction = eventToEventTypeFunction;
	}

	protected Function<ET, Long> getEventTypeToLongFunction()
	{
		return _eventTypeToLongFunction;
	}

	protected Function<EV, ET> getEventToEventTypeFunction()
	{
		return _eventToEventTypeFunction;
	}

	public LT addListenerContext( Consumer<EV> consumer, ET ... eventTypesToRegister )
	{
		LT listener = createListenerContext( consumer, eventTypesToRegister );
		add( listener );

		return listener;
	}

	protected ET getEventType( EV evt )
	{
		return( getEventToEventTypeFunction().apply(evt) );
	}

	protected abstract LT createListenerContextInternal( Consumer<EV> consumer, ET ... eventTypesToRegister );

	protected LT createListenerContext( Consumer<EV> consumer, ET ... eventTypesToRegister )
	{
		return( createListenerContextInternal( consumer, eventTypesToRegister ) );
	}

	@Override
	protected boolean hasToSend( LT listener, EV evt )
	{
		return( listener.isActivated( getEventType(evt) ) );
	}

	public void remove( Consumer<EV> listenerFunction, ET ... eventTypesToRemove )
	{
		ListIterator<LT> it = _list.listIterator();
		while( it.hasNext() )
		{
			LT elem = it.next();
			if( elem.getListenerFunction().equals( listenerFunction ) )
			{
				if( ( eventTypesToRemove != null ) && ( eventTypesToRemove.length > 0 ) )
					elem.deactivateEventTypes(eventTypesToRemove);
				else
					it.remove();
			}
		}
	}
}
