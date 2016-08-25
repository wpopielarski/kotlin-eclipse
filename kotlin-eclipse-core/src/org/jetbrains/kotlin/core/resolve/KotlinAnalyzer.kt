/*******************************************************************************
* Copyright 2000-2016 JetBrains s.r.o.
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
package org.jetbrains.kotlin.core.resolve

import org.eclipse.core.resources.IProject
import org.jetbrains.kotlin.core.model.KotlinAnalysisFileCache
import org.jetbrains.kotlin.core.model.KotlinEnvironment
import org.jetbrains.kotlin.core.model.getEnvironment
import org.jetbrains.kotlin.psi.KtFile

object KotlinAnalyzer {
    fun analyzeFile(jetFile: KtFile): AnalysisResultWithProvider {
        return KotlinAnalysisFileCache.getAnalysisResult(jetFile)
    }
    
<<<<<<< HEAD
    fun analyzeFile(jetFile: KtFile, environment: KotlinCommonEnvironment): AnalysisResultWithProvider {
        return KotlinAnalysisFileCache.getAnalysisResult(jetFile, environment)
    }

    fun analyzeFiles(javaProject: IJavaProject, filesToAnalyze: Collection<KtFile>): AnalysisResultWithProvider {
        if (filesToAnalyze.size == 1) {
            return analyzeFile(filesToAnalyze.iterator().next())
        }
<<<<<<< HEAD
        
        val kotlinEnvironment = KotlinEnvironment.getEnvironment(javaProject)
        return analyzeFiles(javaProject, kotlinEnvironment, filesToAnalyze)
=======

        val kotlinEnvironment = KotlinEnvironment.getEnvironment(javaProject.project)
        return analyzeFiles(kotlinEnvironment, filesToAnalyze)
>>>>>>> 4e6f838... Replace IJavaProject with raw IProject in several places
    }
    
<<<<<<< HEAD
    private fun analyzeFiles(javaProject: IJavaProject,
                             kotlinEnvironment: KotlinEnvironment,
=======
    fun analyzeFiles(files: Collection<KtFile>, environment: KotlinCommonEnvironment): AnalysisResultWithProvider {
        if (files.size == 1) {
            return analyzeFile(files.single(), environment)
        }
        
        if (environment !is KotlinEnvironment) {
            throw IllegalStateException("It is impossible to resolve several files at once with non KotlinEnvironment: $environment")
=======
    fun analyzeFiles(files: Collection<KtFile>): AnalysisResultWithProvider {
        return when {
            files.isEmpty() -> throw IllegalStateException("There should be at least one file to analyze")
            
            files.size == 1 -> analyzeFile(files.single())
            
            else -> {
                val environment = getEnvironment(files.first().project)
                if (environment == null) {
                    throw IllegalStateException("There is no environment for project: ${files.first().project}")
                }
                
                if (environment !is KotlinEnvironment) {
                    throw IllegalStateException("Only KotlinEnvironment can be used to analyze several files")
                }
                
                analyzeFiles(environment, files)
            }
>>>>>>> 607db5a... Refactor and simplify Kotlin analyer API
        }
    }
    
    fun analyzeProject(eclipseProject: IProject): AnalysisResultWithProvider {
        val environment = KotlinEnvironment.getEnvironment(eclipseProject)
        return analyzeFiles(environment, emptyList())
    }

    private fun analyzeFiles(kotlinEnvironment: KotlinEnvironment,
>>>>>>> c00d89f... Fix organize imports for scripts: generalize KotlinCacheService
                             filesToAnalyze: Collection<KtFile>): AnalysisResultWithProvider {
        return EclipseAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                javaProject,
                kotlinEnvironment.getProject(),
                filesToAnalyze)
    }
}