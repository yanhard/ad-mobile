package com.admobile.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import jakarta.persistence.Id;

@Entity
@Table(name="message")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String config;
}
