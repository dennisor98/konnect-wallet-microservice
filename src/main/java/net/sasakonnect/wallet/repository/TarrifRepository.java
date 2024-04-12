package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.invoice.Tariff;

public interface TarrifRepository extends JpaRepository<Tariff, String> {
    Optional<Tariff> findByMinLessThanEqualAndMaxGreaterThanEqualAndChannelType(double min, double max, ChannelType channelType);
    Optional<Tariff> findByMinLessThanEqualAndMaxGreaterThanEqualAndChannelTypeAndOpponentAccountContaining(double min, double max, ChannelType channelType, String element);
   
    @Query("SELECT t FROM Tariff t WHERE t.max = :max AND t.min = :min AND t.channelType = :channelType  ORDER BY t.id ASC LIMIT 1")
    Optional<Tariff> findMaxAndMin(@Param("channelType") ChannelType channelType, @Param("min") int min, @Param("max") int max);
    
    List<Tariff> findByChannelType(ChannelType channelType);

}
