package net.sasakonnect.wallet.domain;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseWalletDomain {

	@Id
	@UuidGenerator(style = UuidGenerator.Style.RANDOM)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected String id;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	@JsonSerialize(using = InstantSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "created_at")
	protected Date createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	@JsonSerialize(using = InstantSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deleted_at")
	protected Date deletedAt;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	@JsonSerialize(using = InstantSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	@UpdateTimestamp
	protected Date updatedAt;

}