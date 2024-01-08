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
package com.frojasg1.general.desktop.edt;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.StreamFunctions;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EventDispatchThreadFunctions extends StreamFunctions
{
	protected static EventDispatchThreadFunctions _instance;

	public static void changeInstance( EventDispatchThreadFunctions inst )
	{
		_instance = inst;
	}

	public static EventDispatchThreadFunctions instance()
	{
		if( _instance == null )
			_instance = new EventDispatchThreadFunctions();
		return( _instance );
	}

	public <RR> ParallelExecutionResult<RR> edtFunctionExecution( Supplier<RR> supplier )
	{
		ParallelExecutionResult<RR> result = new ParallelExecutionResult<>();
		SwingUtilities.invokeLater( () -> {
			try
			{
				RR res = supplier.get();
				result.setResult( res );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			finally
			{
				result.getSemaphore().release();
			}
		} );
		return( result );
	}

	public boolean isEventDispatchThread()
	{
		return( SwingUtilities.isEventDispatchThread() );
	}

	public Exception safeMethodExecution( ExecutionFunctions.UnsafeMethod runnable )
	{
		return(  ExecutionFunctions.instance().safeMethodExecution( runnable ) );
	}

	public void edtAsinchronousMethodExecution( ExecutionFunctions.UnsafeMethod runnable )
	{
		if( isEventDispatchThread() )
			safeMethodExecution( runnable );
		else
			SwingUtilities.invokeLater( () -> safeMethodExecution( runnable ) );
	}
}
