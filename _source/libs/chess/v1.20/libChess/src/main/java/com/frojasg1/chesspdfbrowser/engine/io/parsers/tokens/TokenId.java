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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens;

/**
 *
 * @author Fran
 */
public enum TokenId
{
	// all the terminal symbols + Identifiers and Constants
	ATTRIBUTE,
	NUMBER,				// used internally
	DOT,				// used internally
	MOVE,
	COMMENT,			// used internally
	STRING,				// used internally
	NAG,				// used internally
	RESULT,
	OPEN_BRACKET,
	CLOSE_BRACKET,
	OPEN_BRACE,
	CLOSE_BRACE,
	OPEN_SQUARE_BRACKET,
	CLOSE_SQUARE_BRACKET,
	RETURN,
	BLANK_LINE,
	IMAGE,
	EOF
}
