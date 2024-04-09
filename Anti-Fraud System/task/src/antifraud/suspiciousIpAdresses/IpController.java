package antifraud.suspiciousIpAdresses;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class IpController {
    @Autowired
    private IpService service;

    @PostMapping
    public IpAddress suspiciousIp(@Valid @RequestBody IpRequest ipRequest) {
        String ip = ipRequest.ip;
        service.validate(ip);
        return service.banIp(ip);
    }

    @DeleteMapping("{ip}")
    @ResponseStatus(HttpStatus.OK)
    public Status deleteIpAddress(@NotEmpty @PathVariable String ip) {
        service.validate(ip);
        service.delete(ip);
        return new Status("IP " + ip + " successfully removed!");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IpAddress> getIpAddresses() {
        return service.returnAllIpAddresses();
    }

    public record IpRequest(@NotEmpty String ip){
    }

    public record Status(String status) {
    }
}
