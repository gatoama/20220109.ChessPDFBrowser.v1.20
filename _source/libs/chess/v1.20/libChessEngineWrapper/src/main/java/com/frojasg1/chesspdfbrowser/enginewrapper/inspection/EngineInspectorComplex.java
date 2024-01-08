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

import com.frojasg1.chesspdfbrowser.enginewrapper.builder.EngineInstanceBuilder;
import com.frojasg1.chesspdfbrowser.enginewrapper.threads.ChessEngineThreadBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInspectorComplex implements EngineInstanceBuilder
{
	protected PullOfExecutorWorkers _uciPullOfThreads = null;
	protected PullOfExecutorWorkers _xboardPullOfThreads = null;

	public void init( int numUciThreads, int numXboardThreads )
	{
		_uciPullOfThreads = createEngineInspectorThreads( numUciThreads );
		_xboardPullOfThreads = createEngineInspectorThreads( numXboardThreads );

		_uciPullOfThreads.start();
		_xboardPullOfThreads.start();
	}

	protected PullOfExecutorWorkers createEngineInspectorThreads( int numThreads )
	{
		PullOfExecutorWorkers result = new PullOfExecutorWorkers( "EngineInspectorComplex" );
		result.init( numThreads );

		return( result );
	}

	@Override
	public UciInstance buildUciInstance()
	{
		UciInstance result = new UciInstance() {
			@Override
			protected void startThread( ChessEngineThreadBase thread )
			{
				_uciPullOfThreads.addPendingExecutor(thread);
			}
		};
		result.init();

		return( result );
	}

	public void stop()
	{
		_uciPullOfThreads.hasToStop();
		_xboardPullOfThreads.hasToStop();
	}

	public void dispose()
	{
		stop();
		_uciPullOfThreads = null;
		_xboardPullOfThreads = null;
	}

	public EngineInspectorTask inspectEngine( String command,
								Consumer<EngineInspectResult> callbackFunction )
	{
		EngineInspectorTask result = createEngineInspectorTask();
		result.init(command, callbackFunction, this);

		return( result );
	}

	protected EngineInspectorTask createEngineInspectorTask()
	{
		EngineInspectorTask result = new EngineInspectorTask();

		return( result );
	}
}
