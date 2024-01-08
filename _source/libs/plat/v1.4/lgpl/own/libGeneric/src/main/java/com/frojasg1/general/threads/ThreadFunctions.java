/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.threads;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.ExecutionFunctions.UnsafeMethod;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThreadFunctions
{
	protected static ThreadFunctions _instance = null;

	public static void changeInstance( ThreadFunctions instance )
	{
		_instance = instance;
	}

	public static ThreadFunctions instance()
	{
		if( _instance == null )
			_instance = new ThreadFunctions();

		return( _instance );
	}

	public void invokeWithDelay( Runnable run, int millisecondsToDelay )
	{
		ThreadFunctions.this.sleep( millisecondsToDelay );

		run.run();
	}

	public void delayedInvoke( Runnable run, int millisecondsToDelay )
	{
		Thread th = new Thread( () -> invokeWithDelay( run, millisecondsToDelay ) );

		th.start();
	}

	public void delayedSafeInvoke( UnsafeMethod run, int millisecondsToDelay )
	{
		delayedInvoke( () -> ExecutionFunctions.instance().safeMethodExecution( run ),
						millisecondsToDelay );
	}

	public void delayedSafeInvokeEventDispatchThread( UnsafeMethod run, int millisecondsToDelay )
	{
		delayedSafeInvoke( () -> SwingUtilities.invokeLater(
									() -> ExecutionFunctions.instance().safeMethodExecution( run ) ),
					millisecondsToDelay );
	}

	public void delayedInvokeEventDispatchThread( Runnable run, int millisecondsToDelay )
	{
		delayedInvoke( () -> SwingUtilities.invokeLater( run ), millisecondsToDelay );
	}

	public void sleep( long milliseconds )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> Thread.sleep( milliseconds ) );
	}

	public void startThread( Runnable runnable )
	{
		Thread thread = new Thread( runnable );
		thread.start();
	}
}
