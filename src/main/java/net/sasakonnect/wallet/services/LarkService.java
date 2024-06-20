package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.PinResetDto;
import net.sasakonnect.wallet.RequestDto.ReversalDto;
import net.sasakonnect.wallet.RequestDto.lark.LarkDTO;
import net.sasakonnect.wallet.RequestDto.lark.ReplyBody;
import net.sasakonnect.wallet.RequestDto.lark.user.LarkMessageDTO;
import net.sasakonnect.wallet.ResponseDto.lark.Event;
import net.sasakonnect.wallet.ResponseDto.lark.EventCallbackDto;
import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserWallet;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.enums.NotificationBody;
import net.sasakonnect.wallet.jobs.LarkUsersSync;
import net.sasakonnect.wallet.repository.LarkUserRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class Element {
    private String tag;

    public Element(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}

class DivElement extends Element {
    private List<Field> fields;

    public DivElement(List<Field> fields) {
        super("div");
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }
}

class Field {
    private boolean isShort;
    private Text text;

    public Field(boolean isShort, Text text) {
        this.isShort = isShort;
        this.text = text;
    }

    public boolean isShort() {
        return isShort;
    }

    public Text getText() {
        return text;
    }
}

class Text {
    private String content;
    private String tag;

    public Text(String content, String tag) {
        this.content = content;
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }
}

@Slf4j
@Service
public class LarkService {
	
	@Autowired
	 LarkUserRepository   larkUserRepository;

	@Autowired
	public LarkUsersSync larkSync;
	
	@Autowired
	UserRepository userRepository;

	
	protected final String botId = "cli_a53a08afc8b8d00a";
	protected String botSecret = "v0SWDp3ppqPHuKQ0ihtTefQiazd7lUFh";
	protected final String larkBaseUrl = "https://open.larksuite.com/open-apis";
	
	
//	String cardTitle,String color,NotificationBody notificationBody
	public void sendOnBoardingMessage(String cardTitle,String color,NotificationBody notificationBody) {
		var accessToken  = this.larkSync.getBotToken(this.botId,this.botSecret);
		System.out.println("token"+accessToken);
		var urlEndpoint = this.larkBaseUrl+"/message/v4/send/";
		Map<String,Object> requestMap = new HashMap<>();
		requestMap.put("msg_type","interactive");
		requestMap.put("chat_id","oc_a9f46991cde6bf92a6b84ee331f5ea99");
		requestMap.put("update_multi", false);
		
		Map<String,Object> card = new HashMap<>();
		Map<String,Object> config = new HashMap<>();
		config.put("wide_screen_mode",true);
//		ArrayList<Element> elements = new ArrayList<>();
		ArrayList<Map<String,Object>> elements = new ArrayList<>();
        Map<String,Object> markdown1 = new HashMap<>();
        markdown1.put("tag", "markdown");
        markdown1.put("content","<at id=ou_58180bf0fcc619b69d7eccfd14741939></at>,<at id=ou_4902c36327956f8db2e29900b559e994></at>,<at id=ou_97ebb1bf896adf1e2f854589fe8f352f></at>");
        
        
        Map<String,Object> markdown2 = new HashMap<>();
        markdown2.put("tag", "markdown");
        markdown2.put("content",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        Map<String,Object> tagSet = new HashMap<>();
        tagSet.put("tag","column_set");
        tagSet.put("flex_mode","none");
        tagSet.put("background_style", "grey");
        
        ArrayList<Map<String,Object>> columns = new ArrayList<>();
        String[] columnNames = {"Date","OB ID","Acc. No.","Acc. Name","Code"};
        for(String col : columnNames) {
        	Map<String,Object>  cols = new HashMap<>(); 
        	cols.put("tag","column");
        	cols.put("width","weighted");
        	cols.put("weight",1);
        	cols.put("vertical_align","top");
            ArrayList<Map<String,Object>> colElements = new ArrayList<>();
            Map<String,Object> colElementsObj = new HashMap<>();
            colElementsObj.put("tag", "markdown");
            colElementsObj.put("content",col);
            colElementsObj.put("text_align", "center");  
            colElements.add(colElementsObj);
        	cols.put("elements", colElements);
           columns.add(cols);
        }
        
        
        
        
        
        Map<String,Object> rowsTagSet = new HashMap<>();
   		rowsTagSet.put("tag","column_set");
   		rowsTagSet.put("flex_mode","bisect");
   		rowsTagSet.put("background_style","grey");
   		rowsTagSet.put("horizontal_spacing","default");
   		ArrayList<Map<String,Object>> rowsCols = new ArrayList<>();
   		
   		Map<String,Object> rowsCol00Objects =  new HashMap<>();
   		rowsCol00Objects.put("tag","column");
   		rowsCol00Objects.put("width","weighted");
   		rowsCol00Objects.put("weight",1);
   		rowsCol00Objects.put("vertical_align","top");
        Map<String,Object> dateColElementsObj = new HashMap<>();
        ArrayList<Map<String,Object>> rowCol00Elements =  new ArrayList<>();
        dateColElementsObj.put("tag","markdown");
        dateColElementsObj.put("content",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dateColElementsObj.put("text_align","center");
        rowCol00Elements.add(dateColElementsObj);
        rowsCol00Objects.put("elements", rowCol00Elements);
        rowsCols.add(rowsCol00Objects);
        
   		Map<String,Object> rowsCol0Objects =  new HashMap<>();
        rowsCol0Objects.put("tag","column");
        rowsCol0Objects.put("width","weighted");
        rowsCol0Objects.put("weight",1);
        rowsCol0Objects.put("vertical_align","top");
        Map<String,Object> obidColElementsObj = new HashMap<>();
        ArrayList<Map<String,Object>> rowCol3Elements =  new ArrayList<>();
        obidColElementsObj.put("tag","markdown");
        obidColElementsObj.put("content",notificationBody.getOnboardingRequestId());
        obidColElementsObj.put("text_align","center");
        rowCol3Elements.add(obidColElementsObj);
        rowsCol0Objects.put("elements", rowCol3Elements);        
        rowsCols.add(rowsCol0Objects);
        
   	  Map<String,Object> rowsCol1Objects =  new HashMap<>();
      rowsCol1Objects.put("tag","column");
      rowsCol1Objects.put("width","weighted");
      rowsCol1Objects.put("weight",1);
      rowsCol1Objects.put("vertical_align","top");
      ArrayList<Map<String,Object>> rowCol1Elements =  new ArrayList<>();
      Map<String,Object> idColElementsObj = new HashMap<>();
      idColElementsObj.put("tag","markdown");
      idColElementsObj.put("content",notificationBody.getAccountId().toString());
      idColElementsObj.put("text_align","center");
      rowCol1Elements.add(idColElementsObj);
      rowsCol1Objects.put("elements",rowCol1Elements);
      rowsCols.add(rowsCol1Objects);
      
      Map<String,Object> rowsCol2Objects =  new HashMap<>();
      rowsCol2Objects.put("tag","column");
      rowsCol2Objects.put("width","weighted");
      rowsCol2Objects.put("weight",1);
      rowsCol2Objects.put("vertical_align","top");
      Map<String,Object> nameColElementsObj = new HashMap<>();
      ArrayList<Map<String,Object>> rowCol2Elements =  new ArrayList<>();
      Optional<User> user = this.userRepository.findByOnboardingRequestId(notificationBody.getOnboardingRequestId());
      nameColElementsObj.put("tag","markdown");
      nameColElementsObj.put("content",user.isPresent() ? user.get().getFirstName()+" "+user.get().getLastName() : "");
      nameColElementsObj.put("text_align","center");
      rowCol2Elements.add(nameColElementsObj);
      rowsCol2Objects.put("elements", rowCol2Elements);
      rowsCols.add(rowsCol2Objects);
      
      
      Map<String,Object> rowsCol3Objects =  new HashMap<>();
      rowsCol3Objects.put("tag","column");
      rowsCol3Objects.put("width","weighted");
      rowsCol3Objects.put("weight",1);
      rowsCol3Objects.put("vertical_align","top");
      
      Map<String,Object> codeColElementsObj = new HashMap<>();
      codeColElementsObj.put("tag","markdown");
      codeColElementsObj.put("content",Integer.toString(notificationBody.getStatus()));
      codeColElementsObj.put("text_align","center");
      ArrayList<Map<String,Object>> rowCol4Elements =  new ArrayList<>();
      rowCol4Elements.add(codeColElementsObj);
      rowsCol3Objects.put("elements", rowCol4Elements);
      rowsCols.add(rowsCol3Objects);
      
      tagSet.put("columns", columns);
      rowsTagSet.put("columns",rowsCols);	       
        elements.add(markdown1);
        elements.add(tagSet);
        elements.add(rowsTagSet);
		card.put("config",config);
		card.put("elements",elements);
		Map<String,Object> header =  new HashMap<>();
		header.put("template",color);
		Map<String,Object> title =  new HashMap<>();
		title.put("content",cardTitle);
		title.put("tag","plain_text");
		header.put("title", title);
		card.put("header", header);
		requestMap.put("card", card);
		
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);   
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestMap,headers);

        
        ResponseEntity<Object> responseEntity = restTemplate.exchange(
        		urlEndpoint.toString(),
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
        
		
	}
	
	public void sendPinResetNotification(User user,Wallet wallet,String type,String status) {
		var accessToken  = this.larkSync.getBotToken(this.botId,this.botSecret);
		var urlEndpoint = this.larkBaseUrl+"/message/v4/send/";
		Map<String,Object> requestMap = new HashMap<>();
		requestMap.put("msg_type","interactive");
		requestMap.put("chat_id","oc_af7a9bacdb2eba15ab57ce122c9eff0a");
		requestMap.put("update_multi", false);
		
		Map<String,Object> card = new HashMap<>();
		Map<String,Object> config = new HashMap<>();
		config.put("wide_screen_mode",true);
//		ArrayList<Element> elements = new ArrayList<>();
		ArrayList<Map<String,Object>> elements = new ArrayList<>();
        Map<String,Object> markdown1 = new HashMap<>();
        markdown1.put("tag", "markdown");
        markdown1.put("content","<at id=ou_58180bf0fcc619b69d7eccfd14741939></at>,<at id=ou_4902c36327956f8db2e29900b559e994></at>,<at id=ou_97ebb1bf896adf1e2f854589fe8f352f></at>");
        
        Map<String,Object> markdown2 = new HashMap<>();
        markdown2.put("tag", "markdown");
        markdown2.put("content",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        Map<String,Object> tagSet = new HashMap<>();
        tagSet.put("tag","column_set");
        tagSet.put("flex_mode","none");
        tagSet.put("background_style", "grey");
        
        ArrayList<Map<String,Object>> columns = new ArrayList<>();
        String[] columnNames = {"Date","Acc. No.","Reseter","Reset Type","Reset status"};
        for(String col : columnNames) {
        	Map<String,Object>  cols = new HashMap<>(); 
        	cols.put("tag","column");
        	cols.put("width","weighted");
        	cols.put("weight",1);
        	cols.put("vertical_align","top");
            ArrayList<Map<String,Object>> colElements = new ArrayList<>();
            Map<String,Object> colElementsObj = new HashMap<>();
            colElementsObj.put("tag", "markdown");
            colElementsObj.put("content",col);
            colElementsObj.put("text_align", "center");  
            colElements.add(colElementsObj);
        	cols.put("elements", colElements);
           columns.add(cols);
        }
        
        
        
        
        
        Map<String,Object> rowsTagSet = new HashMap<>();
   		rowsTagSet.put("tag","column_set");
   		rowsTagSet.put("flex_mode","bisect");
   		rowsTagSet.put("background_style","grey");
   		rowsTagSet.put("horizontal_spacing","default");
   		ArrayList<Map<String,Object>> rowsCols = new ArrayList<>();
   		
   		Map<String,Object> rowsCol00Objects =  new HashMap<>();
   		rowsCol00Objects.put("tag","column");
   		rowsCol00Objects.put("width","weighted");
   		rowsCol00Objects.put("weight",1);
   		rowsCol00Objects.put("vertical_align","top");
        Map<String,Object> dateColElementsObj = new HashMap<>();
        ArrayList<Map<String,Object>> rowCol00Elements =  new ArrayList<>();
        dateColElementsObj.put("tag","markdown");
        dateColElementsObj.put("content",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dateColElementsObj.put("text_align","center");
        rowCol00Elements.add(dateColElementsObj);
        rowsCol00Objects.put("elements", rowCol00Elements);
        rowsCols.add(rowsCol00Objects);
        
   		Map<String,Object> rowsCol0Objects =  new HashMap<>();
        rowsCol0Objects.put("tag","column");
        rowsCol0Objects.put("width","weighted");
        rowsCol0Objects.put("weight",1);
        rowsCol0Objects.put("vertical_align","top");
        Map<String,Object> obidColElementsObj = new HashMap<>();
        ArrayList<Map<String,Object>> rowCol3Elements =  new ArrayList<>();
        obidColElementsObj.put("tag","markdown");
        obidColElementsObj.put("content",wallet.getAccountId());
        obidColElementsObj.put("text_align","center");
        rowCol3Elements.add(obidColElementsObj);
        rowsCol0Objects.put("elements", rowCol3Elements);        
        rowsCols.add(rowsCol0Objects);
        
   	  Map<String,Object> rowsCol1Objects =  new HashMap<>();
      rowsCol1Objects.put("tag","column");
      rowsCol1Objects.put("width","weighted");
      rowsCol1Objects.put("weight",1);
      rowsCol1Objects.put("vertical_align","top");
      ArrayList<Map<String,Object>> rowCol1Elements =  new ArrayList<>();
      Map<String,Object> idColElementsObj = new HashMap<>();
      idColElementsObj.put("tag","markdown");
      idColElementsObj.put("content",user.getFirstName()+"  "+user.getLastName());
      idColElementsObj.put("text_align","center");
      rowCol1Elements.add(idColElementsObj);
      rowsCol1Objects.put("elements",rowCol1Elements);
      rowsCols.add(rowsCol1Objects);
      
      Map<String,Object> rowsCol2Objects =  new HashMap<>();
      rowsCol2Objects.put("tag","column");
      rowsCol2Objects.put("width","weighted");
      rowsCol2Objects.put("weight",1);
      rowsCol2Objects.put("vertical_align","top");
      Map<String,Object> nameColElementsObj = new HashMap<>();
      ArrayList<Map<String,Object>> rowCol2Elements =  new ArrayList<>();
      nameColElementsObj.put("tag","markdown");
      nameColElementsObj.put("content",type);
      nameColElementsObj.put("text_align","center");
      rowCol2Elements.add(nameColElementsObj);
      rowsCol2Objects.put("elements", rowCol2Elements);
      rowsCols.add(rowsCol2Objects);
      
      
      Map<String,Object> rowsCol3Objects =  new HashMap<>();
      rowsCol3Objects.put("tag","column");
      rowsCol3Objects.put("width","weighted");
      rowsCol3Objects.put("weight",1);
      rowsCol3Objects.put("vertical_align","top");
      
      Map<String,Object> codeColElementsObj = new HashMap<>();
      codeColElementsObj.put("tag","markdown");
      codeColElementsObj.put("content",status);
      codeColElementsObj.put("text_align","center");
      ArrayList<Map<String,Object>> rowCol4Elements =  new ArrayList<>();
      rowCol4Elements.add(codeColElementsObj);
      rowsCol3Objects.put("elements", rowCol4Elements);
      rowsCols.add(rowsCol3Objects);
      
    
      
      tagSet.put("columns", columns);
      rowsTagSet.put("columns",rowsCols);	       
        elements.add(markdown1);
        elements.add(tagSet);
        elements.add(rowsTagSet);
		card.put("config",config);
		card.put("elements",elements);
		Map<String,Object> header =  new HashMap<>();
		header.put("template","orange");
		Map<String,Object> title =  new HashMap<>();
		title.put("content","PIN RESET ATTEMPT");
		title.put("tag","plain_text");
		header.put("title", title);
		card.put("header", header);
		requestMap.put("card", card);
		
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);   
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestMap,headers);

        
        ResponseEntity<Object> responseEntity = restTemplate.exchange(
        		urlEndpoint.toString(),
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
        
		
	}
   
   public Object getLarkUsers(Integer pageNumber,Integer pageSize) {
	   
	   Page<LarkUser> larkUsers = this.larkUserRepository.findAll(PageRequest.of(pageNumber,pageSize));
	   Map<String,Object> data =  new HashMap<>();
       Map<String,Object> payload =  new HashMap<>();
	   payload.put("success",true);
	   payload.put("message","Request succesfull");			
	  List<Map<String, Object>> usersCollection = larkUsers.get().map(user -> {
	  Map<String,Object> resultData = new HashMap<>();
				    resultData.put("id",user.getId());
				    resultData.put("open_id",user.getOpenId());
				    resultData.put("name",user.getName());
				    resultData.put("email",user.getEmail());
				    resultData.put("mobileNumber",user.getMobileNumber());
				    resultData.put("avatarUrl",user.getAvartarUrl());
				    return resultData;
				}).collect(Collectors.toList());
				data.put("result",usersCollection);
			
			data.put("totalRows",Double.valueOf(larkUsers.getTotalElements()));
			data.put("pageSize", larkUsers.getSize());
			data.put("currentPage",larkUsers.getNumber());
			data.put("nextPage",larkUsers.hasNext()?larkUsers.nextPageable().getPageNumber() : null);
			data.put("hasNextPage",larkUsers.hasNext());
			data.put("hasPreviousPage",larkUsers.hasPrevious());
			payload.put("payload",data);
			return payload;
		
		   }
   
      public void syncLarkUsers() {
    	  this.larkSync.syncLarkDeptUsers();
      }
      
      public void synLarkDepartments() {
    	  this.larkSync.getLarkDepartments();
    }
      
      
    public ResponseEntity<Object> search(String searchString,Integer pageNumber,Integer pageSize){
    	Page<LarkUser> users = this.larkUserRepository.searchLarkUser(searchString, PageRequest.of(pageNumber,pageSize));
    	if(!users.isEmpty()) {
    		var larkUsers = users.stream().map(l->{
    			return l;
    		}).collect(Collectors.toList());
    		
    		ResponsePagerClass<LarkUser> page =  ResponsePagerClass.<LarkUser>builder()
		    		    .page(users)
		    		    .build();
    		Map<String,Object> map = new HashMap<>();
    		map.put("success",true);
    		map.put("users", larkUsers);
    		map.put("page",page.getPagingInfo());
    		
    		return ResponseEntity.status(HttpStatus.OK).body(map);
    	}else {
    		Map<String,Object> map = new HashMap<>();
    		map.put("success",true);
    		map.put("users", new ArrayList<>());
    		
    		return ResponseEntity.status(HttpStatus.OK).body(map);
    	}
    }
    
    public void sendPinResetApprovalNotification(PinResetDto req,Wallet wallet,User user,float rank) {
     var accessToken  = this.larkSync.getBotToken(this.botId,this.botSecret);
	 var urlEndpoint = this.larkBaseUrl+"/message/v4/send/";
	        String header = "PIN RESET REQUEST";
	        String message = 
	        		        "Date:"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\n"+
	        		        "Requested By(Support): "+user.getFirstName()+" "+user.getLastName()+"\n"+
	        		        "Approver: <at id=%s></at>".formatted(req.getApprover())+"\n"+
	        		        "Account Name: "+req.getFirstName()+" "+req.getLastName()+"\n"+
	        		        "Account No: "+wallet.getAccountId()+"\n"+
	        		        "Mobile: "+req.getMobileNumber() +"\n\n"+
	        		        "**User Questions Responses**"+"\n"+
	        		        "**Phone**: "+req.getMobileNumber()+"\n"+
	        		        "**First Name**: "+req.getFirstName()+"\n"+
	        		        "**Last Name**: "+req.getLastName()+"\n"+
	        		        "**ID No**: "+req.getIdNumber()+"\n"+
	        		        "**Acc Bal**: "+req.getBalance()+"\n"+
	        		        "**Last Received Amount**: "+req.getLastReceivedAmount()+"\n"+
	        		        "**Last sent Amount**: "+req.getLastSentAmount()+"\n"+
	        		        "**Reason:** "+req.getResetReason()+"\n\n"+
	        		        "**Response Rank(Auto)**: "+rank+"%";
	        		       
	        		        
	        		        ;

	        Map<String, Object> card = new HashMap<>();
	        card.put("msg_type", "interactive");
//	        oc_f11965f2d1af0ecb6e39c29ff7beec86   //test group
//	        oc_a9f46991cde6bf92a6b84ee331f5ea99
	        //oc_af7a9bacdb2eba15ab57ce122c9eff0a
	        card.put("chat_id", "oc_af7a9bacdb2eba15ab57ce122c9eff0aadd");
	        card.put("update_multi", false);

	        Map<String, Object> cardObj = new HashMap<>();
	        Map<String, Object> config = new HashMap<>();
	        config.put("wide_screen_mode", true);
	        cardObj.put("config", config);

	        Map<String, Object> div = new HashMap<>();
	        div.put("tag", "div");

	        Map<String, Object> field1 = new HashMap<>();
	        field1.put("is_short", true);
	        Map<String, Object> text1 = new HashMap<>();
	        text1.put("tag", "lark_md");
	        text1.put("content", "<at id=ou_58180bf0fcc619b69d7eccfd14741939></at>,<at id=ou_4902c36327956f8db2e29900b559e994></at>,<at id=ou_97ebb1bf896adf1e2f854589fe8f352f></at>"+"\n" + message.replace(",", ""));
	        field1.put("text", text1);

	        Map<String, Object> field2 = new HashMap<>();
	        field2.put("is_short", false);
	        Map<String, Object> text2 = new HashMap<>();
	        text2.put("tag", "lark_md");
	        text2.put("content", "");
	        field2.put("text", text2);

	        div.put("fields", Arrays.asList(field1, field2));
	        cardObj.put("elements", Arrays.asList(div));

	        Map<String, Object> headerMap = new HashMap<>();
	        headerMap.put("template", "blue");
	        Map<String, Object> title = new HashMap<>();
	        title.put("tag", "plain_text");
	        title.put("content", header);
	        headerMap.put("title", title);
	        cardObj.put("header", headerMap);

	        card.put("card", cardObj);

	        
        RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);   
        HttpEntity<Object> requestEntity = new HttpEntity<>(card,headers);

        
        ResponseEntity<Object> responseEntity = restTemplate.exchange(
        		urlEndpoint.toString(),
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
        
        
        
    }
    
    public void sendReversalRequestNotification(User user,ReversalDto req,Wallet wallet,Transaction transaction){
    	  var accessToken  = this.larkSync.getBotToken(this.botId,this.botSecret);
    		 var urlEndpoint = this.larkBaseUrl+"/im/v1/messages/";
    		        String header = "TRANSACTION REVERSAL REQUEST";
    		        String message = 
    		        		        "**Date**:"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\n"+
    		        		        "**Requested By**: "+user.getFirstName().toUpperCase()+" "+user.getLastName().toUpperCase()+"\n"+
    		        		        "**Transaction Date**: "+transaction.getCreatedAt().toString()+"\n"+
    		        		        "**Reference**: "+transaction.getTxId()+"\n"+
    		        		        "**Account Name**: "+transaction.getAccountName().toUpperCase()+"\n"+
    		        		        "**Account No**: "+wallet.getAccountId()+"\n"+
    		        		        "**Mobile**: "+req.getMobileNumber()+"\n"+
    		        		        "**Receiver Acc. No**:"+transaction.getOppoAccountId()+"\n"+
    		        		        "**Receiver Acc Name**:"+transaction.getOppoAccountName()+"\n"+
    		        		        "**Amount**: " +
    		        		        transaction.getAmount().abs().add(transaction.getFeeAmount()).setScale(2, RoundingMode.HALF_UP).toPlainString()+"\n"+
    		        		        "Description"+req.getDescription();


    		        Map<String, Object> card = new HashMap<>();
    		        card.put("msg_type", "interactive");
//    		        oc_f11965f2d1af0ecb6e39c29ff7beec86   //test group
//    		        oc_a9f46991cde6bf92a6b84ee331f5ea99
    		        card.put("chat_id", "oc_251f1f064314a925347ecc32d9ea3d3e");
    		        card.put("update_multi", false);

    		        Map<String, Object> cardObj = new HashMap<>();
    		        Map<String, Object> config = new HashMap<>();
    		        config.put("wide_screen_mode", true);
    		        cardObj.put("config", config);

    		        Map<String, Object> div = new HashMap<>();
    		        div.put("tag", "div");

    		        Map<String, Object> field1 = new HashMap<>();
    		        field1.put("is_short", true);
    		        Map<String, Object> text1 = new HashMap<>();
    		        text1.put("tag", "lark_md");
    		        text1.put("content", "<at id=ou_58180bf0fcc619b69d7eccfd14741939></at>,<at id=ou_4902c36327956f8db2e29900b559e994></at>,<at id=ou_97ebb1bf896adf1e2f854589fe8f352f></at>"+"\n" + message.replace(",", ""));
    		        field1.put("text", text1);

    		        Map<String, Object> field2 = new HashMap<>();
    		        field2.put("is_short", false);
    		        Map<String, Object> text2 = new HashMap<>();
    		        text2.put("tag", "lark_md");
    		        text2.put("content", "");
    		        field2.put("text", text2);

    		        div.put("fields", Arrays.asList(field1, field2));
    		        cardObj.put("elements", Arrays.asList(div));

    		        Map<String, Object> headerMap = new HashMap<>();
    		        headerMap.put("template", "blue");
    		        Map<String, Object> title = new HashMap<>();
    		        title.put("tag", "plain_text");
    		        title.put("content", header);
    		        headerMap.put("title", title);
    		        cardObj.put("header", headerMap);

    		        card.put("card", cardObj);

    		        
    	        RestTemplate restTemplate = new RestTemplate();
    			HttpHeaders headers = new HttpHeaders();
    	        headers.setContentType(MediaType.APPLICATION_JSON);
    	        headers.set("Authorization", "Bearer "+accessToken);   
    	        HttpEntity<Object> requestEntity = new HttpEntity<>(card,headers);

    	        
    	        ResponseEntity<Object> responseEntity = restTemplate.exchange(
    	        		urlEndpoint.toString(),
    	                HttpMethod.POST,
    	                requestEntity,
    	                Object.class
    	        );
    	            	
    }
    
    public void replyMessageTag(EventCallbackDto callBackData) {
   	 var urlEndpoint = this.larkBaseUrl+"/im/v1/messages/";
    	Event event = callBackData.getEvent();
//    	if(event.is_mention()) {
       
   String message;
    Optional<User> user =  this.userRepository.findByMobile(event.getText_without_at_bot().trim());
    log.info(event.getText_without_at_bot());
    if(user.isPresent()) {
    	var u = user.get();
    	List<UserWallet> wallets = u.getUserWallets();
    	
    	String status;
    	if(wallets.isEmpty()) {
    		status = "Pending";
    	}else {
    		status =  "Approved";
    	}
    	String template = 
    	        "Date: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\n"+
    	        "Mobile: "+u.getMobile()+"\n"+
    	        "Acc Name: "+u.getFirstName()+" "+u.getLastName()+"\n"+
    	        "Status: "+status;
        message = "{\"text\":\"<at open_id="+event.getUser_open_id()+"></at>" + template.replace("\n", "\\n").replace("\"", "\\\"") + "\"}";

    }else {
    	message = "{\"text\":\"No account found\"}";
    }
    
    
	  String template = 
	        "Date:"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"\n"+
	        "Mobile: \n"+
	        "Acc Name:\n"+
	        "Status: ";
    		var messageBody =  ReplyBody.builder()
    				           .content(message)
    				           .msg_type("text")
    				           .uuid(callBackData.getUuid())
    				           .receive_id_type("open_id")
    				           .build();
    		
    		 RestTemplate restTemplate = new RestTemplate();
 			HttpHeaders headers = new HttpHeaders();
 	        headers.setContentType(MediaType.APPLICATION_JSON);
 	        headers.set("Authorization", "Bearer "+this.larkSync.getBotToken(botId, botSecret));   
 	        HttpEntity<Object> requestEntity = new HttpEntity<>(messageBody,headers);
    		log.info("{reply response}"+urlEndpoint+event.getOpen_message_id()+"/reply");		        		   

 	        
 	        ResponseEntity<Object> responseEntity = restTemplate.exchange(
 	        		(urlEndpoint+event.getOpen_message_id()+"/reply").toString(),
 	                HttpMethod.POST,
 	                requestEntity,
 	                Object.class
 	        );
    	}
//    }

  
}
