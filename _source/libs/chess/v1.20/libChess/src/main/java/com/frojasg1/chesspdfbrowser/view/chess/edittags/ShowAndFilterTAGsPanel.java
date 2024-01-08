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
package com.frojasg1.chesspdfbrowser.view.chess.edittags;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItemComponentResizedListener;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.view.chess.renderer.cellrenderer.JLabelRenderer;
import com.frojasg1.desktop.libtablecolumnadjuster.TableColumnAdjuster;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.string.CreateCustomString;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Usuario
 */
public class ShowAndFilterTAGsPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
									implements MouseMotionListener, MouseListener,
												ActionListener, InternationalizedStringConf,
												ChangeLanguageClientInterface,
												ListSelectionListener,
												ResizeRelocateItemComponentResizedListener,
												InternallyMappedComponent
{
	protected static int MAX_NUMBER_OF_FILTERS_SAVED = 10;
	protected static String CONF_VALUE = "VALUE";

	protected SelectTAG_controller_interface _controller = null;

	protected ChessGameHeaderInfo _header = null;
	protected FilterTAGconfiguration _configuration = null;

	protected Vector<String> _vectorOfFilters = null;
	protected GroupOfTAGsInfo[] _arrayOfgroupOfTAGsInfo = null;

	protected boolean _modifiedByProgram = false;

	protected MapResizeRelocateComponentItem _mapRRCI = null;

	protected boolean _changedByCode = false;

	protected JCheckBox _lastGroupCheckBox = null;

	protected ContextualMenu _popupMenu = null;

	protected InternationalizedStringConf _languageConfiguration = null;

	protected ChangeLanguageServerInterface _changeLanguageServer = null;

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected boolean _initializing = true;

	@Override
	public void resizeRelocateItemComponentResized(Component comp, double newZoomFactor)
	{
		if( comp == jT_tags )
		{
			if( _currentZoomFactorForComponentResized != newZoomFactor )
			{
				updateTable();

				_currentZoomFactorForComponentResized = newZoomFactor;
			}
		}
	}

	protected static class GroupOfTAGsInfo
	{
		protected int _groupId = -1;
		protected JCheckBox _checkBox = null;

		public GroupOfTAGsInfo( int groupId, JCheckBox checkBox )
		{
			_groupId = groupId;
			_checkBox = checkBox;
		}
		
		public int getGroupId()
		{
			return( _groupId );
		}

		public JCheckBox getCheckBox()
		{
			return( _checkBox );
		}
	}

	public ChessGameHeaderInfo getHeader()
	{
		return( _header );
	}
	
	public void setHeader( ChessGameHeaderInfo header )
	{
		_header = header;
		updateTable();
	}

	/**
	 * Creates new form ShowAndFilterTAGsPanel
	 */
	public ShowAndFilterTAGsPanel( SelectTAG_controller_interface controller,
									ChessGameHeaderInfo header, FilterTAGconfiguration configuration )
	{
		super.init();

		_controller = controller;
		_header = header;
		_configuration = configuration;

		_vectorOfFilters = new Vector<String> ();
		
		initComponents();

		_popupMenu = new ContextualMenu();

//		createGroupOfTAGsInfoArray();

		_mapRRCI = createResizeRelocateInfo();

		setInitialValues();

//		setListeners();

//		updateTable();
	}

	public JPopupMenu getPopupMenu()
	{
		return( _popupMenu );
	}
	
	protected void createGroupOfTAGsInfoArray()
	{
		_arrayOfgroupOfTAGsInfo = new GroupOfTAGsInfo[]
									{
										new GroupOfTAGsInfo( ChessGameHeaderInfo.EVENT_RELATED_INFORMATION, jCB_event ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.GAME_CONCLUSION, jCB_gameConclusion ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.MANDATORY, jCB_mandatory ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.MISCELLANEOUS, jCB_miscellaneous ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.OPENING_RELATED_INFORMATION, jCB_opening ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.PLAYER_RELATED_INFORMATION, jCB_player ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.TIME_AND_DATE_RELATED_INFORMATION, jCB_timeAndDate ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.TIME_CONTROL, jCB_timeControl ),
										new GroupOfTAGsInfo( ChessGameHeaderInfo.ALTERNATIVE_STARTING_POSITION, jCB_alternativeStartingPosition )
									};
	}

	public void translateMappedComponents(ComponentMapper compMapper)
	{
		jCB_Filter = compMapper.mapComponent( jCB_Filter );
		jCB_alternativeStartingPosition = compMapper.mapComponent( jCB_alternativeStartingPosition );
		jCB_event = compMapper.mapComponent( jCB_event );
		jCB_gameConclusion = compMapper.mapComponent( jCB_gameConclusion );
		jCB_mandatory = compMapper.mapComponent( jCB_mandatory );
		jCB_miscellaneous = compMapper.mapComponent( jCB_miscellaneous );
		jCB_opening = compMapper.mapComponent( jCB_opening );
		jCB_player = compMapper.mapComponent( jCB_player );
		jCB_timeAndDate = compMapper.mapComponent( jCB_timeAndDate );
		jCB_timeControl = compMapper.mapComponent( jCB_timeControl );
		jLFilter = compMapper.mapComponent( jLFilter );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );
		jT_tags = compMapper.mapComponent( jT_tags );
		_popupMenu._menuItem_selectAll = compMapper.mapComponent( _popupMenu._menuItem_selectAll );
		_popupMenu._menuItem_unselectAll = compMapper.mapComponent( _popupMenu._menuItem_unselectAll );
		_popupMenu._menuItem_selectOnlyThisGroup = compMapper.mapComponent( _popupMenu._menuItem_selectOnlyThisGroup );
	}

	public void internationalizationInitializationEndCallback()
	{
		setListeners();
		createGroupOfTAGsInfoArray();
		_initializing = false;
		updateTable();
	}

	public void setListeners()
	{
		setListenersForMouse( jCB_event );
		setListenersForMouse( jCB_gameConclusion );
		setListenersForMouse( jCB_mandatory );
		setListenersForMouse( jCB_miscellaneous );
		setListenersForMouse( jCB_opening );
		setListenersForMouse( jCB_player );
		setListenersForMouse( jCB_timeAndDate );
		setListenersForMouse( jCB_timeControl );
		setListenersForMouse( jCB_alternativeStartingPosition );

		setListenersForMouse( jPanel1 );
/*
		jCB_Filter.getEditor().getEditorComponent().addKeyListener( new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jCB_FilterKeyReleased(evt);
            }
        });
*/
	}

	protected void setListenersForMouse( Component comp )
	{
		comp.addMouseListener( this );
		comp.addMouseMotionListener( this );
		
		if( comp instanceof JCheckBox )
		{
			JCheckBox cb = (JCheckBox) comp;
			cb.addActionListener( this );
		}
	}

	protected void setInitialValues()
	{
		setInitialForCheckBoxesValues();
		
		updateCheckBoxItemsFromConfiguration();
	}
	
	protected void setInitialForCheckBoxesValues()
	{
		if( _configuration != null )
		{
			jCB_event.setSelected( _configuration.getEventFilterSelected() );
			jCB_gameConclusion.setSelected( _configuration.getGameConclusionFilterSelected());
			jCB_mandatory.setSelected( _configuration.getMandatoryFilterSelected() );
			jCB_miscellaneous.setSelected( _configuration.getMiscellaneousFilterSelected() );
			jCB_opening.setSelected( _configuration.getOpeningFilterSelected() );
			jCB_player.setSelected( _configuration.getPlayerFilterSelected() );
			jCB_timeAndDate.setSelected( _configuration.getTimeAndDateFilterSelected() );
			jCB_timeControl.setSelected( _configuration.getTimeControlFilterSelected() );
			jCB_alternativeStartingPosition.setSelected( _configuration.getAlternativeStartingFilterSelected() );
		}
	}

	protected void updateConfigurationFromCheckBoxes()
	{
		if( _configuration != null )
		{
			_configuration.setEventFilterSelected( jCB_event.isSelected() );
			_configuration.setGameConclusionFilterSelected( jCB_gameConclusion.isSelected() );
			_configuration.setMandatoryFilterSelected( jCB_mandatory.isSelected() );
			_configuration.setMiscellaneousFilterSelected( jCB_miscellaneous.isSelected() );
			_configuration.setOpeningFilterSelected( jCB_opening.isSelected() );
			_configuration.setPlayerFilterSelected( jCB_player.isSelected() );
			_configuration.setTimeAndDateFilterSelected( jCB_timeAndDate.isSelected() );
			_configuration.setTimeControlFilterSelected( jCB_timeControl.isSelected() );
			_configuration.setAlternativeStartingFilterSelected( jCB_alternativeStartingPosition.isSelected() );
		}
	}

	protected void updateConfigurationFromFilterComboBox()
	{
		if( _configuration != null )
		{
			Iterator<String> it = _vectorOfFilters.iterator();
			int index = 1;

			while( it.hasNext() )
			{
				_configuration.setFilterItemForTAGs( index, it.next() );
				index++;
			}
		}
	}

	public void updateConfiguration()
	{
		updateConfigurationFromCheckBoxes();
		updateConfigurationFromFilterComboBox();
	}

	protected void updateCheckBoxItemsFromConfiguration()
	{
		if( _configuration != null )
		{
			_vectorOfFilters.clear();

			int index=1;
			String filterItem = "";
			do
			{
				filterItem = _configuration.getFilterItemForTAGs( index );
				if( filterItem != null )
					_vectorOfFilters.add(filterItem);
				index++;
			}
			while( ( filterItem != null ) && ( index <= MAX_NUMBER_OF_FILTERS_SAVED ) );

			DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>( _vectorOfFilters );
			jCB_Filter.setModel( dcbm );

			if( _vectorOfFilters.size() > 0 )
			{
				_modifiedByProgram = true;
				jCB_Filter.setSelectedIndex(0);
				_modifiedByProgram = false;
			}
		}
	}

	protected void changeSelectedFilterVector( String filter )
	{
		int repeatedIndex = getRepeatedIndex( _vectorOfFilters, filter );

		if( repeatedIndex > 0 )
			_vectorOfFilters.remove( repeatedIndex );

		_vectorOfFilters.insertElementAt( filter, 0 );

		while( _vectorOfFilters.size() > MAX_NUMBER_OF_FILTERS_SAVED )
			_vectorOfFilters.remove( MAX_NUMBER_OF_FILTERS_SAVED );
	}

	protected void updateComboBox_newFilterSelected( String filter )
	{
		changeSelectedFilterVector( filter );

		_modifiedByProgram = true;

		DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>( _vectorOfFilters );
		jCB_Filter.setModel( dcbm );

		if( _vectorOfFilters.size() > 0 )
		{
			jCB_Filter.setSelectedIndex(0);
		}

		_modifiedByProgram = false;
	}

	protected void newFilterSelected( String filter )
	{
		updateComboBox_newFilterSelected( filter );

		updateTable();
	}

	protected int getRepeatedIndex( Vector<String> vector, String item )
	{
		int result = -1;
		if( item != null )
		{
			int index = 0;
			Iterator<String> it = vector.iterator();
			while( (result == -1 ) && it.hasNext() )
			{
				String current = (String) it.next();
				if( item.equals( current ) )
					result = index;
				index++;
			}
		}
		return( result );
	}

	protected Object[] getColumnIds()
	{
		Object[] result = null;

		if( _header == null )
			result = new Object[]{	"TAG"	};
		else
			result = new Object[]{	"TAG", getInternationalString( CONF_VALUE )	};

		return( result );
	}

	protected Object[] getTAGrow( String tag )
	{
		Object[] result = null;

		if( _header == null )
			result = new Object[]{ tag };
		else
		{
			String value = _header.get( tag );
			if( value == null )
				value = "";

			result = new Object[]{	tag, value	};
		}

		return( result );
	}

/*
	protected void addAllRowsToTable( DefaultTableModel dtm )
	{
		String filter = (String) jCB_Filter.getSelectedItem();
		addAllRowsToTable( dtm, filter );
	}
*/
	protected void addAllRowsToTable( DefaultTableModel dtm, String filter )
	{
		Pattern pattern = null;

		try
		{
			pattern = Pattern.compile( filter );
		}
		catch( Throwable th )
		{
			pattern = null;
		}

		for( int ii=0; ii<_arrayOfgroupOfTAGsInfo.length; ii++ )
		{
			if( _arrayOfgroupOfTAGsInfo[ii].getCheckBox().isSelected() )
			{
				String[] tagsOfGroupArray = ChessGameHeaderInfo.getTAGgroup( _arrayOfgroupOfTAGsInfo[ii].getGroupId() );

				for( int jj=0; jj<tagsOfGroupArray.length; jj++ )
				{
					boolean matches = (pattern == null);

					if( ! matches )
					{
						Matcher matcher = pattern.matcher( tagsOfGroupArray[jj] );

						matches = matcher.find();
					}

					if( matches )
						dtm.addRow( getTAGrow( tagsOfGroupArray[jj] ));
				}
			}
		}
	}

	protected String getSelectedTag()
	{
		String result = null;
		
		int[] selection = jT_tags.getSelectedRows();
		if( ( selection != null ) && ( selection.length > 0 ) )
		{
			result = (String) jT_tags.getModel().getValueAt( jT_tags.convertRowIndexToModel(selection[0]), 0 );
		}
		
		return( result );
	}

	protected void selectPreviousSelectedTag( String tagToSelect )
	{
		TableModel model = jT_tags.getModel();

		int index = -1;
		for( int ii=0; (ii<model.getRowCount()) && (index == -1); ii++ )
		{
			if( model.getValueAt( ii, 0 ).equals( tagToSelect ) )
				index = ii;
		}

		if( index > -1 )
		{
			jT_tags.changeSelection(index, 0, false, false);
			_controller.selectTAG(tagToSelect);
		}
		else
		{
			_controller.selectTAG(null);
		}
	}

	public void updateTable()
	{
		if( !_initializing )
		{
			try
			{
				String selectedTag = getSelectedTag();
				String editedTag = (String) jCB_Filter.getEditor().getItem();

				DefaultTableModel dtm = new DefaultTableModel();

				dtm.setColumnIdentifiers( getColumnIds() );

				if( editedTag == null )
					editedTag = selectedTag;

				addAllRowsToTable( dtm, editedTag );

				jT_tags.setModel( dtm );

				selectPreviousSelectedTag( selectedTag );

				jT_tags.getSelectionModel().addListSelectionListener( this );

				for (int ii = 0; ii < jT_tags.getColumnCount(); ii++)
				{
					Class<?> col_class = jT_tags.getColumnClass(ii);
					jT_tags.setDefaultEditor(col_class, null);        // remove editor
					jT_tags.setDefaultRenderer( col_class, createCellRenderer() );
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						M_adjustColumnWidths();
					}
				});

			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected TableCellRenderer createCellRenderer()
	{
		TableCellRenderer result = new JLabelRenderer( jT_tags.getFont() );

		if( wasLatestModeDark() )
			result = getColorInversor().createTableCellRendererColorInversor(result, "cells");

		return( result );
	}

	@Override
	public void valueChanged(ListSelectionEvent lse)
	{
		if (!lse.getValueIsAdjusting())
		{
			try
			{
				String tag = getSelectedTag();

				if( (tag != null ) && ( _controller != null ) )
					_controller.selectTAG( tag );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void M_adjustColumnWidths()
	{
		TableColumnAdjuster tca = new TableColumnAdjuster(jT_tags);

		try
		{
			int maxWidth = 250;
			tca.setMaxWidthAllowed(maxWidth);
		}
		catch( Throwable th )
		{
		}

		tca.adjustColumns();

		jScrollPane1.setViewportView( jT_tags );
	}

	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		if( _mapRRCI == null )
		{
			_mapRRCI = createResizeRelocateInfo();
		}

		return( _mapRRCI );
	}

	protected MapResizeRelocateComponentItem createResizeRelocateInfo()
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();

		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jCB_Filter, ResizeRelocateItem.RESIZE_TO_RIGHT  );

			mapRRCI.putResizeRelocateComponentItem( jCB_opening, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jCB_gameConclusion, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jCB_miscellaneous, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jCB_timeAndDate, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jCB_timeControl, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jCB_alternativeStartingPosition, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
																		ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );


			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );

			boolean postponeInitialization = false;
			ResizeRelocateItem rriJT_tags = mapRRCI.putResizeRelocateComponentItem( jT_tags,
																ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.RESIZE_TO_BOTTOM,
																postponeInitialization );

			mapRRCI.putResizeRelocateComponentItem(rriJT_tags);
			rriJT_tags.addResizeRelocateItemComponentResizedListener( this );
//			mapRRCI.putResizeRelocateComponentItem( jT_tags, ResizeRelocateItem.RESIZE_TO_RIGHT +
//																ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	
		return( mapRRCI );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jCB_mandatory = new javax.swing.JCheckBox();
        jCB_player = new javax.swing.JCheckBox();
        jCB_event = new javax.swing.JCheckBox();
        jCB_opening = new javax.swing.JCheckBox();
        jCB_timeAndDate = new javax.swing.JCheckBox();
        jCB_timeControl = new javax.swing.JCheckBox();
        jCB_gameConclusion = new javax.swing.JCheckBox();
        jCB_miscellaneous = new javax.swing.JCheckBox();
        jCB_alternativeStartingPosition = new javax.swing.JCheckBox();
        jLFilter = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jT_tags = new javax.swing.JTable();
        jCB_Filter = new javax.swing.JComboBox();

        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(590, 270));
        setLayout(null);

        jPanel2.setPreferredSize(new java.awt.Dimension(590, 270));
        jPanel2.setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(null);

        jCB_mandatory.setText("Mandatory TAGs");
        jCB_mandatory.setName("jCB_mandatory"); // NOI18N
        jPanel1.add(jCB_mandatory);
        jCB_mandatory.setBounds(10, 10, 160, 24);

        jCB_player.setText("Player");
        jCB_player.setName("jCB_player"); // NOI18N
        jPanel1.add(jCB_player);
        jCB_player.setBounds(10, 40, 160, 24);

        jCB_event.setText("Event");
        jCB_event.setName("jCB_event"); // NOI18N
        jPanel1.add(jCB_event);
        jCB_event.setBounds(10, 70, 160, 24);

        jCB_opening.setText("Opening");
        jCB_opening.setName("jCB_opening"); // NOI18N
        jPanel1.add(jCB_opening);
        jCB_opening.setBounds(170, 10, 160, 24);

        jCB_timeAndDate.setText("Time and date");
        jCB_timeAndDate.setName("jCB_timeAndDate"); // NOI18N
        jPanel1.add(jCB_timeAndDate);
        jCB_timeAndDate.setBounds(170, 40, 160, 24);

        jCB_timeControl.setText("Time Control");
        jCB_timeControl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCB_timeControl.setName("jCB_timeControl"); // NOI18N
        jPanel1.add(jCB_timeControl);
        jCB_timeControl.setBounds(170, 70, 160, 24);

        jCB_gameConclusion.setText("Game conclusion");
        jCB_gameConclusion.setName("jCB_gameConclusion"); // NOI18N
        jPanel1.add(jCB_gameConclusion);
        jCB_gameConclusion.setBounds(330, 40, 250, 24);

        jCB_miscellaneous.setText("Miscellaneous");
        jCB_miscellaneous.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCB_miscellaneous.setName("jCB_miscellaneous"); // NOI18N
        jPanel1.add(jCB_miscellaneous);
        jCB_miscellaneous.setBounds(330, 70, 250, 24);

        jCB_alternativeStartingPosition.setText("Alternative starting position");
        jCB_alternativeStartingPosition.setName("jCB_alternativeStartingPosition"); // NOI18N
        jPanel1.add(jCB_alternativeStartingPosition);
        jCB_alternativeStartingPosition.setBounds(330, 10, 250, 24);

        jPanel2.add(jPanel1);
        jPanel1.setBounds(0, 10, 590, 100);

        jLFilter.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLFilter.setText("Filter :");
        jLFilter.setName("jLFilter"); // NOI18N
        jPanel2.add(jLFilter);
        jLFilter.setBounds(10, 120, 90, 16);

        jT_tags.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jT_tags.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jT_tags);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(10, 150, 570, 110);

        jCB_Filter.setEditable(true);
        jCB_Filter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCB_Filter.setMinimumSize(new java.awt.Dimension(127, 20));
        jCB_Filter.setName("jCB_Filter"); // NOI18N
        jCB_Filter.setPreferredSize(new java.awt.Dimension(127, 20));
        jCB_Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_FilterActionPerformed(evt);
            }
        });
        jPanel2.add(jCB_Filter);
        jCB_Filter.setBounds(110, 120, 330, 20);

        add(jPanel2);
        jPanel2.setBounds(0, 0, 590, 270);
    }// </editor-fold>//GEN-END:initComponents

    private void jCB_FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_FilterActionPerformed
        // TODO add your handling code here:

		if( ! _modifiedByProgram )
		{
			try
			{
				ComboBoxModel<String> dcbm = jCB_Filter.getModel();

				String selectedItem = (String) dcbm.getSelectedItem();
				if( ( selectedItem != null ) &&
					!( ( _vectorOfFilters.size() > 0 ) && _vectorOfFilters.get(0).equals( selectedItem ) ) )
				{
					newFilterSelected( selectedItem );
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

    }//GEN-LAST:event_jCB_FilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jCB_Filter;
    private javax.swing.JCheckBox jCB_alternativeStartingPosition;
    private javax.swing.JCheckBox jCB_event;
    private javax.swing.JCheckBox jCB_gameConclusion;
    private javax.swing.JCheckBox jCB_mandatory;
    private javax.swing.JCheckBox jCB_miscellaneous;
    private javax.swing.JCheckBox jCB_opening;
    private javax.swing.JCheckBox jCB_player;
    private javax.swing.JCheckBox jCB_timeAndDate;
    private javax.swing.JCheckBox jCB_timeControl;
    private javax.swing.JLabel jLFilter;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jT_tags;
    // End of variables declaration//GEN-END:variables


	protected JPopupMenu getComboPopup( JComboBox combo )
	{
		return( ComboBoxFunctions.instance().getComboPopup(combo) );
	}

	protected void selectAll( boolean value )
	{
		_changedByCode = true;
		jCB_event.setSelected( value );
		jCB_gameConclusion.setSelected( value );
		jCB_mandatory.setSelected( value );
		jCB_miscellaneous.setSelected( value );
		jCB_opening.setSelected( value );
		jCB_player.setSelected( value );
		jCB_timeAndDate.setSelected( value );
		jCB_timeControl.setSelected( value );
		jCB_alternativeStartingPosition.setSelected( value );
		_changedByCode = false;
	}

	protected void selectAll()
	{
		selectAll( true );
		updateTable();
	}
	
	protected void unselectAll()
	{
		selectAll( false );
		updateTable();
	}

	protected void onlySelectThisGroup()
	{
		if( _lastGroupCheckBox != null )
		{
			selectAll( false );
			_lastGroupCheckBox.setSelected(true);
			updateTable();
		}
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent evt)
	{
		if( ! _changedByCode )
		{
			updateTable();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent evt)
	{
		if( SwingUtilities.isRightMouseButton(evt) )
		{
			doPopup( evt );
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if( ! ScreenFunctions.isInsideComponent( jPanel1, e.getLocationOnScreen() ) )
			_lastGroupCheckBox = null;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent evt)
	{
		Component comp = evt.getComponent();
		
		if( comp instanceof JCheckBox )
			_lastGroupCheckBox = (JCheckBox) comp;
	}

	protected void doPopup( MouseEvent evt )
	{
		_popupMenu.doPopup(evt);
	}

	protected void enablePopupMenuItems()
	{
		_popupMenu.setAllEnabled(true);

		if( _lastGroupCheckBox == null )
		{
			_popupMenu.setSelectOnlyThisGroupEnabled(false);
		}
	}

	@Override
	public String getLanguage()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _changeLanguageServer != null )
		{
			_changeLanguageServer.unregisterChangeLanguageObserver( this );
		}
	}

	@Override
	public void registerToChangeLanguageAsObserver( ChangeLanguageServerInterface conf)
	{
		if( ( _changeLanguageServer == null ) && ( conf != null ) )
		{
			_changeLanguageServer = conf;
			_changeLanguageServer.registerChangeLanguageObserver( this );
		}
	}

	public void releaseResources()
	{
		unregisterFromChangeLanguageAsObserver();
	}

	@Override
	public void changeLanguage(String language) throws Exception
	{
		updateTable();

//		SwingUtilities.invokeLater( () -> jScrollPane1.setSize( jScrollPane1.getSize() ) );
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		if( _languageConfiguration != null )
			_languageConfiguration.registerInternationalString( label, value );
	}

	@Override
	public String getInternationalString(String label)
	{
		String result = null;

		if( _languageConfiguration != null )
			result = _languageConfiguration.getInternationalString( label );

		return( result );
	}

	public void setLanguageConfiguration( InternationalizedStringConf languageConfiguration )
	{
		_languageConfiguration = languageConfiguration;

		registerInternationalString(CONF_VALUE, "Value" );

		if( _languageConfiguration instanceof ChangeLanguageServerInterface )
		{
			ChangeLanguageServerInterface clsi = (ChangeLanguageServerInterface) _languageConfiguration;
			registerToChangeLanguageAsObserver( clsi );
		}
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( getInternationalString( label ), args) );
	}


	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jCB_Filter = compMapper.mapComponent( jCB_Filter );
		jCB_alternativeStartingPosition = compMapper.mapComponent( jCB_alternativeStartingPosition );
		jCB_event = compMapper.mapComponent( jCB_event );
		jCB_gameConclusion = compMapper.mapComponent( jCB_gameConclusion );
		jCB_mandatory = compMapper.mapComponent( jCB_mandatory );
		jCB_miscellaneous = compMapper.mapComponent( jCB_miscellaneous );
		jCB_opening = compMapper.mapComponent( jCB_opening );
		jCB_player = compMapper.mapComponent( jCB_player );
		jCB_timeAndDate = compMapper.mapComponent( jCB_timeAndDate );
		jCB_timeControl = compMapper.mapComponent( jCB_timeControl );
		jLFilter = compMapper.mapComponent( jLFilter );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );
		jT_tags = compMapper.mapComponent( jT_tags );

		super.setComponentMapper(compMapper);
	}

	protected class ContextualMenu extends BaseJPopupMenu
	{
		JMenuItem _menuItem_selectAll = null;
		JMenuItem _menuItem_unselectAll = null;
		JMenuItem _menuItem_selectOnlyThisGroup = null;

		public ContextualMenu()
		{
			super(ShowAndFilterTAGsPanel.this);

			_menuItem_selectAll = new JMenuItem( "Select all groups" );
			_menuItem_selectAll.setName( "_menuItem_selectAll" );
			_menuItem_unselectAll = new JMenuItem( "Unselect all groups" );
			_menuItem_unselectAll.setName( "_menuItem_unselectAll" );
			_menuItem_selectOnlyThisGroup = new JMenuItem( "Select only this group" );
			_menuItem_selectOnlyThisGroup.setName( "_menuItem_selectOnlyThisGroup" );

			addMenuComponent( _menuItem_selectOnlyThisGroup );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_selectAll );
			addMenuComponent( _menuItem_unselectAll );

			addMouseListenerToAllComponents();
		}

		public void setAllEnabled( boolean value )
		{
			_menuItem_selectAll.setEnabled(value);
			_menuItem_unselectAll.setEnabled(value);
			_menuItem_selectOnlyThisGroup.setEnabled(value);
		}

		public void setSelectAllEnabled( boolean value )
		{
			_menuItem_selectAll.setEnabled(value);
		}

		public void setUnselectAllEnabled( boolean value )
		{
			_menuItem_unselectAll.setEnabled(value);
		}

		public void setSelectOnlyThisGroupEnabled( boolean value )
		{
			_menuItem_selectOnlyThisGroup.setEnabled(value);
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			Component comp = (Component) evt.getSource();
			
			if( comp == _menuItem_selectAll )
				selectAll();
			if( comp == _menuItem_unselectAll )
				unselectAll();
			else if( comp == _menuItem_selectOnlyThisGroup )
				onlySelectThisGroup();

			setVisible(false);
		}

		@Override
		protected void preparePopupMenuItems() {
			enablePopupMenuItems();
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper) {
			_menuItem_selectAll = mapper.mapComponent(_menuItem_selectAll);
			_menuItem_unselectAll = mapper.mapComponent(_menuItem_unselectAll);
			_menuItem_selectOnlyThisGroup = mapper.mapComponent(_menuItem_selectOnlyThisGroup);

			super.setComponentMapper(mapper);
		}
	}

}
