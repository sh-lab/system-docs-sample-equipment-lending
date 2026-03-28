package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import net.shlab.hogefugapiyo.equipmentlending.application.command.ApproveLendingRequestCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas502ApproveLendingRequestApplicationServiceImplTest {

    @Mock
    private ApproveLendingRequestCommandService approveLendingRequestCommandService;

    @InjectMocks
    private HfpElSas502ApproveLendingRequestApplicationServiceImpl applicationService;

    @Test
    void approveDelegatesToCommandService() {
        applicationService.approve("ADMIN1", 2001L, "承認する。", 0);

        verify(approveLendingRequestCommandService).execute(
                new ApproveLendingRequestCommandService.Request("ADMIN1", 2001L, "承認する。", 0)
        );
    }
}
