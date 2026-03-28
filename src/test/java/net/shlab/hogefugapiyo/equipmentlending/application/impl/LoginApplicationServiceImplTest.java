package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindLoginUserQueryServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindLoginUserQueryService;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginApplicationServiceImplTest {

    @Mock
    private FindLoginUserQueryService findLoginUserQueryService;

    @InjectMocks
    private LoginApplicationServiceImpl loginApplicationService;

    @Test
    void findLoginUserDelegatesQueryServiceWithUserId() {
        var expected = Optional.of(new FindLoginUserQueryServiceImpl.Response("USER01", UserRole.USER));
        when(findLoginUserQueryService.execute(eq(new FindLoginUserQueryServiceImpl.Request("USER01")))).thenReturn(expected);

        var actual = loginApplicationService.findLoginUser("USER01");

        assertThat(actual).isEqualTo(expected);
        verify(findLoginUserQueryService).execute(new FindLoginUserQueryServiceImpl.Request("USER01"));
    }
}
