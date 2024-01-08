/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.listeners.consumer;

import com.frojasg1.general.listeners.ListOfListenersNotifyEvtBase;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EV> - Event
 */
public class ListOfConsumerListenersBase<EV> extends ListOfListenersNotifyEvtBase<Consumer<EV>, EV>
{
	public ListOfConsumerListenersBase()
	{
		super( Function.identity() );
	}

	public ListOfConsumerListenersBase( ListOfConsumerListenersBase that )
	{
		super(that);
	}
}
