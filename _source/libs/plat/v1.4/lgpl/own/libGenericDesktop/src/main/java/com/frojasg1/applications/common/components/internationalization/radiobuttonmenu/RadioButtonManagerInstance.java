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
package com.frojasg1.applications.common.components.internationalization.radiobuttonmenu;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.components.ZoomJRadioButtonMenuItem;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class RadioButtonManagerInstance implements RadioButtonManagerInterface
{
	protected ButtonGroup _bg;
	protected JMenu _menu;

	// the next attribute has to have direct access to Ancestor JFrame or JDialog
	protected Component _ancestor;

	protected boolean _initializing = true;

	protected ItemListener _listener;

	protected BaseApplicationConfigurationInterface _appConf;

	public RadioButtonManagerInstance( ButtonGroup bg, JMenu menu, Component ancestor,
										BaseApplicationConfigurationInterface appConf )
	{
		_bg = bg;
		_menu = menu;
		_appConf = appConf;
		_ancestor = ancestor;
	}

	public void setMenu( JMenu menu )
	{
		_menu = menu;
	}

	public JMenu getMenu()
	{
		return( _menu );
	}

	public Component getAncestor() {
		return _ancestor;
	}

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appConf );
	}

	public void setListener( ItemListener listener )
	{
		_listener = listener;
	}

	public ButtonGroup getButtonGroup()
	{
		return( _bg );
	}

	public JPopupMenu getPopupSubmenu()
	{
		JPopupMenu result = null;
		if( _menu != null )
			result = _menu.getPopupMenu();
		return( result );
	}

	public Map<String, Component> getRadioButtonMenuItemMap()
	{
		HashMap<String, Component> result = new HashMap<>();

		int numSeparators = 0;
		for( int ii=0; ii<getPopupSubmenu().getComponentCount(); ii++ )
		{
			Component comp = getPopupSubmenu().getComponent( ii );

			if( comp instanceof AbstractButton )
			{
				AbstractButton btn = (AbstractButton) comp;

				result.put( btn.getText(), comp );
			}
			else if( comp instanceof Separator )
			{
				numSeparators++;

				result.put( "Separator" + numSeparators, comp );
			}
			else
			{
				throw( new RuntimeException( "Error: componentType not expected in JPopupMenu of JRadioButtonMenuItems, " +
												comp.getClass().getName() ) );
			}
		}

		return( result );
	}

	public void setRadioButtonSubmenu()
	{
		invokeUpdateRadioButtonSubmenu();
	}

	protected AbstractButton getSelectedRadioButton()
	{
		AbstractButton result = null;

		ButtonModel model = _bg.getSelection();

		Enumeration<AbstractButton> enumeration = _bg.getElements();
		while( ( result == null ) && enumeration.hasMoreElements() )
		{
			AbstractButton btn = enumeration.nextElement();
			if( btn.getModel() == model )
				result = btn;
		}

		return( result );
	}

	protected void invokeUpdateRadioButtonSubmenu()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run()
					{
						invokeUpdateRadioButtonSubmenu();
					}
			});
		}
		else
		{
			updateRadioButtonSubmenu();
			updateSelectionInMenu();
		}
	}

	public void updateSelectionInMenu()
	{
		String selectedItem = getConfiguredItemToBeSelected();
		if( selectedItem != null )
			setSelectionInMenu( selectedItem );
	}

	protected void updateRadioButtonSubmenu( )
	{
		updateSubmenu(getPopupSubmenu() );
		_initializing = false;
	}

	
	protected void updateSubmenu( JPopupMenu submenu )
	{
		Collection<String>[] listsOfElements = getListsOfElementsForMenu();
		
//		ListOfLanguagesResult_int listOfLanguages = GenericFunctions.instance().getObtainAvailableLanguages().getTotalListOfAvailableLanguages();
		updateSubmenu(submenu, listsOfElements );
	}

	protected void updateSubmenu( JPopupMenu submenu, Collection<String>[] listsOfElements )
	{
		if( ! areListsOfAvailableElementsTheSameAsInTheMenu(submenu, listsOfElements) )
		{
			createNewSubmenuItems(submenu, listsOfElements );
		}
	}

	protected boolean areListsOfAvailableElementsTheSameAsInTheMenu( JPopupMenu submenu, Collection<String>[] listsOfElements )
	{
		boolean result = false;

		if(  _initializing )
		{
			result = false;
		}
		else if( submenu != null )
		{
			MenuElement[] elems = submenu.getSubElements();

			if( ( ( listsOfElements == null ) || ( listsOfElements.length == 0 ) )
				&& (elems.length==0) )
			{
				result = true;
			}
			else if( ( listsOfElements == null ) || ( listsOfElements.length == 0 ) )
			{
				result = false;
			}
			else
			{
				result = true;
				int languageCount = 0;
				int menuIndex = 0;

				boolean elemChecked = false;
				boolean separatorChecked = false;
				for( int ii=0; result && (menuIndex<elems.length) && (ii<listsOfElements.length); ii++ )
				{
					Iterator<String> it = listsOfElements[ii].iterator();
					while( result && ( menuIndex < elems.length ) && it.hasNext() )
					{
						String elem = it.next();
						if( elems[menuIndex] instanceof JRadioButtonMenuItem )
						{
							JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) elems[menuIndex];
							result = ( elem.equals( radioButton.getText() ) );

							languageCount++;
							menuIndex++;
							elemChecked = true;
							separatorChecked = false;
						}
					}

					if( elemChecked && ! separatorChecked &&
						( ii<( listsOfElements.length - 1 ) ) &&
						( listsOfElements[ii+ 1].size() > 0 ) )
					{
						if( menuIndex >= elems.length )
							result = false;
						else
						{
							result = ( elems[menuIndex] instanceof Separator );
							menuIndex++;
						}
					}
				}
				result = result && ( menuIndex == elems.length ) &&
						( languageCount == CollectionFunctions.instance().getNumElems( listsOfElements ) );
			}
		}
		return( result );
	}

	protected void resetSubmenu( JPopupMenu submenu )
	{
		if( submenu != null )
		{
			if( _bg != null )
			{
				Enumeration<AbstractButton> enumeration = _bg.getElements();
				while( enumeration.hasMoreElements() )
				{
					_bg.remove( enumeration.nextElement() );
				}
			}
			else
			{
				_bg = new ButtonGroup();
			}

			submenu.removeAll();
		}
	}

	protected void createNewSubmenuItems( JPopupMenu submenu, Collection<String>[] listsOfElements )
	{
		if( submenu != null )
		{
			resetSubmenu(submenu );

			if( ( listsOfElements != null ) && ( listsOfElements.length > 0 ) )
			{
				boolean previouslySet = false;
				for( int ii=0; ii<listsOfElements.length; ii++ )
				{
					if( previouslySet &&
						( listsOfElements[ii] != null ) && ( listsOfElements[ii].size() > 0 ) )
					{
						submenu.addSeparator();
						previouslySet = false;
					}

					addListOfItems(submenu, _bg, listsOfElements[ii] );
					previouslySet = previouslySet || ( listsOfElements[ii].size() > 0 );
				}
			}
		}
	}

	protected ZoomJRadioButtonMenuItem createZoomJRadioButtonMenuItem( String elem )
	{
		return( new ZoomJRadioButtonMenuItem( elem ) );
	}

	protected JRadioButtonMenuItem createAndUpdateRadioButtonInternal( String elem )
	{
		ZoomJRadioButtonMenuItem result = createZoomJRadioButtonMenuItem( elem );

		result.setName(elem);
		result.setZoomFactor( getAppliConf().getZoomFactor() );
		result.setVerticalAlignment( AbstractButton.CENTER );
		result.setVerticalTextPosition(0);
		result.initBeforeCopyingAttributes();
		result.switchToZoomUI();

		return( result );
	}

	protected JRadioButtonMenuItem createAndUpdateRadioButton( String elem )
	{
		ZoomJRadioButtonMenuItem result = createZoomJRadioButtonMenuItem( elem );

		result.setName(elem);
		result.setZoomFactor( getAppliConf().getZoomFactor() );
		result.setVerticalAlignment( AbstractButton.CENTER );
		result.setVerticalTextPosition(0);
		result.initBeforeCopyingAttributes();
		result.switchToZoomUI();

		invertIfNecessary( result );

		return( result );
	}

	protected void addListOfItems( JPopupMenu submenu, ButtonGroup bg, Collection<String> listOfItems )
	{
		if( ( submenu != null ) && ( listOfItems != null ) )
		{
			Iterator<String> it = listOfItems.iterator();
			while( it.hasNext() )
			{
				String elem = it.next();
				JRadioButtonMenuItem rb = createAndUpdateRadioButton( elem );

//				result.setText( it.next() );
				bg.add(rb);
				submenu.add( rb );
				if( _listener != null )
					rb.addItemListener( _listener );
			}
		}
	}

	protected void setSelectionInMenu( String element )
	{
		try
		{
			if( ( element != null ) && ( getPopupSubmenu() != null ) )
			{
				MenuElement[] elems = getPopupSubmenu().getSubElements();

				for( int ii=0; ii<elems.length; ii++ )
				{
					if( elems[ii] instanceof JRadioButtonMenuItem )
					{
						JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) elems[ii];
						if( element.equals( radioButton.getText() ) )
						{
							if( ! radioButton.isSelected() )
							{
								radioButton.setSelected(true);
							}
							break;
						}
					}
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public ChangeRadioButtonMenuItemListResult addItemToMenu( String item )
	{
		addItemToList( item );

		Map< String, Component > originalMap = getRadioButtonMenuItemMap();
		invokeUpdateRadioButtonSubmenu();
		Map< String, Component > finalMap = getRadioButtonMenuItemMap();

		ChangeRadioButtonMenuItemListResult result = new ChangeRadioButtonMenuItemListResult( getPopupSubmenu(),
																								originalMap, finalMap );
		return( result );
	}

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode( getAncestor() ) );
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor( getAncestor() ) );
	}

	protected void invertIfNecessary( Component comp )
	{
		boolean isDarkMode = isDarkMode();
		if( isDarkMode )
		{
			ColorInversor ci = getColorInversor();

			if( ci != null )
				ci.invertSingleComponentColors(comp);
		}
	}

	public abstract void addItemToList( String item );

	public abstract String getConfiguredItemToBeSelected( );

	public abstract Collection<String>[] getListsOfElementsForMenu();
/*	{

		GenericFunctions.instance().getObtainAvailableLanguages().newLanguageSetToConfiguration(language);

		updateRadioButtonSubmenu();
	}
*/
}
