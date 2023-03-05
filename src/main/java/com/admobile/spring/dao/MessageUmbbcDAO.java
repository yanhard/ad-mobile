package com.admobile.spring.dao;

import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;

import org.apache.ibatis.annotations.Select;

@Primary
public interface MessageUmbbcDAO{

	@Select("SELECT value FROM mesaage WHERE config = #{param}")
	public String selectMessageValue(String param);
}