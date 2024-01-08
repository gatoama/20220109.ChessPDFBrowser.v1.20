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

import org.jogamp.java3d.PositionPathInterpolator;
import org.jogamp.java3d.SceneGraphObject;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Point3f;

import org.jogamp.java3d.utils.scenegraph.io.retained.Controller;
import org.jogamp.java3d.utils.scenegraph.io.retained.SymbolTableData;

public class PositionPathInterpolatorState extends PathInterpolatorState {

    private Point3f[] positions;

    public PositionPathInterpolatorState(SymbolTableData symbol,Controller control) {
        super( symbol, control );
    }

    @Override
    public void writeConstructorParams( DataOutput out ) throws IOException {
        super.writeConstructorParams( out );

        positions = new Point3f[ knots.length ];
        for(int i=0; i<positions.length; i++)
            positions[i] = new Point3f();

        ((PositionPathInterpolator)node).getPositions( positions );
        for(int i=0; i<positions.length; i++)
            control.writePoint3f( out, positions[i] );
    }

    @Override
    public void readConstructorParams( DataInput in ) throws IOException {
        super.readConstructorParams( in );

        positions = new Point3f[ knots.length ];
        for(int i=0; i<positions.length; i++)
            positions[i] = control.readPoint3f( in );
    }

    @Override
    public SceneGraphObject createNode( Class j3dClass ) {
        return createNode( j3dClass, new Class[] { org.jogamp.java3d.Alpha.class,
                                                    TransformGroup.class,
                                                    Transform3D.class,
                                                    knots.getClass(),
                                                    positions.getClass() },
                                      new Object[] { null,
                                                     null,
                                                     new Transform3D(),
                                                     knots,
                                                     positions } );
    }

    @Override
    protected org.jogamp.java3d.SceneGraphObject createNode() {
        return new PositionPathInterpolator( null, null, new Transform3D(), knots, positions );
    }


}
