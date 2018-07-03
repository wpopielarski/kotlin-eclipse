package org.jetbrains.kotlin.core.preferences

import org.jetbrains.kotlin.core.Activator
import org.jetbrains.kotlin.config.JvmTarget
import org.eclipse.core.runtime.preferences.IScopeContext
import org.eclipse.core.runtime.preferences.DefaultScope
import org.osgi.service.prefs.Preferences as InternalPreferences
import org.eclipse.core.runtime.preferences.InstanceScope

class KotlinProperties(scope: IScopeContext = InstanceScope.INSTANCE) : Preferences(scope, Activator.PLUGIN_ID) {
    var globalsOverridden by BooleanPreference()
    
    var jvmTarget by EnumPreference<JvmTarget>()

    val compilerPlugins by ChildCollection(::CompilerPlugin)
    
    var compilerFlags by StringPreference()

    companion object {
        // Property object in instance scope (workspace) must be created after one in global scope (see: init())
        val workspaceInstance by lazy { KotlinProperties() }

        @JvmStatic
        fun init() {
            // Creating property object in default scope assures that values from 'preferences.ini' are loaded
            KotlinProperties(DefaultScope.INSTANCE)
        }
    }
}

class CompilerPlugin(scope: IScopeContext, path: String) : Preferences(scope, path) {
    var jarPath by StringPreference()

    var args by ListPreference()
    
    var active by BooleanPreference()
    
    // Marker property allowing overwriting plugin definitions. Should never be persisted.
    var removed: Boolean = false
}
