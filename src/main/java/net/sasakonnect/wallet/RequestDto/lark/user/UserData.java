package net.sasakonnect.wallet.RequestDto.lark.user;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
	public boolean hasMore;
	public String page_token;
    public List<UserDTO> items;
}
