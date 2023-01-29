package com.hybris.yps.hyeclipse.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.hybris.yps.hyeclipse.beans.SpringBeanDefinition;

public class CreateSpringBeanDefinitionPage extends WizardPage {

	private static final String COPY_SPRING_BEAN_DEFINITION_TO_CLIPBOARD = "Copy Spring Bean Definition to clipboard";
	private Text id;
	private Text clazz;
	private Text alias;
	private Text qualifiedName;
	private Text selection;
	private Composite container;
	private SpringBeanDefinition sbd;

	private KeyListener genericKeyListener;
	private Listener readOnlyListener;

	public CreateSpringBeanDefinitionPage(SpringBeanDefinition sbd) {
		super(COPY_SPRING_BEAN_DEFINITION_TO_CLIPBOARD);
		setTitle(COPY_SPRING_BEAN_DEFINITION_TO_CLIPBOARD);
		setDescription(COPY_SPRING_BEAN_DEFINITION_TO_CLIPBOARD);
		this.sbd = sbd;

		genericKeyListener = new KeyListener() {

			@Override
			public void keyReleased(KeyEvent event) {
				Text widget = (Text) event.widget;
				if (!widget.getText().isEmpty()) {
					setPageComplete(true);
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {

			}
		};

		readOnlyListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				e.doit = false;
			}
		};

	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		id = createField("Bean ID:", sbd.getId(), true);
		clazz = createField("Class  :", sbd.getClazz(), true);
		alias = createField("Alais   :", sbd.getSad().getAlias(), true);
		createStaticField("Qualified Name: ", sbd.getClazz());
		createStaticField("Selection : ", sbd.getSelection());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		id.setLayoutData(gd);
		clazz.setLayoutData(gd);
		alias.setLayoutData(gd);
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public Text createField(String label, String text, boolean useGenericListener) {
		Label nLabel = new Label(container, SWT.NONE);
		nLabel.setText(label);
		Text textObj = new Text(container, SWT.BORDER | SWT.SINGLE);
		textObj.setText(text);
		if (useGenericListener) {
			textObj.addKeyListener(genericKeyListener);
		}

		return textObj;
	}

	public Text createStaticField(String label, String text) {
		Label nLabel = new Label(container, SWT.NONE);
		nLabel.setText(label);
		Text textObj = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		textObj.setText(text);
//		textObj.addListener(SWT.Verify, readOnlyListener);

		return textObj;
	}

	
	
	/**
	 * Check if the contents of the page are valid.
	 * 
	 * Remember that the source file could be optional. So any checks will only
	 * apply if there is actually anything specified.
	 * 
	 * @return true if the picked archive exists and is readable. False otherwise.
	 */
	public boolean validatePage() {
		return false;
	}

}
