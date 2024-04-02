package com.hybris.yps.hyeclipse.utils;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

import com.hybris.yps.hyeclipse.beans.SpringBeanDefinition;

public final class MiscUtils {

	private MiscUtils() {
	};

	public static String decapitalize(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}

		char c[] = string.toCharArray();
		c[0] = Character.toLowerCase(c[0]);

		return new String(c);
	}

	public static String capitalize(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}

		char c[] = string.toCharArray();
		c[0] = Character.toUpperCase(c[0]);

		return new String(c);
	}

	public static String getQualifiedName(ITextEditor editor) {
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		IType primaryType = typeRoot.findPrimaryType();
		return primaryType.getFullyQualifiedName();
	}

	public static void copyStringToClipBoard(Shell activeShell, String string) throws SWTError {
		Clipboard clipboard = new Clipboard(activeShell.getDisplay());
		Object[] data = null;
		Transfer[] dataTypes = null;
		data = new Object[] { string };
		dataTypes = new Transfer[] { TextTransfer.getInstance() };
		try {
			clipboard.setContents(data, dataTypes);
		} catch (SWTError e) {
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
				throw e;
			}
			if (MessageDialog.openQuestion(activeShell, "CopyQualifiedNameAction_ErrorTitle",
					"CopyQualifiedNameAction_ErrorDescription")) {
				clipboard.setContents(data, dataTypes);
			}
		} finally {
			clipboard.dispose();
		}
	}

	public static SpringBeanDefinition createSpringBeanDefinition(String selection, String qualifiedName) {
		if (selection == null || selection.isBlank() || qualifiedName == null || qualifiedName.isBlank()) {
			return null;
		}
		String id = selection;
		String defaultString1 = "default";
		String defaultString2 = "Default";
		if (id.contains(defaultString1) || id.contains(defaultString2)) {
			id = capitalize(selection.substring(7));
		}
		return new SpringBeanDefinition(selection, id, qualifiedName);

	}

}
