package net.sasakonnect.wallet.RequestDto.lark;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public  class Item {
    String chatId;
    String open_department_id;
    List<Integer> groupChatEmployeeTypes;
    I18nName i18nName;
    String leader_user_id;
   List<Leader> leaders;
   int member_count;
   String name;
   String order;
   String parent_department_id;
   int primary_member_count;
   Status status;
}