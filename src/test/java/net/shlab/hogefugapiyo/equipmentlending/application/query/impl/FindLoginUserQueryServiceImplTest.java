package net.shlab.hogefugapiyo.equipmentlending.application.query.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.repository.query.LoginUserQueryRepository;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindLoginUserQueryServiceImplTest {

    @Mock
    private LoginUserQueryRepository loginUserQueryRepository;

    @InjectMocks
    private FindLoginUserQueryServiceImpl service;

    @Test
    void executeReturnsRepositoryResultForUserId() {
        var expected = Optional.of(new FindLoginUserQueryServiceImpl.Response("USER01", UserRole.ADMIN));
        when(loginUserQueryRepository.findByUserId("USER01")).thenReturn(expected);

        var actual = service.execute(new FindLoginUserQueryServiceImpl.Request("USER01"));

        assertThat(actual).isEqualTo(expected);
        verify(loginUserQueryRepository).findByUserId("USER01");
    }
}
