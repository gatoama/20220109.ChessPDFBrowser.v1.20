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
package com.frojasg1.general.desktop.view;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.matchers.impl.IntegerToleranceMatcher;
import com.frojasg1.general.reflection.ReflectionFunctions;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentFunctions
{
	protected static ComponentFunctions _instance;

	public static void changeInstance( ComponentFunctions inst )
	{
		_instance = inst;
	}

	public static ComponentFunctions instance()
	{
		if( _instance == null )
			_instance = new ComponentFunctions();
		return( _instance );
	}

	public void releaseResources( Component comp )
	{
		if( comp instanceof ReleaseResourcesable )
			( (ReleaseResourcesable) comp ).releaseResources();
	}

	public void browseComponentHierarchy( Component comp, ExecuteToComponent executeToComp )
	{
		if( comp != null )
		{
			if( executeToComp != null )
			{
				Component relatedComponent = executeToComp.executeToComponent(comp);
				if( relatedComponent != null )
					browseComponentHierarchy( relatedComponent, executeToComp );
			}

			if( ( comp instanceof JFrame ) ||
				( comp instanceof JRootPane ) ||
				( comp instanceof JLayeredPane ) ||
				( comp instanceof JPanel ) ||
				( comp instanceof JInternalFrame )  ||
				( comp instanceof Container ) )
			{
/*
				if( comp instanceof JSplitPane	)
				{
					JSplitPane jsp = (JSplitPane) comp;
					if( jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
					{
						browseComponentHierarchy( jsp.getLeftComponent(), executeToComp );
						browseComponentHierarchy( jsp.getRightComponent(), executeToComp );
					}
					else if( jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
					{
						browseComponentHierarchy( jsp.getTopComponent(), executeToComp );
						browseComponentHierarchy( jsp.getBottomComponent(), executeToComp );
					}
				}
				else if( comp instanceof JTable )
					browseComponentHierarchy( ( (JTable) comp ).getTableHeader(), executeToComp );

				else if( comp instanceof JScrollPane )
				{
					JScrollPane sp = (JScrollPane) comp;
					browseComponentHierarchy( sp.getViewport(), executeToComp );
				}
				else if( comp instanceof JViewport )
				{
					JViewport vp = (JViewport) comp;
					browseComponentHierarchy( vp.getView(), executeToComp );
				}
*/
				if( comp instanceof JTabbedPane )
				{
					JTabbedPane tabbedPane = (JTabbedPane) comp;
					for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
					{
						browseComponentHierarchy( tabbedPane.getComponentAt(ii), executeToComp );
					}
				}
//				else if( comp instanceof JComboBox )
//				{
//					JComboBox combo = (JComboBox) comp;
//					BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);
//
//					browseComponentHierarchy( popup, executeToComp );
//				}
				else if( comp instanceof Container )
				{
					Container contnr = (Container) comp;

					if( comp instanceof JMenu )
						browseComponentHierarchy( ( ( JMenu ) comp ).getPopupMenu(), executeToComp );

					for( int ii=0; ii<contnr.getComponentCount(); ii++ )
					{
						browseComponentHierarchy( contnr.getComponent(ii), executeToComp );
					}
				}

				if( comp instanceof JComboBox )
				{
					JComboBox combo = (JComboBox) comp;
					JPopupMenu popup = ComboBoxFunctions.instance().getComboPopup(combo);
//					BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

					browseComponentHierarchy( popup, executeToComp );
				}
				else if( comp instanceof JComponent )
				{
					browseComponentHierarchy( ((JComponent) comp).getComponentPopupMenu(), executeToComp );
				}

			}
		}
	}

	public void browseComponentHierarchy( Component comp, BiConsumer<Component, ComponentProcessingResult> executeToComp )
	{
		if( comp != null )
		{
			ComponentProcessingResult compProcessResult = new ComponentProcessingResult();
			if( executeToComp != null )
			{
				executeToComp.accept(comp, compProcessResult);
				for( Component child: compProcessResult.getLinkedChildrenList() )
					browseComponentHierarchy( child, executeToComp );
			}

			if( compProcessResult.hasToProcessChildren() &&
				( ( comp instanceof JFrame ) ||
					( comp instanceof JRootPane ) ||
					( comp instanceof JLayeredPane ) ||
					( comp instanceof JPanel ) ||
					( comp instanceof JInternalFrame )  ||
					( comp instanceof Container )
				)
			  )
			{
/*
				if( comp instanceof JSplitPane	)
				{
					JSplitPane jsp = (JSplitPane) comp;
					if( jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
					{
						browseComponentHierarchy( jsp.getLeftComponent(), executeToComp );
						browseComponentHierarchy( jsp.getRightComponent(), executeToComp );
					}
					else if( jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
					{
						browseComponentHierarchy( jsp.getTopComponent(), executeToComp );
						browseComponentHierarchy( jsp.getBottomComponent(), executeToComp );
					}
				}
				else if( comp instanceof JTable )
					browseComponentHierarchy( ( (JTable) comp ).getTableHeader(), executeToComp );

				else if( comp instanceof JScrollPane )
				{
					JScrollPane sp = (JScrollPane) comp;
					browseComponentHierarchy( sp.getViewport(), executeToComp );
				}
				else if( comp instanceof JViewport )
				{
					JViewport vp = (JViewport) comp;
					browseComponentHierarchy( vp.getView(), executeToComp );
				}
*/
				if( comp instanceof JTabbedPane )
				{
					JTabbedPane tabbedPane = (JTabbedPane) comp;
					for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
					{
						browseComponentHierarchy( tabbedPane.getComponentAt(ii), executeToComp );
					}
				}
//				else if( comp instanceof JComboBox )
//				{
//					JComboBox combo = (JComboBox) comp;
//					BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);
//
//					browseComponentHierarchy( popup, executeToComp );
//				}
				else if( comp instanceof Container )
				{
					Container contnr = (Container) comp;

					if( comp instanceof JMenu )
						browseComponentHierarchy( ( ( JMenu ) comp ).getPopupMenu(), executeToComp );

					for( int ii=0; ii<contnr.getComponentCount(); ii++ )
					{
						browseComponentHierarchy( contnr.getComponent(ii), executeToComp );
					}
				}

				if( comp instanceof JComboBox )
				{
					JComboBox combo = (JComboBox) comp;
					JPopupMenu popup = ComboBoxFunctions.instance().getComboPopup(combo);
//					BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

					browseComponentHierarchy( popup, executeToComp );
				}
				else if( comp instanceof JComponent )
				{
					browseComponentHierarchy( ((JComponent) comp).getComponentPopupMenu(), executeToComp );
				}
			}

			compProcessResult.reset();
		}
	}

	public String getComponentString( Component comp )
	{
		String result = "null";
		if( comp != null )
		{
			result = String.format( "%s name=%s", comp.getClass().getName(), comp.getName() );
		}

		return( result );
	}

	public boolean isVisible( Component comp )
	{
		boolean isVisible = true;
		
		Component current = comp;
		while( isVisible && ( current != null ) )
		{
			isVisible = current.isVisible();

			current = current.getParent();
		}

		return( isVisible );
	}

	public Component getAncestor( Component comp )
	{
		Component result = null;
		if( comp != null )
			result = SwingUtilities.getRoot( comp );

		return( result );
	}

	public boolean isAnyParentInstanceOf( Class<?> clazz, Component comp )
	{
		return( getFirstParentInstanceOf(clazz, comp) != null );
	}

	public <CC> CC getFirstParentInstanceOf( Class<CC> clazz, Component comp )
	{
		CC result = null;

		if( ( comp != null ) && ( clazz != null ) )
		{
			Component current = comp.getParent();
			while( ( current != null ) && ( result == null ) )
			{
				if( clazz.isInstance(current) )
					result = (CC) current;

				current = current.getParent();
			}
		}

		return( result );
	}

	public boolean isAnyParent( Component possibleParent, Component comp )
	{
		boolean result = false;
		
		if( ( possibleParent != null ) && ( comp != null ) )
		{
			Component current = comp.getParent();
			while( ( current != null ) && !result )
			{
				result = ( possibleParent == current );
				if( current instanceof JDialog )
					break;
				current = current.getParent();
			}
		}

		return( result );
	}

	public boolean isViewportView( Component component )
	{
		return( getScrollPaneOfViewportView( component ) != null );
	}

	public JScrollPane getScrollPaneOfViewportView( Component component )
	{
		JScrollPane jsp = getScrollPane( component );
		if( jsp != null )
			if( jsp.getViewport().getView() != component )
				jsp = null;

		return( jsp );
	}

	public JViewport getViewport( Component comp )
	{
		JViewport result = null;
		if( comp != null )
		{
			Component parent = comp.getParent();
			if( parent instanceof JViewport )
				result = (JViewport) parent;
		}

		return( result );
	}

	public JScrollPane getScrollPane( Component component )
	{
		ComponentFunctions cf;
		JScrollPane result = null;
		if( component != null )
		{
			Component parent = component.getParent();
			if( parent instanceof JViewport )
				parent = parent.getParent();

			if( parent instanceof JScrollPane )
				result = (JScrollPane) parent;
		}

		return( result );
	}

	public Component getFocusedComponent()
	{
		return( KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() );
	}

	public boolean isComponentListenerAdded( Component comp, ComponentListener listener )
	{
		return( isGenListenerAdded( comp, listener, (com) -> com.getComponentListeners() ) );
	}

	public boolean isMouseListenerAdded( Component comp, MouseListener listener )
	{
		return( isGenListenerAdded( comp, listener, (com) -> com.getMouseListeners() ) );
	}

	public <CC, LL> boolean isGenListenerAdded( CC comp, LL listener, Function<CC, LL[]> getter )
	{
		boolean result = false;
		if( ( comp != null ) && ( getter != null ) )
		{
			LL[] arr = getter.apply(comp);
			result = Arrays.stream( arr ).anyMatch( (lsnr) -> listener==lsnr );
		}

		return( result );
	}

	protected String getComponentName( Component comp )
	{
		String result = ViewFunctions.instance().instance().getComponentName(comp);

		return( result );
	}

	public boolean selectJTabbedPaneIndex( JTabbedPane tp, String panelName )
	{
		boolean result = false;

		if( tp != null )
		{
			for( int ii=0; ii<tp.getTabCount(); ii++ )
			{
				Component tab = tp.getComponentAt( ii );
				String name = getComponentName( tab );
				if( Objects.equals( name, panelName ) )
				{
					result = true;
					tp.setSelectedComponent(tab);
				}
			}
		}

		return( result );
	}

	public boolean positionOnScreenMatches( Component comp, Point originPointOnScreen,
		int tolerance )
	{
		
		return( positionMatches(comp, c -> c.getLocationOnScreen(), originPointOnScreen,
			tolerance ) );
	}

	public <CC, MM> boolean positionMatches( CC obj, Function<CC, MM> getter,
		MM magnitudeToCompare, int tolerance )
	{
		boolean result = false;

		if( obj != null )
		{
			result = ExecutionFunctions.instance().safeFunctionExecution(
				() -> IntegerToleranceMatcher.instance().match(
					getter.apply(obj), magnitudeToCompare, tolerance ) );
		}

		return( result );
	}

	public <MM> List<Component> getMatchingChildComponents( Container cont,
		Function<Component, MM> getter, MM magnitudeToCompare,
		int tolerance )
	{
		List<Component> result = new ArrayList<>();
		browseComponentHierarchy( cont, (comp) -> {
			if( positionMatches( comp, getter, magnitudeToCompare, tolerance ) )
				result.add( comp );

			return(null);
		});

		return( result );
	}

	public <MM> List<Component> getMatchingChildComponents( Container cont,
		BiFunction<Component, MM, Boolean> matcher, MM magnitudeToCompare )
	{
		List<Component> result = new ArrayList<>();
		if( matcher != null )
		{
			browseComponentHierarchy( cont, (comp) -> {
				if( matcher.apply( comp, magnitudeToCompare ) )
					result.add( comp );

				return(null);
			});
		}

		return( result );
	}

	public Rectangle getBoundsOnScreen( Component comp )
	{
		Rectangle result = null;
		Point origin = ExecutionFunctions.instance().safeSilentFunctionExecution( () -> comp.getLocationOnScreen() );
		if( origin != null )
		{
			Dimension dimen = comp.getSize();
			result = new Rectangle( origin, dimen );
		}

		return( result );
	}

	public <CC> CC getFirstParentOfClass( Component comp, Class<CC> clazz )
	{
		CC result = null;
		if( clazz != null )
		{
			Component tmpComp = comp;
			while( ( result == null ) && ( tmpComp != null ) )
			{
				if( clazz.isInstance(tmpComp) )
					result = (CC) tmpComp;

				tmpComp = tmpComp.getParent();
			}
		}

		return( result );
	}

	protected boolean componentContainsScreenPoint( Component comp, Point screenPoint )
	{
		return( ViewFunctions.instance().componentContainsScreenPoint(comp, screenPoint ) );
	}

	public void inspectHierarchy( Component comp )
	{
		inspectHierarchy(comp,
			current -> {
				while( current != null )
				{
					Component comp2 = current;
					ExecutionFunctions.instance().safeSilentMethodExecution(() -> {
							System.out.println(
								String.format( "Componente. Clase: %s, nombre: %s, límites: %s, background: %s",
									comp2.getClass().getName(), comp2.getName(), comp2.getBounds(),
									ExecutionFunctions.instance().safeSilentFunctionExecution( () -> Integer.toHexString( comp2.getBackground().getRGB() ) )
								)
							);
					}
					);

					current = current.getParent();
				}
		} );
	}

	public void inspectHierarchy( Component comp, Consumer<Component> processComponentFunction )
	{
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent me )
			{
				Component current = me.getComponent();

				ExecutionFunctions.instance().safeMethodExecution( () -> processComponentFunction.accept(current) );
			}
		};
		browseComponentHierarchy( comp, comp2 -> { comp2.addMouseListener( adapter ); return( null ); } );
	}

	public interface ExecuteToComponent
	{
		public Component executeToComponent( Component comp );
	}

	public Rectangle getClip(Component component)
	{
		Rectangle result = null;
		if( component != null )
		{
			result = component.getBounds();

			JScrollPane jsp = getScrollPane(component);
			if( jsp != null )
			{
				JScrollBar jsb = jsp.getHorizontalScrollBar();
				if( ( jsb != null ) && jsb.isVisible() )
				{
					result.x = jsb.getValue();
					result.width = jsb.getVisibleAmount();
				}

				jsb = jsp.getVerticalScrollBar();
				if( ( jsb != null ) && jsb.isVisible() )
				{
					result.y = jsb.getValue();
					result.height = jsb.getVisibleAmount();
				}
			}
		}

		return( result );
	}

	public ComponentUI getUI( JComponent jcomp )
	{
//		Object obj = ExecutionFunctions.instance().safeSilentFunctionExecution( () -> jcomp.getClientProperty( SwingUtilities2.COMPONENT_UI_PROPERTY_KEY ) );

		ComponentUI result = (ComponentUI) ReflectionFunctions.instance().invokeMethod( "getUI", jcomp );

		return( result );
	}

	public static class ComponentProcessingResult
	{
		protected boolean _processChildren = true;

		protected List<Component> _linkedChildrenList = new ArrayList<>();

		public boolean hasToProcessChildren() {
			return _processChildren;
		}

		public void setProcessChildren(boolean _processChildren) {
			this._processChildren = _processChildren;
		}

		public void addLinkedChildren( Component linkedChild )
		{
			_linkedChildrenList.add( linkedChild );
		}

		public List<Component> getLinkedChildrenList()
		{
			return( _linkedChildrenList );
		}

		public void reset()
		{
			_linkedChildrenList.clear();
			_processChildren = true;
		}
	}
}
