/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MMO - Model for output.
	MMI - Model for input
*/
public interface LinesAndModelTranslator<MMO, MMI>
	extends LinesToModelTranslator<MMI>, ModelToLinesTranslator<MMO>
{
}
