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

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.ConfigurationItemJPanelBase;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SpinConfigurationItemJPanel
	extends ConfigurationItemJPanelBase<Integer, SpinConfigurationItem> {
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected SpinConfigurationItem _configurationItem = null;

	/**
	 * Creates new form ButtonConfigurationItemJPanel
	 */
	public SpinConfigurationItemJPanel() {
	}

	@Override
	public void init( SpinConfigurationItem bci )
	{
		_configurationItem = bci;

		super.init();
	}

	protected void initContents()
	{
		if( _configurationItem != null )
		{
			jL_name.setText( _configurationItem.getName() );

			jSl_value.setMinimum( _configurationItem.getMin() );
			jSl_value.setMaximum( _configurationItem.getMax() );

			SpinnerModel sm = new SpinnerNumberModel((Number) _configurationItem.getDefaultValue(),
														_configurationItem.getMin(),
														_configurationItem.getMax(), 1); //default value,lower bound,upper bound,increment by
			jSp_value.setModel(sm);
			jSp_value.setValue( _configurationItem.getValueWithDefaultValue() );
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
        jSp_value = new javax.swing.JSpinner();
        jSl_value = new javax.swing.JSlider();

        setLayout(null);

        jPanel1.setLayout(null);

        jL_name.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_name.setText("Name :");
        jPanel1.add(jL_name);
        jL_name.setBounds(5, 15, 205, 14);

        jSp_value.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSp_valueStateChanged(evt);
            }
        });
        jPanel1.add(jSp_value);
        jSp_value.setBounds(215, 15, 75, 20);

        jSl_value.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSl_valueStateChanged(evt);
            }
        });
        jPanel1.add(jSl_value);
        jSl_value.setBounds(295, 10, 165, 26);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 465, 45);
    }// </editor-fold>//GEN-END:initComponents

    private void jSp_valueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSp_valueStateChanged
        // TODO add your handling code here:

		updateSliderValue( getSpinnerValue() );

    }//GEN-LAST:event_jSp_valueStateChanged

    private void jSl_valueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSl_valueStateChanged
        // TODO add your handling code here:

		updateSpinnerValue( getSliderValue() );

    }//GEN-LAST:event_jSl_valueStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jL_name;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSl_value;
    private javax.swing.JSpinner jSp_value;
    // End of variables declaration//GEN-END:variables

	protected Integer getSliderValue()
	{
		return( (Integer) jSl_value.getValue() );
	}

	protected Integer getSpinnerValue()
	{
		return( (Integer) jSp_value.getValue() );
	}

	protected void updateSliderValue( Integer value )
	{
		if( ( value != null ) && !Objects.equals( value, getSliderValue() ) )
			jSl_value.setValue( value );
	}

	protected void updateSpinnerValue( Integer value )
	{
		if( ( value != null ) && !Objects.equals( value, getSpinnerValue() ) )
			jSp_value.setValue( value );
	}

	@Override
	public Integer getValue()
	{
		return( getSliderValue() );
	}

	@Override
	public void setValue( Integer value )
	{
		updateSliderValue( value );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jSl_value = compMapper.mapComponent( jSl_value );
		jSp_value = compMapper.mapComponent( jSp_value );
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
	public Class<Integer> getValueClass()
	{
		return( Integer.class );
	}

	@Override
	public void releaseResources()
	{
		_configurationItem = null;
	}

	@Override
	public void setMinimumSize( Dimension dimen )
	{
		super.setMinimumSize( dimen );
	}

	@Override
	public void setBounds( Rectangle bounds )
	{
		super.setBounds( bounds );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );
	}
}