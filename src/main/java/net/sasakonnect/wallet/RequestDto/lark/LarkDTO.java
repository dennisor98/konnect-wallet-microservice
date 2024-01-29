package net.sasakonnect.wallet.RequestDto.lark;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;


@Data
@NoArgsConstructor
@AllArgsConstructor
 public class LarkDTO {
    public  int code;
    public    DataRes data;
      String msg;
}