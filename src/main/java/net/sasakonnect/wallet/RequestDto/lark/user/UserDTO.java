package net.sasakonnect.wallet.RequestDto.lark.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.RequestDto.lark.user.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
     Avatar avatar;
     String city;
     String country;
     List<String> department_ids;
     String description;
     String email;
     String employee_no;
     int employee_type;
     String en_ame;
     String enterprise_email;
     int gender;
     boolean is_tenant_manager;
     String job_title;
     long join_time;
     String leader_user_id;
     String mobile;
     boolean mobile_visible;
     String name;
     String open_id;
     List<Order> orders;
     Status status;
     String union_id;
     String user_id;
     String work_station;
     
    
}