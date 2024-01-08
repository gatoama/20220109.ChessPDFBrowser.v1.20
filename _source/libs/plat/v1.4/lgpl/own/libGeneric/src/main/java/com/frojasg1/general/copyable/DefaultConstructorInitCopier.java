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
package com.frojasg1.general.copyable;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import com.frojasg1.general.structures.Pair;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultConstructorInitCopier {

	protected static DefaultConstructorInitCopier _instance = null;
	
	public static void changeInstance( DefaultConstructorInitCopier instance )
	{
		_instance = instance;
	}

	public static DefaultConstructorInitCopier instance()
	{
		if( _instance == null )
			_instance = new DefaultConstructorInitCopier();

		return( _instance );
	}

	public <CC> CC copy( CC that )
	{
		CC result = null;

		if( that != null )
		{
			if( ReflectionFunctions.instance().isImmutable(that) )
				result = that;
			else if( that instanceof Invariant )
				result = that;
			else if( that instanceof List )
				result = (CC) copyList( (List) that );
			else if( that instanceof Collection )
				result = (CC) copyCollection( (Collection) that );
			else if( that instanceof Map )
				result = (CC) copyMap( (Map) that );
			else if( that instanceof Pair )
				result = (CC) copyPair( (Pair) that );
			else if( ReflectionFunctions.instance().hasCopyConstructor(that) )
			{
				result = (CC) ExecutionFunctions.instance().safeFunctionExecution( () -> that.getClass().getConstructor( that.getClass() ).newInstance( that ) );
			}
			else
			{
				result = (CC) ExecutionFunctions.instance().safeFunctionExecution( () -> that.getClass().getConstructor( ).newInstance() );

				copy( result, that );
			}
		}

		return( result );
	}

	protected Dimension copyDimension( Dimension that )
	{
		return( new Dimension( that ) );
	}

	protected Pair copyPair( Pair that )
	{
		Pair result = null;
		if( that != null )
		{
			result = new Pair<>( copy( that.getKey() ), copy( that.getValue() ) );
		}

		return( result );
	}

	public <CC> void copy( CC this_, CC that )
	{
		if( ( this_ != null ) && ( that != null ) )
		{
			ExecutionFunctions.instance().safeMethodExecution( () ->
				this_.getClass().getMethod( "init", this_.getClass() ).invoke( this_, this_.getClass().cast(that) )
															);
		}
	}

	public List copyList( List that )
	{
		List result = null;
		if( that != null )
		{
			if( ! that.getClass().getName().equals( "java.util.Collections$SynchronizedRandomAccessList" ) )
				result = ExecutionFunctions.instance().safeFunctionExecution( () -> (List) that.getClass().getConstructor().newInstance() );

			if( result == null )
			{
				result = new ArrayList();
				if( that.getClass().getName().equals( "java.util.Collections$SynchronizedRandomAccessList" ) )
					result = Collections.synchronizedList( result );
			}

			for( Object item: that )
				result.add( copy( item ) );
		}

		return( result );
	}

	public <CC extends Collection> CC copyCollection( CC that )
	{
		CC result = null;
		if( that != null )
		{
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> (CC) that.getClass().getConstructor().newInstance() );

			if( result != null )
			{
				for( Object item: that )
					result.add( copy( item ) );
			}
		}

		return( result );
	}

	public <KK, CC> Map<KK, CC> copyMap( Map<KK, CC> that )
	{
		Map<KK, CC> result = null;

		if( that != null )
		{
			result = (Map<KK, CC>) ExecutionFunctions.instance().safeFunctionExecution( () -> that.getClass().getConstructor( ).newInstance() );

			for( Map.Entry<KK, CC> entry: that.entrySet() )
			{
				result.put( copy( entry.getKey() ), copy( entry.getValue() ) );
			}
		}

		return( result );
	}

	public Properties copyProperties( Properties that )
	{
		Properties result = null;
		if( that != null )
		{
			result = new Properties();
			
			for( String key: that.stringPropertyNames() )
			{
				result.setProperty(key, that.getProperty(key) );
			}
		}

		return( result );
	}

	public <CC> CC[] copyArray( Class<CC[]> clazz, CC[] that )
	{
		CC[] result = null;
		if( that != null )
		{
			result = clazz.cast( Array.newInstance(clazz.getComponentType(), that.length) );

			for( int ii=0; ii<result.length; ii++ )
				result[ii] = copy( that[ii] );
		}

		return( result );
	}
}
