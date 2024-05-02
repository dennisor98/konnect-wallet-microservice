package net.sasakonnect.wallet.tools;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ResponsePagerClass<T> {
	 private final Page<T> page;

	    public Map<String, Object> getPagingInfo() {
	        Map<String, Object> data = new HashMap<>();
	        data.put("totalRows", Double.valueOf(page.getTotalElements()));
	        data.put("pageSize", page.getSize());
	        data.put("currentPage", page.getNumber());
	        data.put("nextPage", page.hasNext() ? page.nextPageable().getPageNumber() : null);
	        data.put("hasNextPage", page.hasNext());
	        data.put("hasPreviousPage", page.hasPrevious());
	        return data;
	    }
}
