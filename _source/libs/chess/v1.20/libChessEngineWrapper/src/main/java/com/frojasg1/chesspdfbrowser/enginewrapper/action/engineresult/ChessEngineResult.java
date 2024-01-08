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
package com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineResult implements EngineActionResult
{
	protected LongAlgebraicNotationMove _bestMove = null;
	protected LongAlgebraicNotationMove _ponderMove = null;
	protected double _score = 0d;

	protected List<EngineMoveVariant> _bestVariants = null;

	public void init()
	{
		_bestVariants = createList();
	}

	protected <CC> List<CC> createList()
	{
		return( new ArrayList<>() );
	}

	public double getScore() {
		return _score;
	}

	public void setScore(double _score) {
		this._score = _score;
	}

	public LongAlgebraicNotationMove getBestMove( )
	{
		return( _bestMove );
	}

	public LongAlgebraicNotationMove getPonderMove()
	{
		return( _ponderMove );
	}

	public void setBestMove( LongAlgebraicNotationMove move )
	{
		_bestMove = move;
	}

	public void setPonderMove( LongAlgebraicNotationMove move )
	{
		_ponderMove = move;
	}

	public List<EngineMoveVariant> getBestVariants()
	{
		return( _bestVariants );
	}

	public void addVariant( EngineMoveVariant variant )
	{
		_bestVariants.add( variant );
	}
}
