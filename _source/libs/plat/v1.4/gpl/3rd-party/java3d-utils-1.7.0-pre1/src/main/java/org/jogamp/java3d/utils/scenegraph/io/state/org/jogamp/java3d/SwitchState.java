/*
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 */

package org.jogamp.java3d.utils.scenegraph.io.state.org.jogamp.java3d;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jogamp.java3d.Switch;

import org.jogamp.java3d.utils.scenegraph.io.retained.Controller;
import org.jogamp.java3d.utils.scenegraph.io.retained.SymbolTableData;

public class SwitchState extends GroupState {

    public SwitchState(SymbolTableData symbol,Controller control) {
        super(symbol, control);
    }

    @Override
    public void writeObject( DataOutput out ) throws IOException {
        super.writeObject( out );
        Switch attr = (Switch)node;
        control.writeSerializedData( out, attr.getChildMask() );
        out.writeInt( attr.getWhichChild() );
    }

    @Override
    public void readObject( DataInput in ) throws IOException {
        super.readObject( in );
        Switch attr = (Switch)node;

        attr.setChildMask( (java.util.BitSet)control.readSerializedData(in) );
        attr.setWhichChild( in.readInt() );
    }

    @Override
    protected org.jogamp.java3d.SceneGraphObject createNode() {
        return new Switch();
    }


}

