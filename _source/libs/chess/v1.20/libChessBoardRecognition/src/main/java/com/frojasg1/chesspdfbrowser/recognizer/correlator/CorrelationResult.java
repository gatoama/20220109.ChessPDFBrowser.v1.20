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
package com.frojasg1.chesspdfbrowser.recognizer.correlator;

import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CorrelationResult
{
	protected ChessFigurePattern _pattern = null;
	protected int _numHits = 0;
	protected int _numNoHits = 0;

	protected int _xDisplacement = 0;
	protected int _yDisplacement = 0;

	protected int _numHitsShape = 0;
	protected int _numNoHitsShape = 0;

	protected boolean _isSummary = false;
	protected long _squareError = 0;
	protected int _numberOfSamples = 0;
	protected double _meanError = 0;

	public void init( ChessFigurePattern pattern )
	{
		setPattern( pattern );
	}

	public ChessFigurePattern getPattern() {
		return _pattern;
	}

	public boolean isSummary() {
		return _isSummary;
	}

	public void setIsSummary(boolean _isSummary) {
		this._isSummary = _isSummary;
	}

	public void copyShape( CorrelationResult that )
	{
		_numHitsShape = that._numHitsShape;
		_numNoHitsShape = that._numNoHitsShape;
	}

	public void setPattern(ChessFigurePattern _pattern) {
		this._pattern = _pattern;
	}

	public int getNumHits() {
		return _numHits;
	}

	public void setNumHits(int _numHits) {
		this._numHits = _numHits;
	}

	public int getNumHitsShape() {
		return _numHitsShape;
	}

	public void setNumHitsShape(int _numHitsGrey) {
		this._numHitsShape = _numHitsGrey;
	}

	public int getTotalPoints() {
		return( _numHits + _numNoHits );
	}

	public int getTotalPointsShape() {
		return( _numHitsShape + _numNoHitsShape );
	}

	public int getXDisplacement() {
		return _xDisplacement;
	}

	public void setXDisplacement(int _xDisplacement) {
		this._xDisplacement = _xDisplacement;
	}

	public int getYDisplacement() {
		return _yDisplacement;
	}

	public void setYDisplacement(int _yDisplacement) {
		this._yDisplacement = _yDisplacement;
	}

	protected double getProportion( int numHits, int totalPoints )
	{
		double result = 0.0D;
		if( totalPoints != 0 )
			result = ( (double) numHits ) / totalPoints;

		return( result );
	}

	public double getProportionOfSuccess()
	{
		return( getProportion( getNumHits(), getTotalPoints() ) );
	}

	public double getProportionOfSuccessShape()
	{
		return( getProportion( getNumHitsShape(), getTotalPointsShape() ) );
	}

	public void incHits()
	{
		_numHits++;
	}

	public void incNoHits()
	{
		_numNoHits++;
	}

	public void incHitsShape()
	{
		_numHitsShape++;
	}

	public void incNoHitsShape()
	{
		_numNoHitsShape++;
	}

	public void addSquareError( long squareError )
	{
		_squareError += squareError;
	}

	public void setNumberOfSamples( int value )
	{
		_numberOfSamples = value;
		_meanError = Math.sqrt( ( (double) _squareError ) / ( 3 * _numberOfSamples ) );
	}

	public double getMeanError()
	{
		return( _meanError );
	}
}
