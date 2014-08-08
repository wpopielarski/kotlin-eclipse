/*******************************************************************************
 * Copyright 2000-2014 JetBrains s.r.o.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package org.jetbrains.kotlin.core.resolve.lang.java.structure;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotationAsAnnotationArgument;
import org.jetbrains.jet.lang.resolve.name.Name;

public class EclipseJavaAnnotationAsAnnotationArgument implements JavaAnnotationAsAnnotationArgument {

    private final IAnnotationBinding annotationBinding;
    private final Name name;
    
    protected EclipseJavaAnnotationAsAnnotationArgument(@NotNull IAnnotationBinding annotation, @NotNull Name name) {
        this.annotationBinding = annotation;
        this.name = name;
    }

    @Override
    @NotNull
    public JavaAnnotation getAnnotation() {
        return new EclipseJavaAnnotation(annotationBinding);
    }

    @Override
    @Nullable
    public Name getName() {
        return name;
    }
}
