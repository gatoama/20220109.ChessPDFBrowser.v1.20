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
package com.frojasg1.general.desktop.view.zoomfactor;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.number.DoubleFunctions;
import com.frojasg1.generic.zoom.ZoomFactorsAvailable;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComboBoxZoomFactorManager {

	protected JComboBox _comboBox = null;
	protected boolean _changedByProgram;

	protected List<ActionListener> _actionListeners = new ArrayList<>();

	protected BaseApplicationConfigurationInterface _appliConf = null;

	public ComboBoxZoomFactorManager( JComboBox comboBox )
	{
		_comboBox = comboBox;
	}

	public void changeZoomFactorComboBox( JComboBox comboBox )
	{
		addExistingListeners( comboBox );
		removeActionListeners();

		_comboBox = comboBox;
	}

	protected void addExistingListeners( JComboBox comboBox )
	{
		if( comboBox != null )
		{
			ActionListener[] array = comboBox.getActionListeners();
			for( ActionListener al: _actionListeners )
			{
				if( ! ArrayFunctions.instance().contains( array, al  ) )
					comboBox.addActionListener(al);
			}
		}
	}

	protected double getZoomFactorFromComboBoxItem( String item )
	{
		double result = -1.0D;

		if( ( item != null ) && ( item.length() > 0 ) &&
			( item.charAt( item.length() - 1 ) == '%' ) )
		{
			try
			{
				String numberStr = item.substring( 0, item.length() - 1 );
				result = Double.valueOf( numberStr ) / 100;
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		return( result );
	}

	protected int selectMostSuitableItemIndex( double zoomFactor )
	{
		ComboBoxModel<String> dcbm = _comboBox.getModel();

		double minDiff = 100;
		int result = -1;
		for( int ii=0; ii<dcbm.getSize(); ii++ )
		{
			double currentZoomFactor = getZoomFactorFromComboBoxItem( dcbm.getElementAt(ii) );
			double currentDiff = DoubleFunctions.instance().abs( currentZoomFactor - zoomFactor );

			if( currentDiff < minDiff )
			{
				minDiff = currentDiff;
				result = ii;
			}
		}

		return( result );
	}

	protected void setAppliConf( BaseApplicationConfigurationInterface applicationConfiguration )
	{
		_appliConf = applicationConfiguration;
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected String[] getAvailableZoomFactors()
	{
		return( ZoomFactorsAvailable.instance().getZoomFactorsAvailable().toArray( new String[0] ) );
	}

	public void init( BaseApplicationConfigurationInterface applicationConfiguration )
	{
		if( _comboBox != null )
		{
			setAppliConf(applicationConfiguration);
			double zoomFactor = getAppliConf().getZoomFactor();

			addActionListenerToComboBox();

			_changedByProgram = true;

			DefaultComboBoxModel dcm = new DefaultComboBoxModel( getAvailableZoomFactors() );
			_comboBox.setModel( dcm );

			int index = selectMostSuitableItemIndex( zoomFactor );

			_comboBox.setSelectedIndex(index);

			_changedByProgram = false;
		}
	}

	protected void addActionListenerToComboBox()
	{
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBzoomActionPerformed(evt);
            }
        });
	}

	protected void jCBzoomActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // TODO add your handling code here:

		if( ! wasChangedByProgram() )
		{
			newZoomFactor( getSelectedZoomFactor() );
		}

    }                                       

	protected void newZoomFactor( double zoomFactor )
	{
		getAppliConf().serverChangeZoomFactor(zoomFactor);
	}

	public double getSelectedZoomFactor()
	{
		double result = 1.0D;
		if( _comboBox != null )
		{
			String selectedItem = (String) _comboBox.getSelectedItem();
			result = getZoomFactorFromComboBoxItem( selectedItem );
		}

		return( result );
	}

	public void addActionListener( java.awt.event.ActionListener actionListener )
	{
		if( _comboBox != null )
		{
			_actionListeners.add( actionListener );
			_comboBox.addActionListener(actionListener);
		}
	}

	protected void removeActionListeners()
	{
		if( _comboBox != null )
		{
			ActionListener listener = null;
			while( !_actionListeners.isEmpty() )
			{
				listener = _actionListeners.remove(0);
				_comboBox.removeActionListener(listener);
			}
		}
	}

	public void release()
	{
		removeActionListeners();
		_comboBox = null;
	}

	public boolean wasChangedByProgram()
	{
		return( _changedByProgram );
	}
}
