
package com.frojasg1.general.desktop.view.componentwrapper.combobox;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.desktop.view.componentwrapper.JComponentWrapper;
import com.frojasg1.general.desktop.view.componentwrapper.combobox.evt.JComboBoxWrapperEventBuilderBase;
import com.frojasg1.general.desktop.view.componentwrapper.combobox.evt.JComboBoxWrapperNewItemSelected;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.transfer.file.FileTransferHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComboBoxUI;

/**
 *
 * @author fjavier.rojas
 */
public class JComboBoxWrapper<RR> extends JComponentWrapper<JComboBox<RR>, ComboBoxUI, JComboBoxWrapperEventBase<RR>, Object>{

	protected Supplier<RR[]> _arrayBuilderForFillingCombo;

	protected Class<RR> _itemClass;

	protected PopupMenuListener _popupMenuListener;

	protected ActionListener _actionListener;

	protected Function<RR, ?> _functionForEquals;

	public JComboBoxWrapper( JComboBox<RR> combo, Class<RR> itemClass, Supplier<RR[]> arrayBuilderForFillingCombo )
	{
		this( combo, itemClass, arrayBuilderForFillingCombo, Function.identity() );
	}

	public JComboBoxWrapper( JComboBox<RR> combo, Class<RR> itemClass,
							Supplier<RR[]> arrayBuilderForFillingCombo,
							Function<RR, ?> functionForEquals)
	{
		this( combo, itemClass, arrayBuilderForFillingCombo, functionForEquals, true );
	}

	protected JComboBoxWrapper( JComboBox<RR> combo, Class<RR> itemClass,
							Supplier<RR[]> arrayBuilderForFillingCombo,
							Function<RR, ?> functionForEquals,
							boolean init)
	{
		super( combo, new JComboBoxWrapperEventBuilderBase<RR>(combo, itemClass, true) );

		_functionForEquals = functionForEquals;
		_itemClass = itemClass;
		_arrayBuilderForFillingCombo = arrayBuilderForFillingCombo;

		if( init )
			init();
	}

	@Override
	protected BaseJPopupMenu createBasePopupMenu() {
		return( null );
	}

	@Override
	protected FileTransferHandler<Object> createFileTransferHandler() {
		return( null );
	}

	@Override
	protected void installUI(ComboBoxUI compUI) {

	}

	@Override
	protected void updateUI(ComboBoxUI compUI) {

	}

	@Override
	protected void addListeners() {
		getCombo().addPopupMenuListener( getPopupMenuListener() );
		getCombo().addActionListener( getActionListener() );
	}

	@Override
	protected void removeListeners() {
		getCombo().removePopupMenuListener( getPopupMenuListener() );
		getCombo().removeActionListener( getActionListener() );
	}

	@Override
	public void updateInternal() {
		fillCombo();
	}

	protected PopupMenuListener createPopupMenuListener()
	{
		return( new PopupMenuListenerForWrapper() );
	}

	protected synchronized PopupMenuListener getPopupMenuListener()
	{
		if( _popupMenuListener == null )
			_popupMenuListener = createPopupMenuListener();
		
		return( _popupMenuListener );
	}

	protected ActionListener createActionListener()
	{
		return( this::fireNewItemSelected );
	}

	protected void fireNewItemSelected( ActionEvent evt )
	{
		notifyEvt( createNewItemSelectedEvent() );
	}

	public JComboBoxWrapperEventBuilderBase<RR> getEventBuilder() {
		return( (JComboBoxWrapperEventBuilderBase<RR>) super.getEventBuilder() );
	}

	protected JComboBoxWrapperNewItemSelected createNewItemSelectedEvent()
	{
		return( getEventBuilder().createJComboBoxWrapperNewItemSelected( getSelectedItem() ) );
	}

	protected synchronized ActionListener getActionListener()
	{
		if( _actionListener == null )
			_actionListener = createActionListener();
		
		return( _actionListener );
	}

	protected RR[] getItems()
	{
		RR[] result = null;

		if( _arrayBuilderForFillingCombo != null )
			result = _arrayBuilderForFillingCombo.get();

		return( result );
	}

	protected ComboBoxModel<RR> createModel( RR[] items )
	{
		return( new DefaultComboBoxModel( items ) );
	}

	public JComboBox<RR> getCombo()
	{
		return( (JComboBox<RR>) getJComponent() );
	}

	public RR getSelectedItem()
	{
		return( (RR) getCombo().getSelectedItem() );
	}

	protected void fillCombo()
	{
		RR[] items = getItems();
		if( hasChanged( items ) )
		{
			ComboBoxModel<RR> model = createModel( items );

			RR selectedItem = getSelectedItem();

			getCombo().setModel(model);

			int newSelectedIndex = ( selectedItem == null ) ?
									-1 :
									ArrayFunctions.instance().getFirstIndexOfEquals(items, selectedItem,
																					_functionForEquals);

			if( newSelectedIndex >= 0 )
				getCombo().setSelectedIndex( newSelectedIndex );
			else if( items.length > 0 )
				getCombo().setSelectedIndex(0);
		}
	}

	protected boolean hasChanged( RR[] items )
	{
		ComboBoxModel model = getCombo().getModel();
		boolean result = ( items.length != model.getSize() );
		for( int ii = 0; !result && (ii < items.length ); ii++ )
			result = !Objects.equals( items[ii], model.getElementAt(ii) );

		return( result );
	}

	protected class PopupMenuListenerForWrapper implements PopupMenuListener
	{
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			updateInternal();
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}
}
