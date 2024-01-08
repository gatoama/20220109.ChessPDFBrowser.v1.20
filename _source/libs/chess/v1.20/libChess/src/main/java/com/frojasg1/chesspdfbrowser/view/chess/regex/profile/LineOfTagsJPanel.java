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
package com.frojasg1.chesspdfbrowser.view.chess.regex.profile;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.chesspdfbrowser.model.regex.utils.BlockRegexUtils;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexConfJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexEditionJPanel;
import com.frojasg1.chesspdfbrowser.view.chess.regex.controller.RegexComboControllerForLineOfProfile;
import com.frojasg1.chesspdfbrowser.view.chess.regex.formatter.RegexDocumentFormatterComboForTagsUpdater;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.combobox.MasterComboBoxJPanel;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.text.decorators.RealTimeTextComponentValidatorReactor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LineOfTagsJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements DesktopViewComponent, ComposedComponent, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "LineOfTagsJPanel.properties";

	public static final String CONF_ERROR_VALIDATING_REGEX=RegexEditionJPanel.CONF_ERROR_VALIDATING_REGEX;
	public static final String CONF_IT_IS_A_VALID_REGEX=RegexEditionJPanel.CONF_IT_IS_A_VALID_REGEX;
	public static final String CONF_REGEXES_FOR_LINE=RegexEditionJPanel.CONF_REGEXES_FOR_LINE;
	public static final String CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT=RegexEditionJPanel.CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT;
	public static final String CONF_REGEX_MATCHES_WITH_TEXT=RegexEditionJPanel.CONF_REGEX_MATCHES_WITH_TEXT;
	public static final String CONF_REGEX_MATCH_RESULT=RegexEditionJPanel.CONF_REGEX_MATCH_RESULT;
	public static final String CONF_REGEX_VALIDATION=RegexEditionJPanel.CONF_REGEX_VALIDATION;

	protected static final String CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT = "BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT";

	protected InternationalizedJDialog _parent = null;

	protected ResizeRelocateItem _rriForRegexTextPane = null;

	protected InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																												ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );
	protected BlockRegexBuilder _regexBuilder = null;

//	protected ValidatorReactor _realTimeValidator = null;
	
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected LineModel _lineModel = null;

	protected MasterComboBoxJPanel _masterComboForTags = null;

	protected RegexComboControllerForLineOfProfile _regexComboController = null;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected RegexDocumentFormatterComboForTagsUpdater _regexDocFormatter = null;

	protected ChangeZoomFactorServerInterface _changeZoomFactorServer = null;


	/**
	 * Creates new form LineOfTagsJPanel
	 */
	public LineOfTagsJPanel( BlockRegexBuilder regexBuilder,
						LineModel lineModel,
							WholeCompletionManager wholeCompletionManager,
							ChangeZoomFactorServerInterface changeZoomFactorServer,
							InternationalizedJDialog parent) {
		super.init();

		_parent = parent;

		initComponents();

		_changeZoomFactorServer = changeZoomFactorServer;
		_wholeCompletionManager = wholeCompletionManager;

		ExecutionFunctions.instance().safeMethodExecution( () -> registerInternationalizedStrings() );

		_lineModel = lineModel;
		_regexBuilder = regexBuilder;

		initOwnComponents();

		addListeners();

		initContents();

//		setIndex( index );
		setWindowConfiguration();
	}

	protected void initContents()
	{
		updateIndex();

		jCB_optional.setSelected( _lineModel.isOptional() );
		jTP_regex.setText( _lineModel.getSynchronizationRegexModel().getExpression() );
//		ThreadFunctions.instance().delayedSafeInvoke( () -> SwingUtilities.invokeLater( () -> { jTP_regex.setCaretPosition(0);
//											jTP_regex.setSelectionStart(0);
//											jTP_regex.setSelectionEnd(0); } ),
//							2250 );

//		_parent.executeDelayedTask( () -> ExecutionFunctions.instance().invokeLaterIfNecessary(() -> { jTP_regex.setCaretPosition(0);
//											jTP_regex.setSelectionStart(0);
//											jTP_regex.setSelectionEnd(0);
//		} ),
//			2250);
			
		_regexComboController.updateCombos();
	}

	protected void initOwnComponents()
	{
//		_realTimeValidator = createRealTimeValidator();

		createRegexComboboxes();
		ContainerFunctions.instance().addComponentToCompletelyFillParent(jPanel5, _masterComboForTags );

//		_regexDocFormatter = createRegexDocFormatter();
	}

	protected String getSelectedTag()
	{
		return( (String) _masterComboForTags.getCombo().getSelectedItem() );
	}

	protected RegexDocumentFormatterComboForTagsUpdater createRegexDocFormatter()
	{
//		jTP_regex.setEditorKit( new StyledEditorKit() );
//		jTP_regex.setEditorKit( new StyledEditorKitForTesting() );
		RegexDocumentFormatterComboForTagsUpdater result = null;
		result = new RegexDocumentFormatterComboForTagsUpdater( jTP_regex, _changeZoomFactorServer,
																_lineModel );

		String selectedTag = getSelectedTag();

		result.init( jTP_regex, _masterComboForTags.getCombo(), selectedTag );

		return( result );
	}

	protected void createRegexComboboxes()
	{
		_regexComboController = createRegexComboController();
	}

	protected RegexComboControllerForLineOfProfile createRegexComboController()
	{
		ComboBoxGroupManager tagCbMan = createComboBoxGroupManager();

		_masterComboForTags = createMasterComboBoxJPanel( tagCbMan );

		ProfileModel profileModel = _lineModel.getParent();
		RegexComboControllerForLineOfProfile result = new RegexComboControllerForLineOfProfile();
		result.init(this, profileModel, tagCbMan, _wholeCompletionManager );

//		_masterComboForProfiles = createMasterComboBoxForProfiles();
//		_masterComboForTags = createMasterComboBoxForTags();

		return( result );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jP_definedTagRegexes, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem(jL_regexExpression1, 0 );
			mapRRCI.putResizeRelocateComponentItem(jL_tagsToExtract, 0 );
			mapRRCI.putResizeRelocateComponentItem( jPanel5, 0 );
			mapRRCI.putResizeRelocateComponentItem( _masterComboForTags, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putAll( _masterComboForTags.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem(jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.SKIP_CHANGE_BOUNDS_AND_REPOSITION ); // +
			_rriForRegexTextPane = mapRRCI.putResizeRelocateComponentItem(jTP_regex, ResizeRelocateItem.RESIZE_TO_RIGHT +
																						ResizeRelocateItem.RESIZE_SCROLLABLE_HORIZONTAL_FREE);
			mapRRCI.putResizeRelocateComponentItem( jB_edit, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jCB_optional, ResizeRelocateItem.MOVE_TO_RIGHT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
/*
	protected RealTimeTextComponentValidatorReactor createRealTimeValidator()
	{
		RealTimeTextComponentValidatorReactor result = new RealTimeTextComponentValidatorReactor(jTP_regex) {
			@Override
			public boolean validate(JTextComponent obj) {
				return( ExecutionFunctions.instance().safeMethodExecution( () -> validateRegex() ) == null );
			}
		};
		result.init();

		return( result );
	}
*/
	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jP_definedTagRegexes = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jL_tagsToExtract = new javax.swing.JLabel();
        jL_regexExpression1 = new javax.swing.JLabel();
        jB_edit = new javax.swing.JButton();
        jCB_optional = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTP_regex = new javax.swing.JTextPane();

        setLayout(null);

        jP_definedTagRegexes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Regexes for line - 1"));
        jP_definedTagRegexes.setName(""); // NOI18N
        jP_definedTagRegexes.setLayout(null);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setLayout(null);
        jP_definedTagRegexes.add(jPanel5);
        jPanel5.setBounds(185, 40, 250, 30);

        jL_tagsToExtract.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_tagsToExtract.setText("Tags to extract :");
        jL_tagsToExtract.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jL_tagsToExtract.setName("jL_tagsToExtract"); // NOI18N
        jP_definedTagRegexes.add(jL_tagsToExtract);
        jL_tagsToExtract.setBounds(10, 50, 170, 14);

        jL_regexExpression1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_regexExpression1.setText("Regular expression :");
        jL_regexExpression1.setName("jL_regexExpression1"); // NOI18N
        jP_definedTagRegexes.add(jL_regexExpression1);
        jL_regexExpression1.setBounds(10, 18, 170, 14);

        jB_edit.setName("name=jB_check,icon=com/frojasg1/generic/resources/addremovemodify/modify.png"); // NOI18N
        jB_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_editActionPerformed(evt);
            }
        });
        jP_definedTagRegexes.add(jB_edit);
        jB_edit.setBounds(510, 16, 20, 20);

        jCB_optional.setText("optional");
        jCB_optional.setName("jCB_optional"); // NOI18N
        jP_definedTagRegexes.add(jCB_optional);
        jCB_optional.setBounds(540, 15, 80, 23);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTP_regex.setName("jTP_regex"); // NOI18N
        jTP_regex.setPreferredSize(null);
        jScrollPane1.setViewportView(jTP_regex);

        jP_definedTagRegexes.add(jScrollPane1);
        jScrollPane1.setBounds(185, 15, 320, 22);

        add(jP_definedTagRegexes);
        jP_definedTagRegexes.setBounds(0, 0, 630, 80);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_editActionPerformed
        // TODO add your handling code here:

		editSyncRegex();

    }//GEN-LAST:event_jB_editActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_edit;
    private javax.swing.JCheckBox jCB_optional;
    private javax.swing.JLabel jL_regexExpression1;
    private javax.swing.JLabel jL_tagsToExtract;
    private javax.swing.JPanel jP_definedTagRegexes;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTP_regex;
    // End of variables declaration//GEN-END:variables

	protected ComboBoxGroupManager createComboBoxGroupManager()
	{
		String key = null;
		ChainedParentForChildComboContentServer contentServer = null;
		ChainedParentChildComboBoxManagerBase result = new ChainedParentChildComboBoxManagerBase( key,
																				_lineModel.getComboBoxContent(),
																				contentServer );
		result.init();

		return( result );
	}

	protected MasterComboBoxJPanel createMasterComboBoxJPanel( ComboBoxGroupManager cbgm )
	{
		MasterComboBoxJPanel result = new MasterComboBoxJPanel( cbgm );
		result.init();
//		result.setName( "PruebaJPanel" );

		return( result );
	}

	protected int calculateIndex()
	{
		return( _lineModel.getIndex() );
	}

	public LineModel getLineModel()
	{
		return( _lineModel );
	}

	public void updateIndex()
	{
		( (TitledBorder) jP_definedTagRegexes.getBorder() ).setTitle( calculateTitle() );
	}

	public String calculateTitle()
	{
		int index = calculateIndex();
		return( createCustomInternationalString(CONF_REGEXES_FOR_LINE, index + 1) );
	}

	public String getExpression()
	{
		return( jTP_regex.getText() );
	}

	public void setExpression( String expression )
	{
		jTP_regex.setText( expression );
	}

	protected void validateRegex() throws ValidationException
	{
		if( _regexBuilder != null )
		{
			boolean success = false;
			String errorMessage = null;
			try
			{
				_regexBuilder.getRegexPattern( getExpression(), null );
//				_regexBuilder.getRegexPattern( getExpression() );
				success = true;
			}
			catch( Exception ex )
			{
				errorMessage = ex.getMessage();
			}

			if( ! success )
				throw( new ValidationException( getInternationalString( CONF_ERROR_VALIDATING_REGEX ) +
													errorMessage,
												jTP_regex ) );
		}
	}

	protected RegexConfJDialog createRegexConfJDialog(Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack)
	{
		boolean modal = true;

		RegexConfJDialog result = null;
		Component window = ComponentFunctions.instance().getAncestor( this );
		if( window instanceof JDialog )
			result = new RegexConfJDialog(
					(JDialog) ComponentFunctions.instance().getAncestor( this ),
					modal, initializationEndCallBack, _wholeCompletionManager );
		else
		{
			JFrame frame = null;
			if( window instanceof JFrame )
				frame = (JFrame) window;
			
			result = new RegexConfJDialog( frame, modal, initializationEndCallBack,
				_wholeCompletionManager );
		}
		result.setTitle( calculateTitle() );

		return( result );
	}

	protected void editSyncRegex()
	{
		String initialRegex = null;

//		RegexOfBlockModel rotm = _lineModel.getSynchronizationRegexModel();

		initialRegex = jTP_regex.getText();

		RegexConfJDialog dial = createRegexConfJDialog(this::editSyncRegexCallback);
		dial.init(null, initialRegex, getLineModel().getRegexWholeContainer(), null );
	}

	protected void editSyncRegexCallback(InternationalizationInitializationEndCallback iiec)
	{
		String result = null;

		RegexConfJDialog dial = (RegexConfJDialog) iiec;

		dial.setVisible(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

			jTP_regex.setText( dial.getExpression() );
//			rotm.setExpression( dial.getExpression() );
		}

//		return( result );
	}

	public void dispose()
	{
/*
		if( _realTimeValidator instanceof RealTimeTextComponentValidatorReactor )
		{
			( (RealTimeTextComponentValidatorReactor)_realTimeValidator).dispose();
			_realTimeValidator = null;
		}
*/
		if( _regexComboController != null )
			_regexComboController.dispose();
		_regexComboController = null;

		if( _masterComboForTags != null )
			_masterComboForTags.dispose();
		_masterComboForTags = null;

		if( _wholeCompletionManager != null )
			_wholeCompletionManager.setInputTextCompletionManager( getRegexWholeContainer(), null, null );
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

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_REGEX_VALIDATION, "Regex validation" );
		registerInternationalString( CONF_IT_IS_A_VALID_REGEX, "It is a valid regex" );

		registerInternationalString( CONF_REGEX_MATCH_RESULT, "Regex match result" );
		registerInternationalString( CONF_REGEX_MATCHES_WITH_TEXT, "Regex matches with text" );
		registerInternationalString( CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT, "Regex does not match with text" );
		registerInternationalString( CONF_ERROR_VALIDATING_REGEX, "Error validating regex : " );
		registerInternationalString( CONF_REGEXES_FOR_LINE, "Regexes for line - $1" );
		registerInternationalString( CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT, "Block to be replaced to get tag is not present. BlockName: $1" );
	}

	@Override
	public LineOfTagsJPanel getComponent()
	{
		return( this );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jB_edit = compMapper.mapComponent(jB_edit );
		jL_regexExpression1 = compMapper.mapComponent(jL_regexExpression1 );
		jL_tagsToExtract = compMapper.mapComponent(jL_tagsToExtract );
		jP_definedTagRegexes = compMapper.mapComponent( jP_definedTagRegexes );
		jPanel5 = compMapper.mapComponent( jPanel5 );
		jTP_regex = compMapper.mapComponent(jTP_regex );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );

		_masterComboForTags = compMapper.mapComponent( _masterComboForTags );

		SwingUtilities.invokeLater( () -> postInit(compMapper) );

		super.setComponentMapper(compMapper);
	}

	protected void postInit(ComponentMapper compMapper)
	{
		if( _regexDocFormatter == null )
		{
			_regexDocFormatter = createRegexDocFormatter();

			_regexDocFormatter.setCombo(_masterComboForTags.getCombo());
			_regexDocFormatter.setNewJTextPane(jTP_regex);
			SwingUtilities.invokeLater( () -> jScrollPane1.setViewportView(jTP_regex) );

			if( isDarkMode() )
				invertFormattersColor( getColorInversor() );

//			_rriForRegexTextPane.setSizeChangedObserved( _regexDocFormatter );

			_regexComboController.setComponentMapper(compMapper);

//			for( KeyListener listener: jTP_regex.getKeyListeners() )
//				jTP_regex.removeKeyListener(listener);
/*
			ThreadFunctions.instance().delayedInvokeEventDispatchThread(() -> {
														Dimension dimen = jScrollPane1.getSize();
														dimen.width++;
														jScrollPane1.setSize( dimen ); 
														},
								1600 );
*/
		}
	}

	public void shrinkTextComponent()
	{
//		jScrollPane1.setViewportView(jTP_regex);
		_rriForRegexTextPane.setSizeChangedObserved( _regexDocFormatter );

//		Dimension dimen = jScrollPane1.getSize();
//		dimen.width++;
//		jScrollPane1.setSize( dimen ); 
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void applyChangesWithoutValidation()
	{
		_lineModel.getSynchronizationRegexModel().setExpression( getExpression() );
		_lineModel.setOptional( jCB_optional.isSelected() );
	}

	public void applyChanges() throws ValidationException
	{
		validateChanges();

		applyChangesWithoutValidation();
	}

	public void validateChanges() throws ValidationException
	{
		validateRegex();
		validateTagReplacements();
		
		_lineModel.setSynchronizationExpression( getExpression() );
		_lineModel.validate();
	}

	@Override
	public Dimension getInternalSize()
	{
		return( jP_definedTagRegexes.getSize() );
	}

	protected RegexWholeFileModel getRegexWholeContainer()
	{
		return( _lineModel.getParent().getParent() );
	}

	protected boolean blockIsPresent( String expression, String blockToFind )
	{
		return( BlockRegexUtils.instance().blockIsPresent(expression, blockToFind) );
	}

	protected void validateTagReplacements() throws ValidationException
	{
		String expression = getExpression();
		for( Map.Entry<String, TagReplacementModel> entry: _lineModel.getMap().entrySet() )
		{
			if( !blockIsPresent( expression, entry.getValue().getBlockToReplaceWith() ) )
				throw( new ValidationException(  createCustomInternationalString( CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT,  entry.getKey() ),
												_masterComboForTags.getCombo() ) );
		}
	}

	protected void addListeners()
	{
		if( _wholeCompletionManager != null )
		{
			jTP_regex.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained( FocusEvent evt )
				{
					if( evt.getComponent() == jTP_regex )
					{
						JTextComponent textComp = _wholeCompletionManager.getTextComponent();
						if( textComp != jTP_regex )
							_wholeCompletionManager.setInputTextCompletionManager(getRegexWholeContainer(),
																		jTP_regex,
																		LineOfTagsJPanel.this );
					}
				}
			});
		}
	}

	@Override
	public Rectangle getInternalBounds() {
		return( jP_definedTagRegexes.getBounds() );
	}

	@Override
	public void releaseResources() {
		_rriForRegexTextPane = null;
		_internationalizedStringConf = null;
		_regexBuilder = null;
		_resizeRelocateInfo = null;
		_lineModel = null;
		_masterComboForTags = null;
		_regexComboController = null;
		_wholeCompletionManager = null;
		_regexDocFormatter = null;
		_changeZoomFactorServer = null;
	}

	protected void invertFormattersColor(ColorInversor colorInversor)
	{
		if( _regexDocFormatter != null )
			_regexDocFormatter.invertColors(colorInversor);
	}

	@Override
	protected void invertColorsChild(ColorInversor colorInversor)
	{
		super.invertColorsChild( colorInversor );

		invertFormattersColor(colorInversor);
//		( (RealTimeTextComponentValidatorReactor) _realTimeValidator ).invertColors( colorInversor );
	}
}
