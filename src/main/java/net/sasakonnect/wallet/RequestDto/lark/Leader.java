package net.sasakonnect.wallet.RequestDto.lark;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public      class Leader {
    @JsonProperty("leaderID")
    String leaderId;
    @JsonProperty("leaderType")
    int leaderType;
}