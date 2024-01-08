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
package com.frojasg1.general.desktop.completion.data;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TotalCompletionData<LL> {
	protected AlternativesForCompletionData<LL> _alternativesForCompletionData;
	protected CurrentParamForCompletionData<LL> _currentParamForCompletionData;
	protected boolean _hasToSetFocus;

	public AlternativesForCompletionData<LL> createAndSetAlternativesForCompletionData()
	{
		AlternativesForCompletionData<LL> result = new AlternativesForCompletionData<>();
		setAlternativesForCompletionData(result);

		return( result );
	}

	public void setAlternativesForCompletionData( AlternativesForCompletionData<LL> value )
	{
		_alternativesForCompletionData = value;
	}

	public AlternativesForCompletionData<LL> getAlternativesForCompletionData()
	{
		return( _alternativesForCompletionData );
	}

	public CurrentParamForCompletionData<LL> createAndSetCurrentParamForCompletionData()
	{
		CurrentParamForCompletionData<LL> result = new CurrentParamForCompletionData<>();
		setCurrentParamForCompletionData( result );

		return( result );
	}

	public void setCurrentParamForCompletionData( CurrentParamForCompletionData<LL> value )
	{
		_currentParamForCompletionData = value;
	}

	public CurrentParamForCompletionData<LL> getCurrentParamForCompletionData()
	{
		return( _currentParamForCompletionData );
	}

	public void setHasToSetFocus( boolean value )
	{
		_hasToSetFocus = value;
	}

	public boolean hasToSetFocus()
	{
		return( _hasToSetFocus );
	}
}
