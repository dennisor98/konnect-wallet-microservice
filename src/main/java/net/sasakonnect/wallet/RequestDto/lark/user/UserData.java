package net.sasakonnect.wallet.RequestDto.lark.user;

import java.util.List;

public class UserData {
	public boolean hasMore;
	public String page_token;
    public List<UserDTO> items;
}
