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
package org.jetbrains.kotlin.ui.editors

import com.intellij.openapi.util.text.StringUtil
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jface.text.IDocument
import org.jetbrains.kotlin.core.builder.KotlinPsiManager
import org.jetbrains.kotlin.core.model.KotlinEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.ui.editors.navigation.KotlinExternalEditorInput
import org.jetbrains.kotlin.ui.editors.occurrences.KotlinMarkOccurrences

open class KotlinFileEditor : KotlinCommonEditor() {
    override val isScript: Boolean
        get() = false
    
    override val parsedFile: KtFile?
        get() = computeJetFile()
    
    override val javaProject: IJavaProject? by lazy {
        eclipseFile?.let { JavaCore.create(it.getProject()) }
    }
    
    override val document: IDocument
        get() = getDocumentProvider().getDocument(getEditorInput())
    private val kotlinMarkOccurrences by lazy { KotlinMarkOccurrences(this) }

    private fun computeJetFile(): KtFile? {
        val file = eclipseFile
        if (file != null && file.exists()) {
            return KotlinPsiManager.getKotlinParsedFile(file) // File might be not under the source root
        }
        
        if (javaProject == null) {
            return null
        }
        
        val environment = KotlinEnvironment.getEnvironment(javaProject!!.project)
        val ideaProject = environment.project
        return KtPsiFactory(ideaProject).createFile(StringUtil.convertLineSeparators(document.get(), "\n"))
    }

    override fun installOccurrencesFinder(forceUpdate: Boolean) {
        editorSite.page.addPostSelectionListener(kotlinMarkOccurrences)
    }

    override fun uninstallOccurrencesFinder() {
        editorSite.page.removePostSelectionListener(kotlinMarkOccurrences)
    }
}

class KotlinExternalReadOnlyEditor : KotlinFileEditor() {
    companion object {
        const val EDITOR_ID = "org.jetbrains.kotlin.ui.editors.KotlinExternalReadOnlyEditor"
    }
    
    override val parsedFile: KtFile?
        get() = (getEditorInput() as KotlinExternalEditorInput).ktFile
}