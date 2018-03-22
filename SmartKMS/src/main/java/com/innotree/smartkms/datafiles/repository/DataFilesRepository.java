package com.innotree.smartkms.datafiles.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innotree.smartkms.datafiles.model.DataFiles;

public interface DataFilesRepository extends JpaRepository<DataFiles, Long> {
	
	DataFiles findByOrgFileName (String orgFileName);
	
	DataFiles findByFileId (String fileId);
	
	List<DataFiles> findByIsImport (boolean isImport);
	
	List<DataFiles> findByElasticIndex (String elasticIndex);
	
	List<DataFiles> findByElasticIndexAndElasticType (String elasticIndex, String elasticType);
}
