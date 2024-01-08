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
package com.frojasg1.chesspdfbrowser.view.chess.regex;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.threads.ThreadFunctions;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexConfJDialog  extends InternationalizedJDialog
	implements AcceptCancelRevertControllerInterface
{
	private final static String a_configurationBaseFileName = "RegexConfJDialog";

	protected RegexEditionJPanel _regexEditionPanel = null;
	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected String _initialRegexName = null;

	protected String _initialRegex = null;

	protected RegexWholeFileModel _regexWholeContainer = null;

//	protected BlockRegexBuilder _regexBuilder = null;

//	protected BlockRegexConfigurationContainer _regexConfContainer = null;

	protected RegexNameView _regexNameView = null;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected String _initialBlockToReplaceWith = null;

	protected boolean _validateAtOnce = false;

	/**
	 * Creates new form RegexConfJDialog
	 */
	public RegexConfJDialog( JFrame parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager )
	{
		super(parent, modal, ApplicationConfiguration.instance(), null,
			initializationEndCallBack, true );
		_wholeCompletionManager = wholeCompletionManager;
	}

	public RegexConfJDialog( JDialog parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager )
	{
		super(parent, modal, ApplicationConfiguration.instance(), null,
			initializationEndCallBack, true );
		_wholeCompletionManager = wholeCompletionManager;
	}

	public void init( String regexName, String initialRegex,
						RegexWholeFileModel regexWholeContainer,
						String initialBlockToReplaceWith )
	{
		_initialRegex = ( initialRegex == null ) ? "" : initialRegex;
		_regexWholeContainer = regexWholeContainer;
//		_regexBuilder = regexBuilder;
//		_regexConfContainer = regexConfContainer;
		_initialRegexName = regexName;
		_initialBlockToReplaceWith = initialBlockToReplaceWith;

		initComponents();

		initOwnComponents();

		setWindowConfiguration();

		revert(null);
	}

	public void setValidateAtOnce( boolean validateAtOnce )
	{
		_validateAtOnce = validateAtOnce;
	}

	protected BlockRegexBuilder getRegexBuilder()
	{
		return( _regexWholeContainer.getBlockRegexBuilder() );
	}

	protected BlockRegexConfigurationContainer getRegexConfContainer()
	{
		return( _regexWholeContainer.getBlockConfigurationContainer() );
	}

	protected RegexEditionJPanel createRegexEditionJPanel()
	{
		return( new RegexEditionJPanel( _regexWholeContainer, _wholeCompletionManager,
										_initialBlockToReplaceWith ) );
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	protected RegexNameView createRegexNameJPanel()
	{
		return( null );
	}

	protected void initOwnComponents()
	{
		_regexNameView = createRegexNameJPanel();
		Dimension size = ( _regexNameView == null ) ? new Dimension(0,0) : _regexNameView.getInternalSize();

		if( _regexNameView != null )
		{
			JPanel regexNameJPanel = (JPanel) _regexNameView;
			getContentPane().add( regexNameJPanel );
			regexNameJPanel.setBounds( 0, 0, size.width, size.height );
		}

		_regexEditionPanel = createRegexEditionJPanel();

		jPanel1.add( _regexEditionPanel );
		jPanel1.setBounds( 0, size.height, jPanel1.getWidth(), jPanel1.getHeight() );
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel1, _regexEditionPanel );
//		_regexEditionPanel.setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );

		jPanel3.setBounds( 0, size.height + jPanel1.getHeight(), jPanel3.getWidth(), jPanel3.getHeight() );
		_acceptPanel = createAcceptCancelRevertPanel();

		jPanel4.add( _acceptPanel );
		_acceptPanel.setBounds( 0, 0, jPanel4.getWidth(), jPanel4.getHeight() );
	}

	protected void setWindowConfiguration( )
	{
/*
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
*/


		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			if( _regexNameView != null )
				mapRRCI.putAll( _regexNameView.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( _regexEditionPanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putAll( _regexEditionPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									getConfigurationBaseFileName(),
									this,
									getParent(),
									null,
									true,
									mapRRCI );

		getInternationalization().setMaxWindowHeightNoLimit(false);
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		setMaximumSize( getSize() );
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
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(10000, 10000));
        setName(""); // NOI18N
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 255)));
        jPanel1.setLayout(null);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 630, 190);

        jPanel3.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setMinimumSize(new java.awt.Dimension(130, 50));
        jPanel4.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel4.setLayout(null);
        jPanel3.add(jPanel4);
        jPanel4.setBounds(240, 10, 130, 50);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 190, 630, 70);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );

		_acceptPanel = compMapper.mapComponent( _acceptPanel );
		_regexEditionPanel = compMapper.mapComponent( _regexEditionPanel );
	}

	protected void revertDelayed()
	{
		if( _regexEditionPanel != null )
			ExecutionFunctions.instance().safeMethodExecution( () -> _regexEditionPanel.revert() );
		else
			ThreadFunctions.instance().delayedSafeInvoke( () -> SwingUtilities.invokeLater( () -> revertDelayed() ), 100 );
	}

	@Override
	public void revert(InformerInterface panel)
	{
		_regexEditionPanel.setExpression( _initialRegex );

		revertDelayed();
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		if( _regexNameView != null )
			_regexNameView.validateChanges();

		_regexEditionPanel.validateRegex();
	}

	protected String getInitialRegexName()
	{
		return( _initialRegexName );
	}

	public String getRegexName()
	{
		String result = null;
		if( _regexNameView != null )
			result = _regexNameView.getRegexOrProfileName();

		return( result );
	}

	@Override
	public void accept(InformerInterface panel)
	{
		validateForm();
		if( wasSuccessful() )
		{
			formWindowClosing(true);
		}
	}

	public String getExpression()
	{
		return( _regexEditionPanel.getExpression() );
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		setWasSuccessful( false );

		formWindowClosing(true);
	}

	protected String getConfigurationBaseFileName()
	{
		return( a_configurationBaseFileName );
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();

		_regexEditionPanel.dispose();
	}

	public String getBlockToReplaceWith()
	{
		return( _regexEditionPanel.getBlockToReplaceWith() );
	}

	@Override
	public void setVisible( boolean value )
	{
		if( value && _validateAtOnce )
			SwingUtilities.invokeLater( () -> accept( null ) );

		super.setVisible( value );
	}
}
