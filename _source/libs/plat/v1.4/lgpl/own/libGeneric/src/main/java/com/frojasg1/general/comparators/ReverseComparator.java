/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.comparators;

import java.util.Comparator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReverseComparator<RR> implements Comparator<RR> {

	protected Comparator<RR> _directComparator;

	public ReverseComparator( Comparator<RR> directComparator )
	{
		_directComparator = directComparator;
	}

	@Override
	public int compare(RR o1, RR o2) {
		return( - _directComparator.compare( o1, o2 ) );
	}
}
