package antifraud.suspiciousIpAdresses;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class IpService {
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    @Autowired
    private IpRepository repository;

    public static void validate(String ip) {
        if (!PATTERN.matcher(ip).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public IpAddress banIp(String ip) {
        if (repository.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        IpAddress ipAddress = new IpAddress(ip);
        return repository.save(ipAddress);
    }

    @Transactional
    public void delete(String ip) {
        if (repository.existsByIp(ip)) {
            repository.deleteIpAddressByIp(ip);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<IpAddress> returnAllIpAddresses() {
        return repository.findByOrderById();
    }
}