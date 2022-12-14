/*
 * Decompiled with CFR 0.150.
 */
package com.hybris.yps.hyeclipse.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

public class LocationRecordingHandler
extends DefaultHandler {
    public static final String KEY_LIN_NO = "com.hybris.ps.tsv.LineNumber";
    public static final String KEY_COL_NO = "com.hybris.ps.tsv.ColumnNumber";
    private Document doc;
    private Locator locator = null;
    private Element current;

    public LocationRecordingHandler(Document doc) {
        this.doc = doc;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    private void setLocationData(Node n) {
        if (this.locator != null) {
            n.setUserData(KEY_LIN_NO, this.locator.getLineNumber(), null);
            n.setUserData(KEY_COL_NO, this.locator.getColumnNumber(), null);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) {
        Element e = null;
        e = localName != null && !"".equals(localName) ? this.doc.createElementNS(uri, localName) : this.doc.createElement(qName);
        this.setLocationData(e);
        if (this.current == null) {
            this.doc.appendChild(e);
        } else {
            this.current.appendChild(e);
        }
        this.current = e;
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); ++i) {
                Attr attr = null;
                if (attrs.getLocalName(i) != null && !"".equals(attrs.getLocalName(i))) {
                    attr = this.doc.createAttributeNS(attrs.getURI(i), attrs.getLocalName(i));
                    attr.setValue(attrs.getValue(i));
                    this.setLocationData(attr);
                    this.current.setAttributeNodeNS(attr);
                    continue;
                }
                attr = this.doc.createAttribute(attrs.getQName(i));
                attr.setValue(attrs.getValue(i));
                this.setLocationData(attr);
                this.current.setAttributeNode(attr);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (this.current == null) {
            return;
        }
        Node parent = this.current.getParentNode();
        if (parent.getParentNode() == null) {
            this.current.normalize();
            this.current = null;
        } else {
            this.current = (Element)this.current.getParentNode();
        }
    }

    @Override
    public void characters(char[] buf, int offset, int length) {
        if (this.current != null) {
            Text n = this.doc.createTextNode(new String(buf, offset, length));
            this.setLocationData(n);
            this.current.appendChild(n);
        }
    }
}

