package net.sasakonnect.wallet.RequestDto.lark.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class Order {
    String departmentId;
    int departmentOrder;
    boolean isPrimaryDept;
    int userOrder;
 }