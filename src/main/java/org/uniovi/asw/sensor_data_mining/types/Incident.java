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

import java.util.Map;

import lombok.Data;

/**
 * Instance of Incident.java
 * 
 * @author
 * @version
 */
@Data
public class Incident {
	private String incidentId;
	private String title;
	private String description;
	private String status;
	private String location;
	private String[] tags;
	private String[] multimedia;
	private Map<String, String> propertyVal;
	private Comment[] comments;
	private String operatorId;

}
