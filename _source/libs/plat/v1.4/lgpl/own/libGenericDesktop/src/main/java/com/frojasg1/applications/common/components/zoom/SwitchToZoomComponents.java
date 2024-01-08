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
package com.frojasg1.applications.common.components.zoom;

import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.buttons.ResizableImage;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJButtonBuilder;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.labels.UrlJLabel;
import com.frojasg1.general.desktop.view.text.CustomizedJPasswordField;
import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.componentcopier.GenericCompCopier;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomMetalComboBoxIcon;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapperBase;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.desktop.view.zoom.ui.ColorInversorMetalToggleButtonUI;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalButtonUI;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import com.frojasg1.general.zoom.ZoomInterface;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SwitchToZoomComponents
{
	protected static final String ROOT_PACKAGE_FOR_COMPONENTS = "com.frojasg1.general.desktop.view.zoom.components";

	protected BaseApplicationConfigurationInterface _appliConf = null;

//	Map<Component, Component> _switchedComponents = new HashMap<>();
	protected ComponentMapperBase _switchedComponentsMapper = null;

	protected MapResizeRelocateComponentItem _mapResizeRelocateItem = null;

	protected ComponentResizingResult _componentResizingResult = null;

	public SwitchToZoomComponents( BaseApplicationConfigurationInterface appliConf,
									MapResizeRelocateComponentItem mapResizeRelocateItem )
	{
		_appliConf = appliConf;
		_mapResizeRelocateItem = mapResizeRelocateItem;
		_switchedComponentsMapper = createComponentMapper();
	}

	protected ComponentMapperBase createComponentMapper()
	{
		return( new ComponentMapperBase() );
	}

	public Component switchToZoomComponents( Component rootComponent )
	{
		return( switchToZoomComponents( rootComponent, 1.0D ) );
	}

	public Component switchToZoomComponents( Component rootComponent, double zoomFactor )
	{
		boolean alwaysCopy = false;
		
		Component result = null;
		if( rootComponent instanceof JFrame )
		{
			JFrame jFrame = (JFrame) rootComponent;

			if( jFrame.getJMenuBar() != null )
			{
				JMenuBar jmb = (JMenuBar) switchToZoomComponents_internal( jFrame.getJMenuBar(),
																			jFrame.getJMenuBar(),
																			alwaysCopy,
																			zoomFactor );
				if( jmb != jFrame.getJMenuBar() )
					jFrame.setJMenuBar( jmb );
			}

			switchToZoomComponents_internal( jFrame.getContentPane().getParent(),
												jFrame.getContentPane().getParent(),
												alwaysCopy,
												zoomFactor );
		}
		else if( rootComponent instanceof JDialog )
		{
			JDialog jDialog = (JDialog) rootComponent;

//			System.out.println( "switchToZoomComponents ( " + rootComponent.getClass().getName() + " ) ---> " +
//								ViewFunctions.instance().traceComponentTree(rootComponent) );

			switchToZoomComponents_internal( jDialog.getContentPane().getParent(),
												jDialog.getContentPane().getParent(),
												alwaysCopy,
												zoomFactor );
		}
		else
		{
			result = switchToZoomComponents_internal(rootComponent, rootComponent,
														alwaysCopy,
														zoomFactor );
		}

//		if( ( rootComponent instanceof JFrame ) ||
//			( rootComponent instanceof JDialog ) )
		{
			try
			{
				switchVariables( rootComponent );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	// it modifies the class name to include the needed features for ResizeRelocateItem
	protected String getNewClassName( String className, ResizeRelocateItem rri )
	{
		String result = className;

		if( rri != null )
		{
			if( rri.isActiveAnyFlagToResizeHorizontallyScrollableComp() )
				result = result + "_CustomHorizSize";

			if( rri.isActiveAnyFlagToResizeVerticallyScrollableComp() )
				result = result + "_CustomVertSize";
		}

		return( result );
	}

	protected String getCompleteClassName( String className )
	{
		return( ROOT_PACKAGE_FOR_COMPONENTS + "." + className );
	}

	protected String getCompleteZoomClassName( String className )
	{
		return( getCompleteClassName( "Zoom" + className ) );
	}

	protected ResizeRelocateItem getResizeRelocateItem( Component oldComp )
	{
		ResizeRelocateItem result = null;
		
		if( _mapResizeRelocateItem != null )
			result = _mapResizeRelocateItem.get( oldComp );

		return( result );
	}

	protected Class getZoomComponentClass( Component comp )
	{
		Class result = null;

		String className = StringFunctions.instance().getSimpleClassName( comp.getClass() );

		ResizeRelocateItem rri = getResizeRelocateItem( comp );
		String newClassName = getNewClassName( className, rri );
		String completeZoomClassName = getCompleteZoomClassName( newClassName );

		// zoom component with modified class name
		result = getClass( completeZoomClassName );

		// modified class name, without zoom
		if( ( result == null ) && !className.equals( newClassName ) )
		{
			String completeClassName = getCompleteClassName( newClassName );
			result = getClass( completeClassName );

			// zoom class name, without being modified
			if( result == null )
			{
				completeZoomClassName = getCompleteZoomClassName( className );
				result = getClass( completeZoomClassName );
			}
		}

		return( result );
	}

	protected Class getClass( String completeClassName )
	{
		Class result = null;

		try
		{
			result = Class.forName( completeClassName );
		}
		catch( Exception ex )
		{
			result = null;
		}

		return( result );
	}

	protected Component createZoomOrCopyComponent( Class classObj )
	{
		Component result = null;

		try
		{
			if( //classObj.getName().equals( "javax.swing.plaf.basic.BasicComboPopup$1" ) ||
				classObj.getName().equals( "javax.swing.JFormattedTextField" ) ||
				classObj.getName().equals( "javax.swing.plaf.basic.BasicComboPopup$1" ) )
			{
				boolean stop = true;
			}
			
			Object resultObj = classObj.getConstructor().newInstance();

			if( resultObj instanceof ZoomComponentInterface )
			{
				ZoomComponentInterface zci = (ZoomComponentInterface) resultObj;
				zci.initBeforeCopyingAttributes();
			}

			if( resultObj instanceof Component )
			{
				result = (Component) resultObj;
			}
		}
		catch( Exception ex )
		{
			if( classObj != null )
				System.out.println( classObj.getName() );

			ex.printStackTrace();
		}

		return( result );
	}

	protected <T extends Component> T createZoomOrCopiedComponentAndCopyIt( Class<?> newClass,
																			T originalComponent,
																			double zoomFactor ) throws IllegalAccessException, InstantiationException
	{
		T result = null;

		if( newClass != null )
			result = (T) createZoomOrCopyComponent( newClass );

		if( result != null )
		{
			copyReflection( originalComponent, result );
				
			if( result instanceof ZoomComponentInterface )
			{
				ZoomComponentInterface zci = (ZoomComponentInterface) result;
				zci.initAfterCopyingAttributes();
				zci.switchToZoomUI();

				((ZoomComponentInterface) result).setZoomFactor(zoomFactor);
			}
		}

		return( result );
	}

	protected <T extends Component> T copy(T originalComponent,
											boolean alwaysCopy,
											double zoomFactor ) throws IllegalAccessException, InstantiationException
	{
		T result = null;
		if( originalComponent.getClass().getName().equals( "javax.swing.JPasswordField" ) )
		{
			result = (T) CustomizedJPasswordField.createCustomizedJPasswordField( (JPasswordField) originalComponent );
			if( result != null )
			{
				boolean log = false;
				copyComponent( originalComponent, result, log );
				( (CustomizedJPasswordField) result ).init();
				( (CustomizedJPasswordField) result ).setBaseApplicationConfiguration(_appliConf);
			}
		}
		else if( originalComponent.getClass().getName().equals( "javax.swing.JButton" ) ||
				originalComponent.getClass().getName().equals( "javax.swing.JToggleButton" ) )
		{
			result = (T) ResizableImageJButtonBuilder.instance().createResizableImageJButton( (AbstractButton) originalComponent );
			if( result != null )
			{
				copyReflection( originalComponent, result );
				( (ResizableImage) result ).resizeImage();
			}
			else if( originalComponent instanceof JToggleButton )
				convertToggleButton( (JToggleButton) originalComponent );
			else if( ( originalComponent instanceof JButton ) )
				convertJButton( (JButton) originalComponent );
		}
		else if( originalComponent.getClass().getName().equals( "javax.swing.JLabel" ) )
		{
			String name = originalComponent.getName();
			if( name != null )
			{
				ComponentNameComponents cnc = new ComponentNameComponents( name );
				if( cnc.getComponent( ComponentNameComponents.URL_COMPONENT ) != null )
				{
					result = (T) new UrlJLabel();
					if( result != null )
					{
						copyReflection( originalComponent, result );
						( (UrlJLabel) result ).init();
						( (UrlJLabel) result ).setUrl( cnc.getComponent( ComponentNameComponents.URL_COMPONENT ) );
					}
				}
			}
		}
		else
		{
			Class<?> newClass = getZoomComponentClass( originalComponent );

			// if there is not a Zoomeable component and we have to copy then copy it exactly
			if( ( newClass == null ) && alwaysCopy )
				newClass = originalComponent.getClass();

			if( newClass != null )
				result = createZoomOrCopiedComponentAndCopyIt( newClass, originalComponent, zoomFactor );

			if( ( result == null ) &&
				( newClass != null ) && ( newClass.getName().indexOf( "$" ) > -1 ) )
			{
				result = originalComponent;
			}
		}

		return( result );
	}

	protected void convertToggleButton( JToggleButton comp )
	{
		comp.setUI( new ColorInversorMetalToggleButtonUI() );
	}

	protected void convertJButton( JButton comp )
	{
		comp.setUI( new ZoomMetalButtonUI() );
	}

	protected <T extends Component> void copyReflection(T originalComponent, T destinationComponent)
		throws IllegalAccessException, InstantiationException
	{
//		boolean log = ( ( originalComponent.getName() != null ) &&
//						( originalComponent.getName().equals( "prueba1" ) ) );
//		boolean log = ( originalComponent.getClass() == MetalScrollButton.class );
//		boolean log = ( originalComponent.getClass() == JFormattedTextField.class );
		boolean log = ( originalComponent.getClass() == JTable.class );
//		boolean log = false;

		copyComponent( originalComponent, destinationComponent, log );
	}

	protected <T extends Component> void copyComponent(T originalComponent,
														T destinationComponent,
														boolean log )
	{
		copyComponentWithCopier( originalComponent, destinationComponent );

//		copyReflection( originalComponent, destinationComponent, log );
	}

	protected <T extends Component> void copyComponentWithCopier(T originalComponent,
																T destinationComponent )
	{
		GenericCompCopier.instance().copy(originalComponent, destinationComponent);
	}
/*
	protected <T extends Component> void copyReflection(T originalComponent,
														T destinationComponent,
														boolean log )
		throws IllegalAccessException, InstantiationException
	{
		Class<?> mostSpecializedClass = originalComponent.getClass();
		Class<?> clazz = mostSpecializedClass;
		if( log )
			System.out.println( StringFunctions.RETURN +
								StringFunctions.RETURN +
								"Copiando " + clazz.getName() + StringFunctions.RETURN + 
								"============================================" + StringFunctions.RETURN);

		while (clazz != null)
		{
			if( log )
				System.out.println(  StringFunctions.RETURN + "Clase: " + clazz.getName() + StringFunctions.RETURN +
									"=======");
			copyFields(originalComponent, destinationComponent, clazz, mostSpecializedClass, log);
			clazz = clazz.getSuperclass();
		}

		if( originalComponent instanceof AbstractButton )
		{
			( (AbstractButton ) destinationComponent ).setSelected( ( (AbstractButton ) originalComponent ).isSelected( ) );
		}
	}
*/
	protected Collection<String> getListenerTypesList( String toStringEventListenerList )
	{
		ArrayList<String> result = new ArrayList<>();
		
		String[] split = toStringEventListenerList.split( "\\s" );
		
		for( int ii=0; ii<split.length; ii++ )
		{
			if( split[ii].equals( "type" ) )
			{
				ii++;
				result.add( split[ii] );
			}
		}

		return( result );
	}

	protected <T extends EventListener> void copyListeners( Component entity, Component newEntity,
									Class<T> clazz,
									EventListenerList originalEventList,
									EventListenerList newEventList )
	{
		T newEntityListener = null;
		T entityListener = null;
		if( clazz.isInstance( entity ) && clazz.isInstance( newEntity ) )
		{
			newEntityListener = (T) newEntity;
			entityListener = (T) entity;
		}

		T[] array = originalEventList.getListeners(clazz);

		for( int ii=0; ii<array.length; ii++ )
		{
			String listenerClassName = array[ii].getClass().getName();
			
			if( !StringFunctions.instance().stringStartsWith(listenerClassName, "javax.swing.plaf" ) )
			{
				T listener = array[ii];
				if( listener == entityListener )
					listener = newEntityListener;

				newEventList.add( clazz, array[ii] );
			}
		}
	}

	protected void copyListeners( Component entity, Component newEntity,
									EventListenerList originalEventList,
									EventListenerList newEventList )
	{
		Collection<String> colOfListenerTypes = getListenerTypesList( originalEventList.toString() );

		Iterator<String> it = colOfListenerTypes.iterator();
		while( it.hasNext() )
		{
			try
			{
				String className = it.next();
				Class<? extends EventListener> clazz = (Class<? extends EventListener>) Class.forName(className);

				copyListeners( entity, newEntity, clazz, originalEventList, newEventList );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

/*
	protected <T extends Component> T copyFields(T entity, T newEntity,
													Class<?> clazz,
													Class<?> mostSpecializedClass,
													boolean log ) throws IllegalAccessException {
		List<Field> fields = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			fields.add(field);
		}
		Container cont;
		
		for (Field field : fields) {
			if( !Modifier.isFinal(field.getModifiers()) )
			{
				try
				{
					field.setAccessible(true);

					Object oldValue = field.get( entity );

					if( ( entity instanceof JComboBox ) &&
						( field.getName().equals( "isEditable" ) ) )
					{
						int kk=0;
					}

					if( //ReflectionFunctions.instance().isSuperClass( field.getType(), Component.class ) ||
						//( oldValue != null ) && oldValue.getClass().getName().contains( "$" ) ||
						( entity instanceof JComboBox ) && field.getName().equals( "isEditable" ) ||
						StringFunctions.instance().stringEndsWith( field.getName(), "Model" ) ||
						field.getName().equals( "parent" ) ||
						field.getName().equals( "changeSupport" ) ||
						field.getName().equals( "tempRectangles" ) ||
						field.getName().equals( "objectLock" ) ||
						field.getName().equals( "appContext" ) ||
						field.getName().equals( "peer" ) ||
						field.getName().equals( "componentObtainingGraphicsFrom" ) ||
						field.getName().equals( "componentObtainingGraphicsFromLock" ) ||
						field.getName().equals( "actionMap" ) ||
						field.getName().equals( "acc" ) ||
						field.getName().equals( "graphicsConfig" ) ||
						field.getName().equals( "model" ) ||
						field.getName().equals( "METHOD_OVERRIDDEN" ) ||
						field.getName().equals( "layoutMgr" ) ||
						field.getName().equals( "aaTextInfo" ) ||
						field.getName().equals( "ui" ) ||
						field.getName().equals( "keymap" ) ||
						field.getName().equals( "highlighter" ) ||
						field.getName().equals( "caret" ) ||
						(
							( StringFunctions.instance().stringEndsWith( field.getName(), "Listener" ) ||
								field.getName().equals( "handler" ) ||
								field.getName().equals( "cursor" ) ||
								field.getName().equals( "dropTarget" ) ||
								field.getName().equals( "border" )  ||
								field.getName().equals( "clientProperties" ) ||
//								field.getName().equals( "visibility" ) ||
//								field.getName().equals( "peerFont" ) ||
//								field.getName().equals( "font" ) ||
//								field.getName().equals( "background" ) ||
//								field.getName().equals( "foreground" ) ||
//								field.getName().equals( "margin" ) ||
//								field.getName().equals( "disabledTextColor" ) ||
//								field.getName().equals( "selectedTextColor" ) ||
//								field.getName().equals( "selectionColor" ) ||
//								field.getName().equals( "caretColor" ) ||
								field.getName().equals( "componentOrientation" )
							) &&
							( field.get(entity) != null ) &&
							( 
								StringFunctions.instance().stringStartsWith( field.get(entity).getClass().getName(),
																			"javax.swing" )
								||
								StringFunctions.instance().stringStartsWith( field.get(entity).getClass().getName(),
																			"java.awt" )
								||
								StringFunctions.instance().stringStartsWith( field.get(entity).getClass().getName(),
																			"sun.swing" )
							)
						)
						||
						( clazz == Container.class ) && field.getName().equals( "component" )
						||
						( clazz == JTabbedPane.class ) && field.getName().equals( "pages" )
						||
						( clazz == JScrollPane.class ) && field.getName().equals( "viewport" )
					  )
					{
						continue;
					}
					else if( StringFunctions.instance().stringEndsWith( field.getName(), "Listener" ) )
					{
						Object listener = field.get(entity);
						if( listener == entity )
							listener = newEntity;

						field.set( newEntity, listener );
					}
					else if( field.getName().equals( "listenerList" ) )
					{
						copyListeners( entity, newEntity,
										(EventListenerList)field.get(entity),
										(EventListenerList)field.get(newEntity) );
					}
					else if( field.getName().toLowerCase().indexOf( "focus" ) == -1 )
					{
						if( log )
							System.out.println( "ComponenteValido: " +
												( newEntity.isValid() ? "verdadero" : "falso" ) +
												"\tCampo copiado: " + field.getName() +
												",\tValor: " + field.get(entity) + StringFunctions.RETURN );

						field.set(newEntity, field.get(entity));
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}
		return newEntity;
	}
*/
	protected MetalComboBoxButton switchToZoomComponentsMetalComboBoxButton_internal( MetalComboBoxButton origBtn,
																						MetalComboBoxButton newBtn,
																						boolean alwaysCopy,
																						double zoomFactor )
	{
		MetalComboBoxButton result = null;

		if( origBtn != null )
		{
			if( newBtn == null )
				newBtn = origBtn;

			if( newBtn != null )
			{
				ZoomMetalComboBoxIcon icon = new ZoomMetalComboBoxIcon();
				icon.setParentButton( newBtn );
				icon.setZoomFactor(zoomFactor);

				newBtn.setComboIcon( icon );
			}

			result = newBtn;
		}

		JScrollBar sb;
		
		return( result );
	}

	protected JComboBox switchToZoomComponentsJComboBox_internal( JComboBox originalCb,
															JComboBox newCb,
															boolean alwaysCopy,
															double zoomFactor )
	{
		JComboBox result = null;

		if( originalCb != null )
		{
			if( newCb == null )
				newCb = originalCb;

//			BasicComboPopup oldPopup = (BasicComboPopup) originalCb.getUI().getAccessibleChild(originalCb, 0);
//			BasicComboPopup newPopup = (BasicComboPopup) newCb.getUI().getAccessibleChild(newCb, 0);
			JPopupMenu oldPopup = (JPopupMenu) originalCb.getUI().getAccessibleChild(originalCb, 0);
			JPopupMenu newPopup = (JPopupMenu) newCb.getUI().getAccessibleChild(newCb, 0);

			Component newComp = switchToZoomComponents_internal( oldPopup, newPopup, alwaysCopy, zoomFactor );

			if( newComp != newPopup ) {
//				newCb.getUI().set
			}

			result = newCb;
			
			if( result != originalCb )
				result.setEditable( originalCb.isEditable() );
		}

		return( result );
	}

	protected void switchToZoomComponentsTabbedPane_internal( JTabbedPane originalTabbedPane,
																JTabbedPane newTabbedPane,
																boolean alwaysCopy,
																double zoomFactor )
	{
		List<Component> listOfOldComponents = new ArrayList<>();
		List<Component> listOfNewComponents = new ArrayList<>();
		List<String> titles = new ArrayList<>();
		boolean hasToReAdd = false;

		Container modelCont = null;

		Component oldComp;
		Component newComp;
		Component tmpOld;
		Component tmpNew;

		JTabbedPane modelTabbedPane = null;

		if( alwaysCopy &&
			( newTabbedPane != null ) &&
			( newTabbedPane.getTabCount() == 0 ) &&
			( originalTabbedPane != null ) &&
			( originalTabbedPane.getTabCount() > 0 ) )
		{
			modelTabbedPane = originalTabbedPane;
		}
		else if( !alwaysCopy )	// if !alwaysCopy then originalTabbedPane is equal to newTabbedPane
		{
			modelTabbedPane = originalTabbedPane;
		}
		else if( alwaysCopy )	// if alwaysCopy then we have to browse all components
		{
			modelTabbedPane = originalTabbedPane;
		}

		if( modelTabbedPane != null )
		{
			for( int ii=0; ii<modelTabbedPane.getTabCount(); ii++ )
			{
				oldComp = originalTabbedPane.getComponentAt(ii);
				newComp = switchToZoomComponents_internal( oldComp,	null, alwaysCopy, zoomFactor );
				
				listOfOldComponents.add( oldComp );
				listOfNewComponents.add( newComp );
				titles.add( modelTabbedPane.getTitleAt( ii ) );

				hasToReAdd = hasToReAdd || ( oldComp != newComp );
			}
		}

		if( hasToReAdd )
		{
			while( newTabbedPane.getTabCount() > 0 )
				newTabbedPane.removeTabAt( 0 );

			Iterator<Component> oldIt = listOfOldComponents.iterator();
			Iterator<Component> newIt = listOfNewComponents.iterator();
			Iterator<String> titleIt = titles.iterator();
			while( oldIt.hasNext() && newIt.hasNext() &&
					titleIt.hasNext() )
			{
				newComp = newIt.next();
				oldComp = oldIt.next();
				String title = titleIt.next();

				newTabbedPane.addTab( title, newComp );
			}
		}
	}

	protected void switchToZoomComponentsContainer_internal( Container originalComp,
																Container newContainer,
																boolean alwaysCopy,
																double zoomFactor )
	{
		if( newContainer instanceof JMenu )
		{
			JMenu jMenu = (JMenu) newContainer;

			switchJMenuElements( jMenu, alwaysCopy, zoomFactor );
/*
					JPopupMenu newJPopupMenu = (JPopupMenu) switchToZoomComponents_internal(jMenu.getPopupMenu(),
																							jMenu.getPopupMenu(),
																							alwaysCopy,
																							zoomFactor );
					if( newJPopupMenu != jMenu.getPopupMenu() )
					{
						jMenu.remove( jMenu.getPopupMenu() );
						jMenu.add( newJPopupMenu );
					}
*/
		}
		else
		{
			final int COPY_ALL = 0;
			final int BROWSE_COMPONENTS = 1;

			List<Component> listOfOldComponents = new ArrayList<>();
			List<Component> listOfNewComponents = new ArrayList<>();
			boolean hasToReAdd = false;

			Container modelCont = null;

			Component oldComp = null;
			Component newComp = null;
			Component tmpOld = null;
			Component tmpNew = null;

			int type = -1;

			if( alwaysCopy &&
				( newContainer != null ) &&
				( newContainer.getComponentCount() == 0 ) &&	// if the new Container does not have components, then we copy them from the original one.
				( originalComp != null ) &&
				( originalComp.getComponentCount() > 0 ) )
			{
				modelCont = originalComp;
				type = COPY_ALL;
			}
			else if( !alwaysCopy )	// if !alwaysCopy then originalComp is equal to newContainer
			{
				modelCont = newContainer;
				type = BROWSE_COMPONENTS;
			}
			else if( alwaysCopy )	// if alwaysCopy then we have to browse all components
			{
				modelCont = null;

				tmpOld = null;
				tmpNew = null;
				int originalContainerIndex = 0;
				for( int ii=0; (ii<newContainer.getComponentCount()) &&
								( originalContainerIndex < originalComp.getComponentCount() ); ii++ )
				{
					tmpNew = newContainer.getComponent(ii);
					oldComp = null;

					for( ;
						(originalContainerIndex<originalComp.getComponentCount() ) && ( oldComp == null );
						originalContainerIndex++ )
					{
						tmpOld = originalComp.getComponent(originalContainerIndex);

						if( tmpOld.getClass().isInstance( tmpNew ) )
							oldComp = tmpOld;
					}

					if( oldComp != null )
					{
						newComp = switchToZoomComponents_internal( oldComp, tmpNew,
																	alwaysCopy, zoomFactor );

						if( newComp != null )
						{
							listOfOldComponents.add( oldComp );
							listOfNewComponents.add( newComp );

							hasToReAdd = hasToReAdd || ( oldComp != newComp );
						}
					}
				}
			}

			if( modelCont != null )
			{
				for( int ii=0; ii<modelCont.getComponentCount(); ii++ )
				{
					oldComp = modelCont.getComponent(ii);

					newComp = null;
					if( type == COPY_ALL )
						newComp = switchToZoomComponents_internal( oldComp, null,
																	alwaysCopy,
																	zoomFactor );
					else if( type == BROWSE_COMPONENTS )
						newComp = switchToZoomComponents_internal( oldComp, oldComp,
																	alwaysCopy,
																	zoomFactor );
					if( newComp != null )
					{
						listOfOldComponents.add( oldComp );
						listOfNewComponents.add( newComp );

						hasToReAdd = hasToReAdd || ( oldComp != newComp );
					}
				}
			}

			if( hasToReAdd )
			{
				// before removingAll, we copy the children
	//			listOfNewComponents = replaceEqualObjects( listOfOldComponents,
	//											listOfNewComponents, zoomFactor );

				clearContainer( newContainer );

				Iterator<Component> oldIt = listOfOldComponents.iterator();
				Iterator<Component> newIt = listOfNewComponents.iterator();
				while( oldIt.hasNext() && newIt.hasNext() )
				{
					newComp = newIt.next();
					oldComp = oldIt.next();

					if( !( newComp instanceof BasicSplitPaneDivider ) )
					{
						newContainer.add( newComp );
						newComp.setBounds( oldComp.getBounds() );
					}
				}
			}
		}
	}

	protected void clearContainer( Container cont )
	{
		if( cont instanceof JSplitPane )
		{
			JSplitPane sp = (JSplitPane) cont;
			if( sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
			{
				sp.setLeftComponent(null);
				sp.setRightComponent(null);
			}
			else
			{
				sp.setTopComponent(null);
				sp.setBottomComponent(null);
			}
		}
		else
			cont.removeAll();
	}

	protected List<Component> replaceEqualObjects( Collection<Component> listOfOldComponents,
													Collection<Component> listOfNewComponents,
													double zoomFactor )
	{
		List<Component> result = new ArrayList<>();

		Component newComp = null;
		Component oldComp = null;
		Iterator<Component> oldIt = listOfOldComponents.iterator();
		Iterator<Component> newIt = listOfNewComponents.iterator();
		while( oldIt.hasNext() && newIt.hasNext() )
		{
			newComp = newIt.next();
			oldComp = oldIt.next();
				
			if( newComp == oldComp )
			{
				boolean alwaysCopy = true;
				newComp = switchToZoomComponents_internal( oldComp, null,
															alwaysCopy,
															zoomFactor );
			}

			result.add( newComp );
		}

		return( result );
	}


	// Object fp is of class: com.frojasg1.sun.swing.FilePane
	protected JTextField getJTextFieldCellRenderer( Object fp )
	{
		JTextField result = null;
/*
		try
		{
			Object obj = ReflectionFunctions.instance().getAttribute( "tableCellEditor", Object.class, fp );

			if( obj == null )
			{
				obj = ReflectionFunctions.instance().invokeMethod( "getDetailsTableCellEditor", fp.getClass(), fp );

//				ReflectionFunctions.instance().setAttribute( "tableCellEditor", fp, FilePane.class, obj );
			}

			if( obj != null )
			{
				result = (JTextField) ReflectionFunctions.instance().getAttribute( "tf", JTextField.class, obj );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
*/
		try
		{
			result = ReflectionFunctions.instance().getAttribute( "editCell", JTextField.class, fp );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected Component switchToZoomComponentsSplitPane( JSplitPane originalSp,
															JSplitPane newSp,
															boolean alwaysCopy,
															double zoomFactor )
	{
		alwaysCopy = false;
		if( originalSp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
		{
			Component newLeft = switchToZoomComponents_internal( originalSp.getLeftComponent(), null, alwaysCopy, zoomFactor );
			newSp.setLeftComponent(newLeft);
			Component newRight = switchToZoomComponents_internal( originalSp.getRightComponent(), null, alwaysCopy, zoomFactor );
			newSp.setRightComponent(newRight);
		}
		else
		{
			Component newTop = switchToZoomComponents_internal( originalSp.getTopComponent(), null, alwaysCopy, zoomFactor );
			newSp.setTopComponent(newTop);
			Component newBottom = switchToZoomComponents_internal( originalSp.getBottomComponent(), null, alwaysCopy, zoomFactor );
			newSp.setBottomComponent(newBottom);
		}

		return( newSp );
	}

	protected Component switchToZoomComponentsScrollPane( JScrollPane originalSp,
															JScrollPane newSp,
															boolean alwaysCopy,
															double zoomFactor )
	{
		JViewport oldVp = originalSp.getViewport();
		JViewport newVp = newSp.getViewport();

		alwaysCopy = false;
//		JViewport newVp2 = (JViewport) switchToZoomComponents_internal( oldVp, newVp, alwaysCopy, zoomFactor );
		Component newViewportView = switchToZoomComponents_internal( oldVp.getView(), null, alwaysCopy, zoomFactor );

//		if( ( newVp != newVp2 ) || ( originalSp != newSp ) )
//			newSp.setViewport(newVp2);
		if( ( oldVp.getView() != newViewportView ) || ( originalSp != newSp ) )
			newSp.setViewportView(newViewportView);

		return( newVp );
	}

	protected Component switchToZoomComponentsViewport( JViewport originalVp,
															JViewport newVp,
															boolean alwaysCopy,
															double zoomFactor )
	{
		Component oldComp = originalVp.getView();
		Component newComp = newVp.getView();

		alwaysCopy = true;
		Component newComp2 = switchToZoomComponents_internal( oldComp, newComp, alwaysCopy, zoomFactor );

		if( newComp != newComp2 )
			newVp.setView(newComp2);

		return( newVp );
	}

	protected Component switchToZoomComponents_internal( Component originalComp,
														Component newComponent,
														boolean alwaysCopy,
														double zoomFactor )
	{
		Component result = getSwitchedComponent(originalComp);
		
		if( result != null )
			return( result );

		if( ( originalComp instanceof InternallyMappedComponent ) &&
			( (InternallyMappedComponent) originalComp).hasBeenAlreadyMapped() )
		{
			return( originalComp );
		}

		if( ( originalComp != null ) &&
			( originalComp.getClass().getName().equals( "javax.swing.plaf.metal.MetalComboBoxButton" ) ) )
		{
			int kk=1;
		}

		if( newComponent == null )
			newComponent = originalComp;	// in case this child component did not exist in the new Container (with alwaysCopy to true)

		result = newComponent;
		Component copy = null;

		if( ( originalComp instanceof JFormattedTextField ) ||
			( newComponent instanceof JFormattedTextField )
			)
		{
			boolean stop = true;
		}

		switchPopupMenu( originalComp, zoomFactor );

		try
		{
			if( //!( newComponent instanceof JScrollPane ) &&
				!( newComponent instanceof JViewport ) ) //&&
//				!( newComponent.getClass().getName().contains( "$" ) ) )
				copy = copy(newComponent, alwaysCopy, zoomFactor );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( copy != null )
		{
			result = copy;
//			alwaysCopy = true;	// if a component is modified, when browsing all its child components, all of them will be copied.
		}

//		if( !( result instanceof JScrollPane ) )
		{
			Class<?> filePaneClass = Classes.getFilePaneClass();	// JRE 7, 8, ...

			if( result instanceof JTabbedPane )
				switchToZoomComponentsTabbedPane_internal( (JTabbedPane) originalComp,
															(JTabbedPane) result,
															alwaysCopy,
															zoomFactor );
			else if( result instanceof JSplitPane )
			{
				switchToZoomComponentsSplitPane( (JSplitPane) originalComp,
													( JSplitPane ) result,
													alwaysCopy,
													zoomFactor);
			}
			else if( result instanceof JScrollPane )
			{
				switchToZoomComponentsScrollPane( (JScrollPane) originalComp,
													( JScrollPane ) result,
													alwaysCopy,
													zoomFactor);
			}
/*
			else if( result instanceof JScrollPane )
			{
				switchToZoomComponentsScrollPane( (JScrollPane) originalComp,
													( JScrollPane ) result,
													alwaysCopy,
													zoomFactor);
			}
*/
			else if( result instanceof JViewport )
			{
				switchToZoomComponentsViewport( (JViewport) originalComp,
													( JViewport ) result,
													alwaysCopy,
													zoomFactor);
			}

//			else if( result instanceof FilePane )
			else if( ( filePaneClass != null ) && filePaneClass.isInstance( result ) )	// JRE 7, 8, ...
			{
//				FilePane fp = (FilePane) result;

//				JPopupMenu popupMenu = fp.getComponentPopupMenu();
				JPopupMenu popupMenu = (JPopupMenu) ReflectionFunctions.instance().invokeMethod( "getComponentPopupMenu",
																								result.getClass(), result );

				JPopupMenu newPopupMenu = (JPopupMenu ) switchToZoomComponents_internal( popupMenu,
																							popupMenu,
																							alwaysCopy,
																							zoomFactor );

				if( newPopupMenu != popupMenu )
					ReflectionFunctions.instance().invokeMethod( "setComponentPopupMenu", result.getClass(),
																	result, newPopupMenu );
//					fp.setComponentPopupMenu( newPopupMenu );

//				JTextField tf = getJTextFieldCellRenderer( fp );
				JTextField tf = getJTextFieldCellRenderer( result );
				if( tf != null )
				{
/*
					tf.addFocusListener( new FocusListener(){
						@Override
						public void focusGained(FocusEvent e)
						{
							int aa=1;
						}

						@Override
						public void focusLost(FocusEvent e)
						{
							int aa=1;
						}
						
					});
*/
					Font font = tf.getFont();
					if( font != null )
						tf.setFont( FontFunctions.instance().getResizedFont( font, (float) ( zoomFactor * font.getSize() )));
				}
			}
			else if( result instanceof JComboBox )
			{
//				switchToZoomComponentsJComboBox_internal( (JComboBox) originalComp,
//															(JComboBox) result,
//															alwaysCopy,
//															zoomFactor );
//				switchToZoomComponentsContainer_internal((Container) originalComp, (Container) result, alwaysCopy, zoomFactor );
			}
			else if( result instanceof MetalComboBoxButton )
			{
				switchToZoomComponentsMetalComboBoxButton_internal( (MetalComboBoxButton) originalComp,
																	(MetalComboBoxButton) result,
																	alwaysCopy,
																	zoomFactor );
			}
			else if( result instanceof Container )
			{
				switchToZoomComponentsContainer_internal((Container) originalComp, (Container) result, alwaysCopy, zoomFactor );
			}

			if( hasToSwitchVariables( originalComp, result ) )
			{
				ComponentFunctions.instance().browseComponentHierarchy(result,
					(comp) -> { ExecutionFunctions.instance().safeMethodExecution( () -> switchVariables(comp) ); return( null ); }
																		);
/*				try
				{
					switchVariables( result );
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
*/
			}
		}

		if( result == null )
			result = originalComp;

		if( originalComp != result )
			putSwitchedComponent(originalComp, result );

		return( result );
	}

	protected void putSwitchedComponent( Component originalComp, Component newComp )
	{
		_switchedComponentsMapper.put(originalComp, newComp );
		if( originalComp instanceof JComboBox )
			_switchedComponentsMapper.put( getComboPopup( (JComboBox) originalComp),
											getComboPopup( (JComboBox) newComp ) );
	}

	protected Component getSwitchedComponent( Component originalComp )
	{
		return( _switchedComponentsMapper.getMap().get(originalComp) );
	}

	protected void switchJMenuElements( JMenu jMenu, boolean alwaysCopy, double zoomFactor )
	{
		Component[] menuElems = jMenu.getMenuComponents();
		Component[] resultElems = new Component[menuElems.length];
		int ii=0;
		for( Component comp: menuElems )
		{
			resultElems[ii] = switchToZoomComponents_internal(comp,	comp, alwaysCopy, zoomFactor );
			ii++;
		}

		for( Component comp: menuElems )
		{
			if( comp instanceof JMenuItem )
			{
				JMenuItem jmi = (JMenuItem) comp;
				jMenu.remove(jmi);
			}
			else
			{
				jMenu.remove(comp);
			}
		}

		for( Component comp: resultElems )
		{
			if( comp instanceof JMenuItem )
			{
				JMenuItem jmi = (JMenuItem) comp;
				jMenu.add(jmi);
			}
			else
			{
				jMenu.add(comp);
			}
		}
	}

	protected void zoomAlignedLabel( Component comp )
	{
		if( comp.getClass().getName().equals( "javax.swing.plaf.metal.MetalFileChooserUI$AlignedLabel" ) )
		{
			try
			{
				Dimension pref = comp.getPreferredSize();
				ReflectionFunctions.instance().setAttribute( "maxWidth", comp,
					Class.forName( "javax.swing.plaf.metal.MetalFileChooserUI$AlignedLabel" ), 0 );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected void zoomPreferredSize( Component comp, double zoomFactor )
	{
		if( comp instanceof JComboBox )
		{
			int kk=0;
		}

		if( ( comp != null ) && ( comp.getParent() != null ) &&
			( comp.getParent().getLayout() != null ) )
		{
			if( comp.getClass().getName().equals( "javax.swing.plaf.metal.MetalFileChooserUI$AlignedLabel" ) )
			{
				zoomAlignedLabel( comp );
			}
			else if( ( comp instanceof JLabel ) ||
				( comp instanceof JButton ) &&
				( ! StringFunctions.instance().isEmpty( ViewFunctions.instance().getText(comp) ) )
			  )
			{
/*
				Dimension newSize = ViewFunctions.instance().getSizeOfComponentWithSizeBasedOnText(comp);
				if( newSize != null )
					comp.setPreferredSize( newSize );
*/
			}
			else if( comp instanceof JComboBox )
			{
/*
				Dimension dimen = comp.getPreferredSize();
				Dimension size = comp.getSize();
				Dimension maximumSize = comp.getMaximumSize();

//				comp.setPreferredSize(
//					ViewFunctions.instance().getNewDimension(dimen,
//															( (JComponent) comp ).getInsets(),
//															zoomFactor)
//									);

				dimen.height = size.height;
				maximumSize.height = size.height;
				Dimension newDimen = ViewFunctions.instance().getNewDimension(dimen,
															( (JComponent) comp ).getInsets(),
															zoomFactor);
				compSetPreferredSize( comp, newDimen );

				Dimension newMaxSize = ViewFunctions.instance().getNewDimension(maximumSize,
															( (JComponent) comp ).getInsets(),
															zoomFactor);
				compSetMaximumSize( comp, newMaxSize );
*/
			}
			else if( comp instanceof JPanel )
			{
				int kk=1;
			}
			else if( comp instanceof JComponent )
			{
				compSetPreferredSize( comp,
					ViewFunctions.instance().getNewDimension(comp.getPreferredSize(),
															( (JComponent) comp ).getInsets(),
															zoomFactor)
									);
			}
			else
			{
				compSetPreferredSize( comp,
					ViewFunctions.instance().getNewDimension(comp.getPreferredSize(), zoomFactor)
									);
			}
		}
	}
	
	protected boolean hasToSwitchVariables( Component originalComp, Component comp )
	{
/*		boolean result = ( comp instanceof JInternalFrame ) ||
						( comp instanceof JPanel ) &&
						( ! comp.getClass().getName().equals( JPanel.class.getName() ) ) ||
						( comp instanceof JFrame ) ||
						( comp instanceof JDialog );
*/
		boolean result = true;
//		boolean result = ( originalComp != null ) && ( originalComp.getParent() == null );

		return( result );
	}

	protected void switchVariables(Object object) throws IllegalAccessException, InstantiationException {

//		if( ( object instanceof InternallyMappedComponent ) &&
//			!( (InternallyMappedComponent) object).hasBeenAlreadyMapped() )
		if( object instanceof InternallyMappedComponent )
		{
			InternallyMappedComponent imc = (InternallyMappedComponent) object;
			imc.setComponentMapper( _switchedComponentsMapper );
		}

		if( object instanceof JComboBox )
		{
			switchVariables( getComboPopup( (JComboBox) object ) );
		}

//		switchVariablesReflection( object );
	}
/*
	protected void switchVariablesReflection(Object object) throws IllegalAccessException, InstantiationException {
		Class<?> clazz = object.getClass();

		while (clazz != null)
		{
			switchFields(object, clazz);
			clazz = clazz.getSuperclass();
		}
	}
*/
	protected void switchFieldsButtonGroup( ButtonGroup bg )
	{
		ArrayList< AbstractButton > changedAbstractButtonList = new ArrayList<>();
		
		Enumeration< AbstractButton > col = bg.getElements();
		while( col.hasMoreElements() )
		{
			AbstractButton ab = col.nextElement();

			Component newComp = _switchedComponentsMapper.mapComponent( ab );
			if( ( newComp != null ) && ( newComp instanceof AbstractButton ) )
			{
				changedAbstractButtonList.add( ab );
			}
		}
		
		if( changedAbstractButtonList.size() > 0 )
		{
			Iterator< AbstractButton > it = changedAbstractButtonList.iterator();
			while( it.hasNext() )
			{
				AbstractButton old = it.next();
				bg.remove(old);
				bg.add( (AbstractButton) _switchedComponentsMapper.mapComponent( old ) );
			}
		}
	}
/*
	protected void switchFields(Object object, Class<?> clazz) throws IllegalAccessException {
		List<Field> fields = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			fields.add(field);
		}
		for (Field field : fields) {
			if( !Modifier.isFinal(field.getModifiers()) )
			{
				field.setAccessible(true);

				Object fieldObj = field.get(object);
				if( fieldObj instanceof Component )
				{
					Component comp = (Component) fieldObj;
					Component newComp = _switchedComponentsMapper.mapComponent( comp );
					if( newComp != null )
						field.set(object, newComp);
				}
				else if( fieldObj instanceof ButtonGroup )
				{
					switchFieldsButtonGroup( (ButtonGroup) fieldObj );
				}
			}
		}
	}
*/
	public ComponentMapperBase getSwitchedComponents()
	{
		return( _switchedComponentsMapper );
	}
/*
	public Map<Component, Component> getSwitchedComponents()
	{
		return( _switchedComponents );
	}
*/

	public Component setZoomFactor( Component rootComponent, double zoomFactor )
	{
		return( setZoomFactor( rootComponent, zoomFactor, comp -> true ) );
	}

	// function to be called from FileChooser or system dialogs.
	public Component setZoomFactor( Component rootComponent, double zoomFactor, SwitchToZoomComponents.ComponentFilter compFilter )
	{
//		Component result = switchToZoomComponents( rootComponent, zoomFactor );
		Component result = switchToZoomComponents( rootComponent, 1.0D );
//		Component result = rootComponent;

		_componentResizingResult = new ComponentResizingResult();
		setZoomFactor_internal( result, zoomFactor, compFilter );

		return( result );
	}

	protected void addComponentResizingResult( Component comp, String addResizing )
	{
		if( _componentResizingResult != null )
		{
			String result = _componentResizingResult.get( comp );
			if( result != null )
				result = result + " | " + addResizing;
			else
				result = addResizing;
			_componentResizingResult.put(comp, result);
		}
	}

	protected Rectangle getBoundsOfComponentSizeBasedOnText( JComponent jcomp, double zoomFactor )
	{
		Rectangle result = null;

		if( jcomp != null )
		{
			Insets insets = jcomp.getInsets();

			Rectangle origBounds = jcomp.getBounds();

			Dimension newSize = ViewFunctions.instance().getSizeOfComponentWithSizeBasedOnText( jcomp );

			int top=0, bottom=0, left=0, right=0;

			if( insets != null )
			{
				top=insets.top;
				bottom = insets.bottom;
				left = insets.left;
				right = insets.right;
			}
			
			if( newSize != null )
			{
				result = new Rectangle( IntegerFunctions.zoomValueCeil( origBounds.x, zoomFactor),
										IntegerFunctions.zoomValueCeil( origBounds.y, zoomFactor),
										newSize.width + left + right + 2,
										newSize.width + top + bottom + 2 );
			}
		}

		return( result );
	}

	protected void compSetBounds( Component comp, Rectangle value )
	{
		comp.setBounds(value);

		addComponentResizingResult( comp, String.format( "setBounds( %s )", value.toString() ) );
	}

	protected void compSetPreferredSize( Component comp, Dimension value )
	{
		comp.setPreferredSize(value);

		addComponentResizingResult( comp, String.format( "setPreferredSize( %s )", value.toString() ) );
	}

	protected void compSetMaximumSize( Component comp, Dimension value )
	{
		comp.setMaximumSize(value);

		addComponentResizingResult( comp, String.format( "setMaximumSize( %s )", value.toString() ) );
	}

	protected void setZoomFactor_layoutNull( Component comp, double zoomFactor )
	{
		if( comp instanceof JComboBox )
		{
			int kk=0;
		}

		if( //( comp instanceof JButton ) ||
			( comp instanceof JLabel ) )
		{
			Rectangle newBounds = getBoundsOfComponentSizeBasedOnText( (JComponent) comp, zoomFactor );
			if( newBounds != null )
				compSetBounds( comp, newBounds );
		}
		else if( comp instanceof JPanel )
		{
			JPanel panel = (JPanel) comp;

			Dimension newDimen = null;
			Rectangle newBounds = null;
			if( panel.getLayout() == null )
			{
				newDimen = wrapPanelBounds( panel );
			}
			else
			{
				newDimen = panel.getPreferredSize();
			}

			newBounds = ViewFunctions.instance().calculateNewBounds( panel.getBounds(), newDimen, zoomFactor );
			compSetBounds( panel, newBounds );
		}
		else
		{
			Insets insets = ViewFunctions.instance().getInsets( comp );
			Rectangle newBounds = ViewFunctions.instance().getNewRectangle( comp.getBounds(), insets, zoomFactor);
			compSetBounds( comp, newBounds );
		}
	}

	protected void setZoomFactor( Component comp, ZoomInterface zi, double zoomFactor )
	{
		zi.setZoomFactor(zoomFactor);
		addComponentResizingResult( comp, String.format( "setZoomFactor( %.2f )", zoomFactor ) );
	}

	protected void zoomLayout( BorderLayout bl, double zoomFactor )
	{
		bl.setHgap( IntegerFunctions.zoomValueFloor( bl.getHgap(), zoomFactor ) );
		bl.setVgap( IntegerFunctions.zoomValueFloor( bl.getVgap(), zoomFactor ) );
	}

	protected void zoomLayout( Container cont, double zoomFactor )
	{
		LayoutManager lm = cont.getLayout();
		if( lm instanceof BorderLayout )
		{
			BorderLayout bl = (BorderLayout) lm;

			zoomLayout(bl, zoomFactor);
		}
	}

	protected void setZoomFactor_single( Component comp, double zoomFactor )
	{
		if( comp == null )
			return;

		if( comp instanceof Container )
		{
			zoomLayout((Container)comp, zoomFactor);
		}

		if( comp instanceof ZoomInterface )
		{
			ZoomInterface zi = (ZoomInterface) comp;
			setZoomFactor( comp, zi, zoomFactor );
		}
		else if( ( comp instanceof JScrollBar ) ||
				( comp instanceof MetalComboBoxButton )// ||
//				( comp instanceof JComboBox )
				)
		{
			MetalComboBoxUI mcui;
			int kk=0;
		}
		else if( ( comp.getParent() != null ) &&
				( comp.getParent().getLayout() == null ) )
		{
			setZoomFactor_layoutNull( comp, zoomFactor );
		}
		else if( ( comp instanceof JPanel ) &&
				( ( (JPanel) comp ).getLayout() == null ) )
		{
			compSetPreferredSize( comp, wrapPanelBounds( (JPanel) comp ) );
		}
		else if( !( ( comp.getParent() != null ) &&
					( comp.getParent().getLayout() == null ) //&&
//					!( comp instanceof JPanel )  &&
//					!( comp instanceof JButton ) &&
//					!( ( comp instanceof JLabel ) &&
//						!comp.getClass().getName().contains( "MetalFileChooserUI" )
//					 )
				  )
				)
		{
			zoomPreferredSize( comp, zoomFactor );
		}
	}

	// for panel.getLayout() == null
	protected Dimension wrapPanelBounds( JPanel panel )
	{
		Dimension maxDimen = new Dimension( 0, 0 );

		for( int ii=0; ii<panel.getComponentCount(); ii++ )
		{
			Rectangle rect = panel.getComponent(ii).getBounds();
			maxDimen.width = IntegerFunctions.max( maxDimen.width, rect.x + rect.width );
			maxDimen.height = IntegerFunctions.max( maxDimen.height, rect.y + rect.height );
		}

		return( maxDimen );
	}

	// function to be called from FileChooser or system dialogs.
	protected void setZoomFactor_internal( Component comp, double zoomFactor, SwitchToZoomComponents.ComponentFilter compFilter )
	{
		if( //( comp instanceof JLabel ) ||
			//( comp instanceof JButton ) ||
			( comp instanceof JComboBox ) )
		{
			int kk=0;
		}

		if( comp instanceof JTabbedPane )
		{
			JTabbedPane tp = (JTabbedPane) comp;
			
			for( int ii=0; ii<tp.getTabCount(); ii++ )
			{
				setZoomFactor_internal( tp.getComponentAt(ii), zoomFactor, compFilter );
			}
		}


		if( comp instanceof JComboBox )
		{
			JComboBox combo = (JComboBox) comp;
//			BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);
			JPopupMenu popup = getComboPopup(combo);

			setZoomFactor_internal( popup, zoomFactor, compFilter );
		}
		else if( comp instanceof JScrollBar )
		{
			
		}
		else if( comp instanceof JViewport )
		{
			
		}
		else if( comp instanceof Container )
		{
			Container cont = (Container) comp;

			if( comp instanceof JMenu )
			{
				JMenu jMenu = (JMenu) comp;

				setZoomFactor_internal( jMenu.getPopupMenu(), zoomFactor, compFilter );
			}

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
			{
				setZoomFactor_internal( cont.getComponent(ii), zoomFactor, compFilter );
			}
		}

		if( comp instanceof JComponent )
		{
			JComponent jcomp = (JComponent) comp;
			if( ! (comp instanceof JComboBox ) )
			{
				setZoomFactor_internal( jcomp.getComponentPopupMenu(), zoomFactor, compFilter );
			}
		}

		if( compFilter.passesFilter( comp ) )
			setZoomFactor_single( comp, zoomFactor );
	}

	protected void switchPopupMenu( Component comp, double zoomFactor )
	{
		if( comp instanceof JComponent )
		{
			JComponent jcomp = (JComponent) comp;
			if( ! (comp instanceof JComboBox ) )
			{
				JPopupMenu popup = jcomp.getComponentPopupMenu();
				if( popup != null )
				{
					JPopupMenu switchedPopup = (JPopupMenu) switchToZoomComponents( popup, zoomFactor );
					jcomp.setComponentPopupMenu(switchedPopup);
				}
			}
		}
	}

	protected JPopupMenu getComboPopup( JComboBox combo )
	{
		return( ComboBoxFunctions.instance().getComboPopup(combo) );
	}

	public ComponentResizingResult getComponentResizingResult()
	{
		return( _componentResizingResult );
	}

	public interface ComponentFilter
	{
		public boolean passesFilter( Component comp );
	}
}
