package com.innotree.smartkms.datafiles.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.innotree.smartkms.datafiles.model.DataFiles;

@Service
public interface IDataFilesService {
	public DataFiles findByOrgFileName (String orgFileName);
	
	public List<DataFiles> findByIsImport (boolean isImport);
	
	public List<DataFiles> findByElasticIndex (String elasticIndex);
	
	public List<DataFiles> findByElasticIndexAndElasticType (String elasticIndex, String elasticType);
	
	public void insertDataFiles(DataFiles dataFiles);
}
