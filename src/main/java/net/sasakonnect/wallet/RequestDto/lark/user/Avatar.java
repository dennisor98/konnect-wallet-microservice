package net.sasakonnect.wallet.RequestDto.lark.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class Avatar {
     String avatar_240;
     String avatar_640;
     String avatar_72;
     String avatar_origin;
}
