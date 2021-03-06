/* 
 * VTextFieldBasedTypeRepresentation.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import java.awt.Dimension;
import java.awt.Font;

/**
 * TypeRepresentation base class for text field based type representations.
 * 
 * Style name: "default"
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type=Object.class, input = true, output = true, style="default")
public abstract class VTextFieldBasedTypeRepresentation
        extends TypeRepresentationBase {

    protected VTextField input;

    /**
     * Constructor.
     */
    public VTextFieldBasedTypeRepresentation() {

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        nameLabel.setAlignmentX(0.0f);

        this.add(nameLabel);

        input = new VTextField(this, "");

        input.setFont(new Font("SansSerif", Font.PLAIN, 11));

        int height = (int) this.input.getMinimumSize().getHeight();
        this.input.setMinimumSize(new Dimension(40, height));

        setInputDocument(input, input.getDocument());

        input.setAlignmentY(0.5f);
        input.setAlignmentX(0.0f);

        this.add(input);
    }

    @Override
    public void setCurrentRepresentationType(
            RepresentationType representationType) {
        if (representationType == RepresentationType.OUTPUT) {
            input.setEditable(false);
        }
        super.setCurrentRepresentationType(representationType);
    }

    @Override
    protected void valueInvalidated() {
        if (isInput()) {
            input.setInvalidState(true);
        }
    }

    @Override
    protected void valueValidated() {
        if (isInput()) {
            input.setInvalidState(false);
        }
    }

    @Override
    public void emptyView() {
        input.setText("");
    }

    @Override
    public void setViewValue(Object o) {
        if (o != null) {
            input.setText(o.toString());
        } else {
            input.setText("");
        }
//        System.err.println(
//                "WINDOW: " + getParentMethod().getParentObject().getID() +
//                " , CLASS: " + getType() + " , VALUE: " + o);
    }

    @Override()
    public String getValueAsCode() {
        String result = "null";

        Object o = getValue();

        if (o != null) {
            result = "\"" + o.toString() + "\"";
        }

        return result;
    }
}
