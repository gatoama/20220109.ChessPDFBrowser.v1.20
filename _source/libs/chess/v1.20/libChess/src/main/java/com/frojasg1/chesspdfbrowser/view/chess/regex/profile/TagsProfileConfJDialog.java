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

import com.frojasg1.applications.common.components.internationalization.ExtendedZoomSemaphore;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.chesspdfbrowser.view.chess.regex.impl.BlockRegexOrProfileNameJPanel;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TagsProfileConfJDialog extends InternationalizedJDialog
	implements AcceptCancelRevertControllerInterface,
				MouseMotionListener, MouseListener
{
	protected static final int NUMBER_OF_WORKERS_FOR_PULL = 6;

	protected static final int THICK_FOR_POPUP_CLUES = 2;

	protected static final String CONF_MOVE_LINE_PANEL_HERE = "MOVE_LINE_PANEL_HERE";

	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected final static String a_configurationBaseFileName = "TagsProfileConfJDialog";

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected BlockRegexBuilder _regexBuilder = null;

	protected BlockRegexOrProfileNameJPanel _nameJPanel = null;

	protected String _initialProfileName = null;

	protected ProfileModel _lastConfirmedProfileModel = null;
	protected ProfileModel _profileModel = null;

	protected List<LineOfTagsJPanel> _listOfLineJPanel = new ArrayList<>();
	protected List<LineOfTagsJPanel> _alreadyShrunklistOfLineJPanel = new ArrayList<>();

	protected MapResizeRelocateComponentItem _linePanelsMapRRCI = null;

	protected ContextualMenu _popupMenu = null;

	protected LineOfTagsJPanel _currentLineClicked = null;
	protected LineOfTagsJPanel _destinationLinePanel = null;

	protected ClueOfPopupMenu _clueOfPopupMenuToShow = null;

	protected boolean _isDragging = false;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected boolean _validateAtOnce = false;

	protected static final int INVERTIBLE_RED_INDEX = 0;
	protected static final int INVERTIBLE_ORANGE_INDEX = 1;
	protected Color[] _invertibleColorModeColors = new Color[] {
		Colors.SEMITRANSPARENT_RED,
		Color.ORANGE
	};

	/**
	 * Creates new form TagsProfileConfJDialog
	 */
	public TagsProfileConfJDialog( JFrame parent, boolean modal,
							WholeCompletionManager wholeCompletionManager )
	{
		super(parent, modal, ApplicationConfiguration.instance() );
		_wholeCompletionManager = wholeCompletionManager;
	}

	public TagsProfileConfJDialog( JDialog parent, boolean modal,
							WholeCompletionManager wholeCompletionManager )
	{
		super(parent, modal, ApplicationConfiguration.instance() );
		_wholeCompletionManager = wholeCompletionManager;
	}

	public void init( String profileName,
						BlockRegexBuilder regexBuilder,
						ProfileModel profileModel )
	{
		setPreventFromRepainting( true );

		_popupMenu = new ContextualMenu();

		_regexBuilder = regexBuilder;

		_profileModel = profileModel;
		confirm();
//		_profileModel.save();

		_initialProfileName = profileName;

		initComponents();

		initOwnComponents();

		addListeners();

		initContents();

		setWindowConfiguration();
	}

	protected Color getInvertibleColor( int index )
	{
		return( _invertibleColorModeColors[index] );
	}

	@Override
	public void setDarkMode( boolean isDarkMode, ColorInversor colorInversor )
	{
		_invertibleColorModeColors = colorInversor.invertColors(_invertibleColorModeColors);

		super.setDarkMode( isDarkMode, colorInversor );
	}

	@Override
	protected int getNumberOfWorkersForPull()
	{
		return NUMBER_OF_WORKERS_FOR_PULL;
	}

	public JPopupMenu getPopupMenu()
	{
		return( _popupMenu );
	}

	protected void confirm()
	{
		_lastConfirmedProfileModel = _copier.copy(_profileModel );
	}

	public ProfileModel getRegexConfContainer()
	{
		return( _profileModel );
	}
/*
	protected RegexEditionJPanel createRegexEditionJPanel()
	{
		return( new RegexEditionJPanel(_regexBuilder, _wholeCompletionManager) );
	}
*/

	public void setValidateAtOnce( boolean validateAtOnce )
	{
		_validateAtOnce = validateAtOnce;
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	protected ProfileNameJPanel createRegexNameJPanel()
	{
		ProfileNameJPanel result = new ProfileNameJPanel (
			this.getRegexConfContainer(), getInitialProfileName(),
			_profileModel.isActive() );

		return( result );
	}

	protected void addListeners()
	{
		addListenersForMouseToHierarchy( getContentPane() );
	}

	protected void removeListeners()
	{
		removeListenersForMouseToHierarchy( getContentPane() );
	}

	protected void initOwnComponents()
	{
		_nameJPanel = createRegexNameJPanel();
		getContentPane().add( _nameJPanel );

		_acceptPanel = createAcceptCancelRevertPanel();
		jPanel4.add( _acceptPanel );
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel4, _acceptPanel );

		createDefaultLineIfEmpty();
		createInitialLinesPanelsWithoutZooming();
		addLinePanelsToContentPane();

		relocatePanels( 1.0D );
	}

	protected boolean isAbleToDrag( Component comp )
	{
		boolean result = ( comp instanceof JPanel ) || ( comp instanceof JLabel );
		result = result && ( _listOfLineJPanel.size() > 1 );

		return( result );
	}

	protected void addListenersForMouse( Component comp )
	{
		if( ! Arrays.stream( comp.getMouseListeners() ).anyMatch( (listener) -> listener == comp ) )
		{
			comp.addMouseListener( this );
			comp.addMouseMotionListener( this );
		}
	}

	protected void removeListenersForMouse( Component comp )
	{
		comp.removeMouseListener( this );
		comp.removeMouseMotionListener( this );
	}

	protected void addListenersForMouseToHierarchy( Component comp )
	{
		ComponentFunctions.instance().browseComponentHierarchy( comp, c -> { addListenersForMouse(c); return( null ); } );
	}

	protected void removeListenersForMouseToHierarchy( Component comp )
	{
		ComponentFunctions.instance().browseComponentHierarchy( comp, c -> { removeListenersForMouse(c); return( null ); } );
	}

	protected void createDefaultLineIfEmpty()
	{
		if( _profileModel.getListOfLines().isEmpty() )
		{
			_profileModel.addEmptyLineOfTagRegexes( 0 );
		}
	}

	protected void addLineOfTagsJPanel( LineOfTagsJPanel panel )
	{
		_listOfLineJPanel.add( panel );
	}

	protected boolean existsPanel( LineModel lineModel )
	{
		boolean result = false;

		for( LineOfTagsJPanel linePanel: _listOfLineJPanel )
			if( lineModel == linePanel.getLineModel() )
			{
				result = true;
				break;
			}

		return( result );
	}

	protected void createInitialLinesPanelsWithoutZooming()
	{
		for( LineModel lineModel: _profileModel.getListOfLines() )
			if( ( a_intern == null ) || ! existsPanel( lineModel ) )
				addLineOfTagsJPanel( createLineOfTagsJPanel( lineModel ) );
	}

	protected LineOfTagsJPanel createLineOfTagsJPanel( LineModel lineModel )
	{
		LineOfTagsJPanel result = new LineOfTagsJPanel( _profileModel.getParent().getBlockRegexBuilder(),
														lineModel, _wholeCompletionManager,
														getAppliConf(),
														this);
		addListenersForMouseToHierarchy( result );

//		result.setName( "PruebaJPanel" );

		return( result );
	}

	protected void addLinePanelsToContentPane()
	{
		Container cp = getContentPane();
		setIgnoreRepaintRecursive( cp, true );

		for( LineOfTagsJPanel lineJPanel: _listOfLineJPanel )
			if( (a_intern == null) || ! alreadyInitialized( lineJPanel ) )
				cp.add( lineJPanel );

//		cp.revalidate();
	}

	protected List<LineOfTagsJPanel> getSortedListOfLineJPanels()
	{
		List<LineOfTagsJPanel> result = new ArrayList<>(_listOfLineJPanel);

		Collections.sort( result, ( l1, l2 ) -> ( l1.calculateIndex()- l2.calculateIndex() ) );

		return( result );
	}

	protected int zoom( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueRound(value, zoomFactor) );
	}

	protected void relocatePanels( double zoomFactorForLinePanels )
	{
		int width = jPanel3.getWidth();

		Dimension size = _nameJPanel.getInternalSize();
		_nameJPanel.setBounds( 0, 0, size.width, size.height );
		int yy = size.height;

		for( LineOfTagsJPanel lineJPanel: getSortedListOfLineJPanels() )
		{
			size = lineJPanel.getInternalSize();
			int height_ = size.height;

			if( ( a_intern != null ) && !alreadyInitialized( lineJPanel ) )
				height_ = zoom( size.height, zoomFactorForLinePanels );

			lineJPanel.setBounds( 0, yy, width, height_ );
			lineJPanel.updateIndex();

			yy += height_;
		}

		jPanel3.setBounds( 0, yy, jPanel3.getWidth(), jPanel3.getHeight() );
//		_acceptPanel.setBounds( 0, 0, jPanel4.getWidth(), jPanel4.getHeight() );
	}


	protected String getStrNotNullValue( String value )
	{
		return( value == null ? "" : value );
	}

	protected void initNameJPanel()
	{
		String value = getStrNotNullValue( getInitialProfileName() );
		_nameJPanel.setRegexOrProfileName(value);

		_nameJPanel.setActive( _profileModel.isActive() );
	}

	protected boolean existsInModel( LineOfTagsJPanel linePanel )
	{
		boolean result = false;

		if( linePanel != null )
			result = ( _profileModel.getListOfLines().indexOf( linePanel.getLineModel() ) >= 0 );

		return( result );
	}

	protected void updateLinePanels()
	{
		setPreventFromRepainting(true);

		removeLinePanels();

		createDefaultLineIfEmpty();
		createInitialLinesPanelsWithoutZooming();
		zoomLinePanels();

		addLinePanelsToContentPane();

		relocatePanelsAfterStart();

		initResizeRelocateItemsOComponentOnTheFly(_listOfLineJPanel,
													_linePanelsMapRRCI, true,
													this::shrinkTextComponents );
	}

	protected void relocatePanelsAfterStart()
	{
		relocatePanels( getZoomFactor() );
	}

	protected void revertContents()
	{
		initContents();

		updateLinePanels();
	}
/*
	protected void zoomLinePanels()
	{
		_linePanelsMapRRCI = new MapResizeRelocateComponentItem();

		for( LineOfTagsJPanel linePanel: _listOfLineJPanel )
		{
			if( ! alreadyInitialized( linePanel ) )
			{
				boolean isAlreadyZoomed = true;
				boolean postponeInitialization = true;
				ResizeRelocateItem rri = ExecutionFunctions.instance().safeFunctionExecution(
									() -> _linePanelsMapRRCI.putResizeRelocateComponentItem( linePanel,
														ResizeRelocateItem.RESIZE_TO_RIGHT,
														postponeInitialization,
														isAlreadyZoomed) );

				_linePanelsMapRRCI.putAll( linePanel.getResizeRelocateInfo() );
				a_intern.switchToZoomComponents( linePanel, _linePanelsMapRRCI );
			}
		}
	}
*/
	protected void zoomLinePanels()
	{
		_linePanelsMapRRCI = new MapResizeRelocateComponentItem();

		for( LineOfTagsJPanel linePanel: _listOfLineJPanel )
		{
			boolean isAlreadyZoomed = true;
			boolean postponeInitialization = true;
			ResizeRelocateItem rri = ExecutionFunctions.instance().safeFunctionExecution(
								() -> _linePanelsMapRRCI.putResizeRelocateComponentItem( linePanel,
//													ResizeRelocateItem.RESIZE_TO_RIGHT,
													ResizeRelocateItem.FILL_WHOLE_WIDTH,
													postponeInitialization,
													isAlreadyZoomed) );

			_linePanelsMapRRCI.putAll( zoomComponentOnTheFly( linePanel, rri ).getMapResizeRelocateComponentItem() );
		}
	}

	protected void removeLinePanel(LineOfTagsJPanel lineJPanel)
	{
		Container cp = getContentPane();

		removeListenersForMouseToHierarchy( lineJPanel );

		cp.remove( lineJPanel );
		lineJPanel.dispose();

		if( a_intern != null )
		{
			ComponentFunctions.instance().browseComponentHierarchy( lineJPanel,
				comp -> { a_intern.removeResizeRelocateComponentItem( comp ); return( null ); } );
		}
	}

	protected void removeLinePanels()
	{
		List<LineOfTagsJPanel> removed = new ArrayList<>();
		for( LineOfTagsJPanel lineJPanel: _listOfLineJPanel )
			if( !existsInModel( lineJPanel ) )
			{
				removeLinePanel( lineJPanel );
				removed.add( lineJPanel );
			}

		for( LineOfTagsJPanel lineJPanel: removed )
			_listOfLineJPanel.remove( lineJPanel );

//		_listOfLineJPanel.clear();
	}

	protected void initContents()
	{
		initNameJPanel();

		jCB_autocompletion.setSelected( getAppliConf().isAutocompletionForRegexActivated() );
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
		Vector<JPopupMenu> vectorJpopupMenus = new Vector<JPopupMenu>();
		vectorJpopupMenus.add( _popupMenu );

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putAll( _nameJPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_WIDTH +
															ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );

			for( LineOfTagsJPanel linePanel: _listOfLineJPanel )
			{
				mapRRCI.putResizeRelocateComponentItem( linePanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
				mapRRCI.putAll( linePanel.getResizeRelocateInfo() );
			}
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
									getAppliConf().getInternationalPropertiesPathInJar(),
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
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		registerInternationalString( CONF_MOVE_LINE_PANEL_HERE, "Move line here" );
		getInternationalization().setMaxWindowHeightNoLimit(false);

		resizeFrameToContents();

		updateLinePanels();

		adjustMaximumSizeToContents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jCB_autocompletion = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 0));
        getContentPane().setLayout(null);

        jPanel3.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setMinimumSize(new java.awt.Dimension(130, 50));
        jPanel4.setLayout(null);
        jPanel3.add(jPanel4);
        jPanel4.setBounds(250, 10, 130, 50);

        jCB_autocompletion.setText("activate auto completion");
        jCB_autocompletion.setName("jCB_autocompletion"); // NOI18N
        jCB_autocompletion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_autocompletionActionPerformed(evt);
            }
        });
        jPanel3.add(jCB_autocompletion);
        jCB_autocompletion.setBounds(10, 20, 220, 23);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 630, 70);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCB_autocompletionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_autocompletionActionPerformed
        // TODO add your handling code here:

        getAppliConf().setIsAutocompletionForRegexActivated( jCB_autocompletion.isSelected() );
    }//GEN-LAST:event_jCB_autocompletionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCB_autocompletion;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables


	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		_acceptPanel = compMapper.mapComponent( _acceptPanel );
		jCB_autocompletion = compMapper.mapComponent( jCB_autocompletion );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
	}

	public void revertModel()
	{
		_profileModel.init(_lastConfirmedProfileModel );
	}

	@Override
	public void revert(InformerInterface panel)
	{
		revertModel();

		revertContents();
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		_nameJPanel.validateChanges();

		for( LineOfTagsJPanel lineJPanel: _listOfLineJPanel )
			lineJPanel.validateChanges();
	}

	protected String getInitialProfileName()
	{
		return( _initialProfileName );
	}

	public String getProfileName()
	{
		return( _nameJPanel.getRegexOrProfileName() );
	}

	public boolean getActive()
	{
		return( _nameJPanel.getActive() );
	}

	protected void applyChanges()
	{
		_profileModel.setProfileName( getProfileName() );
		_profileModel.setActive( getActive() );

		for( LineOfTagsJPanel lineJPanel: _listOfLineJPanel )
			ExecutionFunctions.instance().safeMethodExecution( () -> lineJPanel.applyChanges() );
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
		revertModel();
//		revert( null );

		setWasSuccessful( false );

		formWindowClosing(true);
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();

		removeLinePanels();
		removeListeners();
	}

	protected void enablePopupMenuItems()
	{
//		_popupMenu.setAllEnabled(true);

		boolean insertsEnabled = ( _listOfLineJPanel.size() < 7 );
		boolean deleteEnabled = ( _listOfLineJPanel.size() > 1  );
		_popupMenu.setDeleteLineEnabled( deleteEnabled );

		_popupMenu.setInsertLineBeforeEnabled( insertsEnabled );
		_popupMenu.setInsertLineAfterEnabled( insertsEnabled );
	}

	protected void setClueOfPopupMenu( ClueOfPopupMenu clueOfPopupMenu )
	{
		_clueOfPopupMenuToShow = clueOfPopupMenu;

		repaint();
	}

	protected void applyChangesOfLines()
	{
		for( LineOfTagsJPanel lineJPanel: _listOfLineJPanel )
			lineJPanel.applyChangesWithoutValidation();
	}


	protected void insertEmptyLineAt( int index )
	{
		applyChangesOfLines();

		_profileModel.addEmptyLineOfTagRegexes( index );

		updateLinePanels();
	}

	protected void deleteLine( LineOfTagsJPanel itemToRemove )
	{
		if( itemToRemove != null )
			_profileModel.eraseLineModel( itemToRemove.getLineModel() );

		updateLinePanels();
	}

	protected void insertLineBefore()
	{
		if( _currentLineClicked != null )
			insertEmptyLineAt( _currentLineClicked.calculateIndex() );
	}

	protected void insertLineAfter()
	{
		if( _currentLineClicked != null )
			insertEmptyLineAt( _currentLineClicked.calculateIndex() + 1 );
	}

	protected void deleteLine()
	{
		applyChangesOfLines();

		deleteLine( _currentLineClicked );
	}

	protected LineOfTagsJPanel getCurrentLineClicked( MouseEvent evt )
	{
		LineOfTagsJPanel result = null;
		for( LineOfTagsJPanel linePanel: _listOfLineJPanel )
		{
			if( MouseFunctions.isMouseInsideComponent( linePanel, evt ) )
			{
				result = linePanel;
				break;
			}
		}

		return( result );
	}

	protected int getZoomedThickForPopupClues()
	{
		return( IntegerFunctions.zoomValueRound( THICK_FOR_POPUP_CLUES, getZoomFactor() ) );
	}

	protected void drawLine( Graphics grp, int xx1, int xx2, int yy )
	{
		int length = xx2 - xx1;
		length = IntegerFunctions.abs( length );

		int gap = length / 10;
		int initialX = (length >= 0) ? xx1 : xx2;
		int finalX = initialX + length - gap;
		initialX += gap;

		int thick = getZoomedThickForPopupClues();
		grp.setColor( getInvertibleColor(INVERTIBLE_RED_INDEX) );
		for( int ii=0; ii<thick; ii++ )
		{
			grp.drawLine( initialX, yy+ii, finalX, yy+ii );
		}
	}

	protected void drawRect( Graphics grp, Rectangle rect, Color color )
	{
		int thick = getZoomedThickForPopupClues();
		ImageFunctions.instance().drawRect(
			grp, rect.x, rect.y, rect.width, rect.height,
			color, -thick );
	}

	@Override
	public void paint( Graphics grp )
	{
		synchronized( this )
		{
			BufferedImage image = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			Graphics grp2 = image.createGraphics();

			super.paint( grp2 );

//			System.out.println( "Painting" );
			if( ( _clueOfPopupMenuToShow != null ) &&
				( _currentLineClicked != null ) )
			{
//				System.out.println( "Showing popup clue" );
				Rectangle rect = ViewFunctions.instance().getBoundsInWindow( _currentLineClicked );
				if( rect != null )
				{
					switch( _clueOfPopupMenuToShow )
					{
						case INSERT_LINE_BEFORE:
							drawLine( grp2, rect.x, rect.x + rect.width, rect.y );
						break;

						case INSERT_LINE_AFTER:
							drawLine( grp2, rect.x, rect.x + rect.width,
								rect.y + rect.height - THICK_FOR_POPUP_CLUES );
						break;

						case DELETE_LINE:
							drawRect( grp2, rect, getInvertibleColor(INVERTIBLE_RED_INDEX) );
						break;

						case MOVE_LINE:
							if( _destinationLinePanel != null )
							{
								drawRect( grp2, rect, getInvertibleColor(INVERTIBLE_ORANGE_INDEX) );

								if( _destinationLinePanel != _currentLineClicked )
								{
									rect = ViewFunctions.instance().getBoundsInWindow( _destinationLinePanel );

									drawRect( grp2, rect, getInvertibleColor(INVERTIBLE_RED_INDEX) );

									paintStringCentered( grp2, getInternationalString( CONF_MOVE_LINE_PANEL_HERE ),
														getInvertibleColor(INVERTIBLE_RED_INDEX), ViewFunctions.instance().getCenter(rect) );
								}
							}
						break;
					}
				}
			}

			grp.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null );
			grp2.dispose();

			if( isShowingPopup() )
				_popupMenu.repaint();
		}
	}

	protected void paintStringCentered( Graphics grp, String string, Color color, Point point )
	{
		ImageFunctions.instance().paintStringCentered(grp,
			FontFunctions.instance().getZoomedFont( getFont(), getZoomFactor() ),
			string, color, point );
	}

	protected boolean isShowingPopup()
	{
		return( _popupMenu.isVisible() );
	}

	protected void setDragging( boolean value )
	{
		boolean wasDragging = _isDragging;
		_isDragging = value;
		if( wasDragging && ! _isDragging )
		{
			setClueOfPopupMenu(null);
			_destinationLinePanel = null;
			_currentLineClicked = null;
		}
		else if( _isDragging )
			setClueOfPopupMenu(ClueOfPopupMenu.MOVE_LINE);
	}

	protected boolean isDragging()
	{
		return( _isDragging );
	}

	protected void moveLinePanel( LineOfTagsJPanel originLinePanel, 
									LineOfTagsJPanel destinationLinePanel )
	{
		applyChangesOfLines();

		if( ( originLinePanel != null ) && ( destinationLinePanel != null ) &&
			( originLinePanel != destinationLinePanel ) )
		{
			int destinationIndex = destinationLinePanel.calculateIndex();

			_profileModel.eraseLineModel( originLinePanel.getLineModel() );
			_profileModel.addEmptyLineOfTagRegexes( destinationIndex, originLinePanel.getLineModel() );

			updateLinePanels();
		}
	}

	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	@Override
	public void mouseDragged(MouseEvent evt)
	{
		if( isDragging() )
		{
			_destinationLinePanel = getCurrentLineClicked( evt );

			setDragging( _destinationLinePanel != null );
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setDragging( false );
	}

	@Override
	public void mouseClicked(MouseEvent evt) {

		if( SwingUtilities.isRightMouseButton(evt) )
		{
			_currentLineClicked = getCurrentLineClicked( evt );

			setDragging( false );
			doPopup( evt );
		}
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		if( SwingUtilities.isLeftMouseButton(evt) )
		{
			if( isAbleToDrag( evt.getComponent() ) )
			{
				_currentLineClicked = getCurrentLineClicked( evt );

				boolean isDragging = ( _currentLineClicked != null );
				if( isDragging )
				{
					_destinationLinePanel = _currentLineClicked;
				}
				setDragging( isDragging );
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if( isDragging() )
		{
			moveLinePanel( _currentLineClicked, _destinationLinePanel );

			setDragging( false );
		}

		if( ! isShowingPopup() )
			_currentLineClicked = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	protected void doPopup( MouseEvent evt )
	{
		if( _currentLineClicked != null )
			_popupMenu.doPopup(evt);
	}

	@Override
	public void setVisible( boolean value )
	{
		if( value && _validateAtOnce )
			SwingUtilities.invokeLater( () -> accept( null ) );

		super.setVisible( value );
	}

	protected void unblockComponentsAfterHavingZoomed( ExtendedZoomSemaphore ezs )
	{
		unblockComponentsAfterHavingZoomed( ezs, this::afterZoomingTask );
	}

	protected void afterZoomingTask()
	{
		shrinkTextComponents();
	}

	protected void shrinkTextComponents()
	{
		executeDelayedInvokeEventDispatchThread( ()-> {
			for( LineOfTagsJPanel linePanel: _listOfLineJPanel)
			{
				if( !_alreadyShrunklistOfLineJPanel.contains( linePanel ) )
				{
					linePanel.shrinkTextComponent();
					_alreadyShrunklistOfLineJPanel.add(linePanel);
				}
			}
		}
		, 500);
	}

	public static class ProfileNameJPanel extends BlockRegexOrProfileNameJPanel
	{
		public static final String GLOBAL_CONF_FILE_NAME = "ProfileNameJPanel.properties";
		
		ProfileModel _tagRegexConfContainer = null;
		public ProfileNameJPanel(ProfileModel tagRegexConfContainer,
									String initialBlockRegexOrProfileName,
									boolean isActive ) {
			super( null, initialBlockRegexOrProfileName, GLOBAL_CONF_FILE_NAME, isActive );

			_tagRegexConfContainer = tagRegexConfContainer;
		}

		@Override
		protected boolean checkIfValidRegexName( String name )
		{
			return( Objects.equals( name, getInitialName() ) || (_tagRegexConfContainer==null) ||
				!_tagRegexConfContainer.getParent().profileExists(name) );
		}
	}

	protected class ContextualMenu extends BaseJPopupMenu
	{
		JMenuItem _menuItem_insertLineBefore = null;
		JMenuItem _menuItem_insertLineAfter = null;
		JMenuItem _menuItem_deleteLine = null;

		public ContextualMenu()
		{
			super(TagsProfileConfJDialog.this);

			_menuItem_insertLineBefore = new JMenuItem( "Insert line before" );
			_menuItem_insertLineBefore.setName( "_menuItem_insertLineBefore" );
			_menuItem_insertLineAfter = new JMenuItem( "Insert line after" );
			_menuItem_insertLineAfter.setName( "_menuItem_insertLineAfter" );
			_menuItem_deleteLine = new JMenuItem( "Delete line" );
			_menuItem_deleteLine.setName( "_menuItem_deleteLine" );

			addMenuComponent(_menuItem_insertLineBefore );
			addMenuComponent(_menuItem_insertLineAfter );
			addMenuComponent( new JSeparator() );
			addMenuComponent(_menuItem_deleteLine );

			addMouseListenerToAllComponents();
		}

		public void setAllEnabled( boolean value )
		{
			_menuItem_insertLineBefore.setEnabled(value);
			_menuItem_insertLineAfter.setEnabled(value);
			_menuItem_deleteLine.setEnabled(value);
		}

		public void setInsertLineBeforeEnabled( boolean value )
		{
			_menuItem_insertLineBefore.setEnabled(value);
		}

		public void setInsertLineAfterEnabled( boolean value )
		{
			_menuItem_insertLineAfter.setEnabled(value);
		}

		public void setDeleteLineEnabled( boolean value )
		{
			_menuItem_deleteLine.setEnabled(value);
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
			if( _menuItem_insertLineBefore.isEnabled() && ( me.getSource() == _menuItem_insertLineBefore ) )
				setClueOfPopupMenu(ClueOfPopupMenu.INSERT_LINE_BEFORE);
			else if( _menuItem_insertLineAfter.isEnabled() && ( me.getSource() == _menuItem_insertLineAfter ) )
				setClueOfPopupMenu(ClueOfPopupMenu.INSERT_LINE_AFTER);
			else if( _menuItem_deleteLine.isEnabled() && ( me.getSource() == _menuItem_deleteLine ) )
				setClueOfPopupMenu(ClueOfPopupMenu.DELETE_LINE);
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			super.mouseExited( me );

			setClueOfPopupMenu(null);
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			Component comp = (Component) evt.getSource();

			if( comp == _menuItem_insertLineBefore )
				insertLineBefore();
			if( comp == _menuItem_insertLineAfter )
				insertLineAfter();
			else if( comp == _menuItem_deleteLine )
				deleteLine();

			setClueOfPopupMenu(null);

			setVisible(false);
		}

		@Override
		protected void preparePopupMenuItems() {
			enablePopupMenuItems();
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper) {
			_menuItem_insertLineBefore = mapper.mapComponent(_menuItem_insertLineBefore);
			_menuItem_insertLineAfter = mapper.mapComponent(_menuItem_insertLineAfter);
			_menuItem_deleteLine = mapper.mapComponent(_menuItem_deleteLine);

			super.setComponentMapper(mapper);
		}
	}

	protected static enum ClueOfPopupMenu
	{
		INSERT_LINE_BEFORE,
		INSERT_LINE_AFTER,
		DELETE_LINE,
		MOVE_LINE
	}

}
