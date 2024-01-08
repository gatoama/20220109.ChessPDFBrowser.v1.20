/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.combobox.evt;

import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventBuilderBase;
import com.frojasg1.general.desktop.view.componentwrapper.combobox.JComboBoxWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.combobox.JComboBoxWrapperEventType;
import com.frojasg1.general.desktop.view.componentwrapper.evt.MultiMapForBuildingJComponentWrapperEvents;
import javax.swing.JComboBox;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JComboBoxWrapperEventBuilderBase<RR>
	extends JComponentWrapperEventBuilderBase<JComboBox, JComboBoxWrapperEventBase<RR>> {

	protected Class<RR> _recordClass;

	public JComboBoxWrapperEventBuilderBase(JComboBox combo, Class<RR> recordClass,
										boolean withInit)
	{
		super( combo );
		_recordClass = recordClass;

		if( withInit )
			init();
	}

	@Override
	protected void fillMultiMap(MultiMapForBuildingJComponentWrapperEvents multiMap) {
        multiMap.put( JComboBoxWrapperEventType.NEW_ITEM_SELECTED, JComboBoxWrapperNewItemSelected.class, JComboBoxWrapperNewItemSelected::new );
	}

	public Class<RR> getRecordClass() {
		return _recordClass;
	}

	public JComboBoxWrapperNewItemSelected<RR> createJComboBoxWrapperNewItemSelected( RR value )
	{
		JComboBoxWrapperNewItemSelected<RR> evt = buildEvent(JComboBoxWrapperNewItemSelected.class);
		evt.setNewItemSelected(value);

		return( evt );
	}
}
