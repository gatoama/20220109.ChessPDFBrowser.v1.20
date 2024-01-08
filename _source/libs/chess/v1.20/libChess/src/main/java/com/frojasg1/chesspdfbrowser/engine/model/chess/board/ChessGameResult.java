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
package com.frojasg1.chesspdfbrowser.engine.model.chess.board;

/**
 *
 * @author Usuario
 */
public enum ChessGameResult
{
	GAME_CONTINUES,
	WHITE_WINS,
	BLACK_WINS,
	WHITE_WINS_CHECK_MATE,
	BLACK_WINS_CHECK_MATE,
	DRAW,
	DRAW_STALE_MATE,
	DRAW_THIRD_REPETITION,
	DRAW_FIFTY_MOVES_WITHOUT_PROGRESS,
	DRAW_MUTUAL_AGREEMENT,
	DRAW_INSUFFICIENT_MATERIAL
}
