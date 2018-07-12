package org.jetbrains.kotlin.preferences

import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.jface.resource.FontDescriptor
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Label
import org.eclipse.ui.dialogs.PropertyPage
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.core.Activator
import org.jetbrains.kotlin.core.preferences.CompilerPlugin
import org.jetbrains.kotlin.core.preferences.KotlinProperties
import org.jetbrains.kotlin.swt.builders.*
import org.jetbrains.kotlin.utils.LazyObservable

abstract class KotlinCompilerPropertyPage : PropertyPage() {
    protected abstract val kotlinProperties: KotlinProperties

    private var languageVersionProxy: LanguageVersion by LazyObservable(
            initialValueProvider = { kotlinProperties.languageVersion },
            onChange = { _, _, value ->
                kotlinProperties.languageVersion = value
                checkApiVersionCorrectness()
            }
    )

    private var apiVersionProxy: ApiVersion by LazyObservable(
            initialValueProvider = { kotlinProperties.apiVersion },
            onChange = { _, _, value ->
                kotlinProperties.apiVersion = value
                checkApiVersionCorrectness()
            }
    )

    private lateinit var apiVersionErrorLabel: Label

    private val pluginEntries: Iterable<CompilerPlugin>
        get() = kotlinProperties.compilerPlugins.entries.filterNot(CompilerPlugin::removed)

    private var selectedPlugin by LazyObservable<CompilerPlugin?>({ null }) { _, _, value ->
        val editable = value != null
        editButton.enabled = editable
        removeButton.enabled = editable
    }

    private lateinit var editButton: View<Button>

    private lateinit var removeButton: View<Button>

    protected abstract fun rebuildTask(monitor: IProgressMonitor?)

    protected fun View<Composite>.createOptionsControls(operations: View<Composite>.() -> Unit = {}) =
            gridContainer(cols = 2) {
                label("JVM target version: ")
                singleOptionPreference(kotlinProperties::jvmTarget,
                        allowedValues = enumValues<JvmTarget>().asList(),
                        nameProvider = JvmTarget::description) {
                    layout(horizontalGrab = true)
                }
                label("Language version: ")
                singleOptionPreference(::languageVersionProxy,
                        allowedValues = enumValues<LanguageVersion>().asList(),
                        nameProvider = LanguageVersion::description) {
                    layout(horizontalGrab = true)
                }
                label("API version: ")
                singleOptionPreference(::apiVersionProxy,
                        allowedValues = enumValues<LanguageVersion>().map { ApiVersion.createByLanguageVersion(it) },
                        nameProvider = ApiVersion::description) {
                    layout(horizontalGrab = true)
                }
                label("")
                apiVersionErrorLabel = label("API version must be lower or equal to language version")
                        .control
                        .apply {
                            font = FontDescriptor.createFrom(control.font)
                                    .increaseHeight(-1)
                                    .createFont(Display.getCurrent())
                            foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED)
                            visible = false
                        }
                group("Compiler plugins:", cols = 2) {
                    layout(horizontalSpan = 2, verticalGrab = true)
                    val list = checkList(::pluginEntries, selectionDelegate = ::selectedPlugin, style = SWT.BORDER) {
                        layout(horizontalGrab = true, verticalGrab = true, verticalSpan = 4)
                        nameProvider = { it.key }
                        checkDelegate = CompilerPlugin::active
                    }
                    button("Add") {
                        onClick {
                            CompilerPluginDialog(control.shell, kotlinProperties.compilerPlugins, null).open()
                            list.refresh()
                        }
                    }
                    editButton = button("Edit") {
                        enabled = false
                        onClick {
                            selectedPlugin?.also { CompilerPluginDialog(shell, kotlinProperties.compilerPlugins, it).open() }
                            list.refresh()
                        }
                    }
                    removeButton = button("Remove") {
                        enabled = false
                        onClick {
                            selectedPlugin?.apply {
                                removed = true
                            }
                            list.refresh()
                        }
                    }
                }
                group("Additional compiler flags") {
                    layout(horizontalSpan = 2, verticalGrab = true)
                    textField(kotlinProperties::compilerFlags, style = SWT.MULTI) { layout(horizontalGrab = true, verticalGrab = true) }
                }
            }.apply(operations)

    final override fun performOk(): Boolean {
        kotlinProperties.compilerPlugins.entries
                .filter(CompilerPlugin::removed)
                .forEach(CompilerPlugin::remove)
        kotlinProperties.flush()
        RebuildJob().schedule()
        return super.performOk()
    }

    private fun checkApiVersionCorrectness() {
        val correct = apiVersionProxy <= ApiVersion.createByLanguageVersion(languageVersionProxy)
        apiVersionErrorLabel.visible = !correct
        isValid = correct
    }

    private inner class RebuildJob : Job("Rebuilding workspace") {

        init {
            priority = Job.BUILD
        }

        override fun run(monitor: IProgressMonitor?): IStatus = try {
            rebuildTask(monitor)
            Status.OK_STATUS
        } catch (e: CoreException) {
            Status(Status.ERROR, Activator.PLUGIN_ID, "Error during build of the project", e)
        }
    }

}

