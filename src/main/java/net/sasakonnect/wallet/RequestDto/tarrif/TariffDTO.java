package net.sasakonnect.wallet.RequestDto.tarrif;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.constant.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffDTO {
    @NotBlank
    @Schema(description = "Tier")
    private String tier;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Channel charge")
    private double channelCharge;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "CB charge")
    private double cbCharge;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Total cost excluding to partner")
    private double totalCostExclToPartner;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Total partner profit")
    private double totalPartnerProfit;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Total margin taxable")
    private double totalMarginTaxable;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Excise duty tax")
    private double exciseDutyTax;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Total cost to user including")
    private double totalCostToUserIncl;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Saved value")
    private double savedValue;
    
    @NotNull
    @Schema(description = "Channel type")
    private ChannelType channelType;

   
    @Schema(description = "Opponent account")
    private List<String> opponentAccount;

    @NotBlank
    @Schema(description = "Tier label")
    private String tierLabel;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Minimum")
    private int min;

    @DecimalMin(value = "0", inclusive = true)

    @Schema(description = "Maximum")
    private int max;

    // Constructors, getters, setters, etc.
}
