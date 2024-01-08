/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.listeners;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <LT>	- Listener
 * @param <EV>	- Event
 */
public class ListOfListenersNotifyEvtBase <LT, EV> extends ListOfListenersBase<LT>
{
	protected Function<LT, Consumer<EV>> _consumerGetter;

	public ListOfListenersNotifyEvtBase(Function<LT, Consumer<EV>> _consumerGetter) {
		this._consumerGetter = _consumerGetter;
	}

	public ListOfListenersNotifyEvtBase(ListOfListenersNotifyEvtBase<LT, EV> that) {
		super(that);

		this._consumerGetter = that._consumerGetter;
	}

	public Function<LT, Consumer<EV>> getConsumerGetter() {
		return _consumerGetter;
	}

	protected Consumer<EV> getEvtConsumer( LT listener )
	{
		return( getConsumerGetter().apply(listener) );
	}

	protected void notifyListener( LT listener, EV evt )
	{
		if( hasToSend(listener, evt) )
			getEvtConsumer(listener).accept(evt);
	}

	protected boolean hasToSend( LT listener, EV evt )
	{
		return( true );
	}

	public void notifyEvt( EV evt )
	{
		for( LT listener: getList() )
		{
			safeMethodExecution( () -> notifyListener(listener, evt) );
		}
	}
}
