/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.chesspdfbrowser.engine.configuration.figureset;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface FigureSetChangedListener {
	public void figureSetChanged( FigureSetChangedObserved observed,
								FigureSet oldValue, FigureSet newFigureSet );
}