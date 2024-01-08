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
package com.frojasg1.general.desktop.view.combobox;

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author fjavier.rojas
 */
public class MasterComboBoxJPanel
	extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements JComboBoxContainer, ComposedComponent,
				ComboBoxGroupManager
{
	protected ComboBoxGroupManager _comboBoxGroupManager = null;

//	protected TextComboBoxContent _cbContents = null;

	protected MapResizeRelocateComponentItem _mapRRCI = null;

	protected ActionListener _actionListener = null;

	protected ControllerInvoker _addInvoker = ( cont, result ) -> cont.added( this, result, null);
	protected ControllerInvoker _removeInvoker = ( cont, result ) -> cont.removed( this, result, null);
	protected ControllerInvoker _modifyInvoker = ( cont, result ) -> cont.modify( this, result);

//	protected AddRemoveModifyItemNewSelectionController<DD> _controller = null;

	protected List<JComboBox> _comboList = null;

	/**
	 * Creates new form MasterComboBoxJPanel
	 */
	public MasterComboBoxJPanel( //AddRemoveModifyItemNewSelectionController controller,
								ComboBoxGroupManager comboBoxGroupManager )
	{
		initComponents();

//		_controller = controller;
		_comboBoxGroupManager = comboBoxGroupManager;
	}

	public void init()
	{
		super.init();

		addListeners();

		_mapRRCI = createResizeRelocateInfo();
//		_cbContents.addSelectionChangedListener((sender, prevItem, newItem) -> { if( _controller != null )	_controller.comboBoxSelectionChanged( this, prevItem, newItem );	});

		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.addComboComp(this);
	}

	public TextComboBoxContent getComboContents()
	{
		TextComboBoxContent result = null;
		if( _comboBoxGroupManager != null )
		{
			result = _comboBoxGroupManager.getComboBoxContent();
		}

		return( result );
	}

	protected ActionListener createActionListener()
	{
		return( evt -> actionPerformed( evt ) );
	}

	protected void addListeners()
	{
		_actionListener = createActionListener();

		jB_add.addActionListener( _actionListener );
		jB_remove.addActionListener( _actionListener );
		jB_modify.addActionListener( _actionListener );
	}

	public JButton getAddButton()
	{
		return( jB_add );
	}

	public JButton getRemoveButton()
	{
		return( jB_remove );
	}

	public JButton getModifyButton()
	{
		return( jB_modify );
	}

	public JComboBox<String> getCombo()
	{
		return( jComboBox1 );
	}

	@Override
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
			boolean postponeInitialization = true;
			mapRRCI.putResizeRelocateComponentItem( this, ResizeRelocateItem.FILL_WHOLE_PARENT, postponeInitialization );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jComboBox1, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jB_add, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jB_remove, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jB_modify, ResizeRelocateItem.MOVE_TO_RIGHT );

//			HintForComponent hfc = new HintForComponent( jB_acceptChanges, "Accept changes" );
//			HintForComponent hfc2 = new HintForComponent( jB_eraseTag, "Erase record" );
//			HintForComponent hfc3 = new HintForComponent( jB_revertChanges, "Revert changes" );
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

        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jB_remove = new javax.swing.JButton();
        jB_add = new javax.swing.JButton();
        jB_modify = new javax.swing.JButton();

        setLayout(null);

        jPanel1.setLayout(null);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(jComboBox1);
        jComboBox1.setBounds(10, 10, 150, 20);

        jB_remove.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_remove.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_remove.setName("name=jB_remove,icon=com/frojasg1/generic/resources/addremovemodify/remove.png"); // NOI18N
        jB_remove.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(jB_remove);
        jB_remove.setBounds(190, 10, 20, 20);

        jB_add.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_add.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_add.setName("name=jB_add,icon=com/frojasg1/generic/resources/addremovemodify/add.png"); // NOI18N
        jB_add.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(jB_add);
        jB_add.setBounds(167, 10, 20, 20);

        jB_modify.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_modify.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_modify.setName("name=jB_modify,icon=com/frojasg1/generic/resources/addremovemodify/modify.png"); // NOI18N
        jB_modify.setOpaque(false);
        jB_modify.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(jB_modify);
        jB_modify.setBounds(213, 10, 20, 20);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 240, 40);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_add;
    private javax.swing.JButton jB_modify;
    private javax.swing.JButton jB_remove;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

	protected void removeListeners()
	{
		jB_add.removeActionListener(_actionListener);
		jB_remove.removeActionListener(_actionListener);
		jB_modify.removeActionListener(_actionListener);
	}

	public void dispose()
	{
		removeListeners();
	}

	protected void actionPerformed( ActionEvent evt )
	{
		if( evt.getSource() == jB_add )
			addEvent();
		else if( evt.getSource() == jB_remove )
			removeEvent();
		else if( evt.getSource() == jB_modify )
			modifyEvent();
	}

	protected AddRemoveModifyItemResult createAddRemoveItemResult()
	{
		return( new AddRemoveModifyItemResultImpl() );
	}

	protected void invokeController( String item, ControllerInvoker invoker )
	{
		AddRemoveModifyItemNewSelectionController controller = _comboBoxGroupManager;
		if( controller != null )
		{
			AddRemoveModifyItemResult ariResult = createAddRemoveItemResult();
			ariResult.setItem(item);

			invoker.invoke( controller, ariResult);
		}
	}
/*
	protected String getItemToAdd()
	{
		return( invokeController( getSelectedItem(), _addInvoker ) );
	}
*/
	protected void addEvent()
	{
//		String newAddedItem = getItemToAdd();
/*
		String selectedItem = getSelectedItem();
		addNewItem( selectedItem );
*/
		invokeController( getSelectedItem(), _addInvoker );
	}
/*
	protected String getItemToRemove()
	{
		return( invokeController( getSelectedItem(), _removeInvoker ) );
	}
*/
	protected void removeEvent()
	{
//		String itemToRemove = getItemToRemove();

//		removeItem( itemToRemove );
		invokeController( getSelectedItem(), _removeInvoker );
	}

	protected void modifyEvent()
	{
		invokeController( getSelectedItem(), _modifyInvoker );
	}

	@Override
	public String getSelectedItem()
	{
		return( (String) jComboBox1.getSelectedItem() );
	}

/*
	public void addNewItem( String item )
	{
		if( ( item != null ) && ( _cbContents != null )  )
			_cbContents.newItemSelected(item);
	}

	public void removeItem( String item )
	{
		if( _cbContents != null )
			_cbContents.removeItem(item);
	}
*/
	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		jB_add = compMapper.mapComponent( jB_add );
		jB_modify = compMapper.mapComponent( jB_modify );
		jB_remove = compMapper.mapComponent( jB_remove );
		jComboBox1 = compMapper.mapComponent( jComboBox1 );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		_comboList = null;

		super.setComponentMapper(compMapper);
	}

	public List<JComboBox> createComboBoxList()
	{
		List<JComboBox> result = new ArrayList<>();
		result.add( jComboBox1 );

		return( result );
	}

	@Override
	public List<JComboBox> getComboBoxList()
	{
		if( _comboList == null )
			_comboList = createComboBoxList();
				
		return( _comboList );
	}

	@Override
	public void updateCombos()
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.updateCombos();
	}

	@Override
	public void updateCombosKeepingSelection()
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.updateCombosKeepingSelection();
	}

	@Override
	public void setListOfItems(List<String> list)
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.setListOfItems(list);
	}

	@Override
	public void setComboBoxContent(TextComboBoxContent content)
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.setComboBoxContent(content);
	}

	@Override
	public TextComboBoxContent getComboBoxContent()
	{
		TextComboBoxContent result = null;

		if( _comboBoxGroupManager != null )
			result = _comboBoxGroupManager.getComboBoxContent();

		return( result );
	}

	@Override
	public void setController(AddRemoveModifyItemNewSelectionController controller)
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.setController(controller);
	}

	@Override
	public List<Component> getComboCompList()
	{
		List<Component> result = null;

		if( _comboBoxGroupManager != null )
			result = _comboBoxGroupManager.getComboCompList();

		return( result );
	}

	@Override
	public void addComboComp(Component comboComp)
	{
		if( _comboBoxGroupManager != null )
			_comboBoxGroupManager.addComboComp(comboComp);
	}

	@Override
	public boolean removeComboComp(Component comboComp)
	{
		boolean result = false;
		if( _comboBoxGroupManager != null )
			result = _comboBoxGroupManager.removeComboComp(comboComp);

		return( result );
	}

	@Override
	public void saveCurrentItem() {
	}

	@Override
	public void added(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback) {
		// intentionally left blank
	}

	@Override
	public void removed(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback) {
		// intentionally left blank
	}

	@Override
	public void modify(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData) {
	}

	@Override
	public void comboBoxSelectionChanged(ComboBoxGroupManager sender, String previousSelectedItem, String newSelection) {

		System.out.println( "MasterComboBoxJPanel - comboBoxSelectionChanged" );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( jPanel1.getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( jPanel1.getBounds() );
	}

	protected interface ControllerInvoker<DD extends AddRemoveModifyItemResult>
	{
		public void invoke( AddRemoveModifyItemNewSelectionController controller, AddRemoveModifyItemResult result );
	}
}
