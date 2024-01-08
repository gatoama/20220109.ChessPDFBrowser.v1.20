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
 */

/*
	EV - Event
	ET - EventType
*/
public class EventListenerContextBase<EV, ET> {
	
	protected Function<ET, Long> _getLongFunction;
	protected Consumer<EV> _listenerFunction;
	protected long _registeredEvents = 0;

	public EventListenerContextBase( Consumer<EV> listenerFunction,
										Function<ET, Long> getLongFunction,
										ET ... eventTypes )
	{
		_getLongFunction = getLongFunction;
		_listenerFunction = listenerFunction;
		setEventTypes( eventTypes );
	}

	public Consumer<EV> getListenerFunction() {
		return _listenerFunction;
	}

	public long getRegisteredEvents() {
		return _registeredEvents;
	}

	public Long getLong( ET type )
	{
		return( _getLongFunction.apply(type) );
	}

	public void activateEventType( ET type )
	{
		_registeredEvents = _registeredEvents | getLong(type);
	}

	public void deactivateEventType( ET type )
	{
		_registeredEvents = _registeredEvents & ( 0xffffffffffffffffL ^ getLong(type) );
	}

	public void deactivateEventTypes( ET ... eventTypes )
	{
		for( ET et: eventTypes )
			deactivateEventType( et );
	}

	public void setEventTypes( ET ... eventTypes )
	{
		_registeredEvents = 0;

		for( ET type: eventTypes )
			activateEventType(type);
	}

	public boolean isActivated( ET eventType )
	{
		return( isActivated( getLong(eventType) ) );
	}

	public boolean isActivated( long longEventType )
	{
		return( (_registeredEvents & longEventType ) != 0 );
	}
}
