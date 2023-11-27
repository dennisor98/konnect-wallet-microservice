package net.sasakonnect.wallet.domain;

import jakarta.persistence.*;
import net.sasakonnect.wallet.domain.BaseWalletDomain;
import net.sasakonnect.wallet.domain.User;

import java.io.Serializable;
import java.util.UUID;

@Entity
class CooporateDetails extends BaseWalletDomain implements Serializable {
    @Column(name = "user_id")
    private  Integer user_id;
    @Column(name = "corporate_email")
    private String  corporate_email;

    @Column(name = "phone")
    private  String phone;

    @Column(name = "isVerified")
    private Boolean isVerified;

    @Column(name = "isEmailVerified")
    private Boolean isEmailVerified;

    @Column(name = "isActive")
    private Boolean isActive;



}