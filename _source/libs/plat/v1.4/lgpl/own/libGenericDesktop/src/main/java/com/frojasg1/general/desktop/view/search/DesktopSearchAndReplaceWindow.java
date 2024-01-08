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
package com.frojasg1.general.desktop.view.search;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.search.SearchWindowConfiguration;
import com.frojasg1.general.search.SearchReplaceContextInterface;
import com.frojasg1.general.search.SearchReplaceForWindowInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.DateFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.persistence.PersistentConfiguration;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerAutomaticSave;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.search.RegExException;
import com.frojasg1.general.search.SearchReplaceTextInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface.ReplaceResultInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface.ReplaceSettingsInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface.SearchResultInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface.SearchSettingsInterface;
import com.frojasg1.general.search.imp.ReplaceSettings;
import com.frojasg1.general.search.imp.SearchSettings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Date;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopSearchAndReplaceWindow extends InternationalizedJFrame
	implements SearchReplaceForWindowInterface
{
	protected static DesktopSearchAndReplaceWindow _instance = null;
	
	public static final String sa_configurationBaseFileName = "searchAndReplaceJFrame";
	protected static final String sa_configurationBaseFileNameForSearchCombo = "searchComboHistory";
	protected static final String sa_configurationBaseFileNameForReplaceCombo = "replaceComboHistory";
//	protected DesktopViewTextComponent _textComp = null;

	protected static final String sa_PROPERTIES_PATH_IN_JAR = GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR;

//	protected SearchReplaceTextInterface _searchReplaceManager = null;
//	protected TextUndoRedoInterface _undoRedoManager = null;

	protected String _configurationBaseFileName = null;

//	protected boolean _newSearch = true;

	protected SearchWindowConfiguration _lastConfiguration = null;

//	protected TextComboBoxHistoryWithProperties _comboBoxHistoryForSearchCB = null;
//	protected TextComboBoxHistoryWithProperties _comboBoxHistoryForReplaceCB = null;

	protected ComboBoxGroupManager _comboGroupManagerForSearchCB = null;
	protected ComboBoxGroupManager _comboGroupManagerForReplaceCB = null;

	protected static final String CONF_SEARCHED_STRING_NOT_FOUND = "SEARCHED_STRING_NOT_FOUND";
	protected static final String CONF_SEARCHED_STRING_FOUND = "SEARCHED_STRING_FOUND";
	protected static final String CONF_STRING_REPLACED = "STRING_REPLACED";
	protected static final String CONF_COULD_NOT_REPLACE_STRING = "COULD_NOT_REPLACE_STRING";
	protected static final String CONF_NO_REPLACEMENTS_DONE = "NO_REPLACEMENTS_DONE";
	protected static final String CONF_NUMBER_OF_REPLACEMENTS_DONE = "NUMBER_OF_REPLACEMENTS_DONE";
	protected static final String CONF_ERROR_IN_REGEX = "ERROR_IN_REGEX";

	protected SearchReplaceContextInterface _src = null;

	public static DesktopSearchAndReplaceWindow createInstance( BaseApplicationConfigurationInterface appConf,
									String configurationBaseFileName )
	{
		new DesktopSearchAndReplaceWindow( appConf, configurationBaseFileName );
		return( _instance );
	}

	public static DesktopSearchAndReplaceWindow instance()
	{
		return( _instance );
	}

	/**
	 * Creates new form SearchAndReplaceWindow
	 */
	public DesktopSearchAndReplaceWindow( BaseApplicationConfigurationInterface appConf,
									String configurationBaseFileName )
	{
		super( appConf );

		if( configurationBaseFileName == null )
			_configurationBaseFileName = sa_configurationBaseFileName;
		else
			_configurationBaseFileName = configurationBaseFileName;

		openConfiguration();

		initComponents();

		setWindowConfiguration();

		_instance = this;
//		_searchReplaceManager = srt;
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

//		initView( );
	}

	public TextUndoRedoInterface getUndoRedoManager()
	{
		return( _src.getUndoRedoManager() );
	}

	// https://stackoverflow.com/questions/9956533/select-all-text-in-editable-jcombobox-and-set-cursor-position
	public void openForSearch( String searchText )
	{
		boolean replace = false;
		openSearchReplace( searchText, replace );
	}

	public void openForReplace( String searchText )
	{
		boolean replace = true;
		openSearchReplace( searchText, replace );
	}

	protected void openSearchReplace( String searchText, boolean replace )
	{
		SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run()
				{
					if( ( searchText != null ) &&
						( searchText.length() > 0 ) )
					{
						jCB_search.getEditor().setItem( searchText );
					}

					jCB_search.getEditor().selectAll();

					jCB_replace.setSelected( replace );
					resizeWindowForReplace( replace );
					setVisible(true);

					deiconify();
					jCB_search.requestFocus();
				}
		});
	}

	protected ComboBoxGroupManager createComboBoxGroupManager( Integer maxItems, JComboBox combo, String confBaseFileName )
	{
		String language = null;
//		ParameterListConfiguration conf = new ParameterListConfiguration( getAppliConf().getConfigurationMainFolder(),
//															getAppliConf().getApplicationNameFolder(),
//															getAppliConf().getApplicationGroup(), language,
//															confBaseFileName + ".properties" );
//		ExecutionFunctions.instance().safeMethodExecution( () -> conf.M_openConfiguration() );
//		TextComboBoxHistoryWithProperties content = new TextComboBoxHistoryWithProperties( maxItems, conf );
//		content.init( (List<String>)null);
//		content.loadItems();

//		ChainedParentChildComboBoxManagerBase result = new ChainedParentChildComboBoxManagerBase( null, content, null ) {
//				public void newItemSelected( String newItem )
//				{
//					super.newItemSelected( newItem );
//					saveComboboxContents( this );
//				}
//		};
		ChainedParentChildComboBoxManagerAutomaticSave result =
			new ChainedParentChildComboBoxManagerAutomaticSave(null, getAppliConf(), confBaseFileName, maxItems, null );
		result.init();
		result.addComboComp( combo );

		return( result );
	}

	protected void initCombos( )
	{
		int maxItems = _lastConfiguration.M_getIntParamConfiguration( SearchWindowConfiguration.CONF_MAX_ITEMS_IN_HISTORY );
		_comboGroupManagerForSearchCB = createComboBoxGroupManager( maxItems, jCB_search, sa_configurationBaseFileNameForSearchCombo );
		_comboGroupManagerForReplaceCB = createComboBoxGroupManager( maxItems, jCB_replaceFor, sa_configurationBaseFileNameForReplaceCombo );
	}

	protected void initView( )
	{
		boolean regEx = _lastConfiguration.M_getBoolParamConfiguration( SearchWindowConfiguration.CONF_IS_REGEX );
		if( regEx )
			jRB_RegularExpression.setSelected( true );
		else
			jRB_NormalText.setSelected( true );
		radioButtonsRegExChanged();

		boolean wholeWords = _lastConfiguration.M_getBoolParamConfiguration( SearchWindowConfiguration.CONF_WHOLE_WORDS );
		jCB_wholeWords.setSelected( wholeWords );

		boolean matchCase = _lastConfiguration.M_getBoolParamConfiguration( SearchWindowConfiguration.CONF_MATCH_CASE );
		jCB_MatchCase.setSelected( matchCase );

		boolean replace = _lastConfiguration.M_getBoolParamConfiguration( SearchWindowConfiguration.CONF_REPLACE );
		jCB_replace.setSelected( replace );
		resizeWindowForReplace( replace );

		boolean alwaysOnTop = _lastConfiguration.M_getBoolParamConfiguration( SearchWindowConfiguration.CONF_ALWAYS_ON_TOP );
		jCB_alwaysOnTop.setSelected( alwaysOnTop );
		setAlwaysOnTop( alwaysOnTop );

		initCombos();

		setStatusText( "" );
	}

	protected void openConfiguration()
	{
		_lastConfiguration = new SearchWindowConfiguration( getAppliConf().getConfigurationMainFolder(),
															getAppliConf().getApplicationNameFolder(),
															getAppliConf().getApplicationGroup(),
															_configurationBaseFileName + ".properties" );

		try
		{
			_lastConfiguration.M_openConfiguration();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
	
	protected SearchSettingsInterface getSearchSettings()
	{
		SearchSettings result = new SearchSettings( jRB_RegularExpression.isSelected(),
													jCB_wholeWords.isSelected(),
													jCB_MatchCase.isSelected(),
													(String) jCB_search.getEditor().getItem(),
													(String) jCB_search.getEditor().getItem() );

		return( result );
	}

	protected ReplaceSettingsInterface getReplaceSettings()
	{
		ReplaceSettings result = new ReplaceSettings( jRB_RegularExpression.isSelected(),
													jCB_wholeWords.isSelected(),
													jCB_MatchCase.isSelected(),
													(String) jCB_search.getEditor().getItem(),
													(String) jCB_search.getEditor().getItem(),
													(String) jCB_replaceFor.getEditor().getItem(),
													(String) jCB_replaceFor.getEditor().getItem() );

		return( result );
	}
	
/*
	public void setUndoRedoManager( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		if( _undoRedoManager != null )
		{
			_undoRedoManager.unregisterListener(this);
		}

		_undoRedoManager = undoRedoManagerOfTextComp;
		if( _undoRedoManager != null )
		{
			setTextComponent( _undoRedoManager.getView() );

			_undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;

		_searchReplaceManager.setUndoRedoManager(undoRedoManagerOfTextComp);
	}

	public void setTextComponent( ViewTextComponent vtc )
	{
		if( vtc instanceof DesktopViewTextComponent )
			_textComp = (DesktopViewTextComponent) vtc;
		else
			_textComp = null;
	}
*/
	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem( );
		try
		{
			mapRRCI.putResizeRelocateComponentItem(jPanel2, ResizeRelocateItem.RESIZE_TO_RIGHT +
															ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem(jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT +
															ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem(jP_textToFind, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jCB_search, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jP_replaceTextFor, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jP_status, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem(jL_status, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jCB_replaceFor, ResizeRelocateItem.RESIZE_TO_RIGHT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
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
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
//									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_PROPERTIES_PATH_IN_JAR,
									_configurationBaseFileName,
									this,
									null,
									vectorJpopupMenus,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									getAppliConf().getZoomFactor(),
									getAppliConf().hasToEnableUndoRedoForTextComponents(),
									getAppliConf().hasToEnableTextCompPopupMenus(),
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);

		a_intern.setMaxWindowHeightNoLimit( false );

		registerInternationalString(CONF_SEARCHED_STRING_NOT_FOUND, "Searched string not found." );
		registerInternationalString(CONF_SEARCHED_STRING_FOUND, "Searched string found." );
		registerInternationalString(CONF_STRING_REPLACED, "String replaced." );
		registerInternationalString(CONF_COULD_NOT_REPLACE_STRING, "Could not replace string." );
		registerInternationalString(CONF_NO_REPLACEMENTS_DONE, "No replacemets have been done." );
		registerInternationalString(CONF_NUMBER_OF_REPLACEMENTS_DONE, "Number of replacements done." );
		registerInternationalString(CONF_ERROR_IN_REGEX, "Error in regular expression." );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_RegEx = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jP_status = new javax.swing.JPanel();
        jL_status = new javax.swing.JLabel();
        jL_hour = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jP_RegExRadioGroupPanel = new javax.swing.JPanel();
        jRB_NormalText = new javax.swing.JRadioButton();
        jRB_RegularExpression = new javax.swing.JRadioButton();
        jP_textToFind = new javax.swing.JPanel();
        jCB_search = new javax.swing.JComboBox<>();
        jCB_alwaysOnTop = new javax.swing.JCheckBox();
        jP_buttons = new javax.swing.JPanel();
        jB_forward = new javax.swing.JButton();
        jB_replaceForward = new javax.swing.JButton();
        jB_replaceBackwards = new javax.swing.JButton();
        jB_backwards = new javax.swing.JButton();
        jB_replaceAll = new javax.swing.JButton();
        jB_replace = new javax.swing.JButton();
        jP_replaceTextFor = new javax.swing.JPanel();
        jCB_replaceFor = new javax.swing.JComboBox<>();
        jP_AdditionalSearchSettingsPanel = new javax.swing.JPanel();
        jCB_MatchCase = new javax.swing.JCheckBox();
        jCB_wholeWords = new javax.swing.JCheckBox();
        jCB_replace = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(606, 250));
        setName("SearchAndReplaceWindow"); // NOI18N
        getContentPane().setLayout(null);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(null);

        jP_status.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jP_status.setName("jP_status"); // NOI18N
        jP_status.setLayout(null);

        jL_status.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jL_status.setForeground(new java.awt.Color(1, 197, 1));
        jL_status.setText("jLabel1");
        jP_status.add(jL_status);
        jL_status.setBounds(70, 3, 510, 14);

        jL_hour.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jL_hour.setText("jLabel1");
        jP_status.add(jL_hour);
        jL_hour.setBounds(10, 3, 60, 14);

        jPanel2.add(jP_status);
        jP_status.setBounds(0, 260, 590, 20);

        jPanel1.setPreferredSize(new java.awt.Dimension(500, 260));
        jPanel1.setLayout(null);

        jP_RegExRadioGroupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Regular expression"));
        jP_RegExRadioGroupPanel.setName("jP_RegExRadioGroupPanel"); // NOI18N
        jP_RegExRadioGroupPanel.setLayout(null);

        bg_RegEx.add(jRB_NormalText);
        jRB_NormalText.setSelected(true);
        jRB_NormalText.setText("Normal text");
        jRB_NormalText.setName("RB_NormalText"); // NOI18N
        jRB_NormalText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_NormalTextActionPerformed(evt);
            }
        });
        jP_RegExRadioGroupPanel.add(jRB_NormalText);
        jRB_NormalText.setBounds(10, 20, 270, 23);
        jRB_NormalText.getAccessibleContext().setAccessibleName("");

        bg_RegEx.add(jRB_RegularExpression);
        jRB_RegularExpression.setText("Regular expression");
        jRB_RegularExpression.setName("RB_RegularExpression"); // NOI18N
        jRB_RegularExpression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_RegularExpressionActionPerformed(evt);
            }
        });
        jP_RegExRadioGroupPanel.add(jRB_RegularExpression);
        jRB_RegularExpression.setBounds(10, 40, 270, 23);

        jPanel1.add(jP_RegExRadioGroupPanel);
        jP_RegExRadioGroupPanel.setBounds(0, 20, 290, 70);

        jP_textToFind.setBorder(javax.swing.BorderFactory.createTitledBorder("Text to find"));
        jP_textToFind.setName("P_textToFind"); // NOI18N
        jP_textToFind.setLayout(null);

        jCB_search.setEditable(true);
        jCB_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_searchActionPerformed(evt);
            }
        });
        jP_textToFind.add(jCB_search);
        jCB_search.setBounds(10, 20, 570, 20);

        jPanel1.add(jP_textToFind);
        jP_textToFind.setBounds(0, 160, 590, 50);

        jCB_alwaysOnTop.setText("Always on top");
        jCB_alwaysOnTop.setName("CB_alwaysOnTop"); // NOI18N
        jCB_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_alwaysOnTopActionPerformed(evt);
            }
        });
        jPanel1.add(jCB_alwaysOnTop);
        jCB_alwaysOnTop.setBounds(10, 0, 260, 23);

        jP_buttons.setLayout(null);

        jB_forward.setText("Search forward");
        jB_forward.setName("B_forward"); // NOI18N
        jB_forward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_forwardActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_forward);
        jB_forward.setBounds(10, 10, 160, 23);

        jB_replaceForward.setText("Replace and next search forward");
        jB_replaceForward.setName("B_replaceForward"); // NOI18N
        jB_replaceForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_replaceForwardActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_replaceForward);
        jB_replaceForward.setBounds(180, 10, 270, 23);

        jB_replaceBackwards.setText("Replace and next search backwards");
        jB_replaceBackwards.setName("B_replaceBackwards"); // NOI18N
        jB_replaceBackwards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_replaceBackwardsActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_replaceBackwards);
        jB_replaceBackwards.setBounds(180, 40, 270, 23);

        jB_backwards.setText("Search backwards");
        jB_backwards.setName("B_backwards"); // NOI18N
        jB_backwards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_backwardsActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_backwards);
        jB_backwards.setBounds(10, 40, 160, 23);

        jB_replaceAll.setText("Replace all");
        jB_replaceAll.setName("B_replaceAll"); // NOI18N
        jB_replaceAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_replaceAllActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_replaceAll);
        jB_replaceAll.setBounds(460, 40, 120, 23);

        jB_replace.setText("Replace");
        jB_replace.setName("B_replace"); // NOI18N
        jB_replace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_replaceActionPerformed(evt);
            }
        });
        jP_buttons.add(jB_replace);
        jB_replace.setBounds(460, 10, 120, 23);

        jPanel1.add(jP_buttons);
        jP_buttons.setBounds(0, 90, 590, 70);

        jP_replaceTextFor.setBorder(javax.swing.BorderFactory.createTitledBorder("Text to replace for"));
        jP_replaceTextFor.setName("P_replaceTextFor"); // NOI18N
        jP_replaceTextFor.setLayout(null);

        jCB_replaceFor.setEditable(true);
        jP_replaceTextFor.add(jCB_replaceFor);
        jCB_replaceFor.setBounds(10, 20, 570, 20);

        jPanel1.add(jP_replaceTextFor);
        jP_replaceTextFor.setBounds(0, 210, 590, 50);

        jP_AdditionalSearchSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Additional searh settings"));
        jP_AdditionalSearchSettingsPanel.setName("P_AdditionalSearchSettingsPanel"); // NOI18N
        jP_AdditionalSearchSettingsPanel.setLayout(null);

        jCB_MatchCase.setText("Match case");
        jCB_MatchCase.setName("CB_MatchCase"); // NOI18N
        jP_AdditionalSearchSettingsPanel.add(jCB_MatchCase);
        jCB_MatchCase.setBounds(10, 20, 280, 23);

        jCB_wholeWords.setText("Whole words");
        jCB_wholeWords.setName("CB_wholeWords"); // NOI18N
        jP_AdditionalSearchSettingsPanel.add(jCB_wholeWords);
        jCB_wholeWords.setBounds(10, 40, 280, 23);

        jCB_replace.setText("Replace");
        jCB_replace.setName("CB_replace"); // NOI18N
        jCB_replace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_replaceActionPerformed(evt);
            }
        });
        jP_AdditionalSearchSettingsPanel.add(jCB_replace);
        jCB_replace.setBounds(10, 60, 280, 23);

        jPanel1.add(jP_AdditionalSearchSettingsPanel);
        jP_AdditionalSearchSettingsPanel.setBounds(290, 0, 300, 90);

        jPanel2.add(jPanel1);
        jPanel1.setBounds(0, 0, 590, 260);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 590, 280);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_forwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_forwardActionPerformed
        // TODO add your handling code here:

		search( true );

    }//GEN-LAST:event_jB_forwardActionPerformed

    private void jB_backwardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_backwardsActionPerformed
		// TODO add your handling code here:

		search( false );

    }//GEN-LAST:event_jB_backwardsActionPerformed

    private void jCB_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_alwaysOnTopActionPerformed
        // TODO add your handling code here:

		this.setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );

    }//GEN-LAST:event_jCB_alwaysOnTopActionPerformed

    private void jCB_replaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_replaceActionPerformed
		// TODO add your handling code here:

		boolean replace = jCB_replace.isSelected();

		resizeWindowForReplace( replace );

    }//GEN-LAST:event_jCB_replaceActionPerformed

    private void jRB_NormalTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_NormalTextActionPerformed
        // TODO add your handling code here:

		radioButtonsRegExChanged();

    }//GEN-LAST:event_jRB_NormalTextActionPerformed

    private void jRB_RegularExpressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_RegularExpressionActionPerformed
        // TODO add your handling code here:

		radioButtonsRegExChanged();

    }//GEN-LAST:event_jRB_RegularExpressionActionPerformed

    private void jB_replaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_replaceActionPerformed
        // TODO add your handling code here:

		replace();

    }//GEN-LAST:event_jB_replaceActionPerformed

    private void jB_replaceForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_replaceForwardActionPerformed
        // TODO add your handling code here:

		replaceAndFindNext( true );

    }//GEN-LAST:event_jB_replaceForwardActionPerformed

    private void jB_replaceBackwardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_replaceBackwardsActionPerformed
        // TODO add your handling code here:

		replaceAndFindNext( false );

    }//GEN-LAST:event_jB_replaceBackwardsActionPerformed

    private void jB_replaceAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_replaceAllActionPerformed
        // TODO add your handling code here:

		replaceAll();

    }//GEN-LAST:event_jB_replaceAllActionPerformed

    private void jCB_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_searchActionPerformed
        // TODO add your handling code here:

		search( true );

//		Point locationOnScreen = ExecutionFunctions.instance().safeFunctionExecution( () -> jP_status.getLocationOnScreen() );
//		Component[] matchingComps = ExecutionFunctions.instance().safeFunctionExecution( () -> getComponentsMatchingPosition( locationOnScreen, 20 ) );
    }//GEN-LAST:event_jCB_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_RegEx;
    private javax.swing.JButton jB_backwards;
    private javax.swing.JButton jB_forward;
    private javax.swing.JButton jB_replace;
    private javax.swing.JButton jB_replaceAll;
    private javax.swing.JButton jB_replaceBackwards;
    private javax.swing.JButton jB_replaceForward;
    private javax.swing.JCheckBox jCB_MatchCase;
    private javax.swing.JCheckBox jCB_alwaysOnTop;
    private javax.swing.JCheckBox jCB_replace;
    private javax.swing.JComboBox<String> jCB_replaceFor;
    private javax.swing.JComboBox<String> jCB_search;
    private javax.swing.JCheckBox jCB_wholeWords;
    private javax.swing.JLabel jL_hour;
    private javax.swing.JLabel jL_status;
    private javax.swing.JPanel jP_AdditionalSearchSettingsPanel;
    private javax.swing.JPanel jP_RegExRadioGroupPanel;
    private javax.swing.JPanel jP_buttons;
    private javax.swing.JPanel jP_replaceTextFor;
    private javax.swing.JPanel jP_status;
    private javax.swing.JPanel jP_textToFind;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRB_NormalText;
    private javax.swing.JRadioButton jRB_RegularExpression;
    // End of variables declaration//GEN-END:variables

	public void search( boolean forward )
	{
		SearchResultInterface result = null;

		SearchSettingsInterface ssi = getSearchSettings();

//		if( _comboGroupManagerForSearchCB != null )
//			_comboGroupManagerForSearchCB.saveCurrentItem();

		result = null;
		boolean regExException = false;

		try
		{
			if( _src != null )
				result = _src.search( ssi, forward );
		}
		catch( RegExException ree )
		{
			result = null;
			regExException = true;
		}

		if( ( result == null ) || !result.resultsDifferentFromNoResults() )
		{
			String status = null;
			if( regExException )
				status = getInternationalString( CONF_ERROR_IN_REGEX );
			else
				status = getInternationalString( CONF_SEARCHED_STRING_NOT_FOUND );
			setStatusFailure( status );
		}
		else
		{
			setStatusSuccess( getInternationalString( CONF_SEARCHED_STRING_FOUND ) );
		}
	}

	protected void replace()
	{
		ReplaceSettingsInterface settings = this.getReplaceSettings();

		if( _comboGroupManagerForReplaceCB != null )
			_comboGroupManagerForReplaceCB.saveCurrentItem();

		ReplaceResultInterface result = null;
		
		boolean regExException = false;
		
		try
		{
			result = _src.replace(settings);
		}
		catch( RegExException ree )
		{
			result = null;
			regExException = true;
		}
		
		if( result != null )
			setStatusSuccess( getInternationalString( CONF_STRING_REPLACED ) );
		else
		{
			String status = null;
			if( regExException )
				status = getInternationalString( CONF_ERROR_IN_REGEX );
			else
				status = getInternationalString( CONF_COULD_NOT_REPLACE_STRING );
			setStatusFailure( status );
		}
	}

	protected void updateConfigurationWithView()
	{
//		_lastConfiguration.setCollectionOfSearchItems(_comboBoxHistoryForSearchCB.getListOfItems() );
//		_lastConfiguration.setCollectionOfReplaceItems(_comboBoxHistoryForReplaceCB.getListOfItems() );

		_lastConfiguration.M_setBoolParamConfiguration( SearchWindowConfiguration.CONF_IS_REGEX, jRB_RegularExpression.isSelected() );
		_lastConfiguration.M_setBoolParamConfiguration( SearchWindowConfiguration.CONF_WHOLE_WORDS, jCB_wholeWords.isSelected() );
		_lastConfiguration.M_setBoolParamConfiguration( SearchWindowConfiguration.CONF_MATCH_CASE, jCB_MatchCase.isSelected() );
		_lastConfiguration.M_setBoolParamConfiguration( SearchWindowConfiguration.CONF_REPLACE, jCB_replace.isSelected() );
		_lastConfiguration.M_setBoolParamConfiguration( SearchWindowConfiguration.CONF_ALWAYS_ON_TOP, jCB_alwaysOnTop.isSelected() );
	}

	protected void saveConfiguration()
	{
		ExecutionFunctions.instance().safeMethodExecution( ()->_lastConfiguration.M_saveConfiguration() );
		ExecutionFunctions.instance().safeMethodExecution( ()-> ((PersistentConfiguration)_comboGroupManagerForSearchCB.getComboBoxContent()).save() );
		ExecutionFunctions.instance().safeMethodExecution( ()-> ((PersistentConfiguration)_comboGroupManagerForReplaceCB.getComboBoxContent()).save() );
	}

	@Override
	public void formWindowClosingEvent( )
	{
		formWindowClosing( false );
	}

	protected void removeListeners()
	{
		_comboGroupManagerForSearchCB.dispose();
		_comboGroupManagerForReplaceCB.dispose();
	}

	@Override
	public void formWindowClosing( boolean close )
	{
		setVisible( false );

		updateConfigurationWithView();
		saveConfiguration();

		if( close )
			removeListeners();

		super.formWindowClosing( close );
	}

	protected void resizeWindowForReplace( boolean replace )
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable() {
					public void run()
					{
						resizeWindowForReplaceEDT( replace );
					}
			});
		}
		else
			resizeWindowForReplaceEDT( replace );
	}

	protected void resizeWindowForReplaceEDT( boolean replace )
	{
		jB_replaceForward.setVisible( replace );
		jB_replaceBackwards.setVisible( replace );
		jP_replaceTextFor.setVisible( replace );
		jB_replace.setVisible( replace );
		jB_replaceAll.setVisible( replace );

		int preferredHeight = (int) ( jP_AdditionalSearchSettingsPanel.getHeight() +
										jP_buttons.getHeight() +
										jP_textToFind.getHeight() );
		if( replace )
		{
			jP_replaceTextFor.setLocation( jP_replaceTextFor.getLocation().x, preferredHeight );
			preferredHeight += (int) jP_replaceTextFor.getHeight();
		}
		else
		{
			jP_replaceTextFor.setLocation( jP_replaceTextFor.getLocation().x,
				preferredHeight + jP_status.getSize().height );
		}

		jP_status.setBounds( 0, preferredHeight, jP_status.getWidth(), jP_status.getHeight() );
		jPanel1.setBounds( jPanel1.getX(), jPanel1.getY(), jPanel1.getWidth(), preferredHeight );
		jPanel2.setBounds( jPanel2.getX(), jPanel2.getY(), jPanel2.getWidth(), preferredHeight + jP_status.getHeight() );

		preferredHeight += jP_status.getHeight();
		Insets borders = a_intern.getFrameBorder();
		if( borders != null )
		{
			preferredHeight += borders.top + borders.bottom;
		}

		Rectangle bounds = getBounds();
		Dimension dimen = getPreferredSize();
		Dimension newDimen = new Dimension( (int) dimen.getWidth(), preferredHeight );
		setPreferredSize( newDimen );
		setMaximumSize( newDimen );
		setMinimumSize( new Dimension( (int) getMinimumSize().getWidth(), preferredHeight ) );

//		Dimension newSize = new Dimension( bounds.width, preferredHeight );
//		getContentPane().setSize( newSize );
		setSize( bounds.width, preferredHeight );
	}

	protected void radioButtonsRegExChanged()
	{
		boolean normal = jRB_NormalText.isSelected();
		
		jB_backwards.setEnabled( normal );
		jB_replaceBackwards.setEnabled( normal );
	}

	protected void replaceAndFindNext( boolean forward )
	{
		ReplaceSettingsInterface settings = this.getReplaceSettings();

		_comboGroupManagerForSearchCB.saveCurrentItem();
		_comboGroupManagerForReplaceCB.saveCurrentItem();

		SearchReplaceTextInterface.ReplaceAndFindNextResultInterface result = null;
		
		boolean regExException = false;

		try
		{
			result = _src.replaceAndFindNext(settings, forward);
		}
		catch( RegExException ree )
		{
			result = ree.getReplaceAndFindResult();
			regExException = true;
		}

		SearchResultInterface searchResult = null;
		ReplaceResultInterface replaceResult = null;

		if( result != null )
		{
			replaceResult = result.getReplaceResult();
			searchResult = result.getSearchResult();
		}

		String status = null;

		if( replaceResult != null )
		{
			status = getInternationalString( CONF_STRING_REPLACED );

			if( ( searchResult != null ) && ( searchResult.resultsDifferentFromNoResults() ) )
			{
				status = status + " " + getInternationalString( CONF_SEARCHED_STRING_FOUND );
				setStatusSuccess( status );
			}
			else
			{
				if( regExException )
					status = status + " " + getInternationalString( CONF_ERROR_IN_REGEX );
				else
					status = status + " " + getInternationalString( CONF_SEARCHED_STRING_NOT_FOUND );

				setStatusFailure( status );
			}
		}
		else
		{
			if( regExException )
				status = getInternationalString( CONF_ERROR_IN_REGEX );
			else
				status = getInternationalString( CONF_COULD_NOT_REPLACE_STRING );

			setStatusFailure( status );
		}
	}

	protected void replaceAll()
	{
		ReplaceSettingsInterface settings = this.getReplaceSettings();

		_comboGroupManagerForSearchCB.saveCurrentItem();
		_comboGroupManagerForReplaceCB.saveCurrentItem();

		SearchResultInterface[] result = null;
		
		boolean regExException = false;
		try
		{
			result = _src.replaceAll(settings);
		}
		catch( RegExException ree )
		{
			result = null;
			regExException = true;
		}

		String status = null;

		if( ( result != null ) && ( result.length > 0 ) )
		{
			status = getInternationalString( CONF_STRING_REPLACED );

			status = String.format( "%s : %d", getInternationalString( CONF_NUMBER_OF_REPLACEMENTS_DONE ), result.length );

			setStatusSuccess( status );
		}
		else
		{
			if( regExException )
				status = getInternationalString( CONF_ERROR_IN_REGEX );
			else
				status = getInternationalString( CONF_NO_REPLACEMENTS_DONE );
			
			setStatusFailure( status );
		}
	}

	protected void setStatusSuccess( String text )
	{
		setStatusText( text );
		jL_status.setForeground( new Color( 1, 191, 1 ) );
	}
	
	protected void setStatusFailure( String text )
	{
		setStatusText( text );
		jL_status.setForeground( Color.RED );
	}
	
	protected void setStatusText( String text )
	{
		Date date = new Date();

		jL_hour.setText( DateFunctions.instance().formatDate(date, "HH:mm:ss", null ) + " - " );
		
		if( ( text == null ) || ( text.length() == 0 ) )
			text = " ";		// so that the JLabel does not size width 0.

		jL_status.setText( text );
	}

	@Override
	public void changeZoomFactor(double zoomFactor)
	{
		boolean replace = jCB_replace.isSelected();
		resizeWindowForReplace( replace );

		super.changeZoomFactor( zoomFactor );
	}

	@Override
	public void setSearchReplaceContext(SearchReplaceContextInterface src)
	{
		_src = src;
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jRB_RegularExpression = compMapper.mapComponent(jRB_RegularExpression);
		jRB_NormalText = compMapper.mapComponent(jRB_NormalText);
		jPanel2 = compMapper.mapComponent(jPanel2);
		jPanel1 = compMapper.mapComponent(jPanel1);
		jP_textToFind = compMapper.mapComponent(jP_textToFind);
		jP_status = compMapper.mapComponent(jP_status);
		jP_replaceTextFor = compMapper.mapComponent(jP_replaceTextFor);
		jP_buttons = compMapper.mapComponent(jP_buttons);
		jP_RegExRadioGroupPanel = compMapper.mapComponent(jP_RegExRadioGroupPanel);
		jP_AdditionalSearchSettingsPanel = compMapper.mapComponent(jP_AdditionalSearchSettingsPanel);
		jL_status = compMapper.mapComponent(jL_status);
		jL_hour = compMapper.mapComponent(jL_hour);
		jCB_wholeWords = compMapper.mapComponent(jCB_wholeWords);
		jCB_search = compMapper.mapComponent(jCB_search);
		jCB_replaceFor = compMapper.mapComponent(jCB_replaceFor);
		jCB_replace = compMapper.mapComponent(jCB_replace);
		jCB_alwaysOnTop = compMapper.mapComponent(jCB_alwaysOnTop);
		jCB_MatchCase = compMapper.mapComponent(jCB_MatchCase);
		jB_replaceForward = compMapper.mapComponent(jB_replaceForward);
		jB_replaceBackwards = compMapper.mapComponent(jB_replaceBackwards);
		jB_replaceAll = compMapper.mapComponent(jB_replaceAll);
		jB_replace = compMapper.mapComponent(jB_replace);
		jB_forward = compMapper.mapComponent(jB_forward);
		jB_backwards = compMapper.mapComponent(jB_backwards);
		bg_RegEx = compMapper.mapComponent(bg_RegEx);

		if( !hasBeenAlreadyMapped() )
			initView( );

		_comboGroupManagerForSearchCB.setComponentMapper(compMapper);
		_comboGroupManagerForReplaceCB.setComponentMapper(compMapper);
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );
	}

	@Override
	public void setBounds( Rectangle rect )
	{
		super.setBounds( rect );
	}

}
