/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model;

import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.codec.GenericSelfCodec;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record
 */
public class ListEncoderToStringBase {
	
	protected String _separator;
	
	public ListEncoderToStringBase( String separator )
	{
		_separator = separator;
	}

	protected <RR> List<RR> copyToList( Enumeration<RR> source )
	{
		return( CollectionFunctions.instance().copyToList(source) );
	}

	protected <RR> String encode( List<RR> list, Function<RR, String> translator )
	{
		String separator = "";
		StringBuilder result = new StringBuilder();
		if( list != null )
			for( RR elem: list )
			{
				result.append(separator).append(translator.apply(elem));
				separator = _separator;
			}

		return( result.toString() );
	}

	protected <RR extends GenericSelfCodec<String>> String encode( List<RR> list )
	{
		return( encode( list, GenericSelfCodec::encode ) );
	}
}
