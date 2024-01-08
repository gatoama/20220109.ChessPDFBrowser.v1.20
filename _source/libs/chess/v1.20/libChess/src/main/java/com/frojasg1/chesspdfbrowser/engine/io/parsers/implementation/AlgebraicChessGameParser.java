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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation;

/**
 *
 * @author Usuario
 */
public class AlgebraicChessGameParser // implements ChessGameParser
{
	protected static String ONE_PLAYER_MOVE_REGEXP = "((\\.\\.\\.|O\\-O|O\\-O\\-O|[__PIECES__]?\\s*[xX]?\\s*[abcdefgh][12345678]\\s*([=]\\s[__PIECES__])?\\s*(\\+|\\+\\+))?\\s*(1\\-0|0\\-1|1/2\\-1/2)?)";
	protected static String MOVE_AND_REPLY_REGEXP = "([1-9][0-9]*)[\\.]?\\s*__MOVE__\\s*__MOVE__?";

	
}
