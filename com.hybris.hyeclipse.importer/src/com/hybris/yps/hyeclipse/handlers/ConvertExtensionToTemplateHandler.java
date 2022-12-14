package com.hybris.yps.hyeclipse.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConvertExtensionToTemplateHandler extends AbstractHandler {

	private IProject project;
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	private static final String CLASS_PREFIX_XPATH = "//extension/@classprefix";
	private static final String MANAGER_NAME_XPATH = "//extension/@managername";
	private static final String PACKAGE_ROOT_XPATH = "//coremodule/@packageroot";
	private static final String EXTENSION_NAME_XPATH = "//extension/@name";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		project = getSelectedExtension(HandlerUtil.getCurrentSelection(event));
		String extensionName = project.getName();
		String absolutPath = project.getLocation().toString();
		String extensionInfoXml = absolutPath + "/extensioninfo.xml";
		String extgenProperties = absolutPath + "/extgen.properties";

		XPath xpath = XPathFactory.newInstance().newXPath();
		File file = new File(extensionInfoXml);
		DocumentBuilder db = getDocumentBuilder();
		if (db == null) {
			return null;
		}

		Document document = getDocument(db, file);
		if (document == null) {
			return null;
		}
		/*-
		 * YEXTNAME_TOKEN=yaddon 
		 * YPACKAGE_TOKEN=yaddonpackage 
		 * YMANAGER_TOKEN=YManager
		 * YCLASSPREFIX_TOKEN=YAddon 
		 * YGENERATED_TOKEN=Generated
		 */
		try {
			String YCLASSPREFIX_TOKEN = xpath.evaluate(CLASS_PREFIX_XPATH, document);
			String YMANAGER_TOKEN = xpath.evaluate(MANAGER_NAME_XPATH, document);
			String YPACKAGE_TOKEN = xpath.evaluate(PACKAGE_ROOT_XPATH, document);
			String YEXTNAME_TOKEN = xpath.evaluate(EXTENSION_NAME_XPATH, document);
			File extgen = new File(extgenProperties);
			extgen.createNewFile();
			FileWriter oFile = new FileWriter(extgen);

			oFile.write("YEXTNAME_TOKEN=" + YEXTNAME_TOKEN + "\n");
			oFile.write("YPACKAGE_TOKEN=" + YPACKAGE_TOKEN + "\n");
			oFile.write("YMANAGER_TOKEN=" + YMANAGER_TOKEN + "\n");
			oFile.write("YCLASSPREFIX_TOKEN=" + YCLASSPREFIX_TOKEN + "\n");
			oFile.write("YGENERATED_TOKEN=Generated");

			oFile.close();

			Node extensioninfo = getChildNode(document.getChildNodes(), "extensioninfo");// .getChildNodes().item(1).getChildNodes().item(9).getClass();
			Node extension = getChildNode(extensioninfo.getChildNodes(), "extension");
			Element createElement = document.createElement("meta");
			createElement.setAttribute("key", "extgen-template-extension");
			createElement.setAttribute("value", "true");
			extension.appendChild(createElement);
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			tr.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Node getChildNode(NodeList nodeList, String string) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			if (item.getNodeName().equals(string)) {
				return item;
			}
		}
		return null;
	}

	private Document getDocument(DocumentBuilder db, File file) {
		try {
			return db.parse(new FileInputStream(file));
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	private DocumentBuilder getDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the {@link IProject} project basing on the current selection
	 * 
	 * @param selection current selection
	 * @return selected project
	 */
	private IProject getSelectedExtension(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object element = structuredSelection.getFirstElement();
		if (element instanceof IProject)
			return (IProject) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IProject.class);
		return (IProject) adapter;
	}

//	private Document createDocument(File file) {
//		Document doc = null;
//		SAXParserFactory factory = SAXParserFactory.newInstance();
//		factory.setValidating(true);
//		factory.setNamespaceAware(true);
//		try {
//			SAXParser sp = factory.newSAXParser();f
//			sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//			LocationRecordingHandler handler = new LocationRecordingHandler(doc);
//			sp.parse(file, (DefaultHandler) handler);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return doc;
//	}
}
