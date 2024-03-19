package net.sasakonnect.wallet.services.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.services.LarkService;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MessageRequest {
    private List<Field> fields;
    private String tag;
}

@Data
@Builder
class Field {
    private boolean isShort;
    private Text text;

    // Getters and setters
    // Constructor
}

@Data
@Builder
class Text {
    private String content;
    private String tag;

    // Getters and setters
    // Constructor
}


@Data
@Builder
class Element {
    private List<Field> fields;
    private String tag;
}
@Service
public class LarkUtilityService extends LarkService {
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    CountDownLatch latch = new CountDownLatch(1);

    public void walletStatementAlert(String header,String message,String idType,String openId) {
   	   this.executorService.execute(()-> {
   		 try {
   			Map<String,Object> map = new HashMap<>();
         	 map.put("msg_type","interactive");
         	 map.put(idType,openId);
         	 map.put("update_multi",false);
         	 
         	 Map<String,Object> cardMap = new HashMap<>();
         	 Map<String,Object> configMap = new HashMap<>();
         	 configMap.put("wide_screen_mode",true);
         	 cardMap.put("config", configMap);
         	 
           List<Field> fields = new ArrayList<>();
           Field field1 = Field.builder()
                          .isShort(true)
                          .build();
           Text text1 = Text.builder()
          		          .content("")
          		          .tag("lark_md")
          		          .build();
           field1.setText(text1);
           
           
           Field field2 = Field.builder()
          		             .isShort(false)
          		             .build();
           
           Text text2 = Text.builder()
          		          .content("")
          		          .tag("lark_md")
          		          .build();
           
           field2.setText(text2);
           fields.add(field1);
           fields.add(field2);
           
           Element elements = Element.builder()
          		            .fields(fields)
          		            .tag("div")
          		            .build();
           cardMap.put("elements", elements);
           
           Map<String,Object> headerMap = new HashMap<>();
           headerMap.put("template","red");
           Map<String,Object> titleMap = new HashMap<>();
           titleMap.put("content",message);
           titleMap.put("tag","plain_text");
           
           headerMap.put("title", titleMap);
           
           cardMap.put("header", headerMap);
         	 map.put("card", cardMap);
         	 
         	RestTemplate restTemplate = new RestTemplate();
      	HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.set("Authorization", "Bearer "+this.larkSync.getBotToken(this.botId,this.botSecret));   
          HttpEntity<Object> requestEntity = new HttpEntity<>(map,headers);

          var urlEndpoint = this.larkBaseUrl+"/message/v4/send/";
          ResponseEntity<Object> responseEntity = restTemplate.exchange(
          		urlEndpoint.toString(),
                  HttpMethod.POST,
                  requestEntity,
                  Object.class
          );
          
          System.out.println(responseEntity);
   		 }finally{
   			 this.latch.countDown();
   			 this.executorService.shutdown();
   		 }
       
   	   });
    }
}
