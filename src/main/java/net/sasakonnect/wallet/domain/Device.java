package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Device extends BaseWalletDomain implements Serializable {

	@OneToMany(mappedBy = "device")
	private List<UserDevice> userDevices;

	@Column(nullable = true)
	private String deviceName;

	@Column(nullable = true)
	private String deviceModel;

	@Column(nullable = true)
	private String deviceOwnerId;

	@Column(nullable = true)
	private String deviceMacAddress;

	@Column(nullable = true)
	private String securityPatch;

	@Column(nullable = true)
	private String sdkInt;

	@Column(nullable = true)
	private String release_version;

	@Column(nullable = true)
	private String previewSdkInt;

	@Column(nullable = true)
	private String incremental;

	@Column(nullable = true)
	private String codename;

	@Column(nullable = true)
	private String baseOS;

	@Column(nullable = true)
	private String board;

	@Column(nullable = true)
	private String bootloader;

	@Column(nullable = true)
	private String brand;

	@Column(nullable = true)
	private String device;

	@Column(nullable = true)
	private String display;

	@Column(unique = true)
	private String fingerprint;

	@Column(nullable = true)
	private String hardware;

	@Column(nullable = true)
	private String host;

	@Column(nullable = true)
	private String manufacturer;

	@Column(nullable = true)
	private String model;

	@Column(nullable = true)
	private String product;

	@ElementCollection
	@Column(nullable = true)
	private List<String> supported32BitAbis;

	@ElementCollection
	@Column(nullable = true)
	private List<String> supported64BitAbis;

	@ElementCollection
	@Column(nullable = true)
	private List<String> supportedAbis;

	@Column(nullable = true)
	private String tags;

	@Column(nullable = true)
	private String type;

	@Column(nullable = true)
	private Boolean isPhysicalDevice;

	@Column(nullable = true)
	private String androidId;

	@ElementCollection
	@Column(nullable = true)
	private List<String> systemFeatures;
}