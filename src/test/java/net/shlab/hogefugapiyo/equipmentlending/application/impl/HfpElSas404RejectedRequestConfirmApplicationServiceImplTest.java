package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import net.shlab.hogefugapiyo.equipmentlending.application.command.ConfirmRejectedRequestCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas404RejectedRequestConfirmApplicationServiceImplTest {

    @Mock
    private ConfirmRejectedRequestCommandService confirmRejectedRequestCommandService;

    @InjectMocks
    private HfpElSas404RejectedRequestConfirmApplicationServiceImpl applicationService;

    @Test
    void confirmDelegatesToCommandService() {
        applicationService.confirm("USER01", 2004L, 1);

        verify(confirmRejectedRequestCommandService).execute(
                new ConfirmRejectedRequestCommandService.Request("USER01", 2004L, 1)
        );
    }
}
