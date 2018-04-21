/*
 * This source file is part of the sensor_data_mining open source project.
 *
 * Copyright (c) 2018 willy and the sensor_data_mining project authors.
 * Licensed under GNU General Public License v3.0.
 *
 * See /LICENSE for license information.
 * 
 */
package org.uniovi.asw.sensor_data_mining.types;

import java.util.Date;

import lombok.Data;

/**
 * Instance of Comment.java
 * 
 * @author 
 * @version 
 */
@Data
public class Comment {
	private Date date;
    private String operatorId = "";
    private String comment = "";
}
