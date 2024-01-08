/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Component;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentEvtWrapperBuilderBase
{
	protected Class<? extends Component> getCompClass( Component component, Set<Class<? extends Component>> options )
	{
		Class<? extends Component> compClass = component.getClass();
		Class<? extends Component> result = null;
		for( Class<? extends Component> clazz: options )
			result = mostSuitable( compClass, clazz, result );

		return( result );
	}

	protected Class<? extends Component> mostSuitable(Class<?  extends Component> compClass,
														Class<? extends Component> clazz,
														Class<?  extends Component> result )
	{
		List< Class<?> > list = ReflectionFunctions.instance().getListOfClassesAndInterfacesOfClassFromTheMostGenericToTheMostParticular(compClass);

		int resultIndex = list.indexOf(result);
		int clazzIndex = list.indexOf(clazz);

		if( ( clazzIndex > -1 ) && ( clazzIndex < resultIndex ) )
			result = clazz;

		return( result );
	}
}
