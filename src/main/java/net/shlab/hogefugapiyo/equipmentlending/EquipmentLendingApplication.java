package net.shlab.hogefugapiyo.equipmentlending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "net.shlab.hogefugapiyo",
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class EquipmentLendingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EquipmentLendingApplication.class, args);
    }
}
