package antifraud.suspiciousIpAdresses;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpRepository extends JpaRepository<IpAddress, Long> {
    IpAddress findByIp(String ipAddress);
    Boolean existsByIp(String ipAddress);
    void deleteIpAddressByIp(String ip);
    List<IpAddress> findByOrderById();
}
