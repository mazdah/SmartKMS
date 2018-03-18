package com.innotree.smartkms.elastic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innotree.smartkms.elastic.model.DataFiles;

public interface DataFilesRepository extends JpaRepository<DataFiles, Long> {
	
	List<DataFiles> findByOrgFileName (String orgFileName);
	
	DataFiles saveAndFlush(DataFiles dataFiles);
}
