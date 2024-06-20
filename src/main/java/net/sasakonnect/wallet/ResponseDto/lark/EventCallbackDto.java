package net.sasakonnect.wallet.ResponseDto.lark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventCallbackDto {
	private String uuid;
    private Event event;
    private String token;
    private String ts;
    private String type;
}
