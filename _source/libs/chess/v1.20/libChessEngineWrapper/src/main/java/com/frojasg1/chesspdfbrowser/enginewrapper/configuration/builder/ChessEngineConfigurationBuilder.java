/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.chesspdfbrowser.enginewrapper.configuration.builder;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ButtonConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.CheckConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ComboConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.StringConfigurationItem;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineConfigurationBuilder
{
	protected static final Pattern _optionLinePattern = Pattern.compile( "^option +name +(.+) +type +([^\\s]+) +(.*)$" );

	protected Map<String, BiFunction<String, Map<String, String>, ConfigurationItem> > _map = null;

	public void init()
	{
		_map = createMap();

		fillInBuilderMap();
	}

	protected <KK,VV> Map<KK,VV> createMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	protected void fillInBuilderMap( )
	{
		_map.put( "check", (nam, map) -> createCheckConfItem( nam, map ) );
		_map.put( "spin", (nam, map) -> createSpinConfItem( nam, map ) );
		_map.put( "combo", (nam, map) -> createComboConfItem( nam, map ) );
		_map.put( "button", (nam, map) -> createButtonConfItem( nam, map ) );
		_map.put( "string", (nam, map) -> createStringConfItem( nam, map ) );
	}

	public ChessEngineConfiguration build( List<String> uciOptions )
	{
		ChessEngineConfiguration result = new ChessEngineConfiguration();
		result.init();

		for( String line: uciOptions )
		{
			ConfigurationItem ci = createConfigurationItem( line );
			if( ci != null )
				result.add( ci );
		}

		return( result );
	}

	public ConfigurationItem createConfigurationItem( String name, String type,
														Map<String, String> pairMap )
	{
		ConfigurationItem result = null;
		BiFunction<String, Map<String, String>, ConfigurationItem> builder = _map.get( type );
		if( builder != null )
		{
			result = builder.apply(name, pairMap);
		}

		return( result );
	}

	protected ConfigurationItem createConfigurationItem( String line )
	{
		ConfigurationItem result = null;
		Matcher matcher = _optionLinePattern.matcher( line );

		if( matcher.find() )
		{
			String name = matcher.group(1);
			String type = matcher.group(2);
			String remaining = matcher.group(3);

			Map<String, String> pairMap = createPairMap( remaining );
			result = createConfigurationItem(name, type, pairMap);
		}

		return( result );
	}

	protected Map<String, String> createPairMap( String remaining )
	{
		Map<String, String> result = new HashMap<>();

		String[] array = remaining.split( "\\s" );

		if( array.length %2 != 0 )
		{
			array = ArrayFunctions.instance().addElement( array, "" );
		}
//			throw( new RuntimeException( "array does not have an even number of elements." ) );

		int index = 1;
		for( int ii=0; ii<array.length; ii+=2 )
		{
			String key = array[ii];
			String value = array[ii+1];
			
			if( key.equals( "var" ) )
				key = key + index++;

			result.put( key, value );
		}

		return( result );
	}

	protected Boolean parseBoolean( String str )
	{
		Boolean result = null;
		
		if( str != null )
			result = Boolean.parseBoolean(str);

		return( result );
	}

	protected ConfigurationItem createCheckConfItem( String name, Map<String, String> map )
	{
		CheckConfigurationItem result = new CheckConfigurationItem( parseBoolean( get( map, "default"  ) ) );

		result.init( name, parseBoolean( get( map, "value"  ) ) );

		return( result );
	}

	protected Integer parseInteger( String str )
	{
		return( IntegerFunctions.parseInt( str ) );
	}

	protected ConfigurationItem createSpinConfItem( String name, Map<String, String> map )
	{
		SpinConfigurationItem result = new SpinConfigurationItem( parseInteger( get( map, "default"  ) ),
			parseInteger( get( map, "min"  ) ), parseInteger( get( map, "max"  ) ) );

		result.init( name, parseInteger( get( map, "value"  ) ) );

		return( result );
	}

	protected String[] getValues( Map<String, String> map )
	{
		List<String> list = new ArrayList<>();

		for( Map.Entry<String, String> entry: map.entrySet() )
			if( entry.getKey().startsWith( "var" ) )
				for( String item: entry.getValue().split( "\\s" ) )
					list.add( item );

		return( list.toArray( new String[list.size()] ) );
	}

	protected ConfigurationItem createComboConfItem( String name, Map<String, String> map )
	{
		String[] values = getValues( map );
		ComboConfigurationItem result = new ComboConfigurationItem( get( map, "default"  ), values );

		result.init( name, get( map, "value"  ) );

		return( result );
	}

	protected ConfigurationItem createButtonConfItem( String name, Map<String, String> map )
	{
		ButtonConfigurationItem result = new ButtonConfigurationItem( );

		result.init( name, null );

		return( result );
	}

	protected ConfigurationItem createStringConfItem( String name, Map<String, String> map )
	{
		StringConfigurationItem result = new StringConfigurationItem( get( map, "default"  ) );

		result.init( name, get( map, "value"  ) );

		return( result );
	}

	protected String get( Map<String, String> map, String key )
	{
		String result = map.get( key );
		if( (result != null ) && result.equals( "null" ) )
			result = null;

		return( result );
	}
}
