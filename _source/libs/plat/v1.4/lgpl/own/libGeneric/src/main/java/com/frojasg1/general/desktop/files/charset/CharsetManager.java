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
package com.frojasg1.general.desktop.files.charset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author Usuario
 */
public class CharsetManager
{
	protected final int sa_beginningOfTheSortedPart = 2;
	protected String sa_autodetectString = "autodetect";

	protected static CharsetManager _instance = null;

	protected Vector<PairDescriptionCharset> _vector = null;

	protected CharsetManager()
	{
		_vector = M_createVectorOfAvailableCharsets( );
	}

	public static CharsetManager instance()
	{
		if( _instance == null )	_instance = new CharsetManager();
		return( _instance );
	}

	public Vector<PairDescriptionCharset> getVectorOfAvailableCharsets()
	{
		return( _vector );
	}
	
	public String getAutodetectString()
	{
		return( sa_autodetectString );
	}

	public void setAutodetectString( String autodetectString )
	{
		sa_autodetectString = autodetectString;
		_vector.firstElement().setDescription(autodetectString);
		_vector.firstElement().setCharsetName(autodetectString);
	}

	public String M_detectCharset( String fileName ) throws IOException
	{
		String result = null;

		java.io.FileInputStream fis = null;

		try
		{
			fis = new java.io.FileInputStream(fileName);
			result = M_detectCharset( fis );
		}
		finally
		{
			if( fis != null )
				fis.close();
		}

		return( result );
	}

	// the next function was mainly got from https://code.google.com/p/juniversalchardet/
	public String M_detectCharset( InputStream is ) throws IOException
	{
		String result = "";

		byte[] buf = new byte[4096];

		// (1)
		UniversalDetector detector = new UniversalDetector(null);

		// (2)
		int nread;
		while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		// (3)
		detector.dataEnd();

		// (4)
		result = detector.getDetectedCharset();
		if (result != null) {
			System.out.println("Detected encoding = " + result);
		} else {
			System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();

		return( result );
	}

	// the next function was mainly got from https://code.google.com/p/juniversalchardet/
	public String M_detectCharset( byte[] bytes ) throws IOException
	{
		String result = "";

		// (1)
		UniversalDetector detector = new UniversalDetector(null);

		// (2)
		int nread = 0;
		int current = 0;
		while ((nread = Math.min(bytes.length - current, 4096)) > 0 && !detector.isDone()) {
			detector.handleData(bytes, current, nread);
			current += nread;
		}
		// (3)
		detector.dataEnd();

		// (4)
		result = detector.getDetectedCharset();
		if (result != null) {
			System.out.println("Detected encoding = " + result);
		} else {
			System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();

		return( result );
	}
/*
	public String M_detectCharset( byte[] data ) throws IOException
	{
		String result = "";

		// (1)
		UniversalDetector detector = new UniversalDetector(null);

		// (2)
		detector.handleData(data, 0, data.length);

		// (3)
		detector.dataEnd();

		// (4)
		result = detector.getDetectedCharset();
		if (result != null) {
			System.out.println("Detected encoding = " + result);
		} else {
			System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();

		return( result );
	}
*/
	// the next function was mainly got from http://www.herongyang.com/Unicode/Java-charset-Supported-Character-Encodings-in-JDK.html
	protected Vector<PairDescriptionCharset> M_createVectorOfAvailableCharsets( )
	{
		Vector<PairDescriptionCharset> result = new Vector<PairDescriptionCharset>();

		PairDescriptionCharset element = new PairDescriptionCharset( sa_autodetectString, sa_autodetectString );
		result.add( 0, element );
		element = new PairDescriptionCharset( "ANSI", "Cp1252" );
		result.add( 1, element );

		SortedMap m = Charset.availableCharsets();
		Set k = m.keySet();
//		System.out.println("Canonical name, Display name,"
//			+" Can encode, Aliases");
		Iterator i = k.iterator();
		while (i.hasNext()) {
			String n = (String) i.next();
			Charset e = (Charset) m.get(n);
			String d = e.displayName();
			boolean c = e.canEncode();
//			System.out.print(n+", "+d+", "+c);
/*
			Set s = e.aliases();
			Iterator j = s.iterator();
			while (j.hasNext()) {
				String a = (String) j.next();         
				System.out.print(", "+a);
			}
			System.out.println("");
*/
			if( c )
			{
				int index = M_getSortedIndex( d, sa_beginningOfTheSortedPart );
				element = new PairDescriptionCharset( d, n );
				result.add( index, element );
			}
		}

		return( result );
	}

	protected int M_getSortedIndex( String charsetName, int initialIndex )
	{
		int result = -1;
		if( ( _vector == null ) || (initialIndex<0) || ( initialIndex > _vector.size() ) )
		{
			result = initialIndex;
		}
		else
		{
			result = M_recursive_getSortedIndex( charsetName, initialIndex, _vector.size() );
		}

		return( result );
	}
	
	protected int M_recursive_getSortedIndex( String charsetName, int initial, int finalIndex )
	{
		int result;

		if( initial == finalIndex )	result = initial;
		else
		{
			int half = (initial+finalIndex)/2;

			if( M_compareIndex( charsetName, initial ) == 0 )	result = initial;
			else if( M_compareIndex( charsetName, finalIndex ) == 0 )	result = finalIndex;
			else if( M_compareIndex( charsetName, half ) == 0 )	result = half;
			else if( ( finalIndex - initial ) <= 1 )
			{
				System.out.println( "Impossible: Sorted position for charset not found: " + charsetName );
				result = _vector.size();
			}
			else
			{
				if( M_compareIndex( charsetName, half ) > 0 )	result = M_recursive_getSortedIndex( charsetName, half, finalIndex );
				else result = M_recursive_getSortedIndex( charsetName, initial, half );
			}
		}

		return( result );
	}
	
	protected int M_compareIndex( String charsetName, int index )
	{
		int result = -1;

		// index has to have an allowed value.

		if( index == _vector.size() )
		{
			int resultComparingValueAtIndexLessOne = charsetName.compareTo( _vector.get(index-1).toString() );

			if( resultComparingValueAtIndexLessOne >= 0 ) result = 0;
			else result = -1;
		}
		else if( index > sa_beginningOfTheSortedPart )
		{
			int resultComparingValueAtIndexLessOne = charsetName.compareTo( _vector.get(index-1).toString() );

			if( index < _vector.size() )
			{
				int resultComparingValueAtIndex = charsetName.compareTo( _vector.get(index).toString() );
				
				if( (resultComparingValueAtIndexLessOne >= 0) && (resultComparingValueAtIndex < 0) )	result = 0;
				else if( resultComparingValueAtIndexLessOne < 0 )		result = -1;
				else if( resultComparingValueAtIndex >= 0 ) result = 1;
				else
					System.out.println( "Impossible: not reasonable result of comparison:  charsetName:" + charsetName +
										". value at index-1:" + _vector.get(index-1).toString() + ". value at index:" +
										_vector.get(index).toString()
						);
			}
			else
			{
				if( resultComparingValueAtIndexLessOne >= 0 ) result = 0;
				else result = -1;
			}
		}
		else if( index == sa_beginningOfTheSortedPart )
		{
			int resultComparingValueAtIndex = charsetName.compareTo( _vector.get(index).toString() );

			if( resultComparingValueAtIndex < 0 ) result = 0;
			else result = 1;
		}
		else
		{
			System.out.println( "Impossible: index has not a reasonable value " + index );
		}

		return( result );
	}
	
	public int M_findCharsetInVector( String charsetName )
	{
		int result = -1;
		if( _vector != null )
		{
			for( int ii=0; (result==-1) && (ii<sa_beginningOfTheSortedPart) && (ii<_vector.size()); ii++ )
			{
				if( charsetName.compareTo( _vector.get(ii).toString() ) == 0 )	result = ii;
			}

			if( (result == -1) && (sa_beginningOfTheSortedPart<_vector.size() ) )
			{
				result = M_recursive_findCharsetInVector( charsetName, sa_beginningOfTheSortedPart, _vector.size()-1 );
			}
		}

		return( result );
	}

	protected int M_recursive_findCharsetInVector( String charsetName, int initial, int finalIndex )
	{
		int result = -1;

		if( initial == finalIndex )	result = initial;
		else
		{
			int half = (initial+finalIndex)/2;

			if( charsetName.compareTo( _vector.get(initial).toString() ) == 0 )		result = initial;
			else if( charsetName.compareTo( _vector.get(finalIndex).toString() ) == 0 )	result = finalIndex;
			else if( charsetName.compareTo( _vector.get(half).toString() ) == 0 )			result = half;
			else if( ( finalIndex - initial ) <= 1 )
			{
				result = -1;
			}
			else
			{
				if( charsetName.compareTo( _vector.get(half).toString() ) > 0 )	result = M_recursive_findCharsetInVector( charsetName, half, finalIndex );
				else result = M_recursive_findCharsetInVector( charsetName, initial, half );
			}
		}

		return( result );
	}
}
