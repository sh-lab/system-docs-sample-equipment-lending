package net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindLoginUserQueryServiceImpl;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(LoginUserQueryRepositoryImpl.class)
class LoginUserQueryRepositoryDataJpaTest {

    @Autowired
    private LoginUserQueryRepositoryImpl loginUserQueryRepository;

    @Test
    void findByUserIdReturnsMatchedUser() {
        FindLoginUserQueryServiceImpl.Response result = loginUserQueryRepository.findByUserId("USER01").orElseThrow();

        assertThat(result.userId()).isEqualTo("USER01");
        assertThat(result.roleCode()).isEqualTo(UserRole.USER);
    }

    @Test
    void findByUserIdReturnsEmptyWhenUnknown() {
        assertThat(loginUserQueryRepository.findByUserId("USER99")).isEmpty();
    }
}
