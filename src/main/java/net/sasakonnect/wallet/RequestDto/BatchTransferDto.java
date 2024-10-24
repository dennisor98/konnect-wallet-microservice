package net.sasakonnect.wallet.RequestDto;

import java.util.List;

import lombok.Data;

@Data
public class BatchTransferDto {
  List<BeneficiaryDataDto> beneficiaryData;
}
