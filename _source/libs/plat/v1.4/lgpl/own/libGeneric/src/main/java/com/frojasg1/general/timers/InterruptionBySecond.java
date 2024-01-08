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
package com.frojasg1.general.timers;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InterruptionBySecond implements Runnable
{
	protected ReentrantLock _lock = new ReentrantLock(true);
	protected Condition _waitingToResume = _lock.newCondition();
	protected Condition _waitingForStart = _lock.newCondition();

	protected Long _start = null;
//	protected long _lastTimeStamp;
//	protected long _lastTotalPausedFromStart = 0;
//	protected long _nextExpectedTimeStamp;

	protected volatile boolean _hasToStop = false;
	protected InterruptionListener _listener;

	protected volatile Long _timeStampStartPause = null;

	protected volatile long _totalPausedFromStart = 0;

	public InterruptionBySecond( InterruptionListener listener )
	{
		_listener = listener;
	}

	protected long getUnitsPerSecond()
	{
		return( 1000000000L );
	}

	protected long getUnitsPerMillisecond()
	{
		return( getUnitsPerSecond() / 1000 );
	}

	public void setHasToStop( boolean value )
	{
		_hasToStop = value;
	}

	public long getTime()
	{
		return( System.nanoTime() );
	}

	@Override
	public void run()
	{
		waitForStart();

//		_lastTimeStamp = _start;

		while( canContinue() )
		{
			long nextDelay = getNextDelayMs( getTime() );

			fireInterruption( getTime() );

			if( canContinue() )
			{
				
				ThreadFunctions.instance().sleep(nextDelay);
			}
		}
	}

	protected boolean canContinue()
	{
		return( !Thread.currentThread().isInterrupted() && !_hasToStop );
	}

	public void waitForStart()
	{
		try
		{
			_lock.lock();

			while( ( _start == null ) && canContinue() )
				ExecutionFunctions.instance().safeMethodExecution( ()->_waitingForStart.await(500, TimeUnit.MILLISECONDS ) );
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void start()
	{
		try
		{
			_lock.lock();

			_start = getTime();
			_timeStampStartPause = null;

			if( _lock.hasWaiters(_waitingForStart) )
				_waitingForStart.signal();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public boolean isInPause()
	{
		try
		{
			_lock.lock();

			return( (_timeStampStartPause != null) );
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void pause()
	{
		try
		{
			_lock.lock();

			if( !isInPause() )
				_timeStampStartPause = getTime();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void resume()
	{
		try
		{
			_lock.lock();

			if( !isInPause() )
			{
				long pauseTime = ( getTime() - _timeStampStartPause );
				_totalPausedFromStart += pauseTime;
				_timeStampStartPause = null;

				if( _lock.hasWaiters(_waitingToResume) )
					_waitingToResume.signal();
			}
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected long getTimeElapsed( long timestamp )
	{
		try
		{
			_lock.lock();

			long timeElapsed = ( timestamp - _start ) - _totalPausedFromStart;

			return( timeElapsed );
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected long getNextDelayMs( long timestamp )
	{
		try
		{
			_lock.lock();

			while( isInPause() && canContinue() )
				ExecutionFunctions.instance().safeMethodExecution( ()->_waitingToResume.await(500, TimeUnit.MILLISECONDS ) );

			long timeElapsed = getTimeElapsed( timestamp );

			return( 1001 - ( (timeElapsed / this.getUnitsPerMillisecond()) % 1000 ) );
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected void fireInterruption( long timestamp )
	{
		( new Thread( () -> interrupt(timestamp) ) ).start();
	}

	protected void interrupt( long timestamp )
	{
		if( _listener != null )
		{
			long timeElapsedMs = getTimeElapsed(timestamp) / this.getUnitsPerMillisecond();
			_listener.newInterruption(this, timestamp, timeElapsedMs,
										buildString( timeElapsedMs ) );
		}
	}

	protected String buildString( long timeElapsedMs )
	{
		long timeElapsedSeconds = timeElapsedMs / 1000;
		int seconds = (int) timeElapsedSeconds % 60;
		int minutes = (int) ( timeElapsedSeconds / 60 ) % 60;
		int hours = (int) timeElapsedSeconds / 3600;

		return( String.format( "%d:%02d:%02d", hours, minutes, seconds ) );
	}

	public void release()
	{
		setHasToStop(true);
		_listener = null;
	}
}
