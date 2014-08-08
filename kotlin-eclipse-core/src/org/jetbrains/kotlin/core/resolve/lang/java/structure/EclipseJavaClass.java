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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.Visibility;
import org.jetbrains.jet.lang.resolve.java.structure.JavaAnnotation;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClassifierType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaField;
import org.jetbrains.jet.lang.resolve.java.structure.JavaMethod;
import org.jetbrains.jet.lang.resolve.java.structure.JavaType;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeParameter;
import org.jetbrains.jet.lang.resolve.java.structure.JavaTypeSubstitutor;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.Name;

import com.google.common.collect.Lists;

public class EclipseJavaClass extends EclipseJavaClassifier<ITypeBinding> implements JavaClass {

    public EclipseJavaClass(ITypeBinding javaElement) {
        super(javaElement);
    }

    @Override
    @NotNull
    public Collection<JavaAnnotation> getAnnotations() {
        return EclipseJavaElementUtil.getAnnotations(getBinding());
    }

    @Override
    @Nullable
    public JavaAnnotation findAnnotation(@NotNull FqName fqName) {
        return EclipseJavaElementUtil.findAnnotation(getBinding(), fqName);
    }

    @Override
    @NotNull
    public Name getName() {
        return Name.guess(getBinding().getName());
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(getBinding().getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(getBinding().getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getBinding().getModifiers());
    }

    @Override
    @NotNull
    public Visibility getVisibility() {
        return EclipseJavaElementUtil.getVisibility(getBinding());
    }

    @Override
    @NotNull
    public List<JavaTypeParameter> getTypeParameters() {
        List<JavaTypeParameter> typeParameters = Lists.newArrayList();
        for (ITypeBinding typeParameter : getBinding().getTypeParameters()) {
            typeParameters.add(new EclipseJavaTypeParameter(typeParameter));
        }
        
        return typeParameters;
    }

    @Override
    @NotNull
    public Collection<JavaClass> getInnerClasses() {
        List<JavaClass> innerClasses = Lists.newArrayList();
        for (ITypeBinding innerClass : getBinding().getDeclaredTypes()) {
            innerClasses.add(new EclipseJavaClass(innerClass));
        }
        
        return innerClasses;
    }

    @Override
    @Nullable
    public FqName getFqName() {
        return new FqName(getBinding().getQualifiedName());
    }

    @Override
    public boolean isInterface() {
        return getBinding().isInterface();
    }

    @Override
    public boolean isAnnotationType() {
        return getBinding().isAnnotation();
    }

    @Override
    public boolean isEnum() {
        return getBinding().isEnum();
    }

    @Override
    @Nullable
    public JavaClass getOuterClass() {
        ITypeBinding outerClass = getBinding().getDeclaringClass();
        return outerClass != null ? new EclipseJavaClass(outerClass) : null;
    }

    @Override
    @NotNull
    public Collection<JavaClassifierType> getSupertypes() {
        return EclipseJavaElementUtil.getSuperTypes(getBinding());
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getMethods() {
        return getMethodsFor(getBinding());
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getAllMethods() {
        List<JavaMethod> allMethods = Lists.newArrayList();
        allMethods.addAll(getMethods());

        for (ITypeBinding typeBinding : Bindings.getAllSuperTypes(getBinding())) {
            allMethods.addAll(getMethodsFor(typeBinding));
        }
        
        return allMethods;
    }
    
    @NotNull
    private static Collection<JavaMethod> getMethodsFor(@NotNull ITypeBinding type) {
        List<JavaMethod> methods = new ArrayList<JavaMethod>();
        for (IMethodBinding method : type.getDeclaredMethods()) {
            methods.add(new EclipseJavaMethod(method));
        }
        
        return methods;
    }

    @Override
    @NotNull
    public Collection<JavaField> getFields() {
        return getFieldsFor(getBinding());
    }
    
    @NotNull
    public static Collection<JavaField> getFieldsFor(@NotNull ITypeBinding type) {
        List<JavaField> fields = new ArrayList<JavaField>();
        for (IVariableBinding field : type.getDeclaredFields()) {
            fields.add(new EclipseJavaField(field));
        }
        
        return fields;
    }

    @Override
    @NotNull
    public Collection<JavaField> getAllFields() {
        List<JavaField> allFields = Lists.newArrayList();
        allFields.addAll(getFields());
        
        for (ITypeBinding typeBinding : Bindings.getAllSuperTypes(getBinding())) {
            allFields.addAll(getFieldsFor(typeBinding));
        }
        
        return allFields;
    }

    @Override
    @NotNull
    public Collection<JavaMethod> getConstructors() {
        Collection<JavaMethod> constructors = Lists.newArrayList();
        for (JavaMethod method : getMethods()) {
            if (method.isConstructor()) {
                constructors.add(method);
            }
        }
        
        return constructors;
    }

    @Override
    @NotNull
    public JavaClassifierType getDefaultType() {
        return new EclipseJavaClassifierType(getBinding().getTypeDeclaration());
    }

    @Override
    @NotNull
    public OriginKind getOriginKind() {
        IType javaType = (IType) getBinding().getJavaElement();
        
        if (javaType instanceof BinaryType) {
            return OriginKind.COMPILED;
        } else {
            return OriginKind.SOURCE;
        }
    }

    @Override
    @NotNull
    public JavaType createImmediateType(@NotNull JavaTypeSubstitutor substitutor) {
        return new EclipseJavaImmediateClass(this, substitutor);
    }

}
