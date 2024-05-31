package net.sasakonnect.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends BaseWalletDomain {

    
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    
    @Column(name = "role_id")
    private String roleId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;
    

    @Column(name = "is_deactivated", columnDefinition = "bool default false")
    private boolean isDeactivated;

    @ManyToOne
    @JoinColumn(name = "assigner_id", referencedColumnName = "id")
    @JsonIgnore
    private User assigner;

    // Constructors, getters, and setters
}
