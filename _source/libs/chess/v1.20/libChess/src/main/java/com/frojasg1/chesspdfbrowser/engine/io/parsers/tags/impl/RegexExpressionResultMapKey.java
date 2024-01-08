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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexExpressionResultMapKey
{
	protected String _expression = null;
	protected List<String> _blocksToReplaceWith = null;
	protected int _combinationOfOptionals = -1;

	public RegexExpressionResultMapKey( String expression, List<String> blocksToReplaceWith,
							int combinationOfOptionals )
	{
		_expression = expression;
		_blocksToReplaceWith = new ArrayList<>( blocksToReplaceWith );
		Collections.sort( _blocksToReplaceWith );

		_combinationOfOptionals = combinationOfOptionals;
	}

	public String getExpression()
	{
		return( _expression );
	}

	public List<String> getBlocksToReplaceWith()
	{
		return( _blocksToReplaceWith );
	}

	public int getCombinationOfOptionals()
	{
		return( _combinationOfOptionals );
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 31 * hash + Objects.hashCode(this._expression);
		hash = 31 * hash + Objects.hashCode(this._blocksToReplaceWith);
		hash = 31 * hash + this._combinationOfOptionals;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RegexExpressionResultMapKey other = (RegexExpressionResultMapKey) obj;
		if (this._combinationOfOptionals != other._combinationOfOptionals) {
			return false;
		}
		if (!Objects.equals(this._expression, other._expression)) {
			return false;
		}
		if (!Objects.equals(this._blocksToReplaceWith, other._blocksToReplaceWith)) {
			return false;
		}
		return true;
	}

}
