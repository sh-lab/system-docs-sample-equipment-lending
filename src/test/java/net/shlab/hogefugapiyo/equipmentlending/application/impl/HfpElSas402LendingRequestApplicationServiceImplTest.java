package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterLendingRequestCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas402LendingRequestApplicationServiceImplTest {

    @Mock
    private RegisterLendingRequestCommandService registerLendingRequestCommandService;

    @InjectMocks
    private HfpElSas402LendingRequestApplicationServiceImpl applicationService;

    @Test
    void registerDelegatesToCommandService() {
        applicationService.register("USER01", List.of(1001L, 1004L), "会議で利用する。");

        verify(registerLendingRequestCommandService).execute(
                new RegisterLendingRequestCommandService.Request("USER01", List.of(1001L, 1004L), "会議で利用する。")
        );
    }
}
