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
package com.frojasg1.chesspdfbrowser.view.chess.regex.impl;

import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexNameView;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.text.decorators.RealTimeTextComponentValidatorReactor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.validator.ValidatorReactor;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockRegexOrProfileNameJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements InternallyMappedComponent, InternationalizedStringConf, RegexNameView
{

	public static final String GLOBAL_CONF_FILE_NAME = "BlockRegexOrProfileNameJPanel.properties";

	protected static final String CONF_REGEX_NAME_MUST_BE_A_VALID_FILE_NAME = "REGEX_NAME_MUST_BE_A_VALID_FILE_NAME";
	protected static final String CONF_REGEX_NAME_ALREADY_EXISTS = "REGEX_NAME_ALREADY_EXISTS";
	protected static final String CONF_ERROR_VALIDATING_REGEX_NAME = "ERROR_VALIDATING_REGEX_NAME";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected BlockRegexConfigurationContainer _regexConfContainer = null;

	protected ValidatorReactor _realTimeValidator = null;

	protected String _initialRegexOrProfileName = null;

	protected String _globalConfFileName = null;

	protected Boolean _initialActive = null;

	public BlockRegexOrProfileNameJPanel(BlockRegexConfigurationContainer regexConfContainer,
								String initialBlockRegexOrProfileName,
								Boolean initialActive ) {
		this( regexConfContainer, initialBlockRegexOrProfileName, GLOBAL_CONF_FILE_NAME,
			initialActive );
	}

	/**
	 * Creates new form BlockRegexNameJPanel
	 */
	public BlockRegexOrProfileNameJPanel(BlockRegexConfigurationContainer regexConfContainer,
								String initialRegexName, String globalConfFileName,
								Boolean initialActive ) {
		super.init();

		_internationalizedStringConf = new InternationalizedStringConfImp( globalConfFileName,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );
		initComponents();

		_initialActive = initialActive;
		_initialRegexOrProfileName = initialRegexName;
		_regexConfContainer = regexConfContainer;

		initializeComponentContents();

		setWindowConfiguration( );
	}

	protected void initializeComponentContents()
	{
		if( _initialRegexOrProfileName != null )
		{
			jTF_blockRegexOrProfileName.setText(_initialRegexOrProfileName );
			jTF_blockRegexOrProfileName.setEditable( false );
		}

		jCB_activated.setVisible( _initialActive != null );
		if( _initialActive != null )
			setActive( _initialActive );
	}

	public void setActive( boolean value )
	{
		jCB_activated.setSelected( value );
	}

	public boolean getActive()
	{
		return( jCB_activated.isSelected() );
	}

	public String getInitialName()
	{
		return( _initialRegexOrProfileName );
	}

	protected BlockRegexConfigurationContainer getRegexConfCont()
	{
		return( _regexConfContainer );
	}

	protected RealTimeTextComponentValidatorReactor createRealTimeValidator()
	{
		RealTimeTextComponentValidatorReactor result = new RealTimeTextComponentValidatorReactor(jTF_blockRegexOrProfileName) {
			@Override
			public boolean validate(JTextComponent obj) {
				return( ExecutionFunctions.instance().safeMethodExecution( () -> validateRegexName() ) == null );
			}
		};
		result.init();

		return( result );
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		ExecutionFunctions.instance().safeMethodExecution( () -> registerInternationalizedStrings() );
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
        jL_blockName = new javax.swing.JLabel();
        jTF_blockRegexOrProfileName = new javax.swing.JTextField();
        jCB_activated = new javax.swing.JCheckBox();

        setLayout(null);

        jPanel1.setLayout(null);

        jL_blockName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_blockName.setText("Regex block name :");
        jL_blockName.setName("jL_blockName"); // NOI18N
        jPanel1.add(jL_blockName);
        jL_blockName.setBounds(0, 15, 270, 16);

        jTF_blockRegexOrProfileName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTF_blockRegexOrProfileName.setName("jTF_blockRegexOrProfileName"); // NOI18N
        jPanel1.add(jTF_blockRegexOrProfileName);
        jTF_blockRegexOrProfileName.setBounds(270, 10, 155, 22);

        jCB_activated.setText("activated");
        jCB_activated.setName("jCB_activated"); // NOI18N
        jPanel1.add(jCB_activated);
        jCB_activated.setBounds(425, 10, 100, 24);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 530, 40);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCB_activated;
    private javax.swing.JLabel jL_blockName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTF_blockRegexOrProfileName;
    // End of variables declaration//GEN-END:variables

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_REGEX_NAME_MUST_BE_A_VALID_FILE_NAME, "Regex name must ($1) be a valid file name" );
		registerInternationalString( CONF_REGEX_NAME_ALREADY_EXISTS, "Regex name ($1) already exists" );
		registerInternationalString( CONF_ERROR_VALIDATING_REGEX_NAME, "Error validating regex name : " );
	}

	@Override
	public String getRegexOrProfileName()
	{
		return( jTF_blockRegexOrProfileName.getText() );
	}

	public JTextComponent getNameJTextComponent()
	{
		return( jTF_blockRegexOrProfileName );
	}

	protected boolean checkIfValidRegexName( String regexName )
	{
		return( Objects.equals( regexName, getInitialName() ) || !_regexConfContainer.contains(regexName) );
	}

	public void validateRegexName() throws ValidationException
	{
		String errorMessage = null;
		try
		{
			String regexName = getRegexOrProfileName();
			if( !checkIfValidRegexName( regexName ) )
				errorMessage = createCustomInternationalString( CONF_REGEX_NAME_ALREADY_EXISTS,
																regexName );

			if( errorMessage == null )
			{
				if( ! FileFunctions.instance().isValidFileName(regexName) )
					errorMessage = createCustomInternationalString( CONF_REGEX_NAME_MUST_BE_A_VALID_FILE_NAME,
																	regexName );
			}
		}
		catch( Exception ex )
		{
			errorMessage = getInternationalString( CONF_ERROR_VALIDATING_REGEX_NAME ) + ex.getMessage();
		}

		if( errorMessage != null )
			throw( new ValidationException( errorMessage, jTF_blockRegexOrProfileName ) );
	}

	@Override
	public void validateChanges() throws ValidationException
	{
		validateRegexName();
	}

	@Override
	public Dimension getInternalSize()
	{
		return( jPanel1.getSize() );
	}

	public void dispose()
	{
		if( _realTimeValidator instanceof RealTimeTextComponentValidatorReactor )
		{
			( (RealTimeTextComponentValidatorReactor)_realTimeValidator).dispose();
			_realTimeValidator = null;
		}
	}

	@Override
	public void setRegexOrProfileName(String value)
	{
		jTF_blockRegexOrProfileName.setText( value );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		jL_blockName = compMapper.mapComponent(jL_blockName );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jTF_blockRegexOrProfileName = compMapper.mapComponent(jTF_blockRegexOrProfileName );
		jCB_activated = compMapper.mapComponent(jCB_activated );

		if( !hasBeenAlreadyMapped() )
			_realTimeValidator = createRealTimeValidator();

		super.setComponentMapper(compMapper);
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	@Override
	protected void invertColorsChild(ColorInversor colorInversor)
	{
		super.invertColorsChild( colorInversor );

		( (RealTimeTextComponentValidatorReactor) _realTimeValidator ).invertColors( colorInversor );
	}
}
