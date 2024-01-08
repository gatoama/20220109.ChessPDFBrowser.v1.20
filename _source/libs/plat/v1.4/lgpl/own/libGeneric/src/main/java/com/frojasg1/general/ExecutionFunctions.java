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
package com.frojasg1.general;

import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.string.CreateCustomString;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ExecutionFunctions
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionFunctions.class);

	protected static ExecutionFunctions _instance;

	public static void changeInstance( ExecutionFunctions inst )
	{
		_instance = inst;
	}

	public static ExecutionFunctions instance()
	{
		if( _instance == null )
			_instance = new ExecutionFunctions();
		return( _instance );
	}

	public Exception safeSilentMethodExecution( UnsafeMethod run )
	{
		return( safeMethodExecution( run, false ) );
	}

	public Exception safeMethodExecution( UnsafeMethod run )
	{
		return( safeMethodExecution( run, true ) );
	}

	public void execute( Runnable runnable )
	{
		if( runnable != null )
			runnable.run();
	}

	protected Exception safeMethodExecution( UnsafeMethod run, boolean traceException )
	{
		Exception result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				run.run();
		}
		catch (InterruptedException ex)
		{
			result = ex;
			Thread.currentThread().interrupt();
		}
		catch( Exception ex )
		{
			result = ex;
			if( traceException )
				ex.printStackTrace();
		}
		catch( LinkageError le )
		{
			if( traceException )
				le.printStackTrace();
		}

		return( result );
	}

	public <CC> CC safeSilentFunctionExecution( UnsafeFunction<CC> run )
	{
		return( safeFunctionExecution( run, false ) );
	}

	public <CC> CC safeFunctionExecution( UnsafeFunction<CC> run )
	{
		return( safeFunctionExecution( run, true ) );
	}

	protected <CC> CC safeFunctionExecution( UnsafeFunction<CC> run, boolean traceException )
	{
		CC result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				result = run.run();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch( Exception | LinkageError ex )
		{
			if( traceException )
				ex.printStackTrace();
		}

		return( result );
	}

	public String createCustomInternationalString( String customStringPattern, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( customStringPattern, args) );
	}

	public DialogsWrapper getDialogsWrapper()
	{
		return( GenericFunctions.instance().getDialogsWrapper() );
	}

	public void invokeAndWait( Runnable runnable )
	{
		safeMethodExecution( () -> SwingUtilities.invokeAndWait(runnable) );
	}

	public <CC> CC functionWithDialog( UnsafeFunction<CC> run, ViewComponent parent,
										int dialogType, String exceptionMessagePattern )
	{
		CC result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				result = run.run();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch( Exception | LinkageError ex )
		{
			invokeAndWait( () ->
				getDialogsWrapper().showMessageDialog(parent,
					createCustomInternationalString(exceptionMessagePattern, ex.getMessage()),
					dialogType)
			);
		}

		return( result );
	}

	public Exception methodWithDialog( UnsafeMethod run, ViewComponent parent,
										int dialogType, String exceptionMessagePattern )
	{
		Throwable th = null;
		Exception result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				run.run();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		catch( Exception ex )
		{
			th = result = ex;
		}
		catch( LinkageError le )
		{
			th = le;
		}

		if( th != null )
		{
			LOGGER.error( "Error at methodWithDialog", th );
			getDialogsWrapper().showMessageDialog(parent,
				createCustomInternationalString(exceptionMessagePattern, th.getMessage()),
				dialogType);
		}

		return( result );
	}

	public <CC> CC runtimeExceptionFunctionExecution( UnsafeFunction<CC> run )
	{
		return( runtimeExceptionFunctionExecution( run, null ) );
	}

	public <CC> CC runtimeExceptionFunctionExecution( UnsafeFunction<CC> run, Supplier<String> messageGetter  )
	{
		CC result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				result = run.run();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
			throw( new RuntimeException( getMessage(messageGetter, ex), ex ) );
		}
		catch( Exception | LinkageError ex )
		{
			LOGGER.warn( "runtimeExceptionFunctionExecution, UnsafeFunction caused exception", ex );
			throw( new RuntimeException( getMessage(messageGetter, ex), ex ) );
		}

		return( result );
	}

	public void nestedInvokeLater( int nestedIndex, Runnable runnable )
	{
		Runnable result = runnable;
		for( int ii=0; ii<nestedIndex; ii++ )
		{
			Runnable current = result;
			result = () -> SwingUtilities.invokeLater( current );
		}

		result.run();
	}

	public Exception runtimeExceptionMethodExecution( UnsafeMethod run )
	{
		return( runtimeExceptionMethodExecution( run, null ) );
	}

	protected String getMessage( Supplier<String> messageGetter, Throwable ex )
	{
		String result = null;
		if( messageGetter != null )
			result = messageGetter.get();
		else if( ex != null )
			result = ex.getMessage();

		return( result );
	}

	public Exception runtimeExceptionMethodExecution( UnsafeMethod run, Supplier<String> messageGetter )
	{
		Throwable th = null;
		Exception result = null;
		try
		{
			if( ! Thread.currentThread().isInterrupted() )
				run.run();
		}
		catch (InterruptedException ex)
		{
			result = ex;
			Thread.currentThread().interrupt();
			throw( new RuntimeException( getMessage(messageGetter, ex), ex ) );
		}
		catch( Exception ex )
		{
			th = result = ex;
		}
		catch( LinkageError le )
		{
			th = le;
		}

		if( th != null )
		{
			LOGGER.warn( "runtimeExceptionFunctionExecution, UnsafeFunction caused exception", th );
			throw( new RuntimeException( getMessage(messageGetter, th), th ) );
		}

		return( result );
	}

	public void invokeLaterIfNecessary( Runnable runnable )
	{
		if( SwingUtilities.isEventDispatchThread() )
			runnable.run();
		else
			SwingUtilities.invokeLater( runnable );
	}

	public interface UnsafeMethod
	{
		public void run() throws Exception;
	}

	public interface UnsafeFunction<CC>
	{
		public CC run() throws Exception;
	}
}
