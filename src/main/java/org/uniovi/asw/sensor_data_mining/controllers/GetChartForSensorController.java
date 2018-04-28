package org.uniovi.asw.sensor_data_mining.controllers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.uniovi.asw.sensor_data_mining.mining.SensorMinig;

import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GetChartForSensorController {

    
    @Autowired
    private EurekaClient eureka;
    
    @HystrixCommand(fallbackMethod = "reliable")
    @RequestMapping(value = "/chart/{sensorId}", method = RequestMethod.GET)
    public String mineData(Model model, @PathVariable("sensorId") String sensorId) {
	log.info("Loading /chart/"+sensorId);
	Map<String, Object> responseMap = new HashMap<String, Object>();
	SensorMinig sensorData = new SensorMinig(sensorId, "", eureka);
	responseMap.put("metric", sensorData.getMetric());
	responseMap.put("lectures", sensorData.getNumberOfLectures());
	responseMap.put("min",sensorData.getMin());
	responseMap.put("max",sensorData.getMax());
	responseMap.put("mean",sensorData.getMean());
	responseMap.put("data", sensorData.reduce());
	
	model.addAttribute("sensorId", sensorId);

	return "chartTemplate";
    }

    public String reliable(Model model, String sensorId) {
	log.error("Falling back in the hystrix safe method.");
	
	Map<String, Object> responseMap = new HashMap<String, Object>();
	responseMap.put("metric", "");
	responseMap.put("lectures", 0);
	responseMap.put("min",0.0);
	responseMap.put("max",0.0);
	responseMap.put("mean",0.0);
	responseMap.put("data", new Object[0]);
	
	model.addAttribute("data", new JSONObject(responseMap).toString());

	return "chartTemplate";
    }
}
