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
package com.frojasg1.general.desktop.view.panels;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SpinnerSliderJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
		implements DesktopViewComponent, ComposedComponent
{
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	int _min = 0;
	int _max = 0;
	int _defaultValue = 0;
	int _increment = 1;

	Integer _oldValue = null;
	Integer _newValue = null;

	protected BiConsumer<Integer, Integer> _listener = null;

	/**
	 * Creates new form ButtonConfigurationItemJPanel
	 */
	public SpinnerSliderJPanel() {
	}

	public void init(int defaultValue, int min, int max, int increment,
						BiConsumer<Integer, Integer> listener )
	{
		super.init();

		_increment = increment;
		_defaultValue = defaultValue;
		_min = min;
		_max = max;

		_listener = listener;

		initComponents_protected();

		initContents();

		setWindowConfiguration();
	}

	protected void initContents()
	{
		jSl_value.setMinimum( _min );
		jSl_value.setMaximum( _max );

		SpinnerModel sm = new SpinnerNumberModel(_defaultValue,
													_min, _max, _increment); //default value,lower bound,upper bound,increment by
		jSp_value.setModel(sm);
		jSp_value.setValue( _defaultValue );
	}

	protected void notify( Integer oldValue, Integer newValue )
	{
		if( ( _listener != null ) &&
			( !Objects.equals( oldValue, _oldValue ) ||
			  !Objects.equals( newValue, _newValue ) )
			&& ( !Objects.equals( _oldValue, newValue ) ||
				!Objects.equals( _newValue, newValue ) )
			)
		{
			_oldValue = oldValue;
			_newValue = newValue;
			_listener.accept(oldValue, newValue);
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
        jSp_value = new javax.swing.JSpinner();
        jSl_value = new javax.swing.JSlider();

        setLayout(null);

        jPanel1.setLayout(null);

        jSp_value.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSp_valueStateChanged(evt);
            }
        });
        jPanel1.add(jSp_value);
        jSp_value.setBounds(10, 5, 75, 20);

        jSl_value.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSl_valueStateChanged(evt);
            }
        });
        jPanel1.add(jSl_value);
        jSl_value.setBounds(90, 5, 235, 26);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 330, 30);
    }// </editor-fold>//GEN-END:initComponents

    private void jSp_valueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSp_valueStateChanged
        // TODO add your handling code here:

		Integer oldValue = getSliderValue();
		Integer newValue = getSpinnerValue();

		updateSliderValue( newValue );
		notify( oldValue, newValue );

    }//GEN-LAST:event_jSp_valueStateChanged

    private void jSl_valueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSl_valueStateChanged
        // TODO add your handling code here:

		Integer oldValue = getSpinnerValue();
		Integer newValue = getSliderValue();

		updateSpinnerValue( newValue );
		notify( oldValue, newValue );

    }//GEN-LAST:event_jSl_valueStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
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

	public Integer getValue()
	{
		return( getSliderValue() );
	}

	public void setValue( Integer value )
	{
		updateSliderValue( value );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jSl_value = compMapper.mapComponent( jSl_value );
		jSp_value = compMapper.mapComponent( jSp_value );
		jPanel1 = compMapper.mapComponent( jPanel1 );

		super.setComponentMapper(compMapper);
	}

	protected void initComponents_protected() {
		initComponents();
	}

	protected JPanel getParentPanel()
	{
		return( jPanel1 );
	}

	@Override
	public void releaseResources()
	{
		_resizeRelocateInfo = null;
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

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			boolean postponeInit = true;
			mapRRCI.putResizeRelocateComponentItem( this, ResizeRelocateItem.FILL_WHOLE_WIDTH, postponeInit );
			mapRRCI.putResizeRelocateComponentItem( getParentPanel(), ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( getParentPanel().getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( getParentPanel().getBounds() );
	}

	@Override
	public SpinnerSliderJPanel getComponent()
	{
		return( this );
	}

	public void setComponentsEnabled( boolean value )
	{
		jSp_value.setEnabled( value );
		jSl_value.setEnabled( value );
	}
}