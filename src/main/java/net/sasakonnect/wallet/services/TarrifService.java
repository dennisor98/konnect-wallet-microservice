package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

}
