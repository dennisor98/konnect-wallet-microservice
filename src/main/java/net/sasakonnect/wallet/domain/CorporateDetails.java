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
    private String  corporate_email;

    @Column(name = "phone")
    private  String phone;

    @Column(name = "isVerified")
    private Boolean isVerified;
    
    @Column(name="password",nullable=true)
    private String password;

    @Column(name = "isEmailVerified")
    private Boolean isEmailVerified;

    @Column(name = "isActive")
    private Boolean isActive;
 
   

}