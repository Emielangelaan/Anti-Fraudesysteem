package antifraud.suspiciousIpAdresses;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class IpAddress {
    @Id
    @Column(name = "ipAddress_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotEmpty
    private String ip;

    public IpAddress(String ip) {
        this.ip = ip;
    }

    public IpAddress() {
    }

    public long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
