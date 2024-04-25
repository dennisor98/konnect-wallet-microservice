package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.domain.Logs;
import net.sasakonnect.wallet.repository.LogsRepository;

@Service
public class LogService {
 @Autowired
 LogsRepository logsRepository;
 
 
 public ResponseEntity<Object> getLogs(Integer pageNumber,Integer pageSize){
	 Page<Logs> logs =  this.logsRepository.findAll(PageRequest.of(pageNumber,pageSize));
	 Map<String,Object> resMap = new HashMap<>();
	 Map<String,Object> map = new HashMap<>();
	 if(!logs.isEmpty()) {
		 map.put("success",true);
		 map.put("message","Request successful");
		 map.put("totalItems",logs.getTotalElements());
		 map.put("totalPages",logs.getTotalPages());
		 map.put("currentPage",logs.getNumber());
		 map.put("hasNext",logs.hasNext());
		 map.put("nextPage",logs.hasNext()?logs.nextOrLastPageable().getPageNumber() : null);
		 map.put("hasPrevious",logs.hasPrevious());
		 map.put("previousPage",logs.hasPrevious()?logs.previousOrFirstPageable().getPageNumber() : null);
		 map.put("logs",logs.get().map(l->{
			 Map<String,Object> logMap = new HashMap<>();
			 logMap.put("createdAt", l.getCreatedAt());
			 logMap.put("logInfo", l.getDescription());
			 return logMap;
		 }).collect(Collectors.toList()));
		 
		 resMap.put("payLoad", map);
		 
		 return ResponseEntity.status(HttpStatus.OK).body(resMap);
	 }else {
		 map.put("success",true);
		 map.put("message","Request successful");
		 map.put("logs",new ArrayList<>());
		 resMap.put("payLoad", map);
		 return ResponseEntity.status(HttpStatus.OK).body(resMap);
	 }
 }
 
 public ResponseEntity<Object> searchLogs(String queryString,Integer pageNumber,Integer pageSize){
	 if(pageSize > 100) {
		 pageSize = 100;
	 }
	 Page<Logs> logs =  this.logsRepository.findAllByDescriptionLike(queryString,PageRequest.of(pageNumber,pageSize));
	 Map<String,Object> resMap = new HashMap<>();
	 Map<String,Object> map = new HashMap<>();
	 if(!logs.isEmpty()) {
		 map.put("success",true);
		 map.put("message","Request successful");
		 map.put("totalItems",logs.getTotalElements());
		 map.put("totalPages",logs.getTotalPages());
		 map.put("currentPage",logs.getNumber());
		 map.put("hasNext",logs.hasNext());
		 map.put("nextPage",logs.hasNext()?logs.nextOrLastPageable().getPageNumber() : null);
		 map.put("hasPrevious",logs.hasPrevious());
		 map.put("previousPage",logs.hasPrevious()?logs.previousOrFirstPageable().getPageNumber() : null);
		 map.put("logs",logs.get().map(l->{
			 Map<String,Object> logMap = new HashMap<>();
			 logMap.put("createdAt", l.getCreatedAt());
			 logMap.put("logInfo", l.getDescription());
			 return logMap;
		 }).collect(Collectors.toList()));
		 
		 resMap.put("payLoad", map);
		 
		 return ResponseEntity.status(HttpStatus.OK).body(resMap);
	 }else {
		 map.put("success",true);
		 map.put("message","Request successful");
		 map.put("logs",new ArrayList<>());
		 resMap.put("payLoad", map);
		 return ResponseEntity.status(HttpStatus.OK).body(resMap);
	 }
 }
 
 

} 
