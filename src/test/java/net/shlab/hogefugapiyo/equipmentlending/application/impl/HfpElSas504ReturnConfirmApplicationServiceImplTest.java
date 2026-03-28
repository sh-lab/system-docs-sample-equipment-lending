package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import net.shlab.hogefugapiyo.equipmentlending.application.command.ReturnConfirmCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas504ReturnConfirmApplicationServiceImplTest {

    @Mock
    private ReturnConfirmCommandService returnConfirmCommandService;

    @InjectMocks
    private HfpElSas504ReturnConfirmApplicationServiceImpl applicationService;

    @Test
    void confirmDelegatesToCommandService() {
        applicationService.confirm("ADMIN1", 2003L, "返却を確認した。", 2);

        verify(returnConfirmCommandService).execute(
                new ReturnConfirmCommandService.Request("ADMIN1", 2003L, "返却を確認した。", 2)
        );
    }
}
