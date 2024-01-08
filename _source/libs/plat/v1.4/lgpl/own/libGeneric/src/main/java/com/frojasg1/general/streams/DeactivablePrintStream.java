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
package com.frojasg1.general.streams;

import java.io.PrintStream;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DeactivablePrintStream extends PrintStream
{
	protected boolean _active = true;
	protected PrintStream _original = null;

	public DeactivablePrintStream( PrintStream ps )
	{
		super( ps );
		_original = ps;
	}

	public PrintStream getOriginal()
	{
		return( _original );
	}

	public void setActive( boolean value )
	{
		_active = value;
	}

	@Override
    public void println()
	{
        if( _active )
			super.println( );
    }

	@Override
    public void println(boolean x)
	{
        if( _active )
			super.println( x );
    }

	@Override
    public void println(char x)
	{
        if( _active )
			super.println( x );
    }

	@Override
	public void println(int x) {
        if( _active )
			super.println( x );
    }

	@Override
    public void println(long x)
	{
        if( _active )
			super.println( x );
    }

	@Override
    public void println(float x)
	{
        if( _active )
			super.println( x );
    }

	@Override
	public void println(double x)
	{
        if( _active )
			super.println( x );
    }

	@Override
    public void println(char x[])
	{
        if( _active )
			super.println( x );
    }

	@Override
    public void println(Object x)
	{
        if( _active )
			super.println( x );
	}

	@Override
    public void println(String s) {
        if( _active )
			super.println( s );
    }

	@Override
    public void print(boolean b)
	{
		if( _active )
			super.print( b );
    }

	@Override
    public void print(char c)
	{
		if( _active )
			super.print( c );
    }

	@Override
    public void print(int i)
	{
		if( _active )
			super.print( i );
    }

	@Override
    public void print(long l)
	{
		if( _active )
			super.print( l );
    }

	@Override
    public void print(float f)
	{
		if( _active )
			super.print( f );
    }

	@Override
    public void print(double d)
	{
		if( _active )
			super.print( d );
    }

	@Override
    public void print(char s[])
	{
		if( _active )
			super.print( s );
    }

	@Override
    public void print(String s)
	{
		if( _active )
			super.print( s );
    }

	@Override
    public void print(Object obj)
	{
		if( _active )
			super.print( obj );
	}

	@Override
	public void flush()
	{
		if( _active )
			super.flush();
	}
}
