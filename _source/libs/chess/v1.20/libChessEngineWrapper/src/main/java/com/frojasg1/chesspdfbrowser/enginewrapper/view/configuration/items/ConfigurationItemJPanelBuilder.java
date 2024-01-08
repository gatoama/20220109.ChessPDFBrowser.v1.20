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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ButtonConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.CheckConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ComboConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.StringConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl.ButtonConfigurationItemJPanel;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl.CheckConfigurationItemJPanel;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl.ComboConfigurationItemJPanel;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl.SpinConfigurationItemJPanel;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl.StringConfigurationItemJPanel;
import com.frojasg1.general.ExecutionFunctions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ConfigurationItemJPanelBuilder
{
	protected Map<Class<? extends ConfigurationItem>, ConfigurationItemJPanelSimpleBuilderData > _map = null;

	public void init()
	{
		_map = createBuilderMap();
	}

	protected Map<Class<? extends ConfigurationItem>, ConfigurationItemJPanelSimpleBuilderData >
		createBuilderMap()
	{
		Map<Class<? extends ConfigurationItem>, ConfigurationItemJPanelSimpleBuilderData > result = new ConcurrentHashMap<>();

		result.put( ButtonConfigurationItem.class, new ConfigurationItemJPanelSimpleBuilderData(
													Object.class,
													ButtonConfigurationItem.class,
													ButtonConfigurationItemJPanel.class,
													(ci, dat) -> build( (ButtonConfigurationItem) ci,
																		( ConfigurationItemJPanelSimpleBuilderData<Object,
																													ButtonConfigurationItem,
																													ButtonConfigurationItemJPanel> ) dat) ) );

		result.put( CheckConfigurationItem.class, new ConfigurationItemJPanelSimpleBuilderData(
													Boolean.class,
													CheckConfigurationItem.class,
													CheckConfigurationItemJPanel.class,
													(ci, dat) -> build( (CheckConfigurationItem) ci,
																		( ConfigurationItemJPanelSimpleBuilderData<Boolean,
																													CheckConfigurationItem,
																													CheckConfigurationItemJPanel> ) dat) ) );

		result.put( ComboConfigurationItem.class, new ConfigurationItemJPanelSimpleBuilderData(
													String.class,
													ComboConfigurationItem.class,
													ComboConfigurationItemJPanel.class,
													(ci, dat) -> build( (ComboConfigurationItem) ci,
																		( ConfigurationItemJPanelSimpleBuilderData<String,
																													ComboConfigurationItem,
																													ComboConfigurationItemJPanel> ) dat) ) );

		result.put( SpinConfigurationItem.class, new ConfigurationItemJPanelSimpleBuilderData(
													Integer.class,
													SpinConfigurationItem.class,
													SpinConfigurationItemJPanel.class,
													(ci, dat) -> build( (SpinConfigurationItem) ci,
																		( ConfigurationItemJPanelSimpleBuilderData<Integer,
																													SpinConfigurationItem,
																													SpinConfigurationItemJPanel> ) dat) ) );

		result.put( StringConfigurationItem.class, new ConfigurationItemJPanelSimpleBuilderData(
													String.class,
													StringConfigurationItem.class,
													StringConfigurationItemJPanel.class,
													(ci, dat) -> build( (StringConfigurationItem) ci,
																		( ConfigurationItemJPanelSimpleBuilderData<String,
																													StringConfigurationItem,
																													StringConfigurationItemJPanel> ) dat) ) );

		return( result );
	}

	public ConfigurationItemToViewPair createConfigurationItemJPanel( ConfigurationItem ci )
	{
		ConfigurationItemToViewPair result = null;

		if( ci != null )
		{
			ConfigurationItemJPanelSimpleBuilderData simpleBuilder = _map.get( ci.getClass() );
			if( simpleBuilder == null )
				throw( new RuntimeException( "Builder for class : " + ci.getClass().getName() + " not found" ) );

			result = build( simpleBuilder.getConfigurationValueClass(),
							simpleBuilder.getConfigurationItemClass(),
							simpleBuilder, ci );
		}

		return( result );
	}

	protected
		<CC, CI extends ConfigurationItem<CC>,
			VV extends ConfigurationItemJPanelBase< CC, CI >>
		ConfigurationItemToViewPair<CC, CI, VV>
		build( Class<CC> valueClass, Class<CI> configurationItemClass,
				ConfigurationItemJPanelSimpleBuilderData<CC, CI, VV> simpleBuilderData,
				CI ci )
	{
		ConfigurationItemToViewPair<CC, CI, VV> result = null;

		ConfigurationItemJPanelBase<CC, CI> view = null;
		if( ( simpleBuilderData != null ) && ( ci != null ) )
			result = simpleBuilderData.getBuilder().apply( ci, simpleBuilderData );

		return( result );
	}

	protected
		<CC, CI extends ConfigurationItem<CC>,
			VV extends ConfigurationItemJPanelBase< CC, CI >>
		ConfigurationItemToViewPair<CC, CI, VV>
		build( CI ci,
				ConfigurationItemJPanelSimpleBuilderData<CC, CI, VV> simpleBuilderData )
	{
		ConfigurationItemToViewPair<CC, CI, VV> result = null;
		VV view = (VV) build( ci, simpleBuilderData.getConfigurationViewClass() );

		result = new ConfigurationItemToViewPair<>( ci, view );

		return( result );
	}

	protected
		<CC, CI extends ConfigurationItem<CC>>
		ConfigurationItemJPanelBase<CC, CI>
		build( CI ci,
				Class<? extends ConfigurationItemJPanelBase<CC, CI> > viewClass )
	{
		ConfigurationItemJPanelBase<CC, CI> result = ExecutionFunctions.instance().safeFunctionExecution( () -> viewClass.getConstructor().newInstance() );
		if( result == null )
			throw( new RuntimeException( "Error when instantiating " + viewClass.getName() + " object with default constructor" ) );
		result.init( ci );

		return( result );
	}

	protected static class ConfigurationItemJPanelSimpleBuilderData< CC, CI extends ConfigurationItem<CC>,
				VV extends ConfigurationItemJPanelBase< CC, CI > >
	{
		protected Class<CI> _configurationItemClass;
		protected Class<CC> _configurationValueClass;
		protected Class<VV> _configurationViewClass;
		protected BiFunction<CI, ConfigurationItemJPanelSimpleBuilderData<CC,CI,VV>,
							ConfigurationItemToViewPair<CC, CI, VV>> _builder;

		public ConfigurationItemJPanelSimpleBuilderData( Class<CC> configurationValueClass,
													Class<CI> configurationItemClass,
													Class<VV> configurationViewClass,
													BiFunction<CI, ConfigurationItemJPanelSimpleBuilderData<CC,CI,VV>,
																ConfigurationItemToViewPair<CC, CI, VV>> builder)
		{
			_configurationItemClass = configurationItemClass;
			_configurationValueClass = configurationValueClass;
			_configurationViewClass = configurationViewClass;
			_builder = builder;
		}

		public Class<CI> getConfigurationItemClass()
		{
			return( _configurationItemClass );
		}

		public Class<CC> getConfigurationValueClass()
		{
			return( _configurationValueClass );
		}

		public Class<VV> getConfigurationViewClass()
		{
			return( _configurationViewClass );
		}

		public BiFunction<CI, ConfigurationItemJPanelSimpleBuilderData<CC,CI,VV>,
							ConfigurationItemToViewPair<CC, CI, VV>> getBuilder()
		{
			return( _builder );
		}
	}
}
