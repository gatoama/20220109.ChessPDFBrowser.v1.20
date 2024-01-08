/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public enum DragAndDropEventType {
	ALL(-1, false, false),
	CAN_IMPORT_FIRED( 1, true, false ),
	DROPPED( 2, false, true );

	protected long _longValue;
	protected boolean _isDroppedEvt;
	protected boolean _isCanImportEvt;

	private DragAndDropEventType( long longValue, boolean isCanImportEvt,
									boolean isDroppedEvt )
	{
		_longValue = longValue;
		_isCanImportEvt = isCanImportEvt;
		_isDroppedEvt = isDroppedEvt;
	}

	public boolean isDroppedEvt() {
		return _isDroppedEvt;
	}

	public boolean isCanImportEvt() {
		return _isCanImportEvt;
	}

	public long getLongValue() {
		return _longValue;
	}
}
