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
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.uniovi.asw.sensor_data_mining.types.Incident;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import lombok.Getter;

/**
 * Instance of SensorMinig.java
 * 
 * @author
 * @version
 */
public class SensorMinig {

	private EurekaClient eureka;

	// private static final String API_GATEWAY =
	// "http://asw-i3a-zuul-eu-west-1.guill.io";

	private String sensorId;
	@Getter
	private String metric;
	private Incident[] incidents;
	private Map<Long, Double> reduced = new HashMap<Long, Double>();

	public SensorMinig( String sensorId, String metric, EurekaClient eureka ) {
		this.sensorId = sensorId;
		this.metric = metric;
		this.eureka = eureka;
		this.execute();
	}

	public Map<Long, Double> reduce() {
		return reduced;
	}

	private void execute() {

		InstanceInfo instance = eureka.getNextServerFromEureka( "operators_service", false );

		incidents = new RestTemplate().getForObject(
				"http://" + instance.getHostName() + ":" + instance.getSecurePort()
						+ "/incidents?operatorId=" + this.sensorId,
				Incident[].class );

		String mostRelevantMetricComputed = mostRelevantMetric();
		if (mostRelevantMetricComputed != null) {
			metric = mostRelevantMetricComputed;
		}

		Arrays.stream( incidents ).forEach( i -> {
			if (i.getPropertyVal().containsKey( metric )) {
				this.reduced.put( i.getDate().getTime(), Double.parseDouble(
						i.getPropertyVal().get( metric ) ) );
			}
		} );
	}

	private String mostRelevantMetric() {
		InstanceInfo instance = eureka.getNextServerFromEureka( "agents_service", false );

		String agentData = new RestTemplate().getForObject(
				"http://" + instance.getHostName() + ":" + instance.getSecurePort() + "/agents/"
						+ this.sensorId,
				String.class );

		if (agentData.toUpperCase().contains( "TEMPERATURE" )) {
			return "temperature";
		} else if (agentData.toUpperCase().contains( "HUMIDITY" )) {
			return "humidity";
		} else if (agentData.toUpperCase().contains( "POWER" )) {
			return "power consumption";
		}
		return null;
	}
}
