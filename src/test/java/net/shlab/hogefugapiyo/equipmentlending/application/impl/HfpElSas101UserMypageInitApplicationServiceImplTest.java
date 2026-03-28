package net.shlab.hogefugapiyo.equipmentlending.application.impl;

import net.shlab.hogefugapiyo.equipmentlending.application.query.impl.FindUserMypageQueryServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.application.query.FindUserMypageQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HfpElSas101UserMypageInitApplicationServiceImplTest {

    @Mock
    private FindUserMypageQueryService findUserMypageQueryService;

    @InjectMocks
    private HfpElSas101UserMypageInitApplicationServiceImpl applicationService;

    @Test
    void initializeDelegatesQueryServiceWithUserId() {
        var expected = new FindUserMypageQueryServiceImpl.Response(
                List.of(new FindUserMypageQueryServiceImpl.RequestItem(101L, "申請1", null, "貸出中")),
                List.of(),
                false
        );
        when(findUserMypageQueryService.execute(eq(new FindUserMypageQueryServiceImpl.Request("USER02")))).thenReturn(expected);

        var actual = applicationService.initialize("USER02");

        assertThat(actual).isEqualTo(expected);
        verify(findUserMypageQueryService).execute(new FindUserMypageQueryServiceImpl.Request("USER02"));
    }
}
