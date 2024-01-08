/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection.functions;

import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListStateContextBase<RR> {
	protected Integer _selectedIndex;
	protected List<RR> _list;
	protected int[] _selection;

	public Integer getSelectedIndex() {
		return _selectedIndex;
	}

	public void setSelectedIndex(Integer _selectedIndex) {
		this._selectedIndex = _selectedIndex;
	}

	public List<RR> getList() {
		return _list;
	}

	public void setList(List<RR> _list) {
		this._list = _list;
	}

	public int[] getSelection() {
		return _selection;
	}

	public void setSelection(int[] _selection) {
		this._selection = _selection;
	}
}
