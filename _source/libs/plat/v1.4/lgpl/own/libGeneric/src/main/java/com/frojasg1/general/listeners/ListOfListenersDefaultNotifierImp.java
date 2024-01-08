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
package com.frojasg1.general.listeners;

import com.frojasg1.general.functions.QuadriConsumer;
import java.util.ArrayList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfListenersDefaultNotifierImp< OB, LT, VV > extends ListOfListenersBase<LT>
															implements ListOfListenersDefaultNotifier<LT, VV>
{
	protected OB _observed;
	protected QuadriConsumer<LT, OB, VV, VV> _genericNotifier;

	public ListOfListenersDefaultNotifierImp(OB observed, QuadriConsumer<LT, OB, VV, VV> genericNotifier)
	{
		super();
		_genericNotifier = genericNotifier;
		_observed = observed;
	}

	protected OB getObserved()
	{
		return( _observed );
	}

	@Override
	public void notifyListeners( VV oldValue, VV newValue )
	{
		for( LT listener: getList() )
			notifyListener( listener, getObserved(), oldValue, newValue );
	}

	protected void notifyListener( LT listener, OB observed, VV oldValue, VV newValue )
	{
		_genericNotifier.accept(listener, observed, oldValue, newValue);
	}
}

