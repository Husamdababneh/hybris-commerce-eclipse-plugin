package com.hybris.yps.hyeclipse.beans;

import com.hybris.yps.hyeclipse.utils.MiscUtils;

public class SpringBeanDefinition {

	private String definition;

	private SpringAliasDefinition sad;
	private String id;
	private String clazz;
	private String selection;

	public SpringBeanDefinition(String selection, String text, String qualifiedName) {
		this.selection = selection;
		this.id = "default" + text;
		this.clazz = qualifiedName;
		this.sad = new SpringAliasDefinition(MiscUtils.decapitalize(text));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public SpringAliasDefinition getSad() {
		return sad;
	}

	public void setSad(SpringAliasDefinition sad) {
		this.sad = sad;
	}

	public void resetDefinition() {
		definition = null;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String toSpringXMl() {
		if (definition != null) {
			return definition;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<alias name=\"");
		sb.append(this.getId()).append("\" alias=\"").append(sad.getAlias()).append("\" />\n");
		sb.append("<bean id=\"");
		sb.append(this.getId()).append("\" class=\"").append(clazz).append("\" />");
		definition = sb.toString();
		return definition;
	}

}
