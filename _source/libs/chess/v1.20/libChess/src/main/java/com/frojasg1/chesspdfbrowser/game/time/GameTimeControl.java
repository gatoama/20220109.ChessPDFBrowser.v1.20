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
package com.frojasg1.chesspdfbrowser.game.time;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GameTimeControl
{
	protected Integer _wMilliSecondsPerMove;
	protected Integer _bMilliSecondsPerMove;
	protected Integer _milliSecWInc;
	protected Integer _milliSecBInc;
	protected Integer _milliSecWTime;
	protected Integer _milliSecBTime;

	public Integer getWMilliSecondsPerMove() {
		return _wMilliSecondsPerMove;
	}

	public void setWMilliSecondsPerMove(Integer _milliSecondsPerMove) {
		this._wMilliSecondsPerMove = _milliSecondsPerMove;
	}

	public Integer getBMilliSecondsPerMove() {
		return _bMilliSecondsPerMove;
	}

	public void setBMilliSecondsPerMove(Integer _milliSecondsPerMove) {
		this._bMilliSecondsPerMove = _milliSecondsPerMove;
	}

	public Integer getMilliSecWInc() {
		return _milliSecWInc;
	}

	public void setMilliSecWInc(Integer _milliSecWInc) {
		this._milliSecWInc = _milliSecWInc;
	}

	public Integer getMilliSecBInc() {
		return _milliSecBInc;
	}

	public void setMilliSecBInc(Integer _milliSecBInc) {
		this._milliSecBInc = _milliSecBInc;
	}

	public Integer getMilliSecWTime() {
		return _milliSecWTime;
	}

	public void setMilliSecWTime(Integer _milliSecWTime) {
		this._milliSecWTime = _milliSecWTime;
	}

	public Integer getMilliSecBTime() {
		return _milliSecBTime;
	}

	public void setMilliSecBTime(Integer _milliSecBTime) {
		this._milliSecBTime = _milliSecBTime;
	}
}
