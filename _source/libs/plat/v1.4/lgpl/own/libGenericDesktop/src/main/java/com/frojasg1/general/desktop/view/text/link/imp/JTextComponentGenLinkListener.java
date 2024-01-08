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
package com.frojasg1.general.desktop.view.text.link.imp;

import com.frojasg1.general.containers.DoubleComparator;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.number.IntegerFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTextComponentGenLinkListener<VV> extends JTextComponentLinks< FormatForText, VV >
{
	public JTextComponentGenLinkListener( Class<VV> vClass)
	{
		this( (pos, fft) -> {
			int result = 1;
			if( fft != null )
			{
				int diff1 = pos - fft.getStart();
				int diff2 = diff1 - ( fft.getLength() - 1 );
				result = IntegerFunctions.sgn( diff1 );
				if( result != IntegerFunctions.sgn( diff2 ) )
					result = 0;
			}
			return( result );
		}, vClass);
	}

	public JTextComponentGenLinkListener( DoubleComparator< Integer, FormatForText > doubleComparator,
								Class<VV> vClass )
	{
		super( doubleComparator, vClass);
	}

}
