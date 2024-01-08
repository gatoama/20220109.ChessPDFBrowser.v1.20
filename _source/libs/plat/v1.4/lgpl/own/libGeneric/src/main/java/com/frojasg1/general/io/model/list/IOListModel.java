/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import com.frojasg1.general.collection.ModifiedStatus;
import com.frojasg1.general.collection.ThreadSafeListWrapper;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface IOListModel<RR> extends ThreadSafeListWrapper<RR, List<RR>>, ModifiedStatus
{
}
