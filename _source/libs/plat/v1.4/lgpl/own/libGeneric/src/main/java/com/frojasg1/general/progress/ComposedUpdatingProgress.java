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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fjavier.rojas
 */
public class ComposedUpdatingProgress  implements UpdatingProgressUpdater {

	protected OperationCancellation _oc;

	protected Map<String, UpdatingProgressUpdater> _map;

	public ComposedUpdatingProgress( OperationCancellation oc )
	{
		_oc = oc;
	}

	public void init( String ... upNames )
	{
		_map = createMap();

		for( String upName: upNames )
		{
			addUpdatingProgress( upName, createUpdatingProgressUpdater() );
		}
	}

	protected Map<String, UpdatingProgressUpdater> createMap()
	{
		return( new HashMap<>() );
	}

	public void addUpdatingProgress( String upName, UpdatingProgressUpdater upu )
	{
		if( upu != null )
		{
			if( upu.up_getOperationCancellation() == null )
				upu.up_setOperationCancellation(_oc);

			_map.put( upName, upu );
		}
	}

	public UpdatingProgressUpdater getUpdatingProgressUpdater( String upName )
	{
		return( _map.get( upName ) );
	}

	protected UpdatingProgressUpdater createUpdatingProgressUpdater()
	{
		GeneralUpdatingProgress result = GeneralUpdatingProgress.new_GeneralUpdatingProgress();

		return( result );
	}

	@Override
	public void up_childStarts()
	{
		for( UpdatingProgressUpdater up: _map.values() )
			up.up_childStarts();
	}

	@Override
	public void up_updateProgressFromChild(double completedOverOne) throws CancellationException
	{
		for( UpdatingProgressUpdater up: _map.values() )
			up.up_updateProgressFromChild( completedOverOne );
	}

	@Override
	public void up_childEnds() throws CancellationException
	{
		for( UpdatingProgressUpdater up: _map.values() )
			up.up_childEnds();
	}

	@Override
	public OperationCancellation up_getOperationCancellation()
	{
		return( _oc );
	}

	@Override
	public void up_setDebug(boolean debug)
	{
		for( UpdatingProgress up: _map.values() )
			up.up_setDebug(debug);
	}

	@Override
	public void up_setOperationCancellation(OperationCancellation oc) {
		for( UpdatingProgressUpdater upu: _map.values() )
		{
			if( upu.up_getOperationCancellation() == _oc )
				upu.up_setOperationCancellation(oc);
		}
		_oc = oc;
	}

	@Override
	public void up_prepareNextSlice(double totalForNextSlice) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void up_prepareNextSlice(String upName, double totalForNextSlice) {
		UpdatingProgressUpdater upu = getUpdatingProgressUpdater(upName);
		if( upu != null )
		{
			upu.up_prepareNextSlice(totalForNextSlice);
		}
	}

	@Override
	public void up_setMinimumPercentageToReport(double minimumFractionToReport_OverOne) {
		for( UpdatingProgressUpdater up: _map.values() )
			up.up_setMinimumPercentageToReport(minimumFractionToReport_OverOne);
	}

	@Override
	public void up_performEnd() throws CancellationException {
		for( UpdatingProgressUpdater up: _map.values() )
			up.up_performEnd();
	}

	@Override
	public void up_skip(double totalToSkip) throws CancellationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void up_skip(String upName, double totalToSkip) throws CancellationException {
		UpdatingProgressUpdater upu = getUpdatingProgressUpdater(upName);
		if( upu != null )
		{
			upu.up_skip(totalToSkip);
		}
	}

	@Override
	public void up_setParentUpdatingProgress(UpdatingProgress up) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void up_reset(double total) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void up_reset(String upName, double total) {
		UpdatingProgressUpdater upu = getUpdatingProgressUpdater(upName);
		if( upu != null )
		{
			upu.up_reset(total);
		}
	}

	@Override
	public void up_updateTotalProgress(double completedOverTotal) throws CancellationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void up_updateTotalProgress(String upName, double completedOverTotal) throws CancellationException {
		UpdatingProgressUpdater upu = getUpdatingProgressUpdater(upName);
		if( upu != null )
		{
			upu.up_updateTotalProgress(completedOverTotal);
		}
	}

	public void dispose()
	{
		Collection<UpdatingProgressUpdater> col = _map.values();
		_map.clear();
		for( UpdatingProgress up: col )
		{
			if( up instanceof GeneralUpdatingProgress )
			{
				GeneralUpdatingProgress.store_GeneralUpdatingProgress( ( GeneralUpdatingProgress ) up );
			}
		}
	}
}
