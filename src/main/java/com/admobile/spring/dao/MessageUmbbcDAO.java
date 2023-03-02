package com.admobile.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.admobile.spring.entity.Message;

@Repository
public interface MessageUmbbcDAO extends JpaRepository<Message,Integer>{

	@Query(value="SELECT value FROM mesaage WHERE config = ?1", nativeQuery=true)
	public String selectMessageValue(String param);
}