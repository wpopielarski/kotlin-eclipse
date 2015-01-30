package org.jetbrains.kotlin.core.tests.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchShortcutExtension;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.buildpath.BuildPathSupport;
import org.jetbrains.kotlin.core.utils.ProjectUtils;
import org.jetbrains.kotlin.testframework.editor.KotlinEditorTestCase;
import org.jetbrains.kotlin.testframework.editor.KotlinProjectTestCase;
import org.jetbrains.kotlin.testframework.editor.TextEditorTest;
import org.jetbrains.kotlin.testframework.utils.KotlinTestUtils;
import org.jetbrains.kotlin.ui.launch.junit.KotlinJUnitLaunchShortcut;
import org.junit.Before;

public class KotlinJUnitLaunchTestCase extends KotlinProjectTestCase {
	@Before
    public void configure() throws CoreException {
		configureProject();
		KotlinTestUtils.addKotlinBuilder(getTestProject().getJavaProject().getProject());
		addJUnitToClasspath();
		getTestProject().addKotlinRuntime();
    }
	
	public void doTest(String testPath) {
		String fileText = KotlinTestUtils.getText(testPath);
		final TextEditorTest testEditor = configureEditor(KotlinTestUtils.getNameByPath(testPath), fileText);
		
		KotlinEditorTestCase.joinBuildThread();
		
		final LaunchShortcutExtension launchShortcut = findKotlinJUnitLaunchShortcut();
		assertNotNull("Kotlin JUnit launch shortcut was not founded", launchShortcut);
		
		ILaunchConfiguration[] launchConfigurations = launchShortcut.getLaunchConfigurations(testEditor.getEditor());
		assertEquals(launchConfigurations.length, 1);
	}
	
	private LaunchShortcutExtension findKotlinJUnitLaunchShortcut() {
		List<LaunchShortcutExtension> launchShortcuts = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchShortcuts();
		for (LaunchShortcutExtension launchShortcutExtension : launchShortcuts) {
			if (KotlinJUnitLaunchShortcut.KOTLIN_JUNIT_LAUNCH_ID.equals(launchShortcutExtension.getId())) {
				return launchShortcutExtension;
			}
		}
		
		return null;
	}
	
	private void addJUnitToClasspath() {
		try {
			IClasspathEntry jUnit4ClasspathEntry = BuildPathSupport.getJUnit4ClasspathEntry();
			ProjectUtils.addContainerEntryToClasspath(getTestProject().getJavaProject(), jUnit4ClasspathEntry);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}
}
