package org.jetbrains.kotlin.swt.builders

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import kotlin.reflect.KMutableProperty0

interface View<out T : Control> {
    val control: T
}

val <T : Control> T.asView: View<T>
    get() = object : View<T> {
        override val control: T = this@asView
    }

inline fun View<Composite>.label(
        text: String = "",
        style: Int = SWT.NONE,
        operations: View<Label>.() -> Unit = {}) =
        Label(control, style).asView.apply {
            control.text = text
            operations()
        }

inline fun View<Composite>.separator(style: Int = SWT.NONE, operations: View<Label>.() -> Unit = {}) =
        Label(control, SWT.HORIZONTAL or SWT.SEPARATOR or style).asView
                .apply(operations)
                .applyDefaultLayoutIfNeeded()

inline fun <T> View<Composite>.singleOptionPreference(
        delegate: KMutableProperty0<T>,
        allowedValues: List<T>,
        nameProvider: (T) -> String = { it.toString() },
        style: Int = SWT.NONE,
        operations: View<Combo>.() -> Unit = {}
) =
        Combo(control, style or SWT.READ_ONLY).apply {
            val valuesMapping = allowedValues.associateByTo(LinkedHashMap(), nameProvider)
            valuesMapping.keys.forEach { this.add(it) }

            delegate.get()
                    .let(nameProvider)
                    .let(valuesMapping.keys::indexOf)
                    .also { select(it) }

            addSelectionListener(object : SelectionAdapter() {
                override fun widgetSelected(event: SelectionEvent) {
                    delegate.set(valuesMapping[(event.widget as Combo).text]!!)
                }
            })
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()

inline fun View<Composite>.button(label: String = "", style: Int = SWT.NONE, operations: View<Button>.() -> Unit = {}) =
        Button(control, style).apply {
            text = label
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()

inline fun View<Composite>.checkbox(
        delegate: KMutableProperty0<Boolean>,
        label: String = "",
        style: Int = SWT.NONE,
        operations: View<Button>.() -> Unit = {}
) =
        Button(control, SWT.CHECK or style).apply {
            text = label
            selection = delegate.get()
            addSelectionListener(object : SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent) {
                    delegate.set(selection)
                }
            })
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()


fun View<Button>.onClick(callback: () -> Unit) {
    control.addSelectionListener(object : SelectionAdapter() {
        override fun widgetSelected(e: SelectionEvent?) {
            callback()
        }
    })
}

val View<Button>.selected: Boolean
    get() = control.selection

inline fun View<Composite>.textField(
        delegate: KMutableProperty0<String?>,
        style: Int = SWT.NONE,
        operations: View<Text>.() -> Unit = {}
) =
        Text(control, style).apply {
            text = delegate.get() ?: ""
            addModifyListener { _ ->
                delegate.set(text)
            }
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()

var View<Text>.text: String
    get() = control.text
    set(value) {
        control.text = value
    }

inline fun View<Composite>.gridContainer(cols: Int = 1, style: Int = SWT.NONE, operations: View<Composite>.() -> Unit = {}) =
        Composite(control, style).apply {
            layout = GridLayout(cols, false)
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()

inline fun View<Composite>.group(title: String? = null, cols: Int = 1, style: Int = SWT.NONE, operations: View<Group>.() -> Unit = {}) =
        Group(control, style).apply {
            title?.also { text = it }
            layoutData = GridData(SWT.FILL, SWT.FILL, true, true)
            layout = GridLayout(cols, false)
        }.asView.apply(operations)
                .applyDefaultLayoutIfNeeded()

var Control.recursiveEnabled: Boolean
    get() = enabled
    set(value) {
        enabled = value
        if (this is Composite) {
            this.children.forEach { it.recursiveEnabled = value }
        }
    }

var View<Control>.enabled: Boolean
    get() = control.enabled
    set(value) {
        control.recursiveEnabled = value
    }

var View<Button>.label: String
    get() = control.text
    set(value) {
        control.text = value
    }

fun View<Control>.layout(
        horizontalSpan: Int = 1,
        verticalSpan: Int = 1,
        horizontalAlignment: Int = SWT.FILL,
        verticalAlignment: Int = SWT.FILL,
        horizontalGrab: Boolean = false,
        verticalGrab: Boolean = false,
        suggestedWidth: Int = SWT.DEFAULT,
        suggestedHeight: Int = SWT.DEFAULT
) {
    control.layoutData = GridData(horizontalAlignment, verticalAlignment, horizontalGrab, verticalGrab, horizontalSpan, verticalSpan).apply {
        widthHint = suggestedWidth
        heightHint = suggestedHeight
    }
}

fun <T : View<Control>> T.applyDefaultLayoutIfNeeded() = apply {
    if (control.layoutData == null) {
        layout()
    }
}