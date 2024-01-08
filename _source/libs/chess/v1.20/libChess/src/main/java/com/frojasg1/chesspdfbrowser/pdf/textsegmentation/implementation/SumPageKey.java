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
package com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation;

import java.awt.Dimension;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SumPageKey
{
	protected boolean _isEvenPage = false;
	protected Dimension _dimension = null;

	public SumPageKey( boolean isEvenPage, Dimension dimension )
	{
		_isEvenPage = isEvenPage;
		_dimension = dimension;
	}

	public boolean isEvenPage() {
		return _isEvenPage;
	}

	public Dimension getDimension() {
		return _dimension;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + (this._isEvenPage ? 1 : 0);
		hash = 83 * hash + Objects.hashCode(this._dimension);
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
		final SumPageKey other = (SumPageKey) obj;
		if (this._isEvenPage != other._isEvenPage) {
			return false;
		}
		if (!Objects.equals(this._dimension, other._dimension)) {
			return false;
		}
		return true;
	}


}
