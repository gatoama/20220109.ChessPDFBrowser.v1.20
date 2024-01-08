/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.threads;

import com.frojasg1.general.ExecutionFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThreadAndRunnableBase<RR extends RunnableBase> {
	
	protected Thread _thread;
	protected RR _runnable;

	protected String _name;

	public ThreadAndRunnableBase()
	{
		this(null);
	}

	public ThreadAndRunnableBase( String name )
	{
		_name = name;
	}

	public Thread getThread() {
		return _thread;
	}

	public RR getRunnable() {
		return _runnable;
	}

	public synchronized void replaceAndStartRunnable(RR runnable) {
		stopCurrent();

		_thread = null;
		setRunnable( runnable );

		start();
	}

	public synchronized void start()
	{
		if( ( _thread != null ) && _thread.isAlive() )
			throw( new IllegalStateException( "Attempting to start another thread when current is alive" ) );

		if( getRunnable() == null )
			throw( new IllegalStateException( "Attempting to start thread when runnable is null" ) );

		( _thread = createThread() ).start();
	}

	protected Thread createThread()
	{
		Thread result = null;
		if( _name != null )
			result = new Thread( getRunnable(), _name );
		else
			result = new Thread( getRunnable() );
		return( result );
	}

	public void setRunnable(RR _runnable) {
		this._runnable = _runnable;
	}

	public boolean isAlive()
	{
		Thread thread = getThread();
		return( ( thread != null ) && thread.isAlive() );
	}

	public synchronized void stopCurrent()
	{
		if( isAlive() )
			stop( getThread(), getRunnable() );

		_thread = null;
	}

	protected void stop( Thread thread, RR runnable )
	{
		new Thread( () -> {
			if( runnable != null )
				runnable.setHasToStop(true);

			ThreadFunctions.instance().sleep(10000);
			
			if( thread.isAlive() )
				ExecutionFunctions.instance().safeMethodExecution( thread::stop );
		}).start();
	}
}
