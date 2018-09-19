package com.ztop.gmall.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class BaseAttrValue implements Serializable{
	
	 	@Id
	    @Column
	    private String id;
	    
	 	@Column
	    private String valueName;
	    
	 	@Column
	    private String attrId;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getValueName() {
			return valueName;
		}

		public void setValueName(String valueName) {
			this.valueName = valueName;
		}

		public String getAttrId() {
			return attrId;
		}

		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}
	 	
	 	
}
