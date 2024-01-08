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
package com.frojasg1.chesspdfbrowser.enginewrapper.action;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputListener;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputServer;
import com.frojasg1.chesspdfbrowser.enginewrapper.utils.EngineWrapperUtils;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class EngineWrapperActionBase<AA extends EngineActionArgs, RR extends EngineActionResult>
					implements EngineWrapperAction, InputOutputListener
{
	protected EngineWrapperUtils _utils = null;

	protected String _name = null;
	protected EngineWrapper _parent = null;
	protected Timer _timer = null;
	protected Consumer<FullEngineActionResult<AA, RR>> _callbackFunction = null;
	protected List<String> _temporalOutput;
	protected EngineActionRequest<AA> _request = null;

	public EngineWrapperActionBase( EngineWrapperUtils utils )
	{
		_utils = utils;
	}

	public abstract void init( EngineWrapper engineWrapper, EngineActionRequest<AA> request,
						Consumer<FullEngineActionResult<AA, RR>> callbackFunction,
						Class<AA> argsClass );

	public void init( String name, EngineWrapper engineWrapper, EngineActionRequest<AA> request,
						Consumer<FullEngineActionResult<AA, RR>> callbackFunction )
	{
		_temporalOutput = createList();
		_name = name;
		_parent = engineWrapper;
		_callbackFunction = callbackFunction;
		setRequest( request );

		getParent().addInputOutputListener( this );
	}

	protected void setRequest( EngineActionRequest<AA> request )
	{
		_request = request;
	}

	protected EngineActionRequest<AA> getRequest()
	{
		return( _request );
	}

	protected AA getArgs()
	{
		return( _request.getArgs() );
	}

	public void start()
	{
		
	}

	protected FullEngineActionResult<AA, RR> createFullNonLastResult( RR actionResult )
	{
		return( _utils.createFullNonLastResult( getParent(), getRequest(), actionResult ) );
	}

	protected FullEngineActionResult<AA, RR> createFullResult( RR actionResult )
	{
		return( _utils.createFullResult( getParent(), getRequest(),
												createSuccessResult(), actionResult ) );
	}

	protected FullEngineActionResult<AA, RR> createFullResult( SuccessResult successResult )
	{
		return( _utils.createFullResult(getParent(), getRequest(),
												successResult, null ) );
	}

	protected <CC> List<CC> createList()
	{
		return( Collections.synchronizedList( new ArrayList<>() ) );
	}

	protected EngineWrapper getParent()
	{
		return( _parent );
	}

	protected Timer getTimer()
	{
		return( _timer );
	}

	protected void setTimer( Timer timer )
	{
		_timer = timer;
	}

	protected void cancelTimer()
	{
		if( getTimer() != null )
			getTimer().cancel();
	}

	public abstract void accept_internal( String line );

	@Override
	public void accept( String line )
	{
		synchronized( _temporalOutput )
		{
			_temporalOutput.add( line );
		}
		safeMethodExecution( () -> accept_internal(line) );
	}

	public String getName()
	{
		return( _name );
	}

	protected Consumer<FullEngineActionResult<AA, RR>> getCallbackFunction()
	{
		return( _callbackFunction );
	}

	protected void resetCurrentAction()
	{
		getParent().removeInputOutputListener( this );

		if( getParent() != null )
			getParent().resetCurrentAction();

		clearTemporalOutput();
	}

//	protected synchronized void callbackInvocation( FullEngineActionResult<AA, RR> result )
	protected void callbackInvocation( FullEngineActionResult<AA, RR> result )
	{
		cancelTimer();

		resetCurrentAction();

		if( getCallbackFunction() != null )
			getCallbackFunction().accept( result );
	}

	protected abstract void callbackInvocation( SuccessResult result );

	protected void initTimer( int delayMs )
	{
		TimerTask task = new TimerTask() {
			public void run() {
				initTimeout();
				getTimer().cancel();
			}
		};
		setTimer( createTimer() );

		getTimer().schedule( task, delayMs);
	}

	protected String getTimeoutErrorString()
	{
		return( String.format( "%s timeout", _name ) );
	}

	protected void initTimeout()
	{
		callbackInvocation( createErrorResult( getTimeoutErrorString() ) );
	}

	protected void send( String command ) throws IOException
	{
		if( getParent() != null )
			getParent().send( command );
	}

	protected Timer createTimer()
	{
		Timer result = new Timer("Timer");

		return( result );
	}

	protected SuccessResult createErrorResult( String errorMessage )
	{
		return( _utils.createErrorResult(errorMessage) );
	}

	protected SuccessResult createErrorResult( Exception ex )
	{
		return( _utils.createErrorResult(ex) );
	}

	protected SuccessResult createSuccessResult( )
	{
		return( _utils.createSuccessfulResult() );
	}

	public Exception safeMethodExecution( ExecutionFunctions.UnsafeMethod method )
	{
		Exception ex = ExecutionFunctions.instance().safeMethodExecution( method );
		if( ex != null )
			callbackInvocation( createErrorResult( ex ) );

		return( ex );
	}

	protected List<String> getTemporalOutput()
	{
		return( _temporalOutput );
	}

	protected List<String> copyTemporalOutput()
	{
		List<String> result = null;
		synchronized( getTemporalOutput() )
		{
			result = new ArrayList<>( getTemporalOutput() );
		}

		return( result );
	}

	protected void clearTemporalOutput()
	{
		synchronized( getTemporalOutput() )
		{
			getTemporalOutput().clear();
		}
	}

	@Override
	public void newOutputLine( InputOutputServer server, String line )
	{
		synchronized( _temporalOutput )
		{
			_temporalOutput.add( line );
		}
	}

	@Override
	public void newInputLine( InputOutputServer server, String line )
	{
		
	}
}
