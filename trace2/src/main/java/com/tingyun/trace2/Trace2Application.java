package com.tingyun.trace2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.servo.jsr166e.ConcurrentHashMapV8.Fun;

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class Trace2Application {
	
	private final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private CounterService counterService;
	
	@Autowired
	private GaugeService gaugeService;
	
	@Bean
    public RestTemplate restTemplate() 
    {
    	return new RestTemplate();
    }
	
	@RequestMapping(value="/trace2", method=RequestMethod.GET)
	public String trace2() {
		logger.info("---call trace2---");
		long start = System.currentTimeMillis();
		counterService.increment("Trace2.trace2.count");
		fun();
		saveToFile("trace2");
		try {
			restTemplate().getForEntity("http://www.baiduxxyy.com", String.class).getBody();
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			logger.error("Request for baidu failed!");
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		gaugeService.submit("Trace2.trace2.time", end-start);
		return "trace2";
	}

	private void saveToFile(String string) {
		FileWriter fWriter = null;
		try {
		    fWriter = new FileWriter("myfile.txt");
			fWriter.write(string);
			fWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(fWriter != null) {
				try {
					fWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private void fun() {
		
		logger.info("---fun-----");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(Trace2Application.class, args);
	}
}
