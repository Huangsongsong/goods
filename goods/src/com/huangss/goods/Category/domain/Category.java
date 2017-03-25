package com.huangss.goods.Category.domain;

import java.util.List;

/**
 * 分类菜单
 * 一级分类和二级分类共用一张表，自身关联，当外键pid为null时，表示一级分类
 * @author 黄松松
 *
 */
public class Category {
	private String cid;
	private String cname;
	private String desc;
	
	private Category parent;//二级分类通过外键pid关联 一级分类的主键
	private List<Category> children;//一个一级分类可以有多个二级分类
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public List<Category> getChildren() {
		return children;
	}
	public void setChildren(List<Category> children) {
		this.children = children;
	}
}
