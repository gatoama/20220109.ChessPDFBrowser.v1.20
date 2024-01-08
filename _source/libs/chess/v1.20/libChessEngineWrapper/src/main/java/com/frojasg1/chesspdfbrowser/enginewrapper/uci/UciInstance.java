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
package com.frojasg1.chesspdfbrowser.enginewrapper.uci;

import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.chesspdfbrowser.enginewrapper.EngineActionExecutorData;
import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapperInstance;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.EngineWrapperActionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EngineCustomCommandArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.action.UciGoEngineWrapperAction;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.action.UciInitEngineWrapperAction;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.action.UciNewGameEngineWrapperAction;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.args.EngineButtonNameArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ButtonConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.interf.UciChessEngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.threads.UciEngineThread;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UciInstance extends EngineWrapperInstance implements UciChessEngineWrapper
{
	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void init( EngineInstanceConfiguration configuration,
		Consumer<FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.INIT,
												configuration, callbackFun );
	}

	@Override
	public void quit()
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> send( "quit" ) );
	}

	public void applyConfigurationBase( ChessEngineConfiguration configuration,
				Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.APPLY_CONFIGURATION,
												configuration, callbackFun );
	}

	protected void fillInRoutingMap( Map<Integer, EngineActionExecutorData> routingMap )
	{
		routingMap.put(UciChessEngineWrapper.INIT,
							new EngineActionExecutorData(
									UciChessEngineWrapper.INIT,
									(execData, req, callback) -> initActionExecutor((EngineActionExecutorData<EngineInstanceConfiguration, EngineActionResult>) execData,
										(EngineActionRequest<EngineInstanceConfiguration>) req,
										(Consumer<FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult>>) callback ),
									() -> createInitAction(),
									EngineInstanceConfiguration.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.APPLY_CONFIGURATION,
							new EngineActionExecutorData(
									UciChessEngineWrapper.APPLY_CONFIGURATION,
									(execData, req, callback) -> applyConfiguration(
										(EngineActionExecutorData<ChessEngineConfiguration, EngineActionResult>) execData,
										(EngineActionRequest<ChessEngineConfiguration>) req,
										(Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>>) callback ),
									() -> null,
									ChessEngineConfiguration.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.APPLY_BUTTON_OPTION_ITEM,
							new EngineActionExecutorData(
									UciChessEngineWrapper.APPLY_BUTTON_OPTION_ITEM,
									(execData, req, callback) -> applyButtonOptionItem(
										(EngineActionExecutorData<EngineButtonNameArgs, EngineActionResult>) execData,
										(EngineActionRequest<EngineButtonNameArgs>) req,
										(Consumer<FullEngineActionResult<EngineButtonNameArgs, EngineActionResult>>) callback ),
									() -> null,
									EngineButtonNameArgs.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.UCI_NEW_GAME,
							new EngineActionExecutorData(
									UciChessEngineWrapper.UCI_NEW_GAME,
									(execData, req, callback) -> genericActionInternalExecutor(
										(EngineActionExecutorData<EngineActionArgs, EngineActionResult>) execData,
										(EngineActionRequest<EngineActionArgs>) req,
										(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>>) callback,
										EngineActionArgs.class,
										EngineActionResult.class ),
									() -> createUciNewGameEngineWrapperAction(),
									EngineActionArgs.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.START_POSITION,
							new EngineActionExecutorData(
									UciChessEngineWrapper.START_POSITION,
									(execData, req, callback) -> startPosition(
										(EngineActionExecutorData<EngineActionArgs, EngineActionResult>) execData,
										(EngineActionRequest<EngineActionArgs>) req,
										(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>>) callback ),
									() -> null,
									EngineActionArgs.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.POSITION,
							new EngineActionExecutorData(
									UciChessEngineWrapper.POSITION,
									(execData, req, callback) -> position(
										(EngineActionExecutorData<EnginePositionArgs, EngineActionResult>) execData,
										(EngineActionRequest<EnginePositionArgs>) req,
										(Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>>) callback ),
									() -> null,
									EnginePositionArgs.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.GO,
							new EngineActionExecutorData(
									UciChessEngineWrapper.GO,
									(execData, req, callback) -> genericActionInternalExecutor(
										(EngineActionExecutorData<ChessEngineGoAttributes, ChessEngineResult>) execData,
										(EngineActionRequest<ChessEngineGoAttributes>) req,
										(Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>>) callback,
										ChessEngineGoAttributes.class,
										ChessEngineResult.class ),
									() -> createGoAction(),
									ChessEngineGoAttributes.class,
									ChessEngineResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.CUSTOM_COMMAND,
							new EngineActionExecutorData(
									UciChessEngineWrapper.CUSTOM_COMMAND,
									(execData, req, callback) -> customCommand(
										(EngineActionExecutorData<EngineCustomCommandArgs, EngineActionResult>) execData,
										(EngineActionRequest<EngineCustomCommandArgs>) req,
										(Consumer<FullEngineActionResult<EngineCustomCommandArgs, EngineActionResult>>) callback ),
									() -> createInitAction(),
									EngineActionArgs.class,
									EngineActionResult.class
							)
						);

		routingMap.put( UciChessEngineWrapper.STOP_THINKING,
							new EngineActionExecutorData(
									UciChessEngineWrapper.STOP_THINKING,
									(execData, req, callback) -> stopThinking(
										(EngineActionExecutorData<EngineActionArgs, EngineActionResult>) execData,
										(EngineActionRequest<EngineActionArgs>) req,
										(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>>) callback ),
									() -> createInitAction(),
									EngineActionArgs.class,
									EngineActionResult.class
							)
						);
	}

	protected void initActionExecutor(
						EngineActionExecutorData<EngineInstanceConfiguration,
												EngineActionResult> execData,
						EngineActionRequest<EngineInstanceConfiguration> request,
						Consumer<FullEngineActionResult<EngineInstanceConfiguration,
												EngineActionResult>> callbackFun )
	{
		if( getCurrentAction() == null )
		{
			if( ! isInitialized() )
				init();

			stopAndClear();

			EngineWrapperActionBase<EngineInstanceConfiguration,
									EngineActionResult> initAction = createGenericAction( execData,
																		request,
																		execData.getActionResultClass(),
																		callbackFun );

			_configuration = request.getArgs();

			createAndStartThread( _configuration, initAction );

			_isInitialized = true;
		}
		// TODO: translate
		else
			throw( new RuntimeException( "Ongoing execution ..." ) );
	}

	protected EngineWrapperActionBase createInitAction()
	{
		EngineWrapperActionBase result = new UciInitEngineWrapperAction( _utils );

		return( result );
	}

	@Override
	protected UciEngineThread createThread()
	{
		return( new UciEngineThread() );
	}
/*
	protected void stopAndClear()
	{
		if( _thread != null )
			ExecutionFunctions.instance().safeMethodExecution( () -> send( "quit" ) );

		_thread = null;
	}
*/

	@Override
	public synchronized void go(ChessEngineGoAttributes goAttr,
		Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFun)
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.GO,
										goAttr, callbackFun );
	}

	protected EngineWrapperActionBase createGoAction()
	{
		EngineWrapperActionBase result = new UciGoEngineWrapperAction( _utils );

		return( result );
	}

	@Override
	public void applyConfiguration( ChessEngineConfiguration configuration,
				Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.APPLY_CONFIGURATION,
										configuration, callbackFun );
	}

	public void applyConfiguration( EngineActionExecutorData<ChessEngineConfiguration,
												EngineActionResult> execData,
				EngineActionRequest<ChessEngineConfiguration> request,
				Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> {

			updateChessEngineConfiguration( request.getArgs() );
//			_configuration.setChessEngineConfiguration( request.getArgs() );

			for( ConfigurationItem confItem: request.getArgs().getMap().values() )
			{
				if( confItem.getValueWithDefaultValue() != null )
					send( confItem.getCommand() );
			}
			return( createSuccessResult() );
		});
	}

	protected void updateChessEngineConfiguration( ChessEngineConfiguration conf )
	{
		if( conf != null )
		{
			Map< String, ConfigurationItem > map = _configuration.getChessEngineConfiguration().getMap();

			for( Map.Entry<String, ConfigurationItem> entry: conf.getMap().entrySet() )
			{
				ConfigurationItem ci = map.get( entry.getKey() );
				if( ci != null )
					ci.setValue( entry.getValue().getValue() );
				else
					map.put( entry.getKey(), entry.getValue() );
			}
		}
	}

	@Override
	public void applyButtonOptionItem( EngineButtonNameArgs buttonNameArgs,
		Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.APPLY_BUTTON_OPTION_ITEM,
										(EngineButtonNameArgs) buttonNameArgs, callbackFun );
	}

	public void applyButtonOptionItem( EngineActionExecutorData<EngineButtonNameArgs,
												EngineActionResult> execData,
				EngineActionRequest<EngineButtonNameArgs> request,
				Consumer<FullEngineActionResult<EngineButtonNameArgs, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> {
			SuccessResult result = null;
			String name = request.getArgs().getButtonName();
			ConfigurationItem confItem = _configuration.getChessEngineConfiguration().getMap().get(name);
			if( confItem instanceof ButtonConfigurationItem )
			{
				send( confItem.getCommand() );
				result = createSuccessResult();
			}
			else
			{
				// TODO: translate
				result = createErrorResult( String.format( "[%s] was not a button option", name ) );
			}
			return( result );
		});
	}

	@Override
	public void uciNewGame(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun)
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.UCI_NEW_GAME,
										(EngineActionArgs) null, callbackFun );
	}

	protected EngineWrapperActionBase<EngineActionArgs, EngineActionResult> createUciNewGameEngineWrapperAction()
	{
		EngineWrapperActionBase result = new UciNewGameEngineWrapperAction( _utils );

		return( result );
	}

	@Override
	public void startPosition(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun)
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.START_POSITION,
										(EngineActionArgs) null, callbackFun );
	}

	public void startPosition( EngineActionExecutorData<EngineActionArgs,
												EngineActionResult> execData,
				EngineActionRequest<EngineActionArgs> request,
				Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> {
			send( "position startposition" );
			return( createSuccessResult() );
		});
	}

	@Override
	public void position( EnginePositionArgs args,
		Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.POSITION,
										args, callbackFun );
	}

	
	public void position( EngineActionExecutorData<EnginePositionArgs,
												EngineActionResult> execData,
				EngineActionRequest<EnginePositionArgs> request,
				Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> {
			send( "position fen " + request.getArgs().getFenString() );
			return( createSuccessResult() );
		});
	}

	@Override
	public void sendCustomCommand( EngineCustomCommandArgs commandArgs,
		Consumer<FullEngineActionResult<EngineCustomCommandArgs, EngineActionResult>> callbackFun )
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.CUSTOM_COMMAND,
										commandArgs, callbackFun );
	}

	@Override
	public void stopThinking(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun)
	{
		genericParticularInputActionExecution( UciChessEngineWrapper.STOP_THINKING,
										(EngineActionArgs) null, callbackFun );
	}

	public void stopThinking( EngineActionExecutorData<EngineActionArgs,
												EngineActionResult> execData,
				EngineActionRequest<EngineActionArgs> request,
				Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun )
	{
		executeGenericBasicAction( request, callbackFun,
				() -> executeStopThinking() );
	}

	protected SuccessResult executeStopThinking() throws IOException
	{
		try
		{
			_lock.lock();

			if( getCurrentAction() instanceof UciGoEngineWrapperAction )
			{
				send( "stop" );

				while( getCurrentAction() instanceof UciGoEngineWrapperAction )
					ExecutionFunctions.instance().safeMethodExecution( () -> _latestActionHasFinished.await( 1000, TimeUnit.MILLISECONDS ) );
			}

			return( createSuccessResult() );
		}
		finally
		{
			_lock.unlock();
		}
	}
}
