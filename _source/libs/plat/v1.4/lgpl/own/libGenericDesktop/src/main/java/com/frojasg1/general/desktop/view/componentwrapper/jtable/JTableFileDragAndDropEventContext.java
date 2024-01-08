/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableFileDragAndDropEventContext {
	protected int _indexWhereToInsert;

	public int getIndexWhereToInsert() {
		return _indexWhereToInsert;
	}

	public void setIndexWhereToInsert(int _indexWhereToInsert) {
		this._indexWhereToInsert = _indexWhereToInsert;
	}
}
