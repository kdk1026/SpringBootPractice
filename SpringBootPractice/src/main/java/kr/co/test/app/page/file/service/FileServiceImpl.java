package kr.co.test.app.page.file.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import common.LogDeclare;
import common.spring.resolver.ParamCollector;
import common.util.ObjectUtil;
import common.util.map.MapUtil;
import kr.co.test.app.common.spring.util.Spring4FileUtil;
import kr.co.test.app.common.spring.util.Spring4FileUtil.FileVO;

@Service
public class FileServiceImpl extends LogDeclare implements FileService {
	
	@Value("#{file}")
	private Properties fileProp;
	
	private List<Map<String, Object>> m_fileList = new ArrayList<Map<String, Object>>();
	
	@Override
	public List<Map<String, Object>> getListFile(ParamCollector paramCollector) {
		return m_fileList;
	}

	@Override
	public FileVO processFileUpload(ParamCollector paramCollector) {
		MultipartFile multipartFile = paramCollector.getMultipartFile("file");
		
		String sDestFilePath = fileProp.getProperty("file.upload.path");
		
		FileVO fileVO = Spring4FileUtil.uploadFile(multipartFile, sDestFilePath);
		
		if (fileVO != null) {
			Map<String, Object> fileMap = ObjectUtil.convertObjectToMap(fileVO);
			m_fileList.add(fileMap);
		}
		
		return fileVO;
	}

	@Override
	public void processFileDownload(ParamCollector paramCollector, int idx, HttpServletResponse response) {
		String sDestFilePath = fileProp.getProperty("file.upload.path");
		
		Map<String, Object> map = m_fileList.get(idx);
		FileVO fileVO = new FileVO();
		MapUtil.convertMapToStruct(map, fileVO);
		
		fileVO.destFilePath = sDestFilePath;
		
		Spring4FileUtil.downloadFile(fileVO, paramCollector.getRequest(), response);
	}
	
}
