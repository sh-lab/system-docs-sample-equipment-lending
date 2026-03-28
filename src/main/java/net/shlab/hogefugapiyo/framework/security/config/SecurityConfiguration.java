package net.shlab.hogefugapiyo.framework.security.config;

import jakarta.servlet.http.HttpServletResponse;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.framework.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.framework.security.UserPrincipal;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final PathPatternRequestMatcher H2_CONSOLE_PATH = PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**");

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            SecurityContextRepository securityContextRepository
    ) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(RoutePaths.ROOT, RoutePaths.LOGIN).permitAll()
                        .requestMatchers(H2_CONSOLE_PATH).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(H2_CONSOLE_PATH))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .securityContext(securityContext -> securityContext.securityContextRepository(securityContextRepository))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .anonymous(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(RoutePaths.LOGIN);
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler(SecurityRouteResolver securityRouteResolver) {
        return (request, response, accessDeniedException) -> {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
                response.sendRedirect(request.getContextPath() + securityRouteResolver.resolveHomePath(userPrincipal));
                return;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        };
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityRouteResolver securityRouteResolver() {
        return new SecurityRouteResolver();
    }
}
