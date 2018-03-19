package com.innotree.smartkms.datafiles.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import com.innotree.smartkms.datafiles.model.DataFiles;
import com.innotree.smartkms.datafiles.repository.DataFilesRepository;

@Service("dataFilesServiceImpl")
public class DataFilesServiceImpl extends QuerydslRepositorySupport implements IDataFilesService {
	
	private static final Logger logger = LoggerFactory.getLogger(DataFilesServiceImpl.class);

	@Autowired
	DataFilesRepository dataFilesRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public DataFilesServiceImpl() {
		super(DataFiles.class);
		// TODO Auto-generated constructor stub
	}
	
	public DataFilesServiceImpl(Class<?> domainClass) {
		super(DataFiles.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DataFiles findByOrgFileName(String orgFileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataFiles> findByIsImport(boolean isImport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataFiles> findByElasticIndex(String elasticIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataFiles> findByElasticIndexAndElasticType(String elasticIndex, String elasticType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertDataFiles(DataFiles dataFiles) {
		// TODO Auto-generated method stub
		dataFilesRepository.saveAndFlush(dataFiles);
	}

}
