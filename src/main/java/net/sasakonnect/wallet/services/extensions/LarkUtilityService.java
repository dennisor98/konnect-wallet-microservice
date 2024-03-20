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

import com.google.gson.Gson;

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
//    	Thread thread =  new Thread(new Runnable(){
//
//			@Override
//			public void run() {
				Map<String, Object> map = new HashMap<>();
				map.put("msg_type", "interactive");
				map.put("chat_id",openId);
				map.put("update_multi", false);

				Map<String, Object> cardMap = new HashMap<>();
				Map<String, Object> configMap = new HashMap<>();
				configMap.put("wide_screen_mode", true);
				cardMap.put("config", configMap);

				List<Map<String, Object>> fields = new ArrayList<>();
				Map<String, Object> field1 = new HashMap<>();
				field1.put("is_short", true);
				Map<String, Object> text1 = new HashMap<>();
				text1.put("content", "");
				text1.put("tag", "lark_md");
				field1.put("text", text1);

				Map<String, Object> field2 = new HashMap<>();
				field2.put("is_short", false);
				Map<String, Object> text2 = new HashMap<>();
				text2.put("content", "");
				text2.put("tag", "lark_md");
				field2.put("text", text2);

				fields.add(field1);
				fields.add(field2);

				List<Map<String, Object>> elements = new ArrayList<>();

				Map<String, Object> element = new HashMap<>();
				List<Map<String, Object>> elementFields = new ArrayList<>();

				field1.put("is_short", true);
				text1.put("content",message);
				text1.put("tag", "lark_md");
				field1.put("text", text1);
				elementFields.add(field1);
				field2.put("is_short", false);
				text2.put("content", "");
				text2.put("tag", "lark_md");
				field2.put("text", text2);
				elementFields.add(field2);
				element.put("fields", elementFields);
				element.put("tag", "div");

				elements.add(element);
				cardMap.put("elements", elements);


				Map<String, Object> headerMap = new HashMap<>();
				headerMap.put("template", "red");
				Map<String, Object> titleMap = new HashMap<>();
				titleMap.put("content", header);
				titleMap.put("tag", "plain_text");

				headerMap.put("title", titleMap);

				cardMap.put("header", headerMap);
				map.put("card", cardMap);

				// Now you can convert the map into JSON
				Gson gson = new Gson();
				String jsonString = gson.toJson(map);
				System.out.println(jsonString);

	         	RestTemplate restTemplate = new RestTemplate();
	      	HttpHeaders headers = new HttpHeaders();
	          headers.setContentType(MediaType.APPLICATION_JSON);
	          headers.set("Authorization", "Bearer "+LarkUtilityService.this.larkSync.getBotToken(LarkUtilityService.this.botId,LarkUtilityService.this.botSecret));   
	          HttpEntity<Object> requestEntity = new HttpEntity<>(map,headers);

	          var urlEndpoint = LarkUtilityService.this.larkBaseUrl+"/message/v4/send/";
	          ResponseEntity<Object> responseEntity = restTemplate.exchange(
	          		urlEndpoint.toString(),
	                  HttpMethod.POST,
	                  requestEntity,
	                  Object.class
	          );
	          
	          System.out.println(responseEntity);
			}
    		
//    	});
    	
//    	thread.start();
   	   
    
}
