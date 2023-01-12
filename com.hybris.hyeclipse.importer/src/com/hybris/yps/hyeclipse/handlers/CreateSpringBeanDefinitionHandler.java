package com.hybris.yps.hyeclipse.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.viewsupport.BindingLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class CreateSpringBeanDefinitionHandler extends AbstractHandler {

	private static final long LABEL_FLAGS = JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.M_FULLY_QUALIFIED
			| JavaElementLabels.I_FULLY_QUALIFIED | JavaElementLabels.T_FULLY_QUALIFIED
			| JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED | JavaElementLabels.T_TYPE_PARAMETERS
			| JavaElementLabels.CU_QUALIFIED | JavaElementLabels.CF_QUALIFIED;

	public static CompilationUnit getCompilationUnit(ICompilationUnit icu, IProgressMonitor monitor) {
		final ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(icu);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(monitor);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.getEditorReferences();

//		IEditorPart currentEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.getActiveEditor();
//
//		

		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
		if (editor.isDirty()) {
			// Message to the user to save the current editor;
		}
		ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		ICompilationUnit icu = (ICompilationUnit) typeRoot.getAdapter(ICompilationUnit.class);
		CompilationUnit cu = getCompilationUnit(icu, null);
		NodeFinder finder = new NodeFinder(cu, sel.getOffset(), sel.getLength());
		ASTNode node = finder.getCoveringNode();

		IBinding binding = getBindingFromNode(node);
		if (binding == null) {
			// LOG here and tell the user
			return null;
		}

		String qualifiedName = getQualifiedName(binding);
		if (qualifiedName == null || qualifiedName.isBlank()) {
			// LOG here and tell the user
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<alias name=\"defualt");
		sb.append(sel.getText()).append("\" alias=\"").append(sel.getText()).append("\" />\n");
		sb.append("<bean id=\"defualt");
		sb.append(sel.getText()).append("\" class=\"").append(qualifiedName).append("\" />");

		StringSelection selection = new StringSelection(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);

		return null;
	}

	private IBinding getBindingFromNode(ASTNode node) {
		IBinding binding = null;
		if (node instanceof Name name) {
			binding = getConstructorBindingIfAvailable(name);
			if (binding != null)
				return binding;
			binding = name.resolveBinding();
		} else if (node instanceof MethodInvocation methodInvocation) {
//			binding = methodInvocation.resolveMethodBinding();
		} else if (node instanceof MethodDeclaration methodDeclaration) {
//			binding = methodDeclaration.resolveBinding();
		} else if (node instanceof Type type) {
//			binding = type.resolveBinding();
		} else if (node instanceof AnonymousClassDeclaration anonymousClassDeclaration) {
//			binding = anonymousClassDeclaration.resolveBinding();
		} else if (node instanceof TypeDeclaration typeDeclaration) {
//			binding = typeDeclaration.resolveBinding();
		} else if (node instanceof CompilationUnit compilationUnit) {
//			return compilationUnit.getJavaElement();
		} else if (node instanceof Expression expression) {
//			binding = expression.resolveTypeBinding();
		} else if (node instanceof ImportDeclaration importDeclaration) {
//			binding = importDeclaration.resolveBinding();
		} else if (node instanceof MemberRef memberRef) {
//			binding = memberRef.resolveBinding();
		} else if (node instanceof MemberValuePair memberValuePair) {
//			binding = memberValuePair.resolveMemberValuePairBinding();
		} else if (node instanceof PackageDeclaration packageDeclaration) {
//			binding = packageDeclaration.resolveBinding();
		} else if (node instanceof TypeParameter typeParameter) {
//			binding = typeParameter.resolveBinding();
		} else if (node instanceof VariableDeclaration variableDeclaration) {
//			binding = variableDeclaration.resolveBinding();
		}

		if (binding == null) {
			// LOG here
		}

		return binding;

	}

	private String getQualifiedName(Object element) {
		if (element instanceof IResource resource)
			return resource.getFullPath().toString();

		if (element instanceof IJarEntryResource resource)
			return resource.getFullPath().toString();

		if (element instanceof LogicalPackage pack)
			return pack.getElementName();

		if (element instanceof IJavaProject || element instanceof IPackageFragmentRoot
				|| element instanceof ITypeRoot) {
			IResource resource;
			try {
				resource = ((IJavaElement) element).getCorrespondingResource();
				if (resource != null)
					return getQualifiedName(resource);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (element instanceof IBinding binding)
			return BindingLabelProvider.getBindingLabel(binding, LABEL_FLAGS);

		return TextProcessor.deprocess(JavaElementLabels.getTextLabel(element, LABEL_FLAGS));
	}

	/**
	 * Checks whether the given name belongs to a {@link ClassInstanceCreation} and
	 * if so, returns its constructor binding.
	 *
	 * @param nameNode the name node
	 * @return the constructor binding or <code>null</code> if not found
	 * @since 3.7
	 */
	private IBinding getConstructorBindingIfAvailable(Name nameNode) {
		ASTNode type = ASTNodes.getNormalizedNode(nameNode);
		StructuralPropertyDescriptor loc = type.getLocationInParent();
		if (loc == ClassInstanceCreation.TYPE_PROPERTY) {
			return ((ClassInstanceCreation) type.getParent()).resolveConstructorBinding();
		}
		return null;
	}

	public void run() {

//		try {
//		Object[] elements = getSelectedElements();
//			if (elements == null) {
//				MessageDialog.openInformation(getShell(), ActionMessages.CopyQualifiedNameAction_InfoDialogTitel,
//						ActionMessages.CopyQualifiedNameAction_NoElementToQualify);
//				return;
//			}

//			Object[] data = null;
//			Transfer[] dataTypes = null;
//
//			if (elements.length == 1) {
//				Object element = elements[0];
//				String qualifiedName = getQualifiedName(element);
//				IResource resource = null;
//				if (element instanceof IJavaElement) {
//					IJavaElement je = ((IJavaElement) element);
//					if (je.exists())
//						resource = je.getCorrespondingResource();
//				} else if (element instanceof IResource)
//					resource = (IResource) element;
//
//				if (resource != null) {
//					IPath location = resource.getLocation();
//					if (location != null) {
//						data = new Object[] { qualifiedName, resource, new String[] { location.toOSString() } };
//						dataTypes = new Transfer[] { TextTransfer.getInstance(), ResourceTransfer.getInstance(),
//								FileTransfer.getInstance() };
//					} else {
//						data = new Object[] { qualifiedName, resource };
//						dataTypes = new Transfer[] { TextTransfer.getInstance(), ResourceTransfer.getInstance() };
//					}
//				} else {
//					data = new Object[] { qualifiedName };
//					dataTypes = new Transfer[] { TextTransfer.getInstance() };
//				}
//			} else {
//				StringBuilder buf = new StringBuilder();
//				buf.append(getQualifiedName(elements[0]));
//				for (int i = 1; i < elements.length; i++) {
//					String qualifiedName = getQualifiedName(elements[i]);
//					buf.append(System.lineSeparator()).append(qualifiedName);
//				}
//				data = new Object[] { buf.toString() };
//				dataTypes = new Transfer[] { TextTransfer.getInstance() };
//			}
//
//			Clipboard clipboard = new Clipboard(getShell().getDisplay());
//			try {
//				clipboard.setContents(data, dataTypes);
//			} catch (SWTError e) {
//				if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
//					throw e;
//				}
//				if (MessageDialog.openQuestion(getShell(), ActionMessages.CopyQualifiedNameAction_ErrorTitle,
//						ActionMessages.CopyQualifiedNameAction_ErrorDescription)) {
//					clipboard.setContents(data, dataTypes);
//				}
//			} finally {
//				clipboard.dispose();
//			}
//		} catch (JavaModelException e) {
//			JavaPlugin.log(e);
//		}
	}

//

//
//	private String getQualifiedName(Object element) throws JavaModelException {
//		if (element instanceof IResource)
//			return ((IResource) element).getFullPath().toString();
//
//		if (element instanceof IJarEntryResource)
//			return ((IJarEntryResource) element).getFullPath().toString();
//
//		if (element instanceof LogicalPackage)
//			return ((LogicalPackage) element).getElementName();
//
//		if (element instanceof IJavaProject || element instanceof IPackageFragmentRoot
//				|| element instanceof ITypeRoot) {
//			IResource resource = ((IJavaElement) element).getCorrespondingResource();
//			if (resource != null)
//				return getQualifiedName(resource);
//		}
//
//		if (element instanceof IBinding)
//			return BindingLabelProvider.getBindingLabel((IBinding) element, LABEL_FLAGS);
//
//		return TextProcessor.deprocess(JavaElementLabels.getTextLabel(element, LABEL_FLAGS));
//	}
//
//	private Object[] getSelectedElements() {
//		if (fEditor != null) {
//			Object element = getSelectedElement(fEditor);
//			if (element == null)
//				return null;
//
//			return new Object[] { element };
//		}

//		ISelection selection = getSelection();
//		if (!(selection instanceof IStructuredSelection))
//			return null;
//
//		List<Object> result = new ArrayList<>();
//		for (Object element : ((IStructuredSelection) selection)) {
//			if (isValidElement(element))
//				result.add(element);
//		}
//		if (result.isEmpty())
//			return null;
//
//		return result.toArray(new Object[result.size()]);
//	}

//	private Object getSelectedElement(JavaEditor editor) {
//		ISourceViewer viewer = editor.getViewer();
//		if (viewer == null)
//			return null;
//
//		Point selectedRange = viewer.getSelectedRange();
//		int length = selectedRange.y;
//		int offset = selectedRange.x;
//
//		ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
//		if (element == null)
//			return null;
//
//		CompilationUnit ast = SharedASTProviderCore.getAST(element, SharedASTProviderCore.WAIT_YES, null);
//		if (ast == null)
//			return null;
//
//		NodeFinder finder = new NodeFinder(ast, offset, length);
//		ASTNode node = finder.getCoveringNode();
//
//		IBinding binding = null;
//		if (node instanceof Name) {
//			binding = getConstructorBindingIfAvailable((Name) node);
//			if (binding != null)
//				return binding;
//			binding = ((Name) node).resolveBinding();
//		} else if (node instanceof MethodInvocation) {
//			binding = ((MethodInvocation) node).resolveMethodBinding();
//		} else if (node instanceof MethodDeclaration) {
//			binding = ((MethodDeclaration) node).resolveBinding();
//		} else if (node instanceof Type) {
//			binding = ((Type) node).resolveBinding();
//		} else if (node instanceof AnonymousClassDeclaration) {
//			binding = ((AnonymousClassDeclaration) node).resolveBinding();
//		} else if (node instanceof TypeDeclaration) {
//			binding = ((TypeDeclaration) node).resolveBinding();
//		} else if (node instanceof CompilationUnit) {
//			return ((CompilationUnit) node).getJavaElement();
//		} else if (node instanceof Expression) {
//			binding = ((Expression) node).resolveTypeBinding();
//		} else if (node instanceof ImportDeclaration) {
//			binding = ((ImportDeclaration) node).resolveBinding();
//		} else if (node instanceof MemberRef) {
//			binding = ((MemberRef) node).resolveBinding();
//		} else if (node instanceof MemberValuePair) {
//			binding = ((MemberValuePair) node).resolveMemberValuePairBinding();
//		} else if (node instanceof PackageDeclaration) {
//			binding = ((PackageDeclaration) node).resolveBinding();
//		} else if (node instanceof TypeParameter) {
//			binding = ((TypeParameter) node).resolveBinding();
//		} else if (node instanceof VariableDeclaration) {
//			binding = ((VariableDeclaration) node).resolveBinding();
//		}
//
//		if (binding != null)
//			return binding.getJavaElement();
//
//		return null;
//	}
}
