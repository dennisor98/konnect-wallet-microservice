package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sasakonnect.wallet.serde.CustomDateSerializer;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceJob extends BaseWalletDomain implements Serializable {

	private static final long serialVersionUID = 3353930865046176169L;

	@Column()
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	@JsonSerialize(using = CustomDateSerializer.class)
	Date startDate;
	
	@Column()
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	@JsonSerialize(using = CustomDateSerializer.class)
	Date endDate;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_user_id", nullable = true)
	private User jobOwner;
	
	@Column(nullable = true, columnDefinition = "TEXT")
	private String description;
	
	@Column(nullable=true)
	String downloadLink;

}

