/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.threads;

import java.util.concurrent.locks.Condition;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReentrantLockWithOneCondition extends LockAndConditionBase {
	
	public ReentrantLockWithOneCondition()
	{
		this(true);
	}

	public ReentrantLockWithOneCondition(boolean init)
	{
		super(1, false);
		if( init )
			init();
	}

	public boolean awaitInterrupted( Integer timeoutMs )
	{
		return( awaitInterrupted( 0, timeoutMs ) );
	}

	public boolean hasWaiters( )
	{
		return( hasWaiters(0) );
	}

	public void signalCondition()
	{
		signalCondition(0);
	}

	public Condition getCondition()
	{
		return( getCondition(0) );
	}
}
