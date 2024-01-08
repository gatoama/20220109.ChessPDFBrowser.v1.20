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
package com.frojasg1.general.desktop.view.zoom.componentcopier;

import com.frojasg1.general.desktop.view.zoom.componentcopier.factory.ComponentCopierFactory;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericCompCopier implements CompCopier<Component>
{
	protected static GenericCompCopier _instance = null;

	protected Map< Class<? extends Component>, CompCopier > _map;

	public static GenericCompCopier instance()
	{
		if( _instance == null )
		{
			_instance = createInstance();
		}

		return( _instance );
	}

	protected static synchronized GenericCompCopier createInstance()
	{
		if( _instance == null )
		{
			_instance = new GenericCompCopier();
			_instance.init();
		}
		
		return( _instance );
	}

	public void init()
	{
		constructorInit();
	}

	protected void constructorInit()
	{
		_map = createCopiersMap();
	}

	protected ComponentCopierFactory createComponentCopierFactory()
	{
		return( new ComponentCopierFactory() );
	}

	protected Map< Class<? extends Component>, CompCopier > createCopiersMap()
	{
		ComponentCopierFactory factory = createComponentCopierFactory();

		Map< Class<? extends Component>, CompCopier > result = new HashMap<>();

		addCopier( factory.createComponentCopier(), result );
		addCopier( factory.createContainerCopier(), result );
		addCopier( factory.createJComponentCopier(), result );
		addCopier( factory.createJTextComponentCopier(), result );
		addCopier( factory.createJEditorPaneCopier(), result );
		addCopier( factory.createJTextPaneCopier(), result );
		addCopier( factory.createAbstractButtonCopier(), result );
		addCopier( factory.createJToggleButtonCopier(), result );
		addCopier( factory.createJCheckBoxCopier(), result );
		addCopier( factory.createJRadioButtonCopier(), result );
		addCopier( factory.createJScrollPaneCopier(), result );
		addCopier( factory.createJSliderCopier(), result );
		addCopier( factory.createJButtonCopier(), result );
		addCopier( factory.createBasicArrowButtonCopier(), result );
		addCopier( factory.createMetalScrollButtonCopier(), result );
		addCopier( factory.createJTextFieldCopier(), result );
		addCopier( factory.createJPasswordFieldCopier(), result );
		addCopier( factory.createJLabelCopier(), result );
		addCopier( factory.createJMenuItemCopier(), result );
		addCopier( factory.createJRadioButtonMenuItemCopier(), result );
		addCopier( factory.createJCheckBoxMenuItemCopier(), result );
		addCopier( factory.createJSpinnerCopier(), result );
		addCopier( factory.createJMenuCopier(), result );
		addCopier( factory.createJComboBoxCopier(), result );

		return( result );
	}

	public void addCopier( CompCopier copier )
	{
		addCopier( copier, _map );
	}

	protected void addCopier( CompCopier copier, Map< Class<? extends Component>, CompCopier > map )
	{
		if( ( map != null ) && ( copier != null ) )
		{
			map.put( copier.getParameterClass(), copier );
		}
	}

	@Override
	public void copy( Component originalComponent, Component newComponent )
	{
		if( ( originalComponent != null ) && ( newComponent != null ) )
		{
			Class<?> origClass = originalComponent.getClass();

			if( ! origClass.isInstance( newComponent ) )
			{
				throw( new RuntimeException( "ERROR: originalComponent must have a class which must be parent of the one of newComponent. " +
											"originalComponentClass: " + origClass.getName() + " newComponentClass: " + newComponent.getClass().getName() ) );
			}

			copyGen( originalComponent, newComponent, originalComponent.getClass() );
		}
	}

	public void copyToNew( Component originalComponent, Component newComponent )
	{
		if( ( originalComponent != null ) && ( newComponent != null ) )
		{
			Class<?> clazzToCopy = newComponent.getClass();
			checkClass( originalComponent, clazzToCopy );
			checkClass( newComponent, clazzToCopy );

			copyGen( originalComponent, newComponent, clazzToCopy );
		}
	}

	protected void checkClass( Component comp, Class<?> clazz )
	{
		if( ! clazz.isInstance( comp ) )
		{
			throw( new RuntimeException( String.format( "Component: %s is not instanceof",
														comp.getClass().getName(),
														clazz.getName() )
										)
				);
		}
	}

	protected void copyGen( Component originalComponent, Component newComponent,
							Class<?> clazzToCopy )
	{
		if( ( originalComponent != null ) && ( newComponent != null ) )
		{
			List<Class<?>> classList = ReflectionFunctions.instance().getListOfClassesOfClassFromTheMostGenericToTheMostParticular(clazzToCopy);

			for( Class<?> clazz: classList )
			{
				Class<? extends Component> compClass = ( Class<? extends Component> ) clazz;

				if( Component.class.isAssignableFrom( compClass ) )
					copy_internal( compClass,
									originalComponent,
									newComponent );
			}
		}
	}

	protected <CC extends Component> void copy_internal( Class<CC> clazz, Component origComp, Component newComp )
	{
		CompCopier cc = _map.get( clazz );

		if( cc != null )
		{
			Class<?> paramClass = cc.getParameterClass();

			if( clazz.equals( paramClass ) )
			{
				CompCopier<CC> componentCopier = (CompCopier<CC>) cc;

				componentCopier.copy( clazz.cast( origComp ), clazz.cast( newComp ) );
			} else {
				throw( new RuntimeException( "ComponentCopier was not of the expected class. Expected class: " + clazz.getName() + " . Class got: " + paramClass.getName() ) );
			}
		}
		else
		{
			throw( new RuntimeException( "Copier not found for class: " + clazz.getName() ) );
		}
	}

	@Override
	public Class<Component> getParameterClass()
	{
		return( Component.class );
	}
}
