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
package com.frojasg1.chesspdfbrowser.enginewrapper.variant;

import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineMoveVariant
{
	protected double _score = 0d;
	protected List<LongAlgebraicNotationMove> _variant = null;

	public void init()
	{
		_variant = createList();
	}

	protected <CC> List<CC> createList()
	{
		return( new ArrayList<>() );
	}

	public void setScore( double score )
	{
		_score = score;
	}

	public void add( LongAlgebraicNotationMove move )
	{
		_variant.add( move );
	}

	public List<LongAlgebraicNotationMove> getListOfMoves()
	{
		return( _variant );
	}

	public double getScore()
	{
		return( _score );
	}
}
