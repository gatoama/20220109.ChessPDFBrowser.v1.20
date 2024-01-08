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
package com.frojasg1.general.progress;

/**
 *
 * @author Usuario
 */
public interface UpdatingProgressUpdater extends UpdatingProgress
{
	// the next functions are only used for nested updating progress.
	// They can be empty for the terminal UpdatingProgress classes.
	public void up_prepareNextSlice( double totalForNextSlice );
	public void up_setMinimumPercentageToReport( double minimumFractionToReport_OverOne );
	public void up_performEnd() throws CancellationException;
	public void up_skip( double totalToSkip ) throws CancellationException;

	public void up_setParentUpdatingProgress( UpdatingProgress up );
	
	//  The next function will be called from outside for terminal reporting progress.
	public void up_reset( double total );
	public void up_updateTotalProgress( double completedOverTotal ) throws CancellationException;
}
