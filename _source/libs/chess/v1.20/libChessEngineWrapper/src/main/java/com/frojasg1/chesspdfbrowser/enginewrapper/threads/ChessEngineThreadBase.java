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
package com.frojasg1.chesspdfbrowser.enginewrapper.threads;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.executor.ExecutorInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import com.frojasg1.chesspdfbrowser.enginewrapper.listeners.InputOutputListener;
import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ChessEngineThreadBase extends Thread
											implements EngineThread, ExecutorInterface
{
	protected String _engineCommand = null;

	protected List<InputOutputListener> _inputOutputListeners = null;

	protected boolean _isRunning = false;

	protected Process _process = null;
	protected volatile BufferedWriter _out = null;
	protected BufferedReader _in = null;

	protected ReentrantLock _lock = new ReentrantLock(true);
	protected Condition _waitForOutputStreamBeCreated = _lock.newCondition();

	protected String _lastCommand = null;

	public ChessEngineThreadBase()
	{
		_inputOutputListeners = createList();
	}

	public void init( String engineCommand )
	{
		_engineCommand = engineCommand;

//		start();
	}

	protected <CC> List<CC> createList()
	{
		return( new ArrayList<>() );
	}

	protected void setIsRunning( boolean value )
	{
		_isRunning = value;
	}

	// Xboard (finally Xboard was not included).
	// https://www.gnu.org/software/xboard/engine-intf.html

	// UCI
	// http://wbec-ridderkerk.nl/html/UCIProtocol.html

	// Connect two processes by streams
	// https://chess.stackexchange.com/questions/16601/connecting-chess-engine-with-a-java-program
	// https://stackoverflow.com/questions/27081918/java-how-to-send-a-value-to-child-process-using-outputstream
	@Override
	public void run()
	{
		try
		{
			setIsRunning( true );
			createProcess();
			createOutputStream();
			createInputStream();

			flush();

			String text;
			while((text = readLine()) !=null) {
				System.out.println( "Line from engine: " + text );
				notifyOutputLine( text );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		finally
		{
			setIsRunning( false );
		}
	}

	protected String readLine() throws IOException
	{
		return( getInputStream().readLine() );
	}

	protected void flush() throws IOException
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> getOutputStream().flush() );
	}

	protected void setProcess( Process process )
	{
		_process = process;
	}

	protected void createProcess() throws IOException
	{
		setProcess(createProcess(_engineCommand ) );
	}

	protected Process createProcess( String chessEngineLongFileName ) throws IOException
	{
		return( Runtime.getRuntime().exec(chessEngineLongFileName) );
	}

	protected Process getProcess()
	{
		return( _process );
	}

	protected void setOutputStream( BufferedWriter writer )
	{
		try
		{
			_lock.lock();

			_out = writer;

			if( _lock.hasWaiters(_waitForOutputStreamBeCreated) )
				_waitForOutputStreamBeCreated.signalAll();
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected BufferedWriter getOutputStream()
	{
		return( _out );
	}

	protected void createOutputStream()
	{
		setOutputStream( createOutputStream( getProcess() ) );
	}

	protected BufferedWriter createOutputStream( Process process )
	{
		return( new BufferedWriter(new OutputStreamWriter(process.getOutputStream())) );
	}

	protected void setInputStream( BufferedReader reader )
	{
		_in = reader;
	}

	protected BufferedReader getInputStream()
	{
		return( _in );
	}

	protected void createInputStream()
	{
		setInputStream( createInputStream( getProcess() ) );
	}

	protected BufferedReader createInputStream( Process process )
	{
		return( new BufferedReader(new InputStreamReader(_process.getInputStream())) );
	}

	protected synchronized List<InputOutputListener> copyInputOutputListenersList( )
	{
		return( new ArrayList(_inputOutputListeners) );
	}

	protected void notifyOutputLine( String text )
	{
		List<InputOutputListener> list = copyInputOutputListenersList();

		for( InputOutputListener listener: list )
			ExecutionFunctions.instance().safeMethodExecution( () -> listener.newOutputLine(this, text) );
	}

	protected void notifyInputLine( String text )
	{
		List<InputOutputListener> list = copyInputOutputListenersList();
		int index = 1;
		for( InputOutputListener listener: list )
		{
			ExecutionFunctions.instance().safeMethodExecution( () -> listener.newInputLine(this, text) );
		}
	}

	public boolean isRunning()
	{
		return( _isRunning );
	}

	public synchronized void clearInputOutputListener()
	{
		_inputOutputListeners.clear();
	}

	@Override
	public synchronized void addInputOutputListener(InputOutputListener listener)
	{
		_inputOutputListeners.add( listener );
	}

	@Override
	public synchronized void removeInputOutputListener(InputOutputListener listener)
	{
		_inputOutputListeners.remove( listener );
	}

	@Override
	public abstract void stopSession() throws IOException;

	@Override
	public void send(String command) throws IOException
	{
		try
		{
			_lock.lock();

			while( getOutputStream() == null )
			{
				try
				{
					_waitForOutputStreamBeCreated.await();
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
			_lastCommand = command;

			getOutputStream().append(command);
			getOutputStream().newLine();

			System.out.println( "command sent: " + command );
			flush();

			notifyInputLine( command );
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected boolean hasToDebugDialog()
	{
		return( Objects.equals( _lastCommand, "stop" ) );
	}

	@Override
	public void hasToStop()
	{
		ThreadFunctions.instance().startThread( () -> {
			ExecutionFunctions.instance().safeMethodExecution( () -> stopSession() );
			clearInputOutputListener();
		});
	}

	@Override
	public void execute()
	{
		run();
	}
}
