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
package com.frojasg1.general.desktop.view.menus;

import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.IconFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu.ComponentContext;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.map.multimap.MultiMap;
import com.frojasg1.general.map.multimap.MultiMapMapEntry;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Usuario
 * @param <CCK>	- ComponentContextKey
 */
public abstract class BaseJPopupMenu<CCK> extends JPopupMenu implements ActionListener,
																	MouseListener,
																	InternallyMappedComponent
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseJPopupMenu.class);

	protected Component _component = null;
	protected Component _ancestor = null;

	protected boolean _alreadyMapped = false;

	protected Class<CCK> _componentContextKeyClass;
	protected ComponentContextMultiMap<CCK> _mapOfComponents;

	protected MouseEvent _popupMouseEvent;

	public BaseJPopupMenu( Component comp )
	{
		this( comp, null );
	}

	public BaseJPopupMenu( Component comp, Class<CCK> componentContextKeyClass )
	{
		FrameworkComponentFunctions.instance().addPopupMenuToParent(this, comp);
		_component = comp;
		_componentContextKeyClass = componentContextKeyClass;
	}

	protected void init()
	{
		_mapOfComponents = createMultiMap();
	}

	protected ComponentContextMultiMap<CCK> createMultiMap()
	{
		return( new ComponentContextMultiMap<>( _componentContextKeyClass ) );
	}

	protected Point getPoupPoint()
	{
		return( getPopupMouseEvent().getPoint() );
	}

	protected MouseEvent getPopupMouseEvent() {
		return _popupMouseEvent;
	}

	protected void setPopupMouseEvent(MouseEvent _popupMouseEvent) {
		this._popupMouseEvent = _popupMouseEvent;
	}

	public Component getAncestor()
	{
		if( ( _ancestor == null ) && ( _component != null ) )
			_ancestor = ComponentFunctions.instance().getAncestor(_component);

		return( _ancestor );
	}

	public void addMouseListenerToAllComponents()
	{
		recursiveAddMouseListener( this );
	}

	protected JCheckBoxMenuItem getCheckbox( CCK key )
	{
		return( getComponent( key, JCheckBoxMenuItem.class ) );
	}

	protected JMenuItem getMenuItem( CCK key )
	{
		return( getComponent( key, JMenuItem.class ) );
	}

	protected <C> C getComponent( CCK key, Class<C> clazz )
	{
		C result = null;
		Component comp = getComponentContextForKey(key).getComponent();
		if( clazz.isInstance(comp) )
			result = clazz.cast( comp );

		return( result );
	}

	protected void empty()
	{
		removeMouseListenerToAllComponents();

		while( getComponentCount() > 0 )
			this.remove( 0 );
	}

	protected void recursiveAddMouseListener( Component comp )
	{
		comp.addMouseListener( this );
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			for( int ii=0; ii<cont.getComponentCount(); ii++ )
			{
				recursiveAddMouseListener( cont.getComponent(ii) );
			}
		}
	}
	
	public void removeMouseListenerToAllComponents()
	{
		recursiveRemoveMouseListener( this );
	}

	protected void recursiveRemoveMouseListener( Component comp )
	{
		comp.removeMouseListener( this );
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			for( int ii=0; ii<cont.getComponentCount(); ii++ )
			{
				recursiveRemoveMouseListener( cont.getComponent(ii) );
			}
		}
	}

	protected void addMenuComponent( Component comp )
	{
		addMenuComponent( null, comp, null, null, null );
	}

	protected ComponentContext createComponentContext( CCK key, AbstractButton comp,
													Supplier<String> textFunction,
													Runnable action )
	{
		ComponentContext result = new ComponentContext();
		result.setKey(key);
		result.setComponent(comp);
		result.setTextFunction(textFunction);
		result.setAction(action);

		return( result );
	}

	protected void addElementToMap( CCK key, Component comp, Supplier<String> textFunction, Runnable action )
	{
		if( ( _mapOfComponents != null ) &&
			( comp instanceof AbstractButton ) && ( key != null ) )
		{
			_mapOfComponents.put( createComponentContext( key, (AbstractButton) comp,
															textFunction, action ) );
		}
	}

	protected void addMenuComponent( CCK key, Component comp, Supplier<String> textFunction,
									Runnable action, String iconResourceName )
	{
		addElementToMap( key, comp, textFunction, action );

		add( comp );
		if( comp instanceof JMenuItem )
		{
			JMenuItem mi = (JMenuItem) comp;
			mi.addActionListener(this);
			
			if( iconResourceName != null )
				setIconForMenuItem( mi, iconResourceName );
		}
	}

	protected void addMenuComponent( JSeparator sep )
	{
		add( sep );
	}

	public <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected Runnable getAction( Component comp )
	{
		return( getIfNotNull( getComponentContext( comp ), ComponentContext::getAction ) );
	}

	protected String getText( Component comp )
	{
		return( getIfNotNull( getComponentContext( comp ), ComponentContext::getText ) );
	}

	protected ComponentContext<CCK> getComponentContext( Component comp )
	{
		return( ( _mapOfComponents != null )
			? _mapOfComponents.get(ComponentContextMultiMap.COMPONENT_KEY_NAME, comp)
			: null );
	}

	protected ComponentContext<CCK> getComponentContextForKey( CCK key )
	{
		return( ( _mapOfComponents != null )
			? _mapOfComponents.get(ComponentContextMultiMap.KEY_NAME, key)
			: null );
	}

	protected Component getComponentForKey( CCK key )
	{
		Component result = null;
		ComponentContext cc = getComponentContextForKey( key );
		if( cc != null )
			result = cc.getComponent();

		return( result );
	}

	@Override
	public void actionPerformed( ActionEvent evt )
	{
		defaultActionPerformed( evt );
	}

	public void setCurrentLanguageTexts()
	{
		if( _mapOfComponents != null )
		{
			for( ComponentContext context: _mapOfComponents.getLabelMap().values() )
			{
				String text = context.getText();
				if( text != null )
					context.getComponent().setText(text);
			}
		}
	}

	public ComponentContextMultiMap<CCK> getMapOfComponents() {
		return _mapOfComponents;
	}

	public void defaultActionPerformed( ActionEvent evt )
	{
		try
		{
			Component comp = (Component) evt.getSource();

			Runnable action = getAction( comp );
			if( action != null )
				action.run();
			else
				LOGGER.info( "Action not found on {}", evt );
		}
		catch( Exception ex )
		{
			LOGGER.warn( "Error performing action on {}", evt );
		}
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
		Point mouseLocation = me.getLocationOnScreen();
		Point leftTopCorner = getLocationOnScreen();
		Point rightBottomCorner = new Point( (int) (leftTopCorner.getX() + getWidth()),
											(int) (leftTopCorner.getY() + getHeight()) );

		if( ( mouseLocation.getX() <= leftTopCorner.getX() ) ||
			( mouseLocation.getX() >= rightBottomCorner.getX() ) ||
			( mouseLocation.getY() <= leftTopCorner.getY() ) ||
			( mouseLocation.getY() >= rightBottomCorner.getY() ) )
		{
			setVisible(false);
		}
	}

	protected abstract void preparePopupMenuItems();



	public void doPopup( MouseEvent evt )
	{
//		createMenuIfAnyChange();
		setPopupMouseEvent( evt );

		preparePopupMenuItems();

		Point position = new Point( evt.getX() - 10, evt.getY() - 10 );
		position = ViewFunctions.instance().getValidPositionToPlaceComponent( this, position );
		show(evt.getComponent(), evt.getX() - 10, evt.getY() - 10);
	}

	@Override
	public void setVisible( boolean value )
	{
		super.setVisible(value);

		if( !value && ( getAncestor() != null ) )
			getAncestor().repaint();
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		ComponentContextMultiMap<CCK> multimapOfComponents = getMapOfComponents();
		if( multimapOfComponents != null )
		{
			Set<ComponentContext<CCK>> set = new HashSet<>();
			for( ComponentContext<CCK> cc: multimapOfComponents.getLabelMap().values() )
			{
				set.add(cc);
				cc.setComponent( compMapper.mapComponent( cc.getComponent() ) );
			}

			multimapOfComponents.clear();

			for( ComponentContext<CCK> cc: set )
				multimapOfComponents.put(cc);
		}

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	public void setIconForMenuItem( AbstractButton menuItem, String iconResourceName )
	{
		IconFunctions.instance().setIconForMenuItem(menuItem, iconResourceName);
	}

	protected static class ComponentContext<CCK2>
	{
		protected AbstractButton _component;
		protected Supplier<String> _textFunction;
		protected Runnable _action;
		protected CCK2 _key;

		public CCK2 getKey() {
			return _key;
		}

		public void setKey(CCK2 _key) {
			this._key = _key;
		}

		public AbstractButton getComponent() {
			return _component;
		}

		public void setComponent(AbstractButton _component) {
			this._component = _component;
		}

		public Supplier<String> getTextFunction() {
			return _textFunction;
		}

		public void setTextFunction(Supplier<String> textFunction) {
			this._textFunction = textFunction;
		}

		public String getText()
		{
			return( ( getTextFunction() != null ) ? getTextFunction().get() : null );
		}

		public Runnable getAction() {
			return _action;
		}

		public void setAction(Runnable _action) {
			this._action = _action;
		}
	}


	protected static class ComponentContextMultiMap<CCK2> extends MultiMap<ComponentContext<CCK2>>
	{
		protected static final String KEY_NAME = "KEY";
		protected static final String COMPONENT_KEY_NAME = "COMPONENT";
		
		protected Class<CCK2> _keyClass;

		public ComponentContextMultiMap( Class<CCK2> keyClass )
		{
			_keyClass = keyClass;

			init();
		}

		public Map<CCK2, ComponentContext<CCK2>> getLabelMap()
		{
			return( this.getMapByName(KEY_NAME, _keyClass) );
		}

		public Map<Component, ComponentContext<CCK2>> getComponentMap()
		{
			return( this.getMapByName(COMPONENT_KEY_NAME, Component.class) );
		}

		@Override
		protected Map<String, MultiMapMapEntry<?, ComponentContext<CCK2>>> fillMultiMapMapEntryMap() {
			Map<String, MultiMapMapEntry<?, ComponentContext<CCK2>>> result = new HashMap<>();

			addMultiMapMapEntry( result, KEY_NAME, _keyClass, ComponentContext::getKey );
			addMultiMapMapEntry( result, COMPONENT_KEY_NAME, Component.class,
								ComponentContext::getComponent );

			return( result );
		}
	}
}
