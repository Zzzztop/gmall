package com.ztop.gmall.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

public class BaseAttrInfo implements Serializable{
	
	@Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
//   
	@Column
    private String attrName;
   
    @Column
    private String catalog3Id;
 
    @Transient
    private List<BaseAttrValue> attrValueList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getCatalog3Id() {
		return catalog3Id;
	}

	public void setCatalog3Id(String catalog3Id) {
		this.catalog3Id = catalog3Id;
	}

	public List<BaseAttrValue> getAttrValueList() {
		return attrValueList;
	}

	public void setAttrValueList(List<BaseAttrValue> attrValueList) {
		this.attrValueList = attrValueList;
	}

	@Override
	public String toString() {
		return "BaseAttrInfo [id=" + id + ", attrName=" + attrName + ", catalog3Id=" + catalog3Id + ", attrValueList="
				+ attrValueList + "]";
	}
    
    
}
