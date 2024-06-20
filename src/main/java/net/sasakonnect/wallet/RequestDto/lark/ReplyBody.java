package net.sasakonnect.wallet.RequestDto.lark;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReplyBody {
	 private String content;
	 private String msg_type;
	 private boolean reply_in_thread;
	 private String receive_id_type;
	 private String uuid;
}
