
package com.frojasg1.general.startapp;

import com.frojasg1.general.ObjectFunctions;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author fjavier.rojas
 */
public class Version implements Comparable<Version>
{
	protected static final Pattern DATE_STRING_EXTRACTOR = Pattern.compile( "^(\\d{8}).*$" );

	protected String _folderName;
	protected Integer[] _subVersions;

	protected String _dateString;

	public Version( String folderName )
	{
		_folderName = folderName;
		_subVersions = parseSubVersions( folderName );
		_dateString = parseDateString( folderName );
	}

	protected String parseDateString( String folderName )
	{
		String result = null;
		if( folderName != null )
		{
			Matcher matcher = DATE_STRING_EXTRACTOR.matcher(folderName);
			if( matcher.matches() )
			{
				matcher.find(0);
				result = matcher.group(1);
			}	
		}
		return( result );
	}

	public String getFolderName() {
		return _folderName;
	}

	public Integer[] getSubVersions()
	{
		return( _subVersions );
	}

	public String getDateString()
	{
		return( _dateString );
	}

	protected Integer[] parseSubVersions( String folderName )
	{
		char first = folderName.charAt(0);
		if( ( first == 'v' ) || ( first == 'V' ) )
		{
			folderName = folderName.substring(1);
		}

		List<Integer> result = Arrays.stream(folderName.split("\\."))
			.map( Integer::parseInt ).collect( Collectors.toList() );

		return ( result.toArray( new Integer[result.size()]) );
	}

	@Override
	public int compareTo( Version that )
	{
		int result = -1;
		if( that != null )
		{
			result = 0;
			int to = Math.min( this._subVersions.length, that._subVersions.length );
			for( int ii = 0; ( result == 0 ) && ( ii < to ); ii++ )
				result = this._subVersions[ii] - that._subVersions[ii];

			if( result == 0 )
				result = this._subVersions.length - that._subVersions.length;
		}

		return( result );
	}

	public int compareDates( Version that )
	{
		int result = -1;
		if( that != null )
			result = ObjectFunctions.instance().compare(this._dateString, that._dateString);

		return( result );
	}
}
