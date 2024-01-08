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
package com.frojasg1.chesspdfbrowser.enginewrapper;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.EngineWrapperActionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EngineCustomCommandArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.impl.EngineActionRequestImpl;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.threads.ChessEngineThreadBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.action.UciNewGameEngineWrapperAction;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.utils.EngineWrapperUtils;
import com.frojasg1.general.functional.interfaces.SupplierWithException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputListener;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputServer;
import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.interf.UciChessEngineWrapper;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class EngineWrapperInstance implements InputOutputListener, EngineWrapper,
														InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "EngineWrapperInstance.properties";
	public static final String CONF_ARGS_NOT_OF_EXPECTED_CLASS = "ARGS_NOT_OF_EXPECTED_CLASS";
	public static final String CONF_ONGOING_EXECUTION = "ONGOING_EXECUTION";

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;


	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	protected ReentrantLock _lock = new ReentrantLock(true);
	protected Condition _latestActionHasFinished = _lock.newCondition();

	protected EngineWrapperUtils _utils = null;
	protected EngineInstanceConfiguration _configuration = null;
	protected volatile ChessEngineThreadBase _thread = null;

	protected EngineWrapperActionBase _currentAction = null;

	protected Map<Integer, EngineActionExecutorData> _routingMap = null;

	protected boolean _isInitialized = false;

	protected List<InputOutputListener> _inputOutputListenerList = null;

	protected void init()
	{
		_utils = createEngineWrapperUtils();
		
		_routingMap = createMap();
		fillInRoutingMap( _routingMap );

		_inputOutputListenerList = createList();

		addInputOutputListener( this );

		_isInitialized = true;
	}

	protected <CC> List<CC> createList()
	{
		return( new ArrayList<>() );
	}

	@Override
	public ChessEngineConfiguration getConfiguration()
	{
		return( _configuration.getChessEngineConfiguration() );
	}

	public boolean isInitialized()
	{
		return( _isInitialized );
	}

	protected abstract void fillInRoutingMap( Map<Integer, EngineActionExecutorData> routingMap );

	protected
		<AA extends EngineActionArgs, RR extends EngineActionResult>
			void genericParticularInputActionExecution(
				EngineActionRequest request,
				Consumer<FullEngineActionResult<AA, RR>> callbackFun )
	{
		EngineActionExecutorData execData = _routingMap.get( request.getRequestType() );

		genericParticularInputActionExecution( execData,
			request,
			callbackFun,
			execData.getArgsClass(),
			execData.getActionResultClass() );
	}

	protected
		<AA extends EngineActionArgs, RR extends EngineActionResult>
			void genericParticularInputActionExecution( int requestType,
				EngineActionArgs args,
				Consumer<FullEngineActionResult<AA, RR>> callbackFun )
	{
		EngineActionExecutorData execData = _routingMap.get( requestType );

		EngineActionRequest<AA> request = createGenericRequest(requestType,
			(AA) args, execData.getArgsClass() );

		genericParticularInputActionExecution( execData,
			request,
			callbackFun,
			execData.getArgsClass(),
			execData.getActionResultClass() );
	}

	protected
	<AA extends EngineActionArgs, RR extends EngineActionResult>
		void genericParticularInputActionExecution( EngineActionExecutorData execData,
			EngineActionRequest request,
			Consumer<FullEngineActionResult<AA, RR>> callbackFun,
			Class<AA> argsClass,
			Class<RR> actionResultClass )
	{
		EngineActionExecutorData<AA, RR> execDataAARR = (EngineActionExecutorData<AA, RR>) execData;
		execDataAARR.getActionExecutionFunction().accept( execDataAARR, request, callbackFun );
	}

	protected <KK,VV> Map<KK,VV> createMap()
	{
		return( new HashMap<>() );
	}

	protected EngineWrapperUtils createEngineWrapperUtils()
	{
		return( new EngineWrapperUtils() );
	}

	protected void setCurrentAction( EngineWrapperActionBase action )
	{
		try
		{
			_lock.lock();

			_currentAction = action;
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected EngineWrapperActionBase getCurrentAction()
	{
		return( _currentAction );
	}

	protected <AA extends EngineActionArgs, RR extends EngineActionResult>
			void genericActionInternalExecutor( EngineActionExecutorData<AA, RR> execData,
						EngineActionRequest<AA> request,
						Consumer<FullEngineActionResult<AA, RR>> callback,
						Class<AA> argsClass,
						Class<RR> actionResultClass )
	{
		EngineWrapperActionBase<AA, RR> newAction = createGenericAction( execData,
							request,
							execData.getActionResultClass(),
							callback );

		setNewAction( newAction );
	}

	protected EngineWrapperActionBase checkAndSetCurrentAction( EngineWrapperActionBase newAction )
	{
		EngineWrapperActionBase currentAction = null;
		try
		{
			_lock.lock();

			currentAction = getCurrentAction();
			if( currentAction == null )
			{
				setCurrentAction( newAction );
				currentAction = newAction;
			}

			return( currentAction );
		}
		finally
		{
			_lock.unlock();
		}
	}
		
	protected void setNewAction( EngineWrapperActionBase newAction )
	{
		EngineWrapperActionBase currentAction = null;
		try
		{
			_lock.lock();

			currentAction = checkAndSetCurrentAction(newAction);
			if( currentAction != newAction )
			{
				newAction.safeMethodExecution( () -> { throw( new RuntimeException( getInternationalString(CONF_ONGOING_EXECUTION) ) ); } );
				setCurrentAction( currentAction );	// as exception will reset currentAction, we restore to the previous one
			}
		}
		finally
		{
			_lock.unlock();
		}

		if( currentAction == newAction )
			newAction.start();
	}

	protected <AA extends EngineActionArgs, RR extends EngineActionResult>
			EngineWrapperActionBase<AA, RR>
		createGenericAction( EngineActionExecutorData execData,
				EngineActionArgs args, Class<AA> argsClass,
				Class<RR> actionResultClass,
				Consumer<FullEngineActionResult<AA, RR>> callbackFun )
	{
		int requestType = execData.getRequestType();
		EngineActionRequest<AA> request = createGenericRequest( requestType,
				args, argsClass );

		return( createGenericAction( execData,
									request,
									actionResultClass,
									callbackFun ) );
	}

	protected <AA extends EngineActionArgs, RR extends EngineActionResult>
			EngineWrapperActionBase<AA, RR>
		createGenericAction( EngineActionExecutorData execData,
				EngineActionRequest<AA> request,
				Class<RR> actionResultClass,
				Consumer<FullEngineActionResult<AA, RR>> callbackFun )
	{
		EngineActionExecutorData<AA, RR> execDataAARR = (EngineActionExecutorData<AA, RR>) execData;

		EngineWrapperActionBase<AA, RR> result = execDataAARR.getActionBuilder().get();
		Class<AA> argsClass = request.getArgsClass();
		result.init( this, request, callbackFun, argsClass );

		return( result );
	}

	protected <AA extends EngineActionArgs> EngineActionRequest<AA> createGenericRequest( int requestType,
				EngineActionArgs args, Class<AA> argsClass )
	{
		if( ( args != null ) && !argsClass.isInstance( args ) )
			throw( new RuntimeException( createCustomInternationalString( CONF_ARGS_NOT_OF_EXPECTED_CLASS,
						args.getClass().getName(), argsClass.getName() ) ) );

		EngineActionRequest<AA> result = new EngineActionRequestImpl( requestType,
					(AA) args, argsClass );

		return( result );
	}

	public EngineInstanceConfiguration getUciInstanceConfiguration()
	{
		return( _configuration );
	}

	protected abstract ChessEngineThreadBase createThread();

	public void stopAndClear()
	{
		if( _thread != null )
			quit();

		_thread = null;
	}

	public abstract void quit();

	public abstract void applyConfigurationBase( ChessEngineConfiguration configuration,
				Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun );

	@Override
	public void send( String command ) throws IOException
	{
		if( _thread != null )
			_thread.send( command );
	}

	@Override
	public void setEmptyEngineConfiguration( ChessEngineConfiguration chessEngineConfiguration )
	{
		if( ( chessEngineConfiguration != null ) &&
			(getUciInstanceConfiguration() != null ) &&
			( getUciInstanceConfiguration().getChessEngineConfiguration() == null ) )
		{
			getUciInstanceConfiguration().setChessEngineConfiguration(chessEngineConfiguration);
		}
	}

	protected void startThread( ChessEngineThreadBase thread )
	{
		thread.start();
	}

	protected void addInputOutputListeners( ChessEngineThreadBase thread,
															Collection<InputOutputListener> lisList )
	{
		try
		{
			_lock.lock();
			for( InputOutputListener listener: lisList )
				thread.addInputOutputListener(listener);
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected void createAndStartThread( EngineInstanceConfiguration configuration )
	{
//		try
		{
//			_lock.lock();

			_thread = createThread();
			addInputOutputListeners( _thread, _inputOutputListenerList );

			_thread.init( configuration.getEngineCommandForLaunching() );

			startThread( _thread );
		}
//		finally
		{
//			_lock.unlock();
		}
	}

	protected void createAndStartThread( EngineInstanceConfiguration configuration, EngineWrapperActionBase initAction )
	{
		setCurrentAction( initAction );
		Exception ex = initAction.safeMethodExecution( () -> createAndStartThread( configuration ) );
		if( ex == null )
			initAction.start();
	}

	@Override
	public void addInputOutputListener( InputOutputListener listener )
	{
		try
		{
			_lock.lock();

			if( _thread == null )
				_inputOutputListenerList.add( listener );
			else
				_thread.addInputOutputListener( listener );
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public void removeInputOutputListener( InputOutputListener listener )
	{
		ThreadFunctions.instance().startThread( () -> { if( _thread != null ) _thread.removeInputOutputListener( listener ); } );
	}

	@Override
	public void newOutputLine(InputOutputServer server, String line)
	{
		if( getCurrentAction() != null )
			getCurrentAction().accept( line );
	}

	@Override
	public void newInputLine(InputOutputServer server, String line)
	{
	}

	public <AA extends EngineActionArgs, RR extends EngineActionResult>
				FullEngineActionResult<AA, RR>
		createFullResult( EngineActionRequest<AA> request,
							SuccessResult successResult )
	{
		return( _utils.createFullResult( this, request, successResult ) );
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

	protected <AA extends EngineActionArgs, RR extends EngineActionResult>
		void executeGenericBasicAction( EngineActionRequest<AA> request,
				Consumer<FullEngineActionResult<AA, RR>> callbackFun,
				SupplierWithException<SuccessResult> executor )

	{
		SuccessResult result = null;
		try
		{
			result = executor.get();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = createErrorResult( ex );
		}
		finally
		{
			this.resetCurrentAction();

			if( callbackFun != null )
				callbackFun.accept( createFullResult( request, result ) );
		}
	}

	protected EngineWrapperActionBase<EngineActionArgs, EngineActionResult> createUciNewGameEngineWrapperAction()
	{
		EngineWrapperActionBase result = new UciNewGameEngineWrapperAction( _utils );

		return( result );
	}

	public void customCommand( EngineActionExecutorData<EngineCustomCommandArgs,
												EngineActionResult> execData,
				EngineActionRequest<EngineCustomCommandArgs> request,
				Consumer<FullEngineActionResult<EngineCustomCommandArgs, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> {
			send( request.getArgs().getCustomCommand() );
			return( createSuccessResult() );
		});
	}

	@Override
	public void resetCurrentAction()
	{
		try
		{
			_lock.lock();

			_currentAction = null;

			if( _lock.hasWaiters(_latestActionHasFinished) )
				_latestActionHasFinished.signal();
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public <AA extends EngineActionArgs, RR extends EngineActionResult>
		void sendRequest( EngineActionRequest<AA> request,
						Consumer<FullEngineActionResult<AA, RR>> callbackFun )
	{
		genericParticularInputActionExecution( request, callbackFun );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalStringStatic(CONF_ARGS_NOT_OF_EXPECTED_CLASS, "args [$1] not of expected class [$2]" );
		registerInternationalStringStatic(CONF_ONGOING_EXECUTION, "Ongoing execution" );
	}

	protected static void registerInternationalStringStatic(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}
}
