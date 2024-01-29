package net.sasakonnect.wallet.RequestDto.lark.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.RequestDto.lark.DataRes;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
  public int  code;
  public String msg;
  public UserData data;
   
}
