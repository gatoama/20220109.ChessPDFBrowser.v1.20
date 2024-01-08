/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.reflection;

import com.frojasg1.general.map.MapWrapper;
import com.frojasg1.general.reflection.ReflectionFunctionFilters.Key;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReflectionFunctionFilters extends MapWrapper<Key, Map<String, Method>> {

	protected static class LazyHolder
	{
		protected static final ReflectionFunctionFilters INSTANCE = new ReflectionFunctionFilters();
	}

	public static ReflectionFunctionFilters instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected ReflectionFunctionFilters()
	{
		super();

		init();
	}

	public Key createKey( Class<?> clazz, String regex )
	{
		return( new Key().setClazz(clazz).setRegex(regex) );
	}

	public Map<String, Method> get( Class<?> clazz, String regex )
	{
		return( get( createKey( clazz, regex ) ) );
	}

	@Override
	protected Map<String, Method> create(Key key) {
		Map<String, Method> result = new HashMap<>();

		Pattern pattern = key.getPattern();
		Map<String, Method> all = getAllMethods( key.getClazz() );

		for( Map.Entry<String, Method> entry: all.entrySet() )
			if( pattern.matcher(entry.getKey()).find() )
				result.put(entry.getKey(), entry.getValue());

		return( result );
	}

	protected Map<String, Method> getAllMethods( Class<?> clazz )
	{
		return( ReflectionFunctions.instance().getMethodsMap(clazz) );
	}

	public static class Key
	{
		protected Class<?> _clazz;
		protected String _regex;
		protected Pattern _pattern;

		public Class<?> getClazz() {
			return _clazz;
		}

		public Key setClazz(Class<?> _clazz) {
			this._clazz = _clazz;

			return( this );
		}

		public String getRegex() {
			return _regex;
		}

		public Key setRegex(String _regex) {
			this._regex = _regex;
			_pattern = Pattern.compile(_regex);

			return( this );
		}

		public Pattern getPattern() {
			return _pattern;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 97 * hash + Objects.hashCode(this._clazz);
			hash = 97 * hash + Objects.hashCode(this._regex);
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
			final Key other = (Key) obj;
			if (!Objects.equals(this._regex, other._regex)) {
				return false;
			}
			if (!Objects.equals(this._clazz, other._clazz)) {
				return false;
			}
			return true;
		}
	}
}
