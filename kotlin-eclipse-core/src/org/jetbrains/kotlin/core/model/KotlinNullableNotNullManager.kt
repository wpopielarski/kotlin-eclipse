/*******************************************************************************
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
package org.jetbrains.kotlin.core.model

import com.intellij.codeInsight.NullabilityAnnotationInfo
import com.intellij.codeInsight.NullableNotNullManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiModifierListOwner

class KotlinNullableNotNullManager(project: Project) : NullableNotNullManager(project) {
	override fun getNullables(): List<String> = emptyList()

	override fun setInstrumentedNotNulls(arg0: MutableList<String>) {

	}

	override fun getInstrumentedNotNulls(): List<String> = emptyList()

	override fun isJsr305Default(arg0: PsiAnnotation, arg1: Array<out PsiAnnotation.TargetType>): NullabilityAnnotationInfo? = null

	override fun setNullables(vararg arg0: String?) {
	}

	override fun getDefaultNotNull(): String = "NotNull"

	override fun getNotNulls(): List<String> = emptyList()

	override fun getDefaultNullable(): String = "Nullable"

	override fun setDefaultNotNull(arg0: String) {
	}

	override fun setNotNulls(vararg arg0: String?) {
	}

	//    For now we get unresolved psi elements and as a result annotations qualified names are short
    init {
        setNotNulls("NotNull")
        setNullables("Nullable")
    }

    override fun setDefaultNullable(defaultNullable: String) {
    }

    override fun hasHardcodedContracts(element: PsiElement): Boolean = false

    override fun isNotNull(owner: PsiModifierListOwner, checkBases: Boolean): Boolean {
		val notNullAnnotations = notNulls.toSet()
		return owner.modifierList?.annotations?.any { annotation ->
			annotation.qualifiedName in notNullAnnotations
        } ?: false
    }

	override fun isNullable(owner: PsiModifierListOwner, checkBases: Boolean) = !isNotNull(owner, checkBases)
}