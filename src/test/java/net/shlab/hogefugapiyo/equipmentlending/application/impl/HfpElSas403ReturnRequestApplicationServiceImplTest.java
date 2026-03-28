package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import net.shlab.hogefugapiyo.equipmentlending.application.command.RegisterReturnRequestCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas403ReturnRequestApplicationServiceImplTest {

    @Mock
    private RegisterReturnRequestCommandService registerReturnRequestCommandService;

    @InjectMocks
    private HfpElSas403ReturnRequestApplicationServiceImpl applicationService;

    @Test
    void registerDelegatesToCommandService() {
        applicationService.register("USER01", 2002L, "返却しました。", 1);

        verify(registerReturnRequestCommandService).execute(
                new RegisterReturnRequestCommandService.Request("USER01", 2002L, "返却しました。", 1)
        );
    }
}
