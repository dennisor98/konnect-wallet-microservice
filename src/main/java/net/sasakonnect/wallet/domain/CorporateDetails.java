package net.sasakonnect.wallet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateDetails extends BaseWalletDomain implements Serializable {  
    @Column
    private String  corporateEmail;

    @Column(name = "phone")
    private  String phone;
 
    
    @Column(name="lark_open_id")
    private String larkOpenId;

    @Column(name = "isActive")
    private Boolean isActive;
    
    @OneToOne()
    @JoinColumn(name="user_pin")
    CorporatePin pin;
 
 
}