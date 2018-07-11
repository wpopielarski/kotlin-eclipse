package org.jetbrains.kotlin.ui.commands

import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IProject
import org.eclipse.jdt.internal.core.CompilationUnit
import org.eclipse.ui.PlatformUI
import org.eclipse.ui.ide.IDE
import org.eclipse.ui.ide.undo.DeleteResourcesOperation

data class ConvertedKotlinData(val file: IFile, val kotlinFileData: String)

fun getDeleteOperation(compilationUnits: Set<CompilationUnit>): DeleteResourcesOperation {
	val resources = compilationUnits.map { it.getResource() }
	return DeleteResourcesOperation(resources.toTypedArray(), "Deleting files", true)
}

fun getCorrespondingProjects(compilationUnits: Set<CompilationUnit>): Set<IProject> {
	return compilationUnits.map { 
		it.getJavaProject().getProject() 
	}.toSet() 
}

fun openEditor(file: IFile) {
	IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
}