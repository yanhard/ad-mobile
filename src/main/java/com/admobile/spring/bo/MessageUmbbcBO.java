package com.admobile.spring.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.admobile.spring.dao.MessageUmbbcDAO;

@Service
public class MessageUmbbcBO {
	@Autowired
	@Qualifier(value="messageUmbbcDAO")
	private MessageUmbbcDAO messageUmbbcDAO;

	@Cacheable("messageUmbbcCache")
	public String messageUmbbc(int i) {
		String param = "message."+ i;
		//MessageUmbbcDAO messageUmbbcDAO = new MessageUmbbcDAO();
		return messageUmbbcDAO.selectMessageValue(param);
	}
}
