/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection.functions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListRecordMoveContext<RR> extends ListStateContextBase<RR> {
	protected int _indexWhereToMove;

	public int getIndexWhereToMove() {
		return _indexWhereToMove;
	}

	public void setIndexWhereToMove(int _indexWhereToMove) {
		this._indexWhereToMove = _indexWhereToMove;
	}
}
