package net.sasakonnect.wallet.ResponseDto.lark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
	private String app_id;
    private String chat_type;
    private String employeeId;
    private boolean is_mention;
    private String message_id;
    private String msg_type;
    private String open_chat_id;
    private String openId;
    private String open_message_id;
    private String parent_id;
    private String root_id;
    private String tenant_key;
    private String text;
    private String text_without_at_bot;
    private String type;
    private String union_id;
    private String user_agent;
    private String user_open_id;
}
