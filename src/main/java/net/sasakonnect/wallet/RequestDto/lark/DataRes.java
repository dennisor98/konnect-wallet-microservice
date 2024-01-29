package net.sasakonnect.wallet.RequestDto.lark;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataRes{
       public boolean hasMore;
       public List<Item> items;
}
