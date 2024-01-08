/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.reflection;

import com.frojasg1.general.map.MapWrapper;
import com.frojasg1.general.reflection.ReflectionFunctionFilters.Key;
import com.frojasg1.general.reflection.ReflectionFunctionMultliStartWithFilters.MultiKey;
import com.frojasg1.general.string.StringFunctions;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReflectionFunctionMultliStartWithFilters extends MapWrapper<MultiKey, List<Map<String, Method>>> {

	protected static final Pattern GET_PREFIX = Pattern.compile( "^\\^([^(]*)\\(.*$" );

	protected static class LazyHolder
	{
		protected static final ReflectionFunctionMultliStartWithFilters INSTANCE = new ReflectionFunctionMultliStartWithFilters();
	}

	public static ReflectionFunctionMultliStartWithFilters instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected ReflectionFunctionMultliStartWithFilters()
	{
		super();

		init();
	}

	public MultiKey createKey( Class<?> clazz, String ... prefixes )
	{
		ReflectionFunctionFilters.Key[] keyArray = new ReflectionFunctionFilters.Key[prefixes.length];
		for( int ii=0; ii < prefixes.length; ii++ )
		{
			String prefix = prefixes[ii];
			keyArray[ii] = createSingleKey( clazz, prefix );
		}

		return( new MultiKey().setMultiKey(keyArray) );
	}

	protected ReflectionFunctionFilters.Key createSingleKey( Class<?> clazz, String prefix )
	{
		return( getReflectionFunctionFilters().createKey(clazz, "^" + prefix + "(.*)$" ) );
	}

	public List<Map<String, Method>> get( Class<?> clazz, String ... prefixes )
	{
		return( get( createKey( clazz, prefixes ) ) );
	}

	@Override
	protected List<Map<String, Method>> create(MultiKey key) {
		List<Map<String, Method>> list = createMultiKeyWholeList(key);

		return( createFilterFilteredMapList( list, key ) );
	}

	protected List<Map<String, Method>> createMultiKeyWholeList(MultiKey key)
	{
		List<Map<String, Method>> result = new ArrayList<>();
		for( Key singleKey: key.getMultiKey() )
			result.add( getAllFilteredMethodsSingleKey(singleKey) );

		return( result );
	}


	protected List<Map<String, Method>> createFilterFilteredMapList(List<Map<String, Method>> listOfFullFilteredMethods,
																	MultiKey key) {
		int size = listOfFullFilteredMethods.size();
		List<Map<String, Method>> result = createListOfEmptyMaps( size );

		if( size > 0 )
		{
			String[] prefixes = getPrefixes(key);

			// it has the current suffix existing functions over all prefix functions
			List<Method> current = new ArrayList<>();

			/*
				functionName:	getValue
				prefix: get
				suffix: Value
			*/
			int index;
			String suffix;
			String prefix;
			String functionName;
			for( Map.Entry<String, Method> entry: listOfFullFilteredMethods.get(0).entrySet() )
			{
				suffix = StringFunctions.instance().removeAtStart( entry.getKey(), prefixes[0] );
				index = 0;
				for( Map<String, Method> mapOfFilteredFunctions: listOfFullFilteredMethods )
				{
					prefix = prefixes[index++];
					functionName = prefix + suffix;
					Method method = mapOfFilteredFunctions.get( functionName );
					if( method != null )
						current.add(method);
					else
						break;
				}

				// if suffix exist for all prefixes, we insert the functions in their associated to prefix map
				if( current.size() == size )
				{
					index = 0;
					for( Method method: current )
						result.get(index++).put(method.getName(), method);
				}

				current.clear();
			}
		}

		return( result );
	}

	protected List<Map<String, Method>> createListOfEmptyMaps( int size )
	{
		List<Map<String, Method>> result = new ArrayList<>();

		for( int ii=0; ii<size; ii++ )
			result.add( new HashMap<>() );

		return( result );
	}

	protected String[] getPrefixes( MultiKey key )
	{
		return( Arrays.stream( key.getMultiKey() ).map( this::getPrefix ) )
			.collect( Collectors.toList() ).toArray( new String[key.getMultiKey().length]);
	}

	protected String getPrefix( Key key )
	{
		return( GET_PREFIX.matcher(key.getRegex()).replaceFirst("$1") );
	}

	protected Map<String, Method> getAllFilteredMethodsSingleKey(Key singleKey)
	{
		return( getReflectionFunctionFilters().get(singleKey) );
	}

	protected ReflectionFunctionFilters getReflectionFunctionFilters()
	{
		return( ReflectionFunctionFilters.instance() );
	}

	protected Map<String, Method> getMethods( ReflectionFunctionFilters.Key singleKey )
	{
		return( ReflectionFunctionFilters.instance().get(singleKey) );
	}

	public static class MultiKey
	{
		protected ReflectionFunctionFilters.Key[] multiKey;

		public ReflectionFunctionFilters.Key[] getMultiKey() {
			return multiKey;
		}

		public MultiKey setMultiKey(ReflectionFunctionFilters.Key ... multiKey) {
			this.multiKey = multiKey;

			return( this );
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 73 * hash + Arrays.deepHashCode(this.multiKey);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MultiKey other = (MultiKey) obj;
			if (!Arrays.deepEquals(this.multiKey, other.multiKey)) {
				return false;
			}
			return true;
		}
	}
}
