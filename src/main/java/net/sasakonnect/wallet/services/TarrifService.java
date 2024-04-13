package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.tarrif.TariffDTO;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.invoice.Tariff;
import net.sasakonnect.wallet.repository.TarrifRepository;

@Service
public class TarrifService {
	@Autowired
	TarrifRepository tarrifRepository;
	
	public ResponseEntity<Object> getCostOn(ChannelType channelType,double amount,String opponnentAccount) {
		Optional<Tariff>tarrif;
		tarrif=this.tarrifRepository.findByMinLessThanEqualAndMaxGreaterThanEqualAndChannelTypeAndOpponentAccountContaining(amount, amount, channelType, opponnentAccount);
		  if(tarrif.isPresent()) {
		    	var userTarrif=Tariff.builder().totalCostToUserIncl(tarrif.get().getTotalCostToUserIncl()).build();

			  Map<String, Object> map = new HashMap<>();

		    	map.put("success", true);
				map.put("payload", userTarrif);
				return ResponseEntity.status(HttpStatus.OK).body(map);  
		  }
		 tarrif= this.tarrifRepository.findByMinLessThanEqualAndMaxGreaterThanEqualAndChannelType(amount, amount,channelType);
	    if(tarrif.isPresent()) {
	    	var userTarrif=Tariff.builder().totalCostToUserIncl(tarrif.get().getTotalCostToUserIncl()).build();
		    Map<String, Object> map = new HashMap<>();

	    	map.put("success", true);
			map.put("payload", userTarrif);
			return ResponseEntity.status(HttpStatus.OK).body(map);
	    	
	    }
	    Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		map.put("message", "tarrif not found Channel : "+channelType+"Amount :"+amount);

		
		map.put("payload", null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
	}

	public ResponseEntity<Object> createTarrif(@Valid TariffDTO t) {
	Optional<Tariff> tariffExists = this.tarrifRepository.findMaxAndMin(t.getChannelType(),t.getMin(),t.getMax());  
	if(tariffExists.isPresent()) {
		Map<String,Object> map =  new HashMap<>();
		map.put("success",false);
		map.put("message","Tarrif tier range already exists");
	
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}else {
		
	try {
	  var tarrif=	Tariff.builder().max(t.getMax()).min(t.getMin()).cbCharge(t.getCbCharge())
		.channelType(t.getChannelType())
		.exciseDutyTax(t.getExciseDutyTax())
		.channelCharge(t.getChannelCharge())
		.opponentAccount(t.getOpponentAccount())
		.totalMarginTaxable(t.getTotalMarginTaxable())
		.savedValue(t.getSavedValue())
		.tierLabel(t.getTierLabel())
		.tier(t.getTier())
		.totalPartnerProfit(t.getTotalPartnerProfit())
		.totalCostToUserIncl(t.getTotalCostToUserIncl()).build();
	Map<String,Object> map =  new HashMap<>();
	map.put("success",true);
	map.put("message","Tarrif created successfully");
	map.put("tarrif",tarrif);
    this.tarrifRepository.save(tarrif);
    return ResponseEntity.status(HttpStatus.OK).body(map);
	}catch(Exception ex) {
		Map<String,Object> map =  new HashMap<>();
		map.put("success",false);
		map.put("message","Error creating tarrif");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	}
	
	}
	
		
	}
	
	public ResponseEntity<Object> getTarrifs(){
		List<Tariff> tarrifs = this.tarrifRepository.findAllByOrderByMinAsc();
		Map<String,Object> map =  new HashMap<>();
		map.put("success", true);
		map.put("tarrifs", !tarrifs.isEmpty()? tarrifs.stream().collect(Collectors.toList()): new ArrayList<>());
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	public ResponseEntity<Object> filterByChannel(ChannelType channelType){
		List<Tariff> tarrifs = this.tarrifRepository.findByChannelTypeOrderByMinAsc(channelType);
		if(tarrifs.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",true);
			map.put("message","Request successfull");
			map.put("tarrifs",new ArrayList<>());
			return  ResponseEntity.status(HttpStatus.OK).body(map);
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("success",true);
			map.put("message","Request successfull");
			map.put("tarrifs",tarrifs.stream().collect(Collectors.toList()));
			return  ResponseEntity.status(HttpStatus.OK).body(map);
		}
	}
	
	public ResponseEntity<Object> editTarrif(String id,TariffDTO t){
		Optional<Tariff> tariff = this.tarrifRepository.findById(id);
		if(tariff.isPresent()) {
			Map<String,Object> map =  new HashMap<>();
			map.put("success", true);
			map.put("message","Tarrif edited successfully");
		 var trf = 	tariff.get();
			 trf.setChannelType(t.getChannelType());
			 trf.setCbCharge(t.getCbCharge());
			 trf.setChannelCharge(t.getChannelCharge());
			 trf.setExciseDutyTax(t.getExciseDutyTax());
			 trf.setMax(t.getMax());
			 trf.setMin(t.getMin());
			 trf.setTier(t.getTier());
			 trf.setTierLabel(t.getTierLabel());
			 trf.setOpponentAccount(t.getOpponentAccount());
			 trf.setSavedValue(t.getSavedValue());
			 trf.setTotalCostExclToPartner(t.getTotalCostExclToPartner());
			 trf.setTotalCostToUserIncl(t.getTotalCostToUserIncl());
			 trf.setTotalMarginTaxable(t.getTotalMarginTaxable());
			 trf.getTotalCostToUserIncl();
			 
			 this.tarrifRepository.save(trf);
		map.put("trf", trf);
		return ResponseEntity.status(HttpStatus.OK).body(map);
		}else {
			Map<String,Object> map =  new HashMap<>();
			map.put("success", false);
			map.put("message","Uknown tarrif");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
			
		}
	}
	
	public ResponseEntity<Object> deleteTarrif(String id){
		Optional<Tariff> tarrif =  this.tarrifRepository.findById(id);
		
		if(tarrif.isPresent()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message","Tarrif deleted");
			this.tarrifRepository.delete(tarrif.get());
		  return ResponseEntity.status(HttpStatus.OK).body(map);
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message","Uknown tarrif");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		
	}

	
}
