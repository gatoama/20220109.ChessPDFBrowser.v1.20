/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.functions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface TriConsumer<FF, SS, TT> {
	public void accept( FF firstParameter, SS secondParameter, TT thridParameter );
}
