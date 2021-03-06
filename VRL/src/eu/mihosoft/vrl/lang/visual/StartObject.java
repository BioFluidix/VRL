/* 
 * StartObject.java
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
package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.InterfaceChangedException;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.reflection.VisualObjectInspector;
import eu.mihosoft.vrl.reflection.WorkflowEvent;
import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.types.MethodRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ComponentInfo(name = "Start", category = "VRL/Control", allowRemoval = false,
        description = "Start Control-Flow")
@ObjectInfo(multipleViews = false, name = "Start", instances = 1,
        controlFlowIn = false, controlFlowOut = true,
        referenceIn = false, referenceOut = false)
public class StartObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient VisualInvocationObject invocation;
    public transient Thread thread;
    private transient DefaultMethodRepresentation mRep;

    @MethodInfo(name = " ", buttonText = "start", hideCloseIcon = true, num = 1)
    public void start(CanvasRequest cReq, MethodRequest mReq,
            @ParamGroupInfo(group = "Settings|false|Control-Flow Settings")
            @ParamInfo(name = "reset instances", options = "value=true;hideConnector=true") boolean resetInstances) throws InterfaceChangedException {

        if(resetInstances) {
            resetInstances(cReq);
        }
        
        mRep = mReq.getMethod();

        if (invocation == null) {
            invocation = new VisualInvocationObject();
        }

        if (!invocation.isRunning()) {
            if (invocation.init(cReq.getCanvas())) {
                invocation.invoke();
            }
        } else {
            invocationStopped(cReq.getCanvas());
            invocation.stop();
        }
    }

    void invocationStarted() {
        mRep.changeInvokeButtonTextIfButtonIsPresent("stop");
    }

    void invocationStopped(VisualCanvas canvas) {
        canvas.fireWorkflowEvent(WorkflowEvent.STOP_WORKFLOW);
        mRep.changeInvokeButtonTextIfButtonIsPresent("start");
    }

    void resetInstances(CanvasRequest cReq) throws InterfaceChangedException {
        // get current inspector
        VisualCanvas canvas = cReq.getCanvas();
        VisualObjectInspector inspector = canvas.getInspector();

        // get object classes
        Collection<Object> objects = inspector.getObjects();
        List<Class<?>> classesOld = new ArrayList<>();
        for (Object o : objects) {
            if (!(o instanceof eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent)) {
                classesOld.add(o.getClass());
            }
        }

        // reload classes
        List<Class<?>> classesNew = new ArrayList<>();
        for (Class<?> cls : classesOld) {
            classesNew.add(canvas.getClassLoader().reloadClass(cls));
        }

        // new instances
        for (Class<?> cls : classesNew) {
            canvas.getInspector().replaceAllObjects(cls,
                    new InstanceCreator(canvas)).getFirst();
        }
    }
}
