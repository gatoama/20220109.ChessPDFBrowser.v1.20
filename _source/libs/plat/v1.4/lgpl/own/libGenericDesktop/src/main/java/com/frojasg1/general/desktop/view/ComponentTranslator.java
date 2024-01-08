/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view;

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizationOwner;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.text.JTextComponent;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentTranslator
{
	protected Map<Class<?>, Class<?>> _firstStepForGettingTranslatorOfComponentMap;
	protected Map<Class<?>, Function<Component, ?>> _directTranslatorMap;

	// first map includes firstStepClass, secondMap includes, for every output class a mapper from firstStepClass and Component into that output class
	protected Map<Class<?>, Map<Class<?>, BiFunction<?, Component, ?>>> _translatorOfFirstStepTranslationFunctionGetterMap;

	protected static volatile ComponentTranslator _instance;

	public static ComponentTranslator instance()
	{
		ComponentTranslator instance = ComponentTranslator._instance;
		if( instance == null )
		{
			synchronized( ComponentTranslator.class )
			{
				instance = ComponentTranslator._instance;
				if( instance == null )
				{
					ComponentTranslator._instance = instance = new ComponentTranslator();
					instance.init();
				}
			}
		}
		return( instance );
	}

	protected void init()
	{
		_firstStepForGettingTranslatorOfComponentMap = createFirstStepMap();
		_directTranslatorMap = createDirectTranslationMap();
		_translatorOfFirstStepTranslationFunctionGetterMap = createMapOfMapsToGetTranslationFunction();
	}

	protected Map<Class<?>, Class<?>> createFirstStepMap()
	{
		Map<Class<?>, Class<?>> result = new HashMap<>();
		result.put(ColorThemeChangeableStatus.class, JFrameInternationalization.class );
		result.put( ResizeRelocateItem.class, JFrameInternationalization.class );
		result.put( TextCompPopupManager.class, JFrameInternationalization.class );
		result.put( TextUndoRedoInterface.class, JFrameInternationalization.class );

		return( result );
	}

	protected Map<Class<?>, Function<Component, ?>> createDirectTranslationMap()
	{
		Map<Class<?>, Function<Component, ?>> result = new HashMap<>();
		result.put( JFrameInternationalization.class, this::getInternationalization );

		return( result );
	}

	protected Map<Class<?>, Map<Class<?>, BiFunction<?, Component, ?>>> createMapOfMapsToGetTranslationFunction()
	{
		Map<Class<?>, Map<Class<?>, BiFunction<?, Component, ?>>> result = new HashMap<>();
		
		Map<Class<?>, BiFunction<?, Component, ?>> jFrameInternTranslatorMap = new HashMap<>();
		jFrameInternTranslatorMap.put(ColorThemeChangeableStatus.class, (inter, com) -> ( (JFrameInternationalization) inter).getColorThemeChangeable(com) );
		jFrameInternTranslatorMap.put(ResizeRelocateItem.class, (inter, com) -> ( (JFrameInternationalization) inter).getResizeRelocateComponentItem(com) );
		jFrameInternTranslatorMap.put(TextCompPopupManager.class, (inter, com) -> ( (JFrameInternationalization) inter).getTextCompPopupManager((JTextComponent) com) );
		jFrameInternTranslatorMap.put(TextUndoRedoInterface.class, (inter, com) -> ( (JFrameInternationalization) inter).getTextUndoRedoManager((JTextComponent) com) );

		result.put( JFrameInternationalization.class, jFrameInternTranslatorMap);

		return( result );
	}

	protected InternationalizationOwner getInternationalizationOwner(Component comp)
	{
		return( ComponentFunctions.instance().getFirstParentOfClass(comp, InternationalizationOwner.class ) );
	}

	protected JFrameInternationalization getInternationalization(Component comp)
	{
		return( getIfNotNull( getInternationalizationOwner(comp),
							InternationalizationOwner::getInternationalization ) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull( obj, getter ) );
	}

	public <CC> CC getAssociatedObject( Component comp, Class<CC> clazz )
	{
		Function<Component, CC> getter = (Function<Component, CC>) _directTranslatorMap.get(clazz);

		CC result = null;
		if( getter != null )
			result = getter.apply( comp );

		return( result );
	}

	protected Class<?> getFirstStepClass( Class outputClass )
	{
		return( _firstStepForGettingTranslatorOfComponentMap.get(outputClass) );
	}

	protected <CC, RR> BiFunction<CC, Component, RR> getBiFunction( Class<CC> firstStepClass,
																	Class<RR> outputClass )
	{
		return( (BiFunction<CC, Component, RR>)
				getIfNotNull( _translatorOfFirstStepTranslationFunctionGetterMap.get(firstStepClass),
							map -> map.get(outputClass) ) );
	}

	public <RR> Function<Component, RR> getComponentTranslator( Component comp,
																Class<RR> outputClass )
	{
		Function<Component, RR> result = null;
		Class firstStepClass = getFirstStepClass( outputClass );
		Object firstStepObj = getAssociatedObject( comp, firstStepClass );
		if( firstStepObj != null )
		{
			BiFunction<Object, Component, RR> biFunc = getBiFunction( firstStepClass, outputClass );
			if( biFunc != null )
				result = comp2 -> biFunc.apply( firstStepObj, comp2 );
		}

		if( result == null )
			result = c -> null;

		return( result );
	}
}
