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
package com.frojasg1.chesspdfbrowser.enginewrapper.inspection;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.builder.EngineInstanceBuilder;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputListener;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputServer;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInspectorTask implements InputOutputListener
{
	protected static final Pattern _patternToExtractName = Pattern.compile( "^(.+)\\s+(\\(|by ).*$" );
	protected static final Pattern _patternToExcludeNonNamePartFromName = Pattern.compile( "^id name *" );


	protected UciInstance _uciInstance = null;

	protected String _command = null;
	protected Consumer<EngineInspectResult> _callbackFunction = null;
	protected EngineInstanceBuilder _engineBuilder = null;

	protected String _name = null;
	protected boolean _existingPreviousLines = false;

	public void init( String command,
						Consumer<EngineInspectResult> callbackFunction,
						EngineInstanceBuilder engineBuilder )
	{
		_command = command;
		_engineBuilder = engineBuilder;
		_callbackFunction = callbackFunction;

		_uciInstance = createUciInstance();
		ThreadFunctions.instance().sleep( 350 ); // to give time for execution
		initUciInstance();
	}

	protected UciInstance createUciInstance()
	{
		return( _engineBuilder.buildUciInstance() );
	}

	protected void initUciInstance()
	{
		_uciInstance.addInputOutputListener(this);
		_uciInstance.init( createEngineInstanceConfiguration(),
							(res) -> processUciInitCallback(res) );
	}

	protected EngineInstanceConfiguration createEngineInstanceConfiguration()
	{
		EngineInstanceConfiguration result = new EngineInstanceConfiguration();
		result.setEngineCommandForLaunching(_command);

		return( result );
	}

	protected void processUciInitCallback( FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult> result )
	{
//		releaseUci();

		boolean isUci = false;
		boolean isXboard = false;
		if( result.getSuccessResult().isAsuccess() )
		{
			isUci = true;
			isXboard = false;
		}
		else
		{
//			createXboardInstance();
		}

		invokeCallbackFunction(
			createEngineInspectResult( isUci, isXboard,
										getName(),
										_uciInstance.getUciInstanceConfiguration().getChessEngineConfiguration() ) );

		releaseUci();
	}

	protected void invokeCallbackFunction( EngineInspectResult result )
	{
		_callbackFunction.accept( result );

		_callbackFunction = null;
	}


	protected EngineInspectResult createEngineInspectResult( boolean isUci, boolean isXboard,
															String name,
															ChessEngineConfiguration chessEngineConfiguration )
	{
		EngineInspectResult result = new EngineInspectResult();

		result.setIsUci(isUci);
		result.setIsXboard(isXboard);
		result.setName(name);
		result.setChessEngineConfiguration(chessEngineConfiguration);

		return( result );
	}

	protected String getName()
	{
		return( _name );
	}

	@Override
	public void newOutputLine(InputOutputServer server, String line)
	{
		if( ! _existingPreviousLines )
		{
			Matcher matcher = _patternToExtractName.matcher( line );
			if( matcher.matches() )
			{
				matcher.find(0);
				_name = matcher.group(1);
			}
			else
				_name = line;

			Matcher matcher2 = _patternToExcludeNonNamePartFromName.matcher( _name );
			_name = matcher2.replaceAll( "" );

			_existingPreviousLines = true;
		}
	}

	@Override
	public void newInputLine(InputOutputServer server, String line)
	{

	}

	protected void releaseUci()
	{
		if( _uciInstance != null )
		{
			_uciInstance.removeInputOutputListener(this);
			_uciInstance.stopAndClear();
		}
	}
}
