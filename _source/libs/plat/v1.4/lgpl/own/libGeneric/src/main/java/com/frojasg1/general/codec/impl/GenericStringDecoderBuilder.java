/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.codec.GenericStringDecoder;
import com.frojasg1.general.codec.Pojo;
import com.frojasg1.general.collection.codec.ThreadSafeArrayListWrapperStringDecoder;
import com.frojasg1.general.collection.impl.ThreadSafeGenListWrapper;
import com.frojasg1.general.map.MapWrapper;
import com.frojasg1.general.structures.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericStringDecoderBuilder extends MapWrapper< Class<?>, GenericStringDecoder >
{
	protected List< Pair< Class<?>, Function<Class<?>, GenericStringDecoder> > > _listForBuilding;


	protected static class LazyHolder
	{
		protected static final GenericStringDecoderBuilder INSTANCE = new GenericStringDecoderBuilder();
	}

	public static GenericStringDecoderBuilder instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public GenericStringDecoderBuilder()
	{
		init();
	}

	@Override
	protected void init()
	{
		super.init();

		_listForBuilding = createListForBuilding();
	}

	protected List< Pair< Class<?>, Function<Class<?>, GenericStringDecoder> > > createListForBuilding()
	{
		List< Pair< Class<?>, Function<Class<?>, GenericStringDecoder> > > result = new ArrayList<>();

		result.add( new Pair<>( Pojo.class, cl -> new DefaultGenericPojoStringDecoderImpl( cl, this ) ) );

		Function<Class<?>, GenericStringDecoder> leafDecoderBuilder = cl -> new DefaultGenericLeafStringDecoderImpl( cl, this );

		result.add( new Pair<>( Number.class, leafDecoderBuilder ) );
		result.add( new Pair<>( Boolean.class, leafDecoderBuilder ) );

		result.add( new Pair<>( String.class, cl -> new DefaultGenericStringStringDecoderImpl( this ) ) );

		result.add(new Pair<>( ThreadSafeGenListWrapper.class, cl -> new ThreadSafeArrayListWrapperStringDecoder(this) ) );

		return( result );
	}

	public GenericStringDecoder get( String className )
	{
		return( get( classForName( className ) ) );
	}

	@Override
	protected GenericStringDecoder create(Class<?> key) {
		GenericStringDecoder result = null;
		if( key != null )
			result = createInternal( key );

		return( result );
	}

	protected <CC> GenericStringDecoder<? super CC> createInternal( Class<CC> clazz )
	{
		GenericStringDecoder<? super CC> result = null;
		
		for( Pair<Class<?>, Function<Class<?>, GenericStringDecoder>> pair: _listForBuilding )
			if( pair.getKey().isAssignableFrom(clazz) )
			{
				result = pair.getValue().apply(clazz);
				break;
			}

		return( result );
	}

	protected Class<?> classForName( String className )
	{
		return( ClassFunctions.instance().classForName(className) );
	}
}
