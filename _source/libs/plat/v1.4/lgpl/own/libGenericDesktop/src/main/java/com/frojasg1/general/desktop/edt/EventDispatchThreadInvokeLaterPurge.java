/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.edt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EventDispatchThreadInvokeLaterPurge {

	protected Map<String, FunctionContext> _map;
	protected int _maxToSkip;

	public EventDispatchThreadInvokeLaterPurge(int maxToSkip)
	{
		this( maxToSkip, true );
	}
	
	public EventDispatchThreadInvokeLaterPurge( int maxToSkip, boolean init )
	{
		_maxToSkip = maxToSkip;
		if( init )
			init();
	}

	protected void init()
	{
		_map = createMap();
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new ConcurrentHashMap() );
	}

	protected FunctionContext createFunctionContext()
	{
		return( new FunctionContext( _maxToSkip ) );
	}

	public void invokeLater( String functionName, Runnable runnable )
	{
		FunctionContext ctxt = _map.computeIfAbsent(functionName, fn -> createFunctionContext() );

		ctxt.invokeLater(runnable);
	}

	protected class FunctionContext
	{
		protected Map<Long, Runnable> _contextMap;
		protected int _contextMaxToSkip;
		protected int _skipped;

		protected long _index;

		public FunctionContext( int maxToSkip )
		{
			_maxToSkip = maxToSkip;
			_contextMap = new ConcurrentHashMap<>();
		}

		protected synchronized long incKey()
		{
			return( _index++ );
		}

		public void invokeLater( Runnable runnable )
		{
			long key = incKey();
			_contextMap.put( key, runnable );

			SwingUtilities.invokeLater( () -> run( key ) );
		}

		protected void run( long key )
		{
			Runnable runnable = _contextMap.remove( key );
			if( ( runnable != null ) && hasToRun() )
				runnable.run();
		}

		protected synchronized boolean hasToRun()
		{
			boolean hasToRun = _contextMap.isEmpty();
			if( !hasToRun )
				hasToRun = ( ++_skipped > _contextMaxToSkip );

			if( hasToRun )
				_skipped = 0;

			return( hasToRun );
		}
	}
}
