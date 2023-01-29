package com.hybris.yps.hyeclipse.wizards;

import org.eclipse.jface.wizard.Wizard;

import com.hybris.yps.hyeclipse.beans.SpringBeanDefinition;

public class CreateSpringBeanDefinitionWizard extends Wizard {

	private CreateSpringBeanDefinitionPage page;

	public CreateSpringBeanDefinitionWizard(SpringBeanDefinition spd) {
		page = new CreateSpringBeanDefinitionPage(spd);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(page);
	}

	@Override
	public String getWindowTitle() {
		return "Create Spring Bean Definition Wizard";
	}

	@Override
	public boolean performFinish() {
//		if (!page.validatePage()) {
//			MessageDialog.openError(getShell(), Messages.AttachSourcesWizard_missingFile,
//					Messages.AttachSourcesWizard_missingFile_long);
//			// and ... abort
//			return false;
//		}

//		File sourceArchive = page.getSourceFile();
//		new ProjectSourceJob(sourceArchive).schedule();
		return true;
	}

}
