/*
 * This source file is part of the sensor_data_mining open source project.
 *
 * Copyright (c) 2018 willy and the sensor_data_mining project authors.
 * Licensed under GNU General Public License v3.0.
 *
 * See /LICENSE for license information.
 * 
 */
package org.uniovi.asw.sensor_data_mining.mining;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.types.ObjectId;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.uniovi.asw.sensor_data_mining.types.Incident;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Instance of SensorMinig.java
 * 
 * @author
 * @version
 */
@Slf4j
public class SensorMinig {

    private EurekaClient eureka;
    private String sensorId;
    @Getter
    private String metric;
    @Getter
    private double mean = 0.0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
    @Getter
    private int numberOfLectures;
    private Incident[] incidents;
    private Object[][] reduced;

    public SensorMinig(String sensorId, String metric, EurekaClient eureka) {
	this.sensorId = sensorId;
	this.metric = metric;
	this.eureka = eureka;
	this.execute();
    }

    public Object[][] reduce() {
	return reduced;
    }

    private void execute() {
	InstanceInfo instance = eureka.getNextServerFromEureka("incidents_service", false);
	UriComponentsBuilder url = UriComponentsBuilder.fromUriString(instance.getHomePageUrl()+ "/incidents");
	url.queryParam("agentId", this.sensorId);
	log.info("Connecting to: " + url.toUriString());

	try {
	    incidents = new RestTemplate().getForObject(url.toUriString(), Incident[].class);
	} catch (RestClientException e) {
	    log.error(e.toString());
	    throw e;
	}

	this.reduced = new Object[incidents.length][2];

	log.info("Retrieved " + incidents.length + " incidents for the sensor: " + this.sensorId);
	log.info("Starting mining the data.");
	String mostRelevantMetricComputed = mostRelevantMetric();
	if (mostRelevantMetricComputed != null) {
	    metric = mostRelevantMetricComputed;
	}
	AtomicInteger counter = new AtomicInteger(0);
	Arrays.stream(incidents).forEach(i -> {

	    // If the incident contains a lecture for the selected metric.
	    if (i.getPropertyVal().containsKey(metric)) {

		// We assign to lecture the value of the metric.
		double lecture = Double.parseDouble(i.getPropertyVal().get(metric));

		// Adding the values to a map that we will return for the client if it wants to
		// process the data.
		this.reduced[counter.get()][0] = new ObjectId(i.getIncidentId()).getDate().getTime();
		this.reduced[counter.get()][1] = lecture;

		// Then compute some useful metrics. Like medium of all the lectures, min
		// lecture and max lecture.
		mean = +lecture;
		if (lecture < min) {
		    this.min = lecture;
		}
		if (lecture > max) {
		    this.max = lecture;
		}
		counter.incrementAndGet();
	    }
	});

	// Finally we compute the medium dividing by the number of lectures.
	this.mean = this.mean / counter.get();

	// We update the number of metrics.
	this.numberOfLectures = counter.get();
    }

    private String mostRelevantMetric() {
	InstanceInfo instance = eureka.getNextServerFromEureka("agents_service", false);
	log.info("Connecting to: " + instance.getHomePageUrl() + "/agents/" + this.sensorId);

	String agentData = new RestTemplate().getForObject(instance.getHomePageUrl() + "/agents/" + this.sensorId,
		String.class);

	if (agentData.toUpperCase().contains("TEMPERATURE")) {
	    return "temperature";
	} else if (agentData.toUpperCase().contains("HUMIDITY")) {
	    return "humidity";
	} else if (agentData.toUpperCase().contains("POWER")) {
	    return "power consumption";
	}
	return null;
    }
}
