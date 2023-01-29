package com.hybris.yps.hyeclipse.utils;

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
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.viewsupport.BindingLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

import com.hybris.yps.hyeclipse.beans.SpringBeanDefinition;

public final class MiscUtils {

	private static final long LABEL_FLAGS = JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.M_FULLY_QUALIFIED
			| JavaElementLabels.I_FULLY_QUALIFIED | JavaElementLabels.T_FULLY_QUALIFIED
			| JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED | JavaElementLabels.T_TYPE_PARAMETERS
			| JavaElementLabels.CU_QUALIFIED | JavaElementLabels.CF_QUALIFIED;

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

	public static CompilationUnit getCompilationUnit(ICompilationUnit icu, IProgressMonitor monitor) {
		final ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setSource(icu);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(monitor);
	}

	private static String getQualifiedName(Object element) {
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
				e.printStackTrace();
			}
		}

		if (element instanceof IBinding binding) {
			String bindingLabel = BindingLabelProvider.getBindingLabel(binding, LABEL_FLAGS);
			String[] split = bindingLabel.split("<");
			return split[0];
		}
		return TextProcessor.deprocess(JavaElementLabels.getTextLabel(element, LABEL_FLAGS));
	}

	public static String getQualifiedName(ITextEditor editor) {
		ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		ICompilationUnit icu = typeRoot.getAdapter(ICompilationUnit.class);
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

		return qualifiedName;
	}

	private static IBinding getBindingFromNode(ASTNode node) {
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

	/**
	 * Checks whether the given name belongs to a {@link ClassInstanceCreation} and
	 * if so, returns its constructor binding.
	 *
	 * @param nameNode the name node
	 * @return the constructor binding or <code>null</code> if not found
	 * @since 3.7
	 */
	private static IBinding getConstructorBindingIfAvailable(Name nameNode) {
		ASTNode type = getNormalizedNode(nameNode);
		StructuralPropertyDescriptor loc = type.getLocationInParent();
		if (loc == ClassInstanceCreation.TYPE_PROPERTY) {
			return ((ClassInstanceCreation) type.getParent()).resolveConstructorBinding();
		}
		return null;
	}

	public static ASTNode getNormalizedNode(ASTNode node) {
		ASTNode current = node;

		if (QualifiedName.NAME_PROPERTY.equals(current.getLocationInParent()))
			current = current.getParent();

		if (QualifiedType.NAME_PROPERTY.equals(current.getLocationInParent())
				|| SimpleType.NAME_PROPERTY.equals(current.getLocationInParent())
				|| NameQualifiedType.NAME_PROPERTY.equals(current.getLocationInParent()))
			current = current.getParent();

		if (ParameterizedType.TYPE_PROPERTY.equals(current.getLocationInParent()))
			current = current.getParent();

		return current;
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
