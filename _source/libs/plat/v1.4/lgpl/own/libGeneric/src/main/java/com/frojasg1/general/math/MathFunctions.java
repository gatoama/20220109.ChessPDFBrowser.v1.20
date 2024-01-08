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
package com.frojasg1.general.math;

import java.util.Random;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MathFunctions {
	
	protected static MathFunctions _instance;

	public static void changeInstance( MathFunctions inst )
	{
		_instance = inst;
	}

	public static MathFunctions instance()
	{
		if( _instance == null )
			_instance = new MathFunctions();
		return( _instance );
	}

	// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
	public int floor_log2( long value )
	{
		return( 63 - Long.numberOfLeadingZeros(value) );
	}

	// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
	public int floor_binlog(long number) // returns 0 for bits=0
	{
		if( number == 0L )
			return( -1 );

		int log = 0;
		long bits = number;
		if ((bits & 0xffffffff00000000L) != 0) {
			bits >>>= 32;
			log = 32;
		}
		if (bits >= 65536) {
			bits >>>= 16;
			log += 16;
		}
		if (bits >= 256) {
			bits >>>= 8;
			log += 8;
		}
		if (bits >= 16) {
			bits >>>= 4;
			log += 4;
		}
		if (bits >= 4) {
			bits >>>= 2;
			log += 2;
		}
//		if (1 << log < number)
//			log++;
		return log + (int)(bits >>> 1);
	}

	public int floor_binlogMixt( long value )
	{
		if ((value & 0xffffffff00000000L) != 0)
		{
			return( floor_log2( value ) );
		}
		else
		{
			return( floor_binlog( value ) );
		}
	}

	public boolean isPowerOfTwo( long value )
	{
		boolean result = false;
		if( value > 1 )
		{
			int count = 0;
			for( long weight = 1; (count <= 1) && (weight <= value) && (weight <= 0x4000000000000000L); weight = weight << 1 )
				if( (value & weight) > 0 )
					count++;

			result = ( count == 1 );
		}
		return( result );
	}

	public double logN( double value, double base )
	{
		return( Math.log(value) / Math.log(base) );
	}



	public static void main( String[] args )
	{
		instance();
		int total = 1000000;
		Random random = new Random();
		long[] array = new long[total];
		for( int ii=0; ii<total; ii++ )
		{
			array[ii] = (long) ( Math.pow( 2, random.nextDouble() * 64 ) + Long.MIN_VALUE );
		}
		long start = System.currentTimeMillis();
		for( int ii=0; ii<total; ii++ )
		{
			int result = 63 - Long.numberOfLeadingZeros( array[ii] );
		}
		long end = System.currentTimeMillis();

		System.out.println( String.format( "%s: %.9f", "Fast function direct", (end-start)/( (double) (total*1000) ) ) );

		start = System.currentTimeMillis();
		for( int ii=0; ii<total; ii++ )
		{
			int result = _instance.floor_log2(array[ii] );
		}
		end = System.currentTimeMillis();

		System.out.println( String.format( "%s: %.9f", "Slower function floor_log2", (end-start)/( (double) (total*1000) ) ) );

		start = System.currentTimeMillis();
		for( int ii=0; ii<total; ii++ )
		{
			int result = _instance.floor_binlog(array[ii] );
		}
		end = System.currentTimeMillis();

		System.out.println( String.format( "%s: %.9f", "Slower function floor_binlog", ((double)end-start)/( (double) (total*1000) ) ) );

		start = System.currentTimeMillis();
		for( int ii=0; ii<total; ii++ )
		{
			int result = _instance.floor_binlogMixt(array[ii] );
		}
		end = System.currentTimeMillis();

		System.out.println( String.format( "%s: %.9f", "Slower function floor_binlogMixt", ((double)end-start)/( (double) (total*1000) ) ) );

		for( int ii=0; ii<total; ii++ )
		{
			int result1 = _instance.floor_log2(array[ii] );
			int result2 = _instance.floor_binlog(array[ii] );

			if( result1 != result2 )
			{
				System.out.println( "Inconsistence:   floor_log2( " + array[ii] + " ) = " + result1 +
									"      -------------> floor_binlog( " + array[ii] + " ) = " + result2 );
			}
		}
	}

}
