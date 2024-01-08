/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComparatorOfAttributes<RR> implements Comparator<RR> {

	protected List< Function<RR, ? extends Comparable<?>> > _getters = new ArrayList<>();

	public <C extends Comparable<C>> void add( Function<RR, C> getter )
	{
		_getters.add( getter );
	}

	@Override
	public int compare(RR o1, RR o2) {
		int result = 0;
		for( Function getter: _getters )
		{
			result = compareAtt( get( o1, getter ), get( o2, getter ) );
			if( result != 0 )
				break;
		}

		return( result );
	}

	protected <C extends Comparable<C>> C get( RR obj, Function<RR, C> getter )
	{
		C result = null;
		if( obj != null )
			result = getter.apply(obj);

		return( result );
	}


	public <C extends Comparable<C>> int compareAtt(C arg0, C arg1) {
		int result = 0;
		if( ( arg0 == null ) && ( arg1 == null ) )
		{
			
		}
		else if( arg0 == null )
			result = -1;
		else if( arg1 == null )
			result = 1;
		else result = arg0.compareTo( arg1 );

		return( result );
	}
}
