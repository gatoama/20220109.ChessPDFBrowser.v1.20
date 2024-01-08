/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.codec.GenericStringDecoder;
import com.frojasg1.general.codec.GenericStringDecoderBase;
import com.frojasg1.general.codec.impl.helpers.AttributeDecoder;
import com.frojasg1.general.codec.impl.helpers.AttributeSplitter;
import com.frojasg1.general.codec.impl.helpers.AttributeValueData;
import com.frojasg1.general.reflection.ReflectionFunctionMultliStartWithFilters;
import com.frojasg1.general.string.translator.GenericStringTranslator;
import com.frojasg1.general.structures.Pair;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultGenericLeafStringDecoderImpl<RR> extends GenericLeafStringDecoderBase<RR>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGenericLeafStringDecoderImpl.class);

	public DefaultGenericLeafStringDecoderImpl( Class<RR> clazz, GenericStringDecoderBuilder builder )
	{
		super( clazz, builder );
	}

	@Override
	public RR decode( String string )
	{
		return( super.decode( string ) );
	}

	protected String getTransformedSimpleValueString( String valueStr )
	{
		String result = valueStr;

		return( result );
	}
}
