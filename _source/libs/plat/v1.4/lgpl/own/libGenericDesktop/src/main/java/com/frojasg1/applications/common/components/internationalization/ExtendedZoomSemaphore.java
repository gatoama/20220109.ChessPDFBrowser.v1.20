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
package com.frojasg1.applications.common.components.internationalization;

import com.frojasg1.general.ExecutionFunctions;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ExtendedZoomSemaphore
{
	protected Semaphore _semaphore;
	protected int _multiplier = 1;
	protected boolean _activated = false;
	protected int _count = 0;

	public void init()
	{
		_semaphore = new Semaphore(0);
	}

	public void setMultiplier( int value )
	{
		_multiplier = value;
	}

	public int getMultiplier()
	{
		return( _multiplier );
	}

	public void setActivated( boolean value )
	{
		_activated = value;
	}

	public boolean isActivated()
	{
		return( _activated );
	}

	public Semaphore getSemaphore()
	{
		return( _semaphore );
	}

	public void increaseCount()
	{
		_count++;
	}

	public void setCount( int count )
	{
		_count = count;
	}

	public void tryAcquire( int ms )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> _semaphore.tryAcquire(getTotalNumPermits(), ms, TimeUnit.MILLISECONDS ) );
	}

	protected int getTotalNumPermits()
	{
		return( getNumPermits( _count ) );
	}

	protected int getNumPermits( int elems )
	{
		return( elems * _multiplier );
	}

	public void skipRelease( int skippedElements )
	{
		getSemaphore().release( getNumPermits( skippedElements ) );
	}
}
