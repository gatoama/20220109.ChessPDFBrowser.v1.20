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
package com.frojasg1.general.string;

import com.frojasg1.general.HexadecimalFunctions;
import com.frojasg1.general.comparators.ComparatorOfComparable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class StringFunctions
{
	public static final String RETURN = System.getProperty("line.separator");
	protected static final Pattern NEW_LINE_PATTERN = Pattern.compile( "\\r?\\n" );

	public static final Comparator<String> EXACT_STRING_COMPARATOR = new ComparatorOfComparable<>();

	protected static StringFunctions _instance;


	public static void changeInstance( StringFunctions inst )
	{
		_instance = inst;
	}

	public static StringFunctions instance()
	{
		if( _instance == null )
			_instance = new StringFunctions();
		return( _instance );
	}

	public boolean isUpperCase( Character ch )
	{
		boolean result = false;
		if( ch != null )
			result = isUpperCase( String.valueOf( ch ) );

		return( result );
	}

	public boolean isUpperCase( String str )
	{
		boolean result = false;

		if( str != null )
			result = str.toUpperCase().equals( str );

		return( result );
	}

	public boolean isEmpty( String str )
	{
		return( ( str == null ) || ( str.length() == 0 ) );
	}

	public int M_compare( char[] array1, char[] array2 )
	{
		int result = 0;
		
		if( ( array1 == null ) && ( array2 == null ) )
			result = 0;
		else if( array1 == null )
			result = -1;
		else if( array2 == null )
			result = 1;
		else
		{
			int length = ( array1.length>array2.length ? array2.length : array1.length );

			for( int ii=0; (ii<length) && (result==0); ii++ )
			{
				if( array1[ii] > array2[ii] )	result = 1;
				else if( array1[ii] < array2[ii] )	result = -1;
			}

			if( (result == 0) && ( array1.length != array2.length ) )
			{
				if( array1.length > array2.length )	result = 1;
				else	result = -1;
			}
		}

		return( result );
	}

	public byte[] M_getBytes( char[] array )
	{
		byte[] result = null;
		
		if( array != null )
		{
			result = new byte[ array.length*4 ];
			for( int ii=0; ii<array.length; ii++ )
			{
				byte[] intBuffer = HexadecimalFunctions.instance().M_getBufferFromInteger( array[ii] );
				System.arraycopy(intBuffer, 0, result, ii*4, 4 );
			}
		}

		return( result );
	}

	public static String[] runRegEx( String patronRegEx, String str )
	{
		String[] result = null;
		Pattern pat = Pattern.compile( patronRegEx );
		Matcher mat = pat.matcher( str );
		if( mat.matches() )
		{
			int numGrupos = mat.groupCount();
			result = new String[ numGrupos + 1 ];
			for( int ii=0; ii<numGrupos+1; ii++ )
			{
				result[ ii ] = mat.group( ii );
			}
		}
		return( result );
	}

	public void append( StringBuilder sb, String str )
	{
		if( str != null ) sb.append( str );
	}

	public int countNumChars( String string, String chars )
	{
		int result = 0;

		if( string != null )
		{
			int pos = 0;
			while( ( pos >= 0) && ( pos < string.length() ) )
			{
				pos = indexOfAnyChar( string, chars, pos );
				if( pos >= 0 )
				{
					result++;
					pos++;
				}
			}
		}

		return( result );
	}

	public int indexOfAnyChar( String string, String chars, int pos )
	{
		int ii = pos;
		if( ii<0 ) ii=0;

		int posFound = -1;
		for( ; (ii< string.length()) && (posFound == -1 ); ii++ )
		{
			if( isAnyChar( string.substring( ii, ii+1 ), chars ) )
				posFound = ii;
		}

		return( posFound );
	}

	public int indexOfAnyCharDistinctFrom( String string, String chars, int pos )
	{
		int ii = pos;
		if( ii<0 ) ii=0;

		int posFound = -1;
		for( ; (ii< string.length()) && (posFound == -1 ); ii++ )
		{
			if( ! isAnyChar( string.substring( ii, ii+1 ), chars ) )
				posFound = ii;
		}

		return( posFound );
	}

	public int lastIndexOfAnyChar( String string, String chars, int pos )
	{
		int ii = pos;
		if( ii>(string.length()-1) ) ii=string.length()-1;

		int posFound = -1;
		for( ; ( ii >= 0 ) && ( posFound == -1 ); ii-- )
		{
			if( isAnyChar( string.substring( ii, ii+1 ), chars ) )
				posFound = ii;
		}

		return( posFound );
	}

	// ideally string is a char, and it is searched inside the set of chars which we want to
	// know if any of them matches
	public boolean isAnyChar( String string, String chars )
	{
		boolean result = false;
		if( ( string != null ) && ( chars != null ) )
			result = chars.indexOf( string ) > -1;

		return( result );
	}

	public int indexOfAnyChar( StringBuilder string, String charsToFind, int pos )
	{
		int ii = pos;
		if( ii<0 ) ii=0;

		int posFound = -1;
		for( ; (ii< string.length()) && (posFound == -1 ); ii++ )
		{
			char cc = string.charAt(ii);
			for( int jj=0; (jj<charsToFind.length()) && (posFound == -1); jj++ )
			{
				if( charsToFind.charAt(jj) == cc )
					posFound = ii;
			}
		}

		return( posFound );
	}

	public int lastIndexOfAnyChar( StringBuilder string, String charsToFind, int pos )
	{
		int ii = pos;
		if( ii>(string.length()-1) ) ii=string.length()-1;

		int posFound = -1;
		for( ; ( ii >= 0 ) && ( posFound == -1 ); ii-- )
		{
			char cc = string.charAt(ii);
			for( int jj=0; (jj<charsToFind.length()) && (posFound == -1); jj++ )
			{
				if( charsToFind.charAt(jj) == cc )
					posFound = ii;
			}
		}

		return( posFound );
	}

	/**
	 * Function which makes a translation of characters.
	 * originSetOfChars must have the same length than destinationSetOfChars.
	 * The first character of originSetOfChars is replaced by the first character
	 * of destinationSetOfChars, an so on.
	 * 
	 * @param string					String to transform
	 * @param originSetOfChars			Set of chars to find in the string.
	 * @param destinationSetOfChars		Set of chars to transform with.
	 * @return							Transformed string
	 */
	public String replaceSetOfChars( String string, String originSetOfChars, String destinationSetOfChars )
	{
		StringBuilder sb = new StringBuilder();

		if( string != null )
		{
			if( ( originSetOfChars != null ) &&
				( destinationSetOfChars != null ) &&
				( originSetOfChars.length() == destinationSetOfChars.length() )
			   )
			{
				for( int ii=0; ii<string.length(); ii++ )
				{
					char newChar = string.charAt( ii );
					int index = originSetOfChars.indexOf( newChar );
					if( index != -1 )
						newChar = destinationSetOfChars.charAt( index );

					sb.append( newChar );
				}
			}
			else
				sb.append( string );
		}

		return( sb.toString() );
	}
	
	public String removeAllCharacters( String inputString, String charToRemove )
	{
		StringBuilder sb = new StringBuilder();

		if( inputString != null )
		{
			if( charToRemove != null )
			{
				for( int ii=0; ii<inputString.length(); ii++ )
				{
					char newChar = inputString.charAt( ii );
					int index = charToRemove.indexOf( newChar );
					if( index == -1 )
						sb.append( newChar );
				}
			}
			else
				sb.append( inputString );
		}

		return( sb.toString() );
	}
	
	public String removeAllCharactersDifferentFromChars( String inputString, String charsToKeep )
	{
		StringBuilder sb = new StringBuilder();

		if( inputString != null )
		{
			if( charsToKeep != null )
			{
				for( int ii=0; ii<inputString.length(); ii++ )
				{
					char newChar = inputString.charAt( ii );
					int index = charsToKeep.indexOf( newChar );
					if( index > -1 )
						sb.append( newChar );
				}
			}
			else
				sb.append( inputString );
		}

		return( sb.toString() );
	}
	
	/**
	 * 
	 * @param str		String from which to take the char
	 * @param position	position of the char to be extracted
	 * @return			returns the char of the position (if the string is long enough) and the char does not repeat towards the end of the string.
	 *					if not, the functions returns null
	 */
	public String getNotRepeatedChar( String str, int position )
	{
		String result = null;
		
		if( ( str != null ) && ( str.length() > position ) )
		{
			result = str.substring(position, position + 1);
			boolean repeated = false;
			if( str.length() > (position+1) )
			{
				int pos = str.indexOf( result, position + 1 );
				if( ( pos > 0 ) && ( pos < str.length() ) )
				{
					repeated = true;
				}
				if( repeated )
					result = null;
			}
		}

		return( result );
	}

	public String buildStringFromRepeatedChar( Character ch, int numberOfRepetitions )
	{
		String result = null;
		
		if( ch != null )
		{
			StringBuilder sb = new StringBuilder( numberOfRepetitions );
			for( int ii=0; ii<numberOfRepetitions; ii++ )
				sb.append( ch );
			
			result = sb.toString();
		}
		
		return( result );
	}

	public boolean stringsEquals( String str1, String str2 )
	{
		boolean result = ( str1 == str2 ) ||
						( str1 != null ) && (str2 != null ) &&
						( str1.equals( str2 ) );

		return( result );
	}

	public String getSimpleClassName( Class classObj )
	{
		String className = classObj.getName();
		int pos = className.lastIndexOf( '.' );
		if( ( pos < 0 ) || ( pos > className.length() ) )
			pos = 0;
		else
			pos++;
		String result = className.substring( pos );

		return( result );
	}

	public boolean stringStartsWith( String str, String startString )
	{
		boolean result = ( str != null ) && ( startString != null ) &&
						( str.length() >= startString.length() ) &&
						( str.substring( 0, startString.length() ).equals( startString ) );

		return( result );
	}

	public boolean stringEndsWith( String str, String endString )
	{
		boolean result = ( str != null ) && ( endString != null ) &&
						( str.length() >= endString.length() ) &&
						( str.substring( str.length() - endString.length(), str.length() ).equals( endString ) );

		return( result );
	}

	public boolean regExMatches( Pattern regexPattern, String text )
	{
		boolean result = false;

		if( ( text != null ) && ( regexPattern != null ) )
		{
			Matcher matcher = regexPattern.matcher( text );
			result = matcher.matches();
		}

		return( result );
	}

	public String regExReplaceWithFirstGroup( Pattern regexPattern, String text )
	{
		String result = null;

		Matcher matcher = regexPattern.matcher(text);
		if( matcher.matches() )
		{
			for( int ii=1; ( result == null ) && ( ii<=matcher.groupCount() ); ii++ )
			{
				String tmp = matcher.replaceFirst( "$" + String.valueOf(ii) );
				if( !StringFunctions.instance().isEmpty(tmp) )
				{
					result = tmp;
				}
			}
		}

		return( result );
	}

	public int compareTo( String str1, String str2 )
	{
		int result;

		if( ( str1 == null ) && ( str2 == null ) )
			result = 0;
		else
		{
			if( str1 == null )
				result = -1;
			else if( str2 == null )
				result = 1;
			else
				result = str1.compareTo( str2 );
		}

		return( result );
	}

    public String[] split(String text, String separator) {
        String[] result = null;

        if (text != null) {
            if ((separator == null) && (separator.length() > 0)) {
                result = new String[]{text};
            } else {
                List<String> resultList = new ArrayList<>();
                int pos = 0;
                int oldPos = pos;
                int to = text.length() - separator.length() + 1;
                while ((pos <= to) && (pos > -1)) {
                    pos = text.indexOf(separator, pos);
                    if ((pos > -1) && (pos < text.length())) {
                        resultList.add(text.substring(oldPos, pos));
                        pos += separator.length();
                    } else {
                        resultList.add(text.substring(oldPos, text.length()));
                    }

                    oldPos = pos;
                }
                result = resultList.toArray(new String[ resultList.size() ] );
            }
        }
        return (result);
    }

	public String repeat( String inputStr, int nn )
	{
		String result = null;
		if( inputStr != null )
		{
			StringBuilder sb = new StringBuilder();
			for( int ii=0; ii<nn; ii++ )
				sb.append( inputStr );

			result = sb.toString();
		}

		return( result );
	}

	public String removeAtEnd( String input, String toRemoveAtEnd )
	{
		String result = input;
		if( ( input != null ) && ( toRemoveAtEnd != null ) &&
			input.endsWith( toRemoveAtEnd ) )
		{
			result = input.substring( 0, input.length() - toRemoveAtEnd.length() );
		}

		return( result );
	}

	public String removeAtStart( String input, String toRemoveAtStart )
	{
		String result = input;
		if( ( input != null ) && ( toRemoveAtStart != null ) &&
			input.startsWith( toRemoveAtStart ) )
		{
			result = input.substring( toRemoveAtStart.length() );
		}

		return( result );
	}

	public Comparator<String> getExactComparator()
	{
		return( EXACT_STRING_COMPARATOR );
	}

	public Character getCharAt( StringBuilder sb, int pos )
	{
		Character result = null;
		if( ( sb != null ) && ( sb.length() > pos ) )
		{
			char[] arr = new char[1];

			sb.getChars(pos, pos+1, arr, 0);

			result = arr[0];
		}
		return( result );
	}

	public String join( String[] array, int startIndex, int length, String separator )
	{
		String result = "";
		String currentSep = "";
		int to = Math.min( array.length, startIndex + length );
		for( int ii=startIndex; ii < to; ii++ )
		{
			result += currentSep + array[ii];
			currentSep = separator;
		}

		return( result );
	}

	// https://stackoverflow.com/questions/454908/split-java-string-by-new-line
	public String[] getLines( String text )
	{
		return( NEW_LINE_PATTERN.split(text) );
	}

	public boolean equalsIgnoreCase( String str1, String str2 )
	{
		boolean result = ( str1 == str2 );
		if( !result && ( str1 != null ) )
			result = str1.equalsIgnoreCase(str2);

		return( result );
	}
}
