package org.jetbrains.kotlin.ui.commands

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.resources.IProject
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.ISources
import org.eclipse.ui.handlers.HandlerUtil

abstract class AbstractProjectActionHandler : AbstractHandler() {
    protected abstract fun isEnabled(project: IProject): Boolean
    protected abstract fun execute(project: IProject)

    final override fun execute(event: ExecutionEvent): Any? {
        val selection = HandlerUtil.getActiveMenuSelection(event)
        val project = projectFromSelection(selection)

        project?.let { execute(it) }

        return null
    }

    final override fun setEnabled(evaluationContext: Any) {
        val selection = HandlerUtil.getVariable(evaluationContext, ISources.ACTIVE_CURRENT_SELECTION_NAME)
        val project = projectFromSelection(selection)

        setBaseEnabled(project?.let { isEnabled(it) } ?: false)
    }

    private fun projectFromSelection(selection: Any?): IProject? =
            (selection as? IStructuredSelection)
                    ?.takeIf { it.size() == 1 }
                    ?.firstElement
                    ?.let {
                        when (it) {
                            is IProject -> it
                            is IJavaProject -> it.project
                            else -> null
                        }
                    }
}