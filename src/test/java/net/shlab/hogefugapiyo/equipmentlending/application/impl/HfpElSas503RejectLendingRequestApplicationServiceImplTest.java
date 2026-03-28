package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import static org.mockito.Mockito.verify;

import net.shlab.hogefugapiyo.equipmentlending.application.command.RejectLendingRequestCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas503RejectLendingRequestApplicationServiceImplTest {

    @Mock
    private RejectLendingRequestCommandService rejectLendingRequestCommandService;

    @InjectMocks
    private HfpElSas503RejectLendingRequestApplicationServiceImpl applicationService;

    @Test
    void rejectDelegatesToCommandService() {
        applicationService.reject("ADMIN1", 2001L, "今回は却下する。", 0);

        verify(rejectLendingRequestCommandService).execute(
                new RejectLendingRequestCommandService.Request("ADMIN1", 2001L, "今回は却下する。", 0)
        );
    }
}
