package net.sasakonnect.wallet.RequestDto.lark;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public   class I18nName {
    @JsonProperty("en_us")
    String enUs;
    @JsonProperty("ja_jp")
    String jaJp;
    @JsonProperty("zh_cn")
    String zhCn;
}