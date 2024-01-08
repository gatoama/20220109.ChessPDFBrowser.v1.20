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
public class DefaultGenericPojoStringDecoderImpl<RR> extends GenericStringDecoderBase<RR>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGenericPojoStringDecoderImpl.class);

	protected Class<RR> _class;

	public DefaultGenericPojoStringDecoderImpl( Class<RR> clazz, GenericStringDecoderBuilder builder )
	{
		super( builder );

		_class = clazz;
	}

	@Override
	public RR decode( String string )
	{
		return( super.decode( string ) );
	}

	@Override
	public RR decodeInternal( String string, String className )
	{
		RR result = createEmptyObject();
		decode( string, result );

		return( result );
	}

	@Override
	protected boolean isClassExpectedInternal( String className )
	{
		return( Objects.equals( className, _class.getName() ) );
	}

	@Override
	protected String getExpectedClassName()
	{
		return( _class.getName() );
	}

	@Override
	public void decode( String string, RR result )
	{
		super.decode( string, result );
	}

	@Override
	public void decodeInternalWithResult( String string, String className, RR result )
	{
		String valueStr = this.removeClassNameAndBraces(string, className);

		createComposedObject( valueStr, result );
	}

	protected RR createComposedObject( String valueStr, RR result )
	{
		List<Pair<String, AttributeValueData>> attributeList = getAttributeList( valueStr );

		return( createComposedObject( result, attributeList ) );
	}

	protected RR createComposedObject( RR result,
								List<Pair<String, AttributeValueData>> attributeList )
	{
		Map<String, Method> setterList = getListOfFunctionsByPrefix( _class ).get(1);

		for( Pair<String, AttributeValueData> pair: attributeList )
			setValue( result, getMethod(setterList, pair), getValue( pair ) );

		return( result );
	}

	protected void setValue( Object result, Method method, Object value )
	{
		runtimeExceptionMethodExecution(
			() -> method.invoke(result, value)
		);
	}

	protected Object getValue( Pair<String, AttributeValueData> pair )
	{
		AttributeValueData avd = pair.getValue();
		GenericStringDecoder decoder = getGenericStringDecoder( avd.getClassName() );

		return( decoder.decode( avd.getAllText() ) );
	}

	protected RR createEmptyObject()
	{
		return( runtimeExceptionFunctionExecution( () -> _class.newInstance() ) );
	}

	protected Method getMethod( Map<String, Method> setterList, Pair<String, AttributeValueData> pair )
	{
		return( setterList.get( createSetterFunctionName( pair.getKey() ) ) );
	}

	protected String createSetterFunctionName( String attributeName )
	{
		return( "set" + attributeName );
	}

	protected AttributeDecoder getAttributeDecoder()
	{
		return( AttributeDecoder.instance() );
	}

	protected List<Pair<String, AttributeValueData>> getAttributeList( String string )
	{
//		String pureAttStr = getAttributesString(string);
		String pureAttStr = string;

		List<Pair<String, AttributeValueData>> result = new ArrayList<>();
		AttributeSplitter splitter = new AttributeSplitter(pureAttStr, getAttributeDecoder());
		Pair<String, AttributeValueData> attStr;
		do
		{
			attStr = splitter.nextPair();
			if( attStr != null )
				result.add(attStr);
		}
		while( attStr != null );

		return( result );
	}
/*
	protected String getAttributesString( String string )
	{
		string = string.trim();
		if( string.isEmpty() ||
			( string.charAt(0) != '{' ) ||
				( string.charAt( string.length() - 1) != '}' ) )
		{
			throw( new RuntimeException( "Attributes not found" ) );
		}
		
		return( string.substring( 1, string.length() - 1 ) );
	}
*/
	protected List<Map<String, Method>> getListOfFunctionsByPrefix( Class<?> clazz )
	{
		return( getListOfFunctionsByPrefix( clazz, "get", "set" ) );
	}

	protected List<Map<String, Method>> getListOfFunctionsByPrefix( Class<?> clazz, String ... prefixes )
	{
		return( ReflectionFunctionMultliStartWithFilters.instance().get( clazz, prefixes ) );
	}
}
