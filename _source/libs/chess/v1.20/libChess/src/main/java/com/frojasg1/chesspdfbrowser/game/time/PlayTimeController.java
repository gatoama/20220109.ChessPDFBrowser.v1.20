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
package com.frojasg1.chesspdfbrowser.game.time;

import com.frojasg1.general.ExecutionFunctions;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PlayTimeController
{
	protected Long _remainingTimeInMs;
	protected Long _incrementTimeInMs;
	protected Long _timePerMoveMs;

	protected Runnable _timeExhaustedCallback;

	protected Long _startTime = null;

    protected Timer _timerForExhausted;
    protected Timer _timerForSeconds;

	protected Runnable _timerTaskForExhausted = null;
	protected Runnable _timerTaskForSeconds = null;

	protected Runnable _refreshTimesFunction = null;

	protected boolean _isStopped = true;

	public void init( Long remainingTimeSec, Long incrementTimeSec,
					Long timePerMove )
	{
		_timePerMoveMs = multiply( timePerMove, 1000 );
		if( _timePerMoveMs != null )
			_remainingTimeInMs = _timePerMoveMs;
		else
			_remainingTimeInMs = multiply( remainingTimeSec, 1000 );

		_incrementTimeInMs = multiply( incrementTimeSec, 1000 );
		if( _incrementTimeInMs == null )
			_incrementTimeInMs = 0L;

		_timerForExhausted = new Timer();
		_timerForSeconds = new Timer();

		_timerTaskForExhausted = new TimerTask() {
			@Override
			public void run()
			{
				timeExhausted();
			}
		};

		_timerTaskForSeconds = new TimerTask() {
			@Override
			public void run()
			{
				timerTaskForSeconds();
			}
		};
	}

	public boolean isStopped()
	{
		return( _isStopped );
	}

	protected void setStopped( boolean value )
	{
		_isStopped = value;
	}

	protected Long multiply( Long value, long factor )
	{
		Long result = null;
		if( value != null )
			result = value * factor;

		return( result );
	}

	public void setTimeExhaustedCallback( Runnable timeExhaustedCallback )
	{
		_timeExhaustedCallback = timeExhaustedCallback;
	}

	public void setRefreshTimesFunction( Runnable refreshTimesFunction )
	{
		_refreshTimesFunction = refreshTimesFunction;
	}

	protected void timerTaskForSeconds()
	{
		refreshTimes();

		restartTimerForSeconds();
	}

	protected void refreshTimes()
	{
		runRunnable( _refreshTimesFunction );
	}

	protected synchronized void timeExhausted()
	{
		if( _startTime != null )
			runRunnable( _timeExhaustedCallback );

		_startTime = null;
	}

	protected void runRunnable( Runnable runnable )
	{
		if( runnable != null )
			runnable.run();
	}

	public synchronized void cancelMove()
	{
		setStopped(true);
		_startTime = null;
		cancelTimers();
	}

	protected void cancelTimers()
	{
		cancel( _timerForExhausted );
		cancel( _timerForSeconds );
	}

	public synchronized void stopTime()
	{
		if( ! isStopped() )
		{
			setStopped(true);

			updateTime();

			cancelTimers();
		}
	}

	public synchronized void pauseTime()
	{
		if( ! isStopped() )
		{
			setStopped(true);

			discountElapsedTime();

			cancelTimers();

			checkTimeExhausted();
		}
	}

	protected synchronized void updateTime()
	{
		discountElapsedTime();
		executeIncrement();

		checkTimeExhausted();
	}

	protected synchronized void checkTimeExhausted()
	{
		if( getRemainingTime() <= 0 )
			timeExhausted();
	}

	protected synchronized void discountElapsedTime()
	{
		Long elapsedTime = getElapsedTime();
		if( elapsedTime == null )
			throw( new RuntimeException( "Stopping clock while not started to think" ) );

		_remainingTimeInMs = _remainingTimeInMs - elapsedTime;

		_startTime = null;
	}

	protected synchronized void executeIncrement()
	{
		if( _timePerMoveMs != null )
			_remainingTimeInMs = _timePerMoveMs;
		else
			_remainingTimeInMs += _incrementTimeInMs;
	}

	protected synchronized Long getElapsedTime()
	{
		Long result = null;
		if( _startTime != null )
			result = System.currentTimeMillis() - _startTime;

		return( result );
	}

	public void startTime()
	{
		if( isStopped() )
		{
			setStopped( false );

			_startTime = System.currentTimeMillis();

			_timerForExhausted = cancelAndNew( _timerForExhausted );
			schedule( _timerForExhausted, _timerTaskForExhausted, getRemainingTimeForExhausted());

			restartTimerForSeconds();
		}
		else
			System.out.println( "Not stopped but tried to start again." );
	}

	protected void schedule( Timer timer, Runnable runnable, long timeInMs )
	{
		if( timeInMs > 0 )
		{
			timer.schedule( new TimerTask() {
					@Override
					public void run()
					{
						runnable.run();
					}
			}, timeInMs );
		}
	}

	protected Timer cancelAndNew( Timer timer )
	{
		cancel( timer );
		return( new Timer() );
	}

	protected void cancel( Timer timer )
	{
		ExecutionFunctions.instance().safeSilentMethodExecution( () -> timer.cancel() );
	}

	protected void restartTimerForSeconds()
	{
		_timerForSeconds = cancelAndNew( _timerForSeconds );
		schedule( _timerForSeconds, _timerTaskForSeconds, getRemainingTimeForSeconds());
	}

	protected long getRemainingTimeForExhausted()
	{
		return( getRemainingTime() + 200 );
	}

	protected long getRemainingTimeForSeconds()
	{
		long remainingTime = getRemainingTime();
		long result = remainingTime % 1000 + 10;

		if( result >= 1000 )
			refreshTimes();

		return( result );
	}

	public long getRemainingTime()
	{
		long result = _remainingTimeInMs;
		Long elapsedTime = getElapsedTime();
		if( elapsedTime != null )
			result -= elapsedTime;

		return( result );
	}

	public void releaseResources()
	{
		cancelTimers();
	
		_timeExhaustedCallback = null;
		_startTime = null;

		_timerForExhausted = null;
		_timerForSeconds = null;

		_timerTaskForExhausted = null;
		_timerTaskForSeconds = null;

		_refreshTimesFunction = null;
	}
}
