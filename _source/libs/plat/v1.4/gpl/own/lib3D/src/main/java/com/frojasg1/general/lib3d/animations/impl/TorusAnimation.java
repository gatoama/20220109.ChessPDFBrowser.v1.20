/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.lib3d.animations.impl;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.general.lib3d.animations.AnimationJava3dBase;
import com.frojasg1.general.lib3d.scenarios.impl.TorusScenario;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TorusAnimation extends AnimationJava3dBase<TorusScenario> {

	protected static final double LOG2 = Math.log(2.0D);

	protected Dimension _dimensionWhereToMoveScenario = null;

	protected int _counter = 0;

	protected Point _position = null;
	protected boolean _xxForward = false;
	protected boolean _yyForward = false;

	protected float _fi = 0;
	protected float _zeta = 0;

	protected boolean _stop = true;

	// https://mkyong.com/java/java-generate-random-integers-in-a-range/
	protected Random _random = new Random();

	protected int _periodOfRotation = 100;
	protected double _movingSpeedFactor = 1.0f;

	public void init( TorusScenario scenario )
	{
		setScenario( scenario );
	}

	public void setEnclosingDimension( Dimension sizeWhereToMoveScenario )
	{
		_dimensionWhereToMoveScenario = sizeWhereToMoveScenario;
	}

	@Override
	public void setMode( int mode ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Point getPosition()
	{
		return( _position );
	}

	@Override
	public synchronized void doStep()
	{
		if( !_stop || !this.isTotallyStopped() )
		{
			_position = calculateNewPosition(_dimensionWhereToMoveScenario, getCanvasSize(), _position );

			calculateNewRotationAngles( _counter++ );

			doRotation();
		}
	}

	protected float calculateFi( int counter )
	{
		return( (float) ( Math.PI * ( 1 + Math.sin( 2 * Math.PI * counter / _periodOfRotation ) ) ) );
	}

	protected float calculateZeta( int counter )
	{
		return( (float) ( Math.PI / 2 * ( 1 + Math.cos( 2 * Math.PI * counter / _periodOfRotation ) ) ) );
	}

	protected void doRotation()
	{
		getScenario().doRotation( _fi, _zeta );
	}

	protected void calculateNewRotationAngles( int counter )
	{
		_fi = calculateFi( counter );
		_zeta = calculateZeta( counter );
	}

	protected Point calculateNewPosition( Dimension dimensionWhereToMoveScenario,
												Dimension canvasSize,
												Point position )
	{
		Point result = null;

		int xx = calculateNewCoordinate( dimensionWhereToMoveScenario.width,
										canvasSize.width, position.x,
										_xxForward, _stop );
		int yy = calculateNewCoordinate( dimensionWhereToMoveScenario.height,
										canvasSize.height, position.y,
										_yyForward, _stop );

		_xxForward = calculateNewIsForward( dimensionWhereToMoveScenario.width,
											canvasSize.width, position.x,
											_xxForward, _stop );
		_yyForward = calculateNewIsForward( dimensionWhereToMoveScenario.height,
											canvasSize.height, position.y,
											_yyForward, _stop );

		result = new Point( xx, yy );

		return( result );
	}

	protected boolean calculateNewIsForward( int sizeWhereToMove, int canvasSize,
												int position, boolean isForward,
												boolean stop )
	{
		boolean result = isForward;

		if( !stop )
		{
			int dist = 0;
			if( isForward )
				dist = sizeWhereToMove - canvasSize - position;
			else
				dist = position;

			if( dist < 3 )
				result = !isForward;
		}

		return( result );
	}

	protected boolean destinationReached( int sizeWhereToMove, int canvasSize,
											int position, boolean isForward )
	{
		boolean result = false;

		// if it is out of the window --> destination reached.
		if( isForward )
			result = ( sizeWhereToMove - position ) < 0;
		else
			result = ( position + canvasSize ) < 0;

		return( result );
	}

	protected boolean isTotallyStopped()
	{
		boolean result = _stop;
		
		if( _stop && ( _position != null ) )
		{
			Point position = _position;
			Dimension canvasSize = getCanvasSize();

			result = destinationReached( _dimensionWhereToMoveScenario.width,
											canvasSize.width, position.x,
											_xxForward ) &&
					destinationReached( _dimensionWhereToMoveScenario.height,
											canvasSize.height, position.y,
											_yyForward );
		}

		return( result );
	}

	protected int calculateNewCoordinate( int sizeWhereToMove, int canvasSize,
											int position, boolean isForward,
											boolean stop )
	{
		int result = position;

		if( !stop || !destinationReached( sizeWhereToMove, canvasSize,
											position, isForward ) )
		{
			int dist1 = IntegerFunctions.abs( position );
			int dist2 = IntegerFunctions.abs( sizeWhereToMove - canvasSize - position );

			int minDistModif = -1;
			if( stop )
			{
				// just the distance to the edge behind
				if( isForward )
					minDistModif = dist2;
				else
					minDistModif = dist1;
			}
			else
			{
				// minimum distance to edges
				int minDist = IntegerFunctions.min( dist1, dist2 );
				minDistModif = IntegerFunctions.max( 1, minDist );
			}

			double delta = _movingSpeedFactor * Math.log( minDistModif ) / LOG2;

			// make go away fast if stopping.
			if( _stop )
				delta *= 3;

			int deltaInt = IntegerFunctions.max( 1, (int) delta );

			if( isForward )
				result += deltaInt;
			else
				result -= deltaInt;
		}

		return( result );
	}

	@Override
	public synchronized void reset()
	{
		if( _stop )
		{
			if( isTotallyStopped() )
			{
				_stop = false;
				_counter = 0;
				setInitialPosition();

				doStep();
			}
			else
			{
				// if it is going away but still inside the window, make it normal again.
				_stop = false;
			}
		}
	}

	protected int getMaxForRandomRangeForPosition()
	{
		Dimension canvasSize = getCanvasSize();
		return( 2 * (_dimensionWhereToMoveScenario.width + canvasSize.width) +
				2* ( _dimensionWhereToMoveScenario.height + canvasSize.height) );
	}

	protected void setInitialPosition() {
		int randomNumberForPosition = getRandomNumberInRange( 0, getMaxForRandomRangeForPosition() );
		boolean randomDirection = getRandomBoolean();

		_position = calculatePositionFromRandomNumber( randomNumberForPosition, randomDirection );
	}

	public Dimension getCanvasSize()
	{
		Dimension result = getCanvas3D().getSize();
		if( ( result.width == 0 ) && ( result.height == 0 ) )
		{
			if( getCanvas3D().getParent() != null )
				result = getCanvas3D().getParent().getSize();
		}

		return( result );
	}

	// initial coordinates for canvas just at the non visible boundaries where the scenario can move.
	protected Point calculatePositionFromRandomNumber( int randomNumberForPosition,
																boolean randomDirection )
	{
		int xx = -1;
		int yy = -1;

		Dimension canvasSize = getCanvasSize();
		int toSubtract = 0;
		if( randomNumberForPosition >= ( toSubtract = 2 * (_dimensionWhereToMoveScenario.width + canvasSize.width ) ) )
		{
			randomNumberForPosition -= toSubtract;

			if( randomNumberForPosition >= ( toSubtract = ( _dimensionWhereToMoveScenario.height + canvasSize.height ) ) )
			{
				randomNumberForPosition -= toSubtract;
				xx = _dimensionWhereToMoveScenario.width;
				yy = randomNumberForPosition - _dimensionWhereToMoveScenario.height;

				_xxForward = false;
				_yyForward = randomDirection;
			}
			else
			{
				xx = -canvasSize.width;
				yy = randomNumberForPosition - _dimensionWhereToMoveScenario.height;

				_xxForward = true;
				_yyForward = randomDirection;
			}
		}
		else
		{
			if( randomNumberForPosition >= ( toSubtract = ( _dimensionWhereToMoveScenario.width + canvasSize.width ) ) )
			{
				randomNumberForPosition -= toSubtract;
				xx = randomNumberForPosition - canvasSize.width;
				yy = _dimensionWhereToMoveScenario.height;

				_xxForward = randomDirection;
				_yyForward = false;
			}
			else
			{
				xx = randomNumberForPosition - canvasSize.width;
				yy = -canvasSize.height;

				_xxForward = randomDirection;
				_yyForward = true;
			}
		}

		Point result = new Point( xx, yy );
		return( result );
	}

	protected int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		return _random.nextInt((max - min) + 1) + min;
	}

	protected boolean getRandomBoolean()
	{
		boolean result = ( getRandomNumberInRange(0,1) == 1 );

		return( result );
	}

	@Override
	public void stopAnimation()
	{
		_stop = true;
	}

	public void setPeriodOfRotation( int period )
	{
		_periodOfRotation = period;
	}

	public void setMovingSpeedFactor( double factor )
	{
		_movingSpeedFactor = factor;
	}
}
