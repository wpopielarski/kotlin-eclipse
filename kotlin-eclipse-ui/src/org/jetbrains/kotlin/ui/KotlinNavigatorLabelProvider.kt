package org.jetbrains.kotlin.ui

import org.eclipse.core.resources.IResource
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.IPackageFragment
import org.eclipse.jdt.internal.ui.navigator.JavaNavigatorLabelProvider
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider
import org.eclipse.swt.graphics.Image

/**
 * Modified version of [JavaNavigatorLabelProvider] that returns correct images for packages
 * that contains only Kotlin source files.
 *
 * Original [JavaNavigatorLabelProvider] treats Kotlin source files as non-Java resources
 * and returns "empty" package icon
 */
class KotlinNavigatorLabelProvider : JavaNavigatorLabelProvider() {

    override fun getImage(element: Any?): Image {
        // Replace instances of IPackageFragment with instances of KotlinAwarePackageFragment
        return super.getImage(when {
            element is IPackageFragment && element.elementType == IJavaElement.PACKAGE_FRAGMENT ->
                KotlinAwarePackageFragment(element)
            else ->
                element
        })
    }

    private class KotlinAwarePackageFragment(private val base: IPackageFragment) : IPackageFragment by base {
        /**
         * Returns true also when a package contains any Kotlin source file.
         *
         * Used by [JavaElementImageProvider.getPackageFragmentIcon]
         */
        override fun hasChildren(): Boolean {
            return base.hasChildren() ||
                    base.nonJavaResources.any { obj ->
                        obj is IResource
                                && obj.type == IResource.FILE
                                && (obj.name.endsWith(".kt") || obj.name.endsWith(".kts"))
                    }
        }
    }
}