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
package com.frojasg1.general.docformats.rtf;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.StreamFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomFontToRtfStream
{
	public static final String GLOBAL_CONF_FILE_NAME = "ZoomFontToRtfStream.properties";

	public static final String CONF_AVOIDING_INFITINE_LOOP = "AVOIDING_INFITINE_LOOP";

	protected double _zoomFactor = 1.0D;

	protected String _transformedRtfDocument;
	protected String _originalRtfDocument;

	Pattern _pat = Pattern.compile( "(\\\\a?fs)([0-9]+)" );


	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	// the class finds the RTF strings that define the text size, and zoom them.
	// the strings that define text size are the following:
	//      /fsNNN   /afsNNN
	// examples:	/fs40      /afs40
	// the size is measured in half points.


	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_AVOIDING_INFITINE_LOOP, "Avoiding infinite loop" );
	}

	protected boolean isFontSizeStr( String str )
	{
		Matcher mat = _pat.matcher( str );
		return( mat.matches() );
	}

	protected String[] splitFontStr( String str )
	{
		String[] result = null;

		Matcher mat = _pat.matcher( str );
		if( mat.matches() )
		{
			int numGroups = mat.groupCount();
			result = new String[ numGroups + 1 ];
			for( int ii=0; ii<numGroups+1; ii++ )
			{
				result[ ii ] = mat.group( ii );
			}
		}

		return( result );
	}

	protected String zoomFontStr( String str )
	{
		String[] array = splitFontStr( str );

		int fontSize = IntegerFunctions.parseInt( array[2] );

		int newFontSize = (new Double( Math.ceil( fontSize * _zoomFactor ) ) ).intValue();

		String  result = array[1] + newFontSize;

		return( result );
	}

	protected String matchesWithFontStr( String rtfInput, String posibleMatchStr, int pos2 )
	{
		boolean matches = false;
		int position = pos2 + posibleMatchStr.length();
		if( position < rtfInput.length() )
		{
			char ch = rtfInput.charAt( position );
			matches = ( ch == ' ' ) || ( ch == '\\' );
		}

		String result = null;
		if( matches )
			result = posibleMatchStr;

		return( result );
	}

	protected String extractNextSubstring( String rtfInput, Matcher mat, int pos )
	{
		String result = null;

		int pos2 = pos;
		int length = rtfInput.length();
		String fontStr = null;
		while( ( pos2 < length ) && (fontStr == null) )
		{
			if( ! mat.find( pos2 ) )
			{
				pos2 = length;
				break;
			}

			String posibleMatchStr = mat.group(0);

			fontStr = matchesWithFontStr( rtfInput, posibleMatchStr, mat.start() );

			pos2 = mat.start();

			if( fontStr == null )
				pos2++;
		}

		if( pos2 == pos )
			result = fontStr;
		else
			result = rtfInput.substring( pos, pos2 );

		if( result == null )
			result = "";

		return( result );
	}

	protected Collection<String> splitDocument( String rtfInput )
	{
		ArrayList<String> result = new ArrayList<>();

		Matcher mat = _pat.matcher( rtfInput );

		int pos = 0;
		int length = rtfInput.length();
		while( pos < length )
		{
			String nextSubstring = extractNextSubstring( rtfInput, mat, pos );
			result.add( nextSubstring );

			if( nextSubstring.length() == 0 )
				throw( new RuntimeException( getInternationalString( CONF_AVOIDING_INFITINE_LOOP ) ) );
														
			pos = pos + nextSubstring.length();
		}

		return( result );
	}

	protected Collection<String> zoomSplitDocument( Collection<String> inputCol )
	{
		ArrayList<String> result = new ArrayList<String>();
		
		Iterator<String> it = inputCol.iterator();
		while( it.hasNext() )
		{
			String nextString = it.next();

			if( isFontSizeStr( nextString ) )
				result.add( zoomFontStr( nextString ) );
			else
				result.add( nextString );
		}

		return( result );
	}

	protected String reassembleSplitStrings( Collection<String> col )
	{
		StringBuilder sb = new StringBuilder();

		Iterator<String> it = col.iterator();
		while( it.hasNext() )
			sb.append( it.next() );

		return( sb.toString() );
	}

	protected String zoomRtfText( String originalRtfDocument )
	{
		String result = null;

		Collection<String> splitDocument = splitDocument( originalRtfDocument );
		
		Collection<String> zoomedDocument = zoomSplitDocument( splitDocument );

		result = reassembleSplitStrings( zoomedDocument );

		return( result );
	}

	public InputStream getInputStream( InputStream is, Charset charset, double zoomFactor )
	{
		_zoomFactor = zoomFactor;

		_originalRtfDocument = StreamFunctions.instance().readTextFromInputStream( is );

		if( _zoomFactor != 1.0D )
			_transformedRtfDocument = zoomRtfText( _originalRtfDocument );
		else
			_transformedRtfDocument = _originalRtfDocument;

		return( StreamFunctions.instance().getInputStream(_transformedRtfDocument, charset) );
	}

	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
}
