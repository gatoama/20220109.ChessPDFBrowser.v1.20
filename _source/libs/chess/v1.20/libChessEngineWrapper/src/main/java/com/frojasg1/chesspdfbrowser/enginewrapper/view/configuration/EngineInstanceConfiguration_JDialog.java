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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.inspection.EngineInspectResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.inspection.EngineInspectorComplex;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.updater.EngineInstanceConfigurationUpdater;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.generic.dialogs.impl.DesktopFileChooserParameters;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.text.decorators.ActionPerformedTextComponentExecutor;
import com.frojasg1.general.desktop.view.text.decorators.RealTimeTextComponentValidatorReactor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import com.frojasg1.general.os.OSValidator;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.xml.persistency.container.SimpleMapContainerOfModels;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInstanceConfiguration_JDialog extends InternationalizedJDialog
	implements AcceptCancelRevertControllerInterface
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public static final String a_configurationBaseFileName = "EngineInstanceConfiguration_JDialog";

	protected static final String CONF_COULD_NOT_VALIDATE_NAME = "COULD_NOT_VALIDATE_NAME";
	protected static final String CONF_EXE_FILTER_DESCRIPTION = "EXE_FILTER_DESCRIPTION";
	protected static final String CONF_ONLY_UCI_TYPE_IS_SUPPORTED = "ONLY_UCI_TYPE_IS_SUPPORTED";
	protected static final String CONF_IT_DOES_NOT_SEEM_TO_BE_AN_UCI_ENGINE = "IT_DOES_NOT_SEEM_TO_BE_AN_UCI_ENGINE";

	public static final String CONF_NEW_ENGINE = "NEW_ENGINE";
	public static final String CONF_VIEW_ENGINE = "VIEW_ENGINE";

	protected EngineInstanceConfiguration _model = null;
	protected EngineInstanceConfiguration _result = null;

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected ActionPerformedTextComponentExecutor _commandActionPerformedExecutor = null;

	protected RealTimeTextComponentValidatorReactor _realTimeValidatorForName = null;
	protected SimpleMapContainerOfModels<String, EngineInstanceConfiguration> _containerOfElements = null;

	protected EngineInspectorComplex _engineInspector = null;

	protected boolean _nameModifiedByUser = false;

	protected ReentrantLock _lockForInspection = new ReentrantLock(true);
	protected Condition _waitingForInspectionToFinish = _lockForInspection.newCondition();
	protected volatile int _numOfPendingInspections = 0;

	/**
	 * Creates new form EngineInstanceConfiguration_JDialog
	 */
	public EngineInstanceConfiguration_JDialog( JFrame parent, boolean modal,
							BaseApplicationConfigurationInterface applicationConfiguration )
	{
		super(parent, modal, applicationConfiguration );
	}

	public EngineInstanceConfiguration_JDialog( JDialog parent, boolean modal,
							BaseApplicationConfigurationInterface applicationConfiguration )
	{
		super(parent, modal, applicationConfiguration );
	}

	public void init( SimpleMapContainerOfModels<String, EngineInstanceConfiguration> container,
						EngineInstanceConfiguration model )
	{
		_containerOfElements = container;
		_model = model;
		_result = copyModel( _model );

		_engineInspector = createEngineInspectorComplex();
		_engineInspector.init( 2, 2 );

//		confirm();
////		_profileModel.save();

		initComponents();

		initOwnComponents();

		addListeners();

		initContents();

		setWindowConfiguration();
	}

	protected EngineInstanceConfiguration copyModel( EngineInstanceConfiguration model )
	{
		EngineInstanceConfiguration result = null;
		if( model != null )
			result = _copier.copy( model );
		else
			result = createEmptyEngineInstanceConfiguration();

		return( result );
	}

	protected EngineInstanceConfiguration createEmptyEngineInstanceConfiguration()
	{
		EngineInstanceConfiguration result = new EngineInstanceConfiguration();

		return( result );
	}

	protected EngineInspectorComplex createEngineInspectorComplex()
	{
		return( new EngineInspectorComplex() );
	}

	protected void addListeners()
	{
//		addListenersForMouseToHierarchy( getContentPane() );
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	public EngineInstanceConfiguration getResult()
	{
		return( _result );
	}

	protected void initOwnComponents()
	{
		_acceptPanel = createAcceptCancelRevertPanel();
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel4, _acceptPanel );
	}

	protected void resetContents()
	{
		jTF_name.setText( "" );
		jTF_commandFileChooser.setText( "" );
		jRB_uci.setSelected( false );
		jRB_xboard.setSelected( false );
	}

	protected void initContents()
	{
		resetContents();
		if( _model != null )
		{
			String name = "";
			if( _model.getName() != null )
				name = _model.getName();
			jTF_name.setText( name );

			jTF_commandFileChooser.setText( _model.getEngineCommandForLaunching() );

			if( _model.getEngineType() == EngineInstanceConfiguration.UCI )
				jRB_uci.setSelected( true );
			else if( _model.getEngineType() == EngineInstanceConfiguration.XBOARD )
				jRB_xboard.setSelected( true );
		}

		boolean isNew = isNew();
		jTF_name.setEnabled( isNew );
		jTF_commandFileChooser.setEnabled( isNew );
		jRB_uci.setEnabled( isNew );
		jRB_xboard.setEnabled( isNew );
	}

	protected boolean isNew()
	{
		return( _model == null );
	}

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( jB_commandFileChooser, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jTF_commandFileChooser, ResizeRelocateItem.RESIZE_TO_RIGHT );

			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									LibConstants.sa_PROPERTIES_PATH_IN_JAR,
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);
/*
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI );
*/
		registerInternationalString( CONF_COULD_NOT_VALIDATE_NAME, "Could not validate name [$1]" );
		registerInternationalString( CONF_EXE_FILTER_DESCRIPTION, ".exe : executable binary" );
		registerInternationalString( CONF_ONLY_UCI_TYPE_IS_SUPPORTED, "Only Uci type is supported" );
		registerInternationalString( CONF_IT_DOES_NOT_SEEM_TO_BE_AN_UCI_ENGINE, "It does not seem to be an Uci engine" );
		registerInternationalString( CONF_NEW_ENGINE, "New engine" );
		registerInternationalString( CONF_VIEW_ENGINE, "Engine configuration" );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();
		setMaximumSize( getSize() );
		getInternationalization().setMaxWindowHeightNoLimit(false);
	}
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_engineType = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jL_name = new javax.swing.JLabel();
        jTF_name = new javax.swing.JTextField();
        jL_command = new javax.swing.JLabel();
        jTF_commandFileChooser = new javax.swing.JTextField();
        jB_commandFileChooser = new javax.swing.JButton();
        jP_engineType = new javax.swing.JPanel();
        jRB_uci = new javax.swing.JRadioButton();
        jRB_xboard = new javax.swing.JRadioButton();
        jB_engineConfiguration = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setLayout(null);
        jPanel1.add(jPanel4);
        jPanel4.setBounds(225, 150, 140, 50);

        jL_name.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_name.setText("Name :");
        jL_name.setName("jL_name"); // NOI18N
        jPanel1.add(jL_name);
        jL_name.setBounds(15, 15, 80, 16);

        jTF_name.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTF_nameKeyReleased(evt);
            }
        });
        jPanel1.add(jTF_name);
        jTF_name.setBounds(100, 15, 230, 24);

        jL_command.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_command.setText("Command :");
        jL_command.setName("jL_command"); // NOI18N
        jPanel1.add(jL_command);
        jL_command.setBounds(5, 45, 90, 16);

        jTF_commandFileChooser.setName(""); // NOI18N
        jPanel1.add(jTF_commandFileChooser);
        jTF_commandFileChooser.setBounds(100, 45, 420, 24);

        jB_commandFileChooser.setText("...");
        jB_commandFileChooser.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_commandFileChooser.setName("jB_commandFileChooser"); // NOI18N
        jB_commandFileChooser.setPreferredSize(new java.awt.Dimension(20, 20));
        jB_commandFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_commandFileChooserActionPerformed(evt);
            }
        });
        jPanel1.add(jB_commandFileChooser);
        jB_commandFileChooser.setBounds(525, 45, 20, 20);

        jP_engineType.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Engine Type"));
        jP_engineType.setName("jP_engineType"); // NOI18N
        jP_engineType.setLayout(null);

        buttonGroup_engineType.add(jRB_uci);
        jRB_uci.setText("UCI");
        jRB_uci.setName("jRB_uci"); // NOI18N
        jP_engineType.add(jRB_uci);
        jRB_uci.setBounds(35, 14, 93, 28);

        buttonGroup_engineType.add(jRB_xboard);
        jRB_xboard.setText("Xboard");
        jRB_xboard.setName("jRB_xboard"); // NOI18N
        jP_engineType.add(jRB_xboard);
        jRB_xboard.setBounds(35, 34, 93, 28);

        jPanel1.add(jP_engineType);
        jP_engineType.setBounds(100, 75, 145, 67);

        jB_engineConfiguration.setName("name=jB_engineConfiguration,icon=com/frojasg1/generic/resources/othericons/configuration.png"); // NOI18N
        jB_engineConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_engineConfigurationActionPerformed(evt);
            }
        });
        jPanel1.add(jB_engineConfiguration);
        jB_engineConfiguration.setBounds(280, 90, 40, 40);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 555, 215);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_commandFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_commandFileChooserActionPerformed
        // TODO add your handling code here:

        String fileName = showFileChooserDialog();
        if( fileName != null )
		{
			_result.setChessEngineConfiguration(null);
			jTF_commandFileChooser.setText( fileName );
		}

    }//GEN-LAST:event_jB_commandFileChooserActionPerformed

    private void jB_engineConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_engineConfigurationActionPerformed
        // TODO add your handling code here:

		launchConfigurationDialog();

    }//GEN-LAST:event_jB_engineConfigurationActionPerformed

    private void jTF_nameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTF_nameKeyReleased
        // TODO add your handling code here:

		_nameModifiedByUser = true;

    }//GEN-LAST:event_jTF_nameKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_engineType;
    private javax.swing.JButton jB_commandFileChooser;
    private javax.swing.JButton jB_engineConfiguration;
    private javax.swing.JLabel jL_command;
    private javax.swing.JLabel jL_name;
    private javax.swing.JPanel jP_engineType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRB_uci;
    private javax.swing.JRadioButton jRB_xboard;
    private javax.swing.JTextField jTF_commandFileChooser;
    private javax.swing.JTextField jTF_name;
    // End of variables declaration//GEN-END:variables

	protected String showFileChooserDialog( )
	{
		List<FilterForFile> ffl = null;

		if( OSValidator.isWindows() )
		{
			FilterForFile fnef1 = new FilterForFile( getInternationalString(CONF_EXE_FILTER_DESCRIPTION),
													"exe" );
			ffl = new ArrayList<FilterForFile>();
			ffl.add( fnef1 );
		}

		FileChooserParameters fcp = new FileChooserParameters();
		fcp.setListOfFilterForFile(ffl);
		fcp.setMode( DesktopFileChooserParameters.FILES_ONLY );
		fcp.setOpenOrSaveDialog(FileChooserParameters.OPEN);
		GenericFileFilter execFilter = GenericFunctions.instance().getDialogsWrapper().getGenericFileFilterChooser().getGenericFileFilter(DialogsWrapper.GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES);
		fcp.setGenericFileFilter( execFilter );

		String fileName = GenericFunctions.instance().getDialogsWrapper().showFileChooserDialog( this, fcp,
																						getAppliConf() );

		return( fileName );
	}

	@Override
	public void releaseResources()
	{
		_realTimeValidatorForName.dispose();

//		_engineInspector.stop();
		_engineInspector.dispose();

		super.releaseResources();
	}

	protected boolean validateName()
	{
		Exception ex = ExecutionFunctions.instance().safeMethodExecution( () -> validateName_exception() );

		return( ex == null );
	}

	protected void validateName_exception() throws ValidationException
	{
		boolean result = false;
		String text = ExecutionFunctions.instance().safeFunctionExecution( () -> jTF_name.getDocument().getText(0, jTF_name.getDocument().getLength() ) );
		if( text != null )
			result = !nameExists(text);

		if( ! result )
			throw( new ValidationException( createCustomInternationalString( CONF_COULD_NOT_VALIDATE_NAME, text ),
					jTF_name ) );
	}

	protected RealTimeTextComponentValidatorReactor createRealTimeValidator()
	{
		RealTimeTextComponentValidatorReactor result = new RealTimeTextComponentValidatorReactor(jTF_name) {
			@Override
			public boolean validate(JTextComponent obj) {
				return( !isNew() || ExecutionFunctions.instance().safeMethodExecution( () -> validateName() ) == null );
			}
		};
		result.init();

		return( result );
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jB_commandFileChooser = compMapper.mapComponent( jB_commandFileChooser );
		jB_engineConfiguration = compMapper.mapComponent( jB_engineConfiguration );
		jL_command = compMapper.mapComponent( jL_command );
		jL_name = compMapper.mapComponent( jL_name );
		jP_engineType = compMapper.mapComponent( jP_engineType );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jRB_uci = compMapper.mapComponent( jRB_uci );
		jRB_xboard = compMapper.mapComponent( jRB_xboard );
		jTF_commandFileChooser = compMapper.mapComponent( jTF_commandFileChooser );
		jTF_name = compMapper.mapComponent( jTF_name );

		_realTimeValidatorForName = createRealTimeValidator();
		_commandActionPerformedExecutor = createActionPerformedTextComponentExecutor();
	}

	protected ActionPerformedTextComponentExecutor createActionPerformedTextComponentExecutor()
	{
		ActionPerformedTextComponentExecutor result = new ActionPerformedTextComponentExecutor( jTF_commandFileChooser ) {
					@Override
					public void execute()
					{
						if( isNew() )
							inspectEngine( getText() );
					}
		};
		
		result.init();

		return( result );
	}

	protected void inspectEngine( String command )
	{
		try
		{
			_lockForInspection.lock();

			_numOfPendingInspections++;
			_engineInspector.inspectEngine(command, (res) -> processEngineInspection(res ) );
		}
		finally
		{
			_lockForInspection.unlock();
		}
	}

	protected int getNumOfPendingInspections()
	{
		try
		{
			_lockForInspection.lock();

			return( _numOfPendingInspections );
		}
		finally
		{
			_lockForInspection.unlock();
		}
	}

	protected void processEngineInspection_internal( EngineInspectResult result )
	{
		if( result != null )
		{
			if( !result.isUci() && !result.isXboard() )
			{
				
			}
			else
			{
				if( result.isUci() )
					jRB_uci.setSelected( true );
				else if( result.isXboard() )
					jRB_xboard.setSelected( true );

				if( !_nameModifiedByUser && ! isEmpty( result.getName() ) )
					jTF_name.setText( calculateNewName( result.getName() ) );

				ChessEngineConfiguration cec = result.getChessEngineConfiguration();
				if( cec != null )
					_result.setChessEngineConfiguration( cec );
			}
		}
	}

	protected boolean isEmpty( String str )
	{
		return( StringFunctions.instance().isEmpty( str ) );
	}

	protected String calculateNewName( String baseName )
	{
		String result = baseName;

		int index = 1;
		while( nameExists( result ) )
		{
			result = baseName + "-" + index;
			index++;
		}

		return( result );
	}

	protected boolean nameExists( String name )
	{
		return( _containerOfElements.get(name) != null );
	}

	protected void processEngineInspection( EngineInspectResult result )
	{
		try
		{
			_lockForInspection.lock();

			SwingUtilities.invokeLater( () -> processEngineInspection_internal( result ) );

			_numOfPendingInspections--;

			if( ( _numOfPendingInspections == 0 ) &&
				( _lockForInspection.hasWaiters( _waitingForInspectionToFinish ) ) )
			{
				_waitingForInspectionToFinish.signalAll();
			}
		}
		finally
		{
			_lockForInspection.unlock();
		}
	}

	protected void launchConfigurationDialog()
	{
		try
		{
			_lockForInspection.lock();

			waitForInspectionToFinish();

			SwingUtilities.invokeLater( () -> launchConfigurationDialog_internal() );
		}
		finally
		{
			_lockForInspection.unlock();
		}
	}

	protected void waitForInspectionToFinish()
	{
		try
		{
			_lockForInspection.lock();

			while( _numOfPendingInspections > 0 )
			{
				try
				{
					_waitingForInspectionToFinish.await( 3000, TimeUnit.MILLISECONDS );
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}
		finally
		{
			_lockForInspection.unlock();
		}
	}

	protected void launchConfigurationDialog_internal()
	{
		EngineInstanceConfigurationUpdater confUpdat = new EngineInstanceConfigurationUpdater(this);

		confUpdat.init( (ChessEngineConfigurationPersistency) null, getAppliConf() );

		confUpdat.launchConfigurationDialog( _result );
	}
/*
	protected void launchConfigurationDialog_internal()
	{
		ChessEngineConfiguration model = _result.getChessEngineConfiguration();
		if( ( model != null ) && ( _result.getChessEngineConfiguration() != null ) )
		{
			ChessEngineConfiguration_JDialog dial = new ChessEngineConfiguration_JDialog(
						this, true, getAppliConf(), iiec -> processConfigurationDialog( iiec ) );
			dial.init( _result.getChessEngineConfiguration() );
		}
	}

	protected void processConfigurationDialog( InternationalizationInitializationEndCallback iiec )
	{
		ChessEngineConfiguration_JDialog dial = (ChessEngineConfiguration_JDialog) iiec;

		dial.setVisibleWithLock( true );

		if( dial.wasSuccessful() )
			_result.setChessEngineConfiguration( dial.getResult() );
	}
*/
	protected void validateCommand() throws ValidationException
	{
		waitForInspectionToFinish();
	}

	protected void validateTypeOfEngine() throws ValidationException
	{
		if( ! jRB_uci.isSelected() )
		{
			throw( new ValidationException( getInternationalString( CONF_ONLY_UCI_TYPE_IS_SUPPORTED ),
											jP_engineType ) );
		}
	}

	protected boolean isConfigurationValid()
	{
		boolean result = ( _result != null ) &&
						( _result.getChessEngineConfiguration() != null );

		return( result );
	}

	protected void validateEngineConfiguration() throws ValidationException
	{
		if( !isConfigurationValid() )
		{
			_result.setChessEngineConfiguration(null);
			inspectEngine( jTF_commandFileChooser.getText() );
			waitForInspectionToFinish();

			if( !isConfigurationValid() )
			{
				throw( new ValidationException( getInternationalString(CONF_IT_DOES_NOT_SEEM_TO_BE_AN_UCI_ENGINE),
												jTF_commandFileChooser ) );
			}
		}
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		if( jTF_name.isEnabled() )
			validateName_exception();

		validateCommand();
		validateEngineConfiguration();
		validateTypeOfEngine();
	}

	protected void applyChanges()
	{
		EngineInstanceConfiguration result = getResult();
		result.setName( jTF_name.getText() );

		result.setEngineCommandForLaunching( jTF_commandFileChooser.getText() );
		if( jRB_uci.isSelected() )
			result.setEngineType( EngineInstanceConfiguration.UCI );
		else if( jRB_xboard.isSelected() )
			result.setEngineType( EngineInstanceConfiguration.XBOARD );
	}

	@Override
	public void revert(InformerInterface panel)
	{
		_result = copyModel( _model );
		initContents();
	}

	@Override
	public void accept(InformerInterface panel)
	{
		validateForm();
		if( wasSuccessful() )
		{
			applyChanges();
			formWindowClosing(true);
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		setWasSuccessful( false );

		formWindowClosing(true);
	}

	@Override
	public void setMinimumSize( Dimension dimen )
	{
		super.setMinimumSize(dimen);
	}

	@Override
	public void setDarkMode(boolean value, ColorInversor colorInversor)
	{
		super.setDarkMode( value, colorInversor );

		if( hasToInvertColors(value) )
			_realTimeValidatorForName.invertColors(colorInversor);
	}
}
