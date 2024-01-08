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

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindowFunctions;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.formatter.RegexDocumentFormatterComboForBlockToReplaceWithUpdater;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexEditionJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase 
		implements DesktopViewComponent, InternallyMappedComponent, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexEditionJPanel.properties";

	public static final String CONF_VALIDATION_ERROR = "VALIDATION_ERROR";

	public static final String CONF_REGEX_VALIDATION = "REGEX_VALIDATION";
	public static final String CONF_IT_IS_A_VALID_REGEX = "IT_IS_A_VALID_REGEX";

	public static final String CONF_REGEX_MATCH_RESULT = "REGEX_MATCH_RESULT";
	public static final String CONF_REGEX_MATCHES_WITH_TEXT = "REGEX_MATCHES_WITH_TEXT";
	public static final String CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT = "REGEX_DOES_NOT_MATCH_WITH_TEXT";
	public static final String CONF_ERROR_VALIDATING_REGEX = "ERROR_VALIDATING_REGEX";

	public static final String CONF_REGEXES_FOR_LINE = "REGEXES_FOR_LINE";

	public static final String CONF_ERROR_VALIDATING_BLOCK_TO_GET_TAG = "ERROR_VALIDATING_BLOCK_TO_GET_TAG";
	public static final String CONF_ERROR_REGEX_DOES_NOT_HAVE_ANY_BLOCK_TO_EXTRACT_TAG_FROM = "ERROR_REGEX_DOES_NOT_HAVE_ANY_BLOCK_TO_EXTRACT_TAG_FROM";

	protected InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																												ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );
	protected RegexWholeFileModel _regexWholeContainer = null;

	protected ResizeRelocateItem _rriForRegexTextPane = null;

	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected ApplicationConfiguration _appliConf = null;

//	protected BlockRegexBuilder _regexBuilder = null;

	protected Pattern _pattern = null;

//	protected ValidatorReactor _realTimeValidator = null;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected RegexDocumentFormatterComboForBlockToReplaceWithUpdater _regexDocFormatter = null;

	protected String _initialBlockNameToReplaceWith = null;

	public RegexEditionJPanel( RegexWholeFileModel regexWholeContainer,
						WholeCompletionManager wholeCompletionManager,
								String initialBlockNameToReplaceWith ) {
		this( regexWholeContainer, ApplicationConfiguration.instance(), wholeCompletionManager,
			initialBlockNameToReplaceWith );
	}

	/**
	 * Creates new form RegexEditionJPanel
	 */
	public RegexEditionJPanel(RegexWholeFileModel regexWholeContainer,
								ApplicationConfiguration appliConf,
								WholeCompletionManager wholeCompletionManager,
								String initialBlockNameToReplaceWith ) {
		super.init();

		initComponents();

		_regexWholeContainer = regexWholeContainer;
		_initialBlockNameToReplaceWith = initialBlockNameToReplaceWith;

		_wholeCompletionManager = wholeCompletionManager;

//		_regexBuilder = regexBuilder;
		_appliConf = appliConf;

		initOwnComponents();

		initializeContents();

		setWindowConfiguration( );
	}

	protected BlockRegexBuilder getRegexBuilder()
	{
		return( _regexWholeContainer.getBlockRegexBuilder() );
	}

	protected void initializeContents()
	{
		jCB_autocompletion.setSelected( getAppliConf().isAutocompletionForRegexActivated() );
	}

	protected void initOwnComponents()
	{
//		_regexDocFormatter = createRegexDocFormatter();

//		_realTimeValidator = createRealTimeValidator();
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
	public void dispose()
	{
/*
		if( _realTimeValidator instanceof RealTimeTextComponentValidatorReactor )
		{
			( (RealTimeTextComponentValidatorReactor)_realTimeValidator).dispose();
			_realTimeValidator = null;
		}
*/
		if( _wholeCompletionManager != null )
			_wholeCompletionManager.setInputTextCompletionManager( _regexWholeContainer, null, null );
	}

	public ApplicationConfiguration getAppliConf()
	{
		return( _appliConf );
	}

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
			mapRRCI.putResizeRelocateComponentItem( jP_regexEdition, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( jB_check, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jB_matches, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.SKIP_CHANGE_BOUNDS_AND_REPOSITION );
			_rriForRegexTextPane = mapRRCI.putResizeRelocateComponentItem(jTP_regex, ResizeRelocateItem.RESIZE_TO_RIGHT +
																						ResizeRelocateItem.RESIZE_SCROLLABLE_HORIZONTAL_FREE );
			mapRRCI.putResizeRelocateComponentItem( jTF_textToCheck, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jTF_result, ResizeRelocateItem.RESIZE_TO_RIGHT );
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

        jP_regexEdition = new javax.swing.JPanel();
        jL_regexExpression = new javax.swing.JLabel();
        jB_check = new javax.swing.JButton();
        jCB_autocompletion = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jL_checkRegularExpression = new javax.swing.JLabel();
        jTF_textToCheck = new javax.swing.JTextField();
        jL_text = new javax.swing.JLabel();
        jB_replace = new javax.swing.JButton();
        jTF_result = new javax.swing.JTextField();
        jL_result = new javax.swing.JLabel();
        jB_matches = new javax.swing.JButton();
        jCB_blockToReplaceWith = new javax.swing.JComboBox<>();
        jL_blockToReplaceWith = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTP_regex = new javax.swing.JTextPane();

        setName(""); // NOI18N
        setLayout(null);

        jP_regexEdition.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Regex edition"));
        jP_regexEdition.setName("jP_regexEdition"); // NOI18N
        jP_regexEdition.setLayout(null);

        jL_regexExpression.setText("Regular expression :");
        jL_regexExpression.setName("jL_regexExpression"); // NOI18N
        jP_regexEdition.add(jL_regexExpression);
        jL_regexExpression.setBounds(10, 40, 200, 14);

        jB_check.setName("name=jB_check,icon=com/frojasg1/generic/resources/othericons/check.png"); // NOI18N
        jB_check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_checkActionPerformed(evt);
            }
        });
        jP_regexEdition.add(jB_check);
        jB_check.setBounds(600, 35, 20, 23);

        jCB_autocompletion.setText("activate auto completion");
        jCB_autocompletion.setName("jCB_autocompletion"); // NOI18N
        jCB_autocompletion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_autocompletionActionPerformed(evt);
            }
        });
        jP_regexEdition.add(jCB_autocompletion);
        jCB_autocompletion.setBounds(315, 10, 275, 23);
        jP_regexEdition.add(jSeparator1);
        jSeparator1.setBounds(25, 85, 590, 10);

        jL_checkRegularExpression.setText("Check regular expression replacement");
        jL_checkRegularExpression.setName("jL_checkRegularExpression"); // NOI18N
        jP_regexEdition.add(jL_checkRegularExpression);
        jL_checkRegularExpression.setBounds(160, 90, 350, 14);
        jP_regexEdition.add(jTF_textToCheck);
        jTF_textToCheck.setBounds(210, 110, 380, 20);

        jL_text.setText("Text to check :");
        jL_text.setName("jL_text"); // NOI18N
        jP_regexEdition.add(jL_text);
        jL_text.setBounds(10, 115, 200, 14);

        jB_replace.setName("name=jB_replace,icon=com/frojasg1/generic/resources/othericons/replace.png"); // NOI18N
        jB_replace.setPreferredSize(new java.awt.Dimension(33, 23));
        jB_replace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_replaceActionPerformed(evt);
            }
        });
        jP_regexEdition.add(jB_replace);
        jB_replace.setBounds(370, 133, 20, 23);

        jTF_result.setEditable(false);
        jTF_result.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTF_result.setForeground(new java.awt.Color(102, 102, 255));
        jP_regexEdition.add(jTF_result);
        jTF_result.setBounds(210, 160, 380, 20);

        jL_result.setText("Replacement result :");
        jL_result.setName("jL_result"); // NOI18N
        jP_regexEdition.add(jL_result);
        jL_result.setBounds(10, 165, 200, 14);

        jB_matches.setName("name=jB_matches,icon=com/frojasg1/generic/resources/othericons/match.png"); // NOI18N
        jB_matches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_matchesActionPerformed(evt);
            }
        });
        jP_regexEdition.add(jB_matches);
        jB_matches.setBounds(600, 110, 20, 23);

        jCB_blockToReplaceWith.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jP_regexEdition.add(jCB_blockToReplaceWith);
        jCB_blockToReplaceWith.setBounds(210, 60, 240, 20);

        jL_blockToReplaceWith.setText("Block to replace with :");
        jL_blockToReplaceWith.setName("jL_blockToReplaceWith"); // NOI18N
        jP_regexEdition.add(jL_blockToReplaceWith);
        jL_blockToReplaceWith.setBounds(10, 65, 200, 14);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(jTP_regex);

        jP_regexEdition.add(jScrollPane1);
        jScrollPane1.setBounds(210, 35, 385, 22);

        add(jP_regexEdition);
        jP_regexEdition.setBounds(0, 0, 630, 190);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_matchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_matchesActionPerformed
        // TODO add your handling code here:

        checkIfTextMatches();
    }//GEN-LAST:event_jB_matchesActionPerformed

    private void jB_replaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_replaceActionPerformed
        // TODO add your handling code here:

        replaceStringWithRegex();
    }//GEN-LAST:event_jB_replaceActionPerformed

    private void jB_checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_checkActionPerformed
        // TODO add your handling code here:

		if( InternationalizedWindowFunctions.instance().validate( this, () -> validateRegex() ) == null )
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(this,
						getInternationalString(CONF_IT_IS_A_VALID_REGEX),
						getInternationalString(CONF_REGEX_VALIDATION),
						DialogsWrapper.INFORMATION_MESSAGE );
		}
    }//GEN-LAST:event_jB_checkActionPerformed

    private void jCB_autocompletionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_autocompletionActionPerformed
        // TODO add your handling code here:

		getAppliConf().setIsAutocompletionForRegexActivated( jCB_autocompletion.isSelected() );

    }//GEN-LAST:event_jCB_autocompletionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_check;
    private javax.swing.JButton jB_matches;
    private javax.swing.JButton jB_replace;
    private javax.swing.JCheckBox jCB_autocompletion;
    private javax.swing.JComboBox<String> jCB_blockToReplaceWith;
    private javax.swing.JLabel jL_blockToReplaceWith;
    private javax.swing.JLabel jL_checkRegularExpression;
    private javax.swing.JLabel jL_regexExpression;
    private javax.swing.JLabel jL_result;
    private javax.swing.JLabel jL_text;
    private javax.swing.JPanel jP_regexEdition;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTF_result;
    private javax.swing.JTextField jTF_textToCheck;
    private javax.swing.JTextPane jTP_regex;
    // End of variables declaration//GEN-END:variables

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jB_check = compMapper.mapComponent( jB_check );
		jB_replace = compMapper.mapComponent( jB_replace );
		jCB_autocompletion = compMapper.mapComponent( jCB_autocompletion );
		jL_checkRegularExpression = compMapper.mapComponent( jL_checkRegularExpression );
		jL_regexExpression = compMapper.mapComponent( jL_regexExpression );
		jL_result = compMapper.mapComponent( jL_result );
		jL_text = compMapper.mapComponent( jL_text );
		jP_regexEdition = compMapper.mapComponent(jP_regexEdition );
		jSeparator1 = compMapper.mapComponent( jSeparator1 );
		jTP_regex = compMapper.mapComponent(jTP_regex );
		jTF_result = compMapper.mapComponent( jTF_result );
		jTF_textToCheck = compMapper.mapComponent( jTF_textToCheck );
		jCB_blockToReplaceWith = compMapper.mapComponent( jCB_blockToReplaceWith );
		jL_blockToReplaceWith = compMapper.mapComponent( jL_blockToReplaceWith );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );

		postInit();

		super.setComponentMapper(compMapper);
	}

	protected void postInit()
	{
		if( _regexDocFormatter == null )
		{
			_wholeCompletionManager.setInputTextCompletionManager(_regexWholeContainer, jTP_regex, this);

			_regexDocFormatter = createRegexDocFormatter();

			_regexDocFormatter.setCombo(jCB_blockToReplaceWith);
			_regexDocFormatter.setNewJTextPane(jTP_regex);
			_regexDocFormatter.updateCombo();

			if( isDarkMode() )
				invertFormattersColor( getColorInversor() );

			_rriForRegexTextPane.setSizeChangedObserved( _regexDocFormatter );


			ThreadFunctions.instance().delayedInvokeEventDispatchThread(() -> {
														Dimension dimen = jScrollPane1.getSize();
														dimen.width++;
														jScrollPane1.setSize( dimen );

														_regexDocFormatter.visualizeSelectedBlock();
														},
								1600 );
		}
	}

	protected RegexDocumentFormatterComboForBlockToReplaceWithUpdater createRegexDocFormatter()
	{
		RegexDocumentFormatterComboForBlockToReplaceWithUpdater result = null;
		result = new RegexDocumentFormatterComboForBlockToReplaceWithUpdater( jTP_regex,
															getAppliConf(), getRegexBuilder() );

		result.init( jTP_regex, jCB_blockToReplaceWith, _initialBlockNameToReplaceWith );

		return( result );
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
		if( getRegexBuilder() != null )
		{
			String errorMessage = null;
			try
			{
				_pattern = getRegexBuilder().getRegexPattern( getExpression(), getBlocksToReplaceWith() ).getPattern();
			}
			catch( Exception ex )
			{
				errorMessage = ex.getMessage();
				_pattern = null;
			}

			if( _pattern == null )
				throw( new ValidationException( getInternationalString( CONF_ERROR_VALIDATING_REGEX ) +
													errorMessage,
												jTP_regex ) );
		}
	}

	protected void checkIfTextMatches()
	{
		InternationalizedWindowFunctions.instance().validate( this, () -> validateRegex() );

		if( _pattern != null )
		{
			String text = jTF_textToCheck.getText();

			Matcher matcher = _pattern.matcher( text );
			String infoText = null;
			int typeOfMessage = -1;
			if( matcher.matches() )
			{
				infoText = getInternationalString(CONF_REGEX_MATCHES_WITH_TEXT);
				typeOfMessage = DialogsWrapper.INFORMATION_MESSAGE;
			}
			else
			{
				infoText = getInternationalString(CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT);
				typeOfMessage = DialogsWrapper.ERROR_MESSAGE;
			}

			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(this,
						infoText,
						getInternationalString(CONF_REGEX_MATCH_RESULT),
						typeOfMessage );
		}
	}

	protected void replaceStringWithRegex()
	{
		jTF_result.setText( "" );
		InternationalizedWindowFunctions.instance().validate( this, () -> validateRegex() );

		if( _pattern != null )
		{
			String text = jTF_textToCheck.getText();

			String replacement = StringFunctions.instance().regExReplaceWithFirstGroup(_pattern, text);

			if( replacement != null )
				jTF_result.setText( replacement );
		}
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

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_REGEX_VALIDATION, "Regex validation" );
		registerInternationalString( CONF_IT_IS_A_VALID_REGEX, "It is a valid regex" );

		registerInternationalString( CONF_REGEX_MATCH_RESULT, "Regex match result" );
		registerInternationalString( CONF_REGEX_MATCHES_WITH_TEXT, "Regex matches with text" );
		registerInternationalString( CONF_REGEX_DOES_NOT_MATCH_WITH_TEXT, "Regex does not match with text" );
		registerInternationalString( CONF_ERROR_VALIDATING_REGEX, "Error validating regex : " );
		registerInternationalString( CONF_REGEXES_FOR_LINE, "Regexes for line - $1" );

		registerInternationalString( CONF_ERROR_REGEX_DOES_NOT_HAVE_ANY_BLOCK_TO_EXTRACT_TAG_FROM, "Error regex does not have any block to extract the tag from" );
		registerInternationalString( CONF_ERROR_VALIDATING_BLOCK_TO_GET_TAG, "Error validating block to get tag. Must be a block name of the regex" );
	}

	@Override
	public RegexEditionJPanel getComponent()
	{
		return( this );
	}

	public String getBlockToReplaceWith()
	{
		return( (String) jCB_blockToReplaceWith.getSelectedItem() );
	}

	public List<String> getBlocksToReplaceWith()
	{
		List<String> result = new ArrayList<>();
		result.add( getBlockToReplaceWith() );

		return( result );
	}

	protected void revertDelayed()
	{
		if( _regexDocFormatter != null )
			ExecutionFunctions.instance().safeMethodExecution( () -> _regexDocFormatter.revert() );
		else
			ThreadFunctions.instance().delayedSafeInvoke( () -> SwingUtilities.invokeLater( () -> revertDelayed() ), 100 );
	}

	public void revert()
	{
		revertDelayed();
	}

	public void validateBlockToExtractWith() throws ValidationException
	{
		if( ! regexHasAnyBlock() )
			throw( new ValidationException( getInternationalString( CONF_ERROR_REGEX_DOES_NOT_HAVE_ANY_BLOCK_TO_EXTRACT_TAG_FROM ),
											jTP_regex ) );

		if( ! blockToReplaceWithExists() )
			throw( new ValidationException( getInternationalString( CONF_ERROR_VALIDATING_BLOCK_TO_GET_TAG ),
											jCB_blockToReplaceWith ) );
	}

	protected boolean regexHasAnyBlock()
	{
		boolean result = false;

		RegexLexicalAnalyser lex = new RegexLexicalAnalyser();
		lex.setOnErrorThrowException( false );

		List<RegexToken> list = lex.getListOfTokens( getExpression() );
		for( RegexToken token: list )
		{
			if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
			{
				result = true;
				break;
			}
		}

		return( result );
	}

	protected boolean blockToReplaceWithExists()
	{
		Boolean result = ExecutionFunctions.instance().safeFunctionExecution(
			() -> _regexDocFormatter.blockIsPresent( getBlockToReplaceWith() ) );

		if( result == null )
			result = false;

		return( result );
	}

	@Override
	public void releaseResources() {
		_internationalizedStringConf = null;
		_regexWholeContainer = null;
		_rriForRegexTextPane = null;
		_resizeRelocateInfo = null;
		_appliConf = null;
		_pattern = null;
		_wholeCompletionManager = null;
		_regexDocFormatter = null;
		_initialBlockNameToReplaceWith = null;
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

		invertFormattersColor( colorInversor );
//		( (RealTimeTextComponentValidatorReactor) _realTimeValidator ).invertColors( colorInversor );
	}
}
