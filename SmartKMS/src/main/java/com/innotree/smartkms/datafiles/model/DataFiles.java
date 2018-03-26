package com.innotree.smartkms.datafiles.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="datafiles")
@Data
public class DataFiles implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="file_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long fileId;
	
	@Column(name="org_file_name")
	private String orgFileName;
	
	@Column(name="saved_file_name")
	private String savedFileName;
	
	@Column(name="file_path")
	private String filePath;
	
	@Column(name="file_size")
	private long fileSize;
	
	@Column(name="elastic_index")
	private String elasticIndex;
	
	@Column(name="elastic_type")
	private String elasticType;
	
	@Column(name="is_import")
	private boolean isImport;
	
	@Column(name="upload_date")
	private Date uploadDate;
	
	@Column(name="update_date")
	private Date updateDate;
	
	@Column(name="import_date")
	private Date importDate;
	
	@Column(name="total_rows")
	private int totalRows;

	public DataFiles(long fileId, String orgFileName, String savedFileName, long fileSize, String elasticIndex,
			String elasticType, boolean isImport, Date updateDate, Date importDate) {
		super();
		this.fileId = fileId;
		this.orgFileName = orgFileName;
		this.savedFileName = savedFileName;
		this.fileSize = fileSize;
		this.elasticIndex = elasticIndex;
		this.elasticType = elasticType;
		this.isImport = isImport;
		this.updateDate = updateDate;
		this.importDate = importDate;
	}

	public DataFiles() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
