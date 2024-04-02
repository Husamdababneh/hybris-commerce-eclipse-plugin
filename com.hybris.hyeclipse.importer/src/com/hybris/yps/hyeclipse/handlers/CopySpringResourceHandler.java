package com.hybris.yps.hyeclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.hybris.yps.hyeclipse.utils.MiscUtils;

public class CopySpringResourceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
		ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();

		if (!handerlDirtyEditor(editor, activeShell)) {
			return null;
		}

		String qualifiedName = MiscUtils.getQualifiedName(editor);

		return null;
	}

	private boolean handerlDirtyEditor(ITextEditor editor, Shell activeShell) {
		if (!editor.isDirty()) {
			return true;
		}
		MessageDialog.openError(activeShell, "Ditry Editor",
				"The Current File has some changes, please save the file before attempting this action");

		return false;
	}

}
