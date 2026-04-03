package net.shlab.hogefugapiyo.equipmentlending.presentation.config;

import net.shlab.hogefugapiyo.equipmentlending.presentation.interceptor.OneTimeTokenValidationInterceptor;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
public class PresentationWebMvcConfiguration {

    @Bean
    OneTimeTokenValidationInterceptor oneTimeTokenValidationInterceptor() {
        return new OneTimeTokenValidationInterceptor();
    }

    @Bean
    MappedInterceptor oneTimeTokenMappedInterceptor(OneTimeTokenValidationInterceptor interceptor) {
        return new MappedInterceptor(
                new String[] {
                        RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING,
                        RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN,
                        RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_REJECTED_CONFIRM,
                        RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE,
                        RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_REJECT,
                        RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_RETURN_CONFIRM,
                        RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER,
                        RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE
                },
                interceptor
        );
    }
}
