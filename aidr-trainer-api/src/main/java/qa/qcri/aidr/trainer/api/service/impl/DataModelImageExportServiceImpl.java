package qa.qcri.aidr.trainer.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import qa.qcri.aidr.trainer.api.dao.DataModelImageExportDao;
import qa.qcri.aidr.trainer.api.service.DataModelImageExportService;

@Service
public class DataModelImageExportServiceImpl implements DataModelImageExportService {

    @Autowired
    private DataModelImageExportDao imageExportDao;

	@Override
	public Long countImageForCollection(String collectionCode) {
		
		Long count = imageExportDao.getTotalImageCountForCollection(collectionCode);
		return count;
	}
}
