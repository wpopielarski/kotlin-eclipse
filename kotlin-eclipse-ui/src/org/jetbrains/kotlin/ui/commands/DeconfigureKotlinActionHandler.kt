package org.jetbrains.kotlin.ui.commands

import org.eclipse.core.resources.IProject
import org.jetbrains.kotlin.core.model.canBeDeconfigured
import org.jetbrains.kotlin.core.model.unconfigureKotlinProject

class DeconfigureKotlinActionHandler : AbstractProjectActionHandler() {
    override fun isEnabled(project: IProject): Boolean =
            canBeDeconfigured(project)

    override fun execute(project: IProject) {
        unconfigureKotlinProject(project)
    }
}