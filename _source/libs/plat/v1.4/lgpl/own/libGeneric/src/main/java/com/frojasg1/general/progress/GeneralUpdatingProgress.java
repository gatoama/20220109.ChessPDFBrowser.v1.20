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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class GeneralUpdatingProgress implements UpdatingProgressUpdater
{
	public static final String GLOBAL_CONF_FILE_NAME = "GeneralUpdatingProgress.properties";

	public static final String CONF_CANCELLED_BY_USER = "CANCELLED_BY_USER";



	protected UpdatingProgress _parentUpdatingProgress = null;

	protected double _total = 1.0D;
	protected double _completedSlicesTotal = 0.0D;
	protected double _totalThisSlice = 0;

	protected double _lastReportedProgressFraction_OverOne = 0.0D;
	protected double _minimumFractionToReport_OverOne = 0.01D;

	protected boolean _debug = false;

	protected OperationCancellation _oc = null;
	
	protected static Object _listLock = new Object();
	protected static ArrayList<GeneralUpdatingProgress> _list = new ArrayList<GeneralUpdatingProgress>();

	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
//			ex.printStackTrace();
		}
	}

	public GeneralUpdatingProgress()
	{
	}

	public static GeneralUpdatingProgress createGeneralUpdatingProgress( UpdatingProgress up,
																		double totalAmount,
																		double minimumPercentageToReport )
	{
		GeneralUpdatingProgress result = null;

		if( up != null )
		{
			result = new GeneralUpdatingProgress();
			result.up_setParentUpdatingProgress(up);
			result.up_reset(totalAmount);
			result.up_setMinimumPercentageToReport(minimumPercentageToReport);
		}

		return( result );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_CANCELLED_BY_USER, "Cancelled by user" );
	}

	@Override
	public void up_setOperationCancellation( OperationCancellation oc )
	{
		_oc = oc;
	}
	
/*
	public GeneralUpdatingProgress( UpdatingProgress up, double total )
	{
		_parentUpdatingProgress = up;
		_total = total;
		if( _parentUpdatingProgress != null )
		{
			_parentUpdatingProgress.up_childStarts();
			_oc = _parentUpdatingProgress.up_getOperationCancellation();
		}
	}

	public GeneralUpdatingProgress( UpdatingProgress up )
	{
		_parentUpdatingProgress = up;
		if( _parentUpdatingProgress != null )
		{
			_parentUpdatingProgress.up_childStarts();
			_oc = _parentUpdatingProgress.up_getOperationCancellation();
		}
	}
*/
	
	@Override
	public void up_prepareNextSlice( double totalForNextSlice )
	{
		up_prepareNextSlice( totalForNextSlice, true );
	}

	public void up_prepareNextSlice( double totalForNextSlice, boolean hasToAddPreviousSlice )
	{
		if( hasToAddPreviousSlice )
			_completedSlicesTotal += _totalThisSlice;

		_totalThisSlice = totalForNextSlice;
		if( _debug )
		{
			System.out.println( String.format( "New slice. _completedSlicesTotal: %,.4f  totalForNextSlice: %,.4f", _completedSlicesTotal, totalForNextSlice ) );
		}
	}
	
	public void up_setMinimumPercentageToReport( double minimumFractionToReport_OverOne )
	{
		_minimumFractionToReport_OverOne = minimumFractionToReport_OverOne;
	}

	@Override
	public void up_skip( double totalToSkip ) throws CancellationException
	{
		_completedSlicesTotal += totalToSkip;
		_totalThisSlice = 0;
		if( _debug )
		{
			System.out.println( String.format( "New skyped slice. _completedSlicesTotal: %,.4f  totalToSkip: %,.4f", _completedSlicesTotal, totalToSkip ) );
		}
		up_updateProgressFromChild(0);
	}

	@Override
	public void up_childStarts()
	{}

	@Override
	public void up_updateProgressFromChild( double completedOverOne ) throws CancellationException
	{
		if( _debug )
			System.out.println( String.format( "up_updateProgressFromChild( %,.4f)", completedOverOne ) );

		double totalCompleted = ( _completedSlicesTotal + completedOverOne * _totalThisSlice ) / _total;

		if( _debug )
		{
			System.out.println( String.format( "Update progress. _lastReportedProgressFraction_OverOne: %,.4f " + 
												" progress received (completedOverOne): %,.4f " + 
												" progress to send: %,.4f",
												_lastReportedProgressFraction_OverOne,
												completedOverOne,
												totalCompleted ) );
		}

		if( ( totalCompleted - _lastReportedProgressFraction_OverOne ) >= _minimumFractionToReport_OverOne )
		{
			if( _parentUpdatingProgress != null )
				_parentUpdatingProgress.up_updateProgressFromChild( totalCompleted );
			_lastReportedProgressFraction_OverOne = totalCompleted;
		}
		
		if( ( _oc != null ) && ( _oc._hasToCancel ) )
			throw( new CancellationException( getInternationalString( CONF_CANCELLED_BY_USER ) ) );
	}

	@Override
	public void up_childEnds() throws CancellationException
	{
		if( _debug )
			System.out.println( "up_childEnds" );
		
		_completedSlicesTotal += _totalThisSlice;
		_totalThisSlice = 0;

		up_updateProgressFromChild(0);
	}

	@Override
	public void up_performEnd() throws CancellationException
	{
		if( _parentUpdatingProgress != null )
			_parentUpdatingProgress.up_childEnds();
	}

	public void up_setDebug( boolean debug )
	{
		_debug = debug;
	}

	public OperationCancellation up_getOperationCancellation()
	{
		return( _oc );
	}

	// for the case of reuse;
	@Override
	public void up_setParentUpdatingProgress( UpdatingProgress up )
	{
		_parentUpdatingProgress = up;

		if( _parentUpdatingProgress != null )
			this.up_setOperationCancellation( _parentUpdatingProgress.up_getOperationCancellation() );
	}

	// This function is only for terminal TotalProgressUpdating.
	public void up_reset( double total )
	{
		_total = total;
		_completedSlicesTotal = 0.0D;
		_totalThisSlice = 0.0D;
		_lastReportedProgressFraction_OverOne = 0.0D;
		_minimumFractionToReport_OverOne = 0.01D;

		if( _parentUpdatingProgress != null )
			_parentUpdatingProgress.up_childStarts();
	}

	// This function is only for terminal TotalProgressUpdating.
	public void up_updateTotalProgress( double completedOverTotal ) throws CancellationException
	{
		if( ( _oc != null ) && ( _oc.getHasToCancel() ) )
			throw( new CancellationException( getInternationalString( CONF_CANCELLED_BY_USER ) ) );
		
		up_updateProgressFromChild( completedOverTotal / _total );
	}

	// to avoid news
	public static GeneralUpdatingProgress new_GeneralUpdatingProgress()
	{
		synchronized( _listLock )
		{
			GeneralUpdatingProgress result = null;
			if( _list.size() > 0 )
				result = _list.remove(0);
			else
			{
				result = new GeneralUpdatingProgress();
			}
			return( result );
		}
	}

	public static void store_GeneralUpdatingProgress( GeneralUpdatingProgress gup )
	{
		synchronized( _listLock )
		{
			if( gup != null )
			{
				gup.up_reset( 0 );
				gup.up_setParentUpdatingProgress( null );
				_list.add( gup );
			}
		}
	}

	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
}
