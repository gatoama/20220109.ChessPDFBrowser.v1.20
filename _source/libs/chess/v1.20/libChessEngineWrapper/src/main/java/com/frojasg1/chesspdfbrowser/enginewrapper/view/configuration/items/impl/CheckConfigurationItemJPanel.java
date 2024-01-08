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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.impl;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.CheckConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.ConfigurationItemJPanelBase;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import javax.swing.JPanel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CheckConfigurationItemJPanel
	extends ConfigurationItemJPanelBase<Boolean, CheckConfigurationItem>
{

	protected CheckConfigurationItem _configurationItem = null;

	/**
	 * Creates new form ButtonConfigurationItemJPanel
	 */
	public CheckConfigurationItemJPanel() {
	}

	@Override
	public void init( CheckConfigurationItem bci )
	{
		_configurationItem = bci;

		super.init();
	}

	protected void initContents()
	{
		if( _configurationItem != null )
		{
			jL_name.setText( _configurationItem.getName() );

			jCB_check.setSelected( _configurationItem.getValueWithDefaultValue() );
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jL_name = new javax.swing.JLabel();
        jCB_check = new javax.swing.JCheckBox();

        setLayout(null);

        jPanel1.setLayout(null);

        jL_name.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_name.setText("Name :");
        jPanel1.add(jL_name);
        jL_name.setBounds(5, 15, 205, 14);

        jCB_check.setText("Check");
        jCB_check.setName("jCB_check"); // NOI18N
        jPanel1.add(jCB_check);
        jCB_check.setBounds(210, 10, 165, 23);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 385, 45);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCB_check;
    private javax.swing.JLabel jL_name;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

	@Override
	public Boolean getValue()
	{
		return( jCB_check.isSelected() );
	}

	@Override
	public void setValue( Boolean value )
	{
		boolean val = false;
		if( value != null )
			val = value;

		jCB_check.setSelected( val );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jCB_check = compMapper.mapComponent( jCB_check );
		jL_name = compMapper.mapComponent( jL_name );
		jPanel1 = compMapper.mapComponent( jPanel1 );
	}

	@Override
	protected void initComponents_protected() {
		initComponents();
	}

	@Override
	protected JPanel getParentPanel()
	{
		return( jPanel1 );
	}

	@Override
	public Class<Boolean> getValueClass()
	{
		return( Boolean.class );
	}

	@Override
	public void releaseResources()
	{
		_configurationItem = null;
	}
}