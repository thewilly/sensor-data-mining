/*
 * This source file is part of the sensor_data_mining open source project.
 *
 * Copyright (c) 2018 willy and the sensor_data_mining project authors.
 * Licensed under GNU General Public License v3.0.
 *
 * See /LICENSE for license information.
 * 
 */
package org.uniovi.asw.sensor_data_mining.controllers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uniovi.asw.sensor_data_mining.mining.SensorMinig;


import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

/**
 * Instance of GetDataFromSensorController.java
 * 
 * @author
 * @version
 */
@Slf4j
@RestController
public class GetDataFromSensorController {

    @Autowired
    private EurekaClient eureka;

    @HystrixCommand(fallbackMethod = "reliable")
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/sensor/{sensorId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> mineData(@PathVariable("sensorId") String sensorId) {
	Map<String, Object> responseMap = new HashMap<String, Object>();
	SensorMinig sensorData = new SensorMinig(sensorId, "", eureka);
	responseMap.put("metric", sensorData.getMetric());
	responseMap.put("lectures", sensorData.getNumberOfLectures());
	responseMap.put("min",sensorData.getMin());
	responseMap.put("max",sensorData.getMax());
	responseMap.put("mean",sensorData.getMean());
	responseMap.put("data", sensorData.reduce());

	return new ResponseEntity<String>(new JSONObject(responseMap).toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> reliable(String sensorId) {
	log.error("Falling back in the hystrix safe method.");
	
	Map<String, Object> responseMap = new HashMap<String, Object>();
	responseMap.put("metric", "");
	responseMap.put("lectures", 0);
	responseMap.put("min",0.0);
	responseMap.put("max",0.0);
	responseMap.put("mean",0.0);
	responseMap.put("data", new Object[0]);

	return new ResponseEntity<String>(new JSONObject(responseMap).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
