package com.supercluster.spring.hystrix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class StudentController {

	@Autowired
    RestTemplate restTemplate;
  
    @RequestMapping(value = "/studentDetails/{studentid}", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public String getStudents(@PathVariable int studentid)
    {
        try {
			System.out.println("Getting Student details for " + studentid);
  
			String response = restTemplate.exchange("http://localhost:8011/findStudentDetails/{studentid}",
			                        HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, studentid).getBody();
  
			System.out.println("Response Body " + response);
  
			return "Student Id -  " + studentid + " [ Student Details " + response+" ]";
		} catch (RestClientException e) {

			System.out.println("EXCEPTION IN REST CALL :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: ");
			e.printStackTrace();
			
			return "Exception happened : Fallback response:: No student details available temporarily";
		}
    }
     
    public String  fallbackMethod(int studentid){
         
        return "Fallback response:: No student details available temporarily";
    }
  
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
	}

}
