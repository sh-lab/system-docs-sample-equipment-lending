package net.shlab.hogefugapiyo.equipmentlending;

import jakarta.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import net.shlab.hogefugapiyo.framework.core.time.CurrentTimeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static net.shlab.hogefugapiyo.framework.security.SecurityMockMvcTestSupport.userPrincipal;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EquipmentLendingApplicationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Clock clock;

    @Autowired
    private CurrentTimeProvider currentTimeProvider;

    @Test
    void contextLoads() {
    }

    @Test
    void currentTimeProviderUsesConfiguredClock() {
        Instant before = Instant.now(clock);
        Instant actual = currentTimeProvider.currentInstant();
        Instant after = Instant.now(clock);

        assertThat(actual).isBetween(before, after);
    }

    @Test
    void seedDataIsLoadedOnStartup() {
        assertThat(countRows("M_USER")).isEqualTo(6);
        assertThat(countRows("M_EQUIPMENT_TYPE")).isEqualTo(3);
        assertThat(countRows("M_EQUIPMENT")).isEqualTo(22);
        assertThat(countRows("T_LENDING_REQUEST")).isEqualTo(15);
        assertThat(countRows("T_LENDING_REQUEST_DETAIL")).isEqualTo(24);
        assertThat(countRowsByApplicant("USER01")).isZero();
        assertThat(countRowsByApplicant("USER02")).isEqualTo(5);
        assertThat(countRowsByApplicant("USER03")).isEqualTo(5);
        assertThat(countRowsByApplicant("USER04")).isEqualTo(5);
        assertThat(countRowsByStatus("PENDING_APPROVAL")).isEqualTo(3);
        assertThat(countRowsByStatus("LENT")).isEqualTo(3);
        assertThat(countRowsByStatus("PENDING_RETURN_CONFIRMATION")).isEqualTo(3);
        assertThat(countRowsByStatus("REJECTED")).isEqualTo(3);
        assertThat(countRowsByStatus("COMPLETED")).isEqualTo(3);
        assertThat(countDetailsByRequest(2001L)).isEqualTo(2);
        assertThat(countDetailsByRequest(2004L)).isEqualTo(2);
        assertThat(countDetailsByRequest(2005L)).isEqualTo(2);
        assertThat(countDetailsByRequest(2011L)).isEqualTo(2);
        assertThat(countDetailsByRequest(2014L)).isEqualTo(2);
        assertThat(countDetailsByRequest(2015L)).isEqualTo(2);
        assertThat(countRows("H_LENDING_REQUEST_HISTORY")).isZero();
        assertThat(countRows("H_LENDING_REQUEST_DETAIL_HISTORY")).isZero();
        assertThat(countRows("H_EQUIPMENT_HISTORY")).isZero();
    }

    @Test
    void userLoginRedirectsToUserMypage() throws Exception {
        mockMvc.perform(post(RoutePaths.LOGIN)
                        .with(csrf())
                        .param("userId", "USER01")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE));
    }

    @Test
    void loginWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post(RoutePaths.LOGIN)
                        .param("userId", "USER01")
                        .param("password", "pass"))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidOneTimeTokenOnV400RedirectsToDuplicateSubmitErrorScreen() throws Exception {
        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)
                        .with(csrf())
                        .param("equipmentIds", "1009", "1010")
                        .param("oneTimeToken", "invalid-token")
                        .param("requestComment", "社内会議で利用する。")
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.DUPLICATE_SUBMIT_ERROR));
    }

    @Test
    void invalidOneTimeTokenOnV500RedirectsToDuplicateSubmitErrorScreen() throws Exception {
        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE)
                        .with(csrf())
                        .param("requestId", "2001")
                        .param("version", "0")
                        .param("oneTimeToken", "invalid-token")
                        .param("reviewComment", "承認する。")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.DUPLICATE_SUBMIT_ERROR));
    }

    @Test
    void invalidOneTimeTokenOnV700RedirectsToDuplicateSubmitErrorScreen() throws Exception {
        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .with(csrf())
                        .param("equipmentId", "1009")
                        .param("equipmentName", "更新後のプロジェクター")
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "棚卸し対象")
                        .param("oneTimeToken", "invalid-token")
                        .param("version", "0")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.DUPLICATE_SUBMIT_ERROR));
    }


    @Test
    @Transactional
    void userCanRegisterLendingRequestFromV400() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST,
                "USER01",
                UserRole.USER,
                builder -> builder.param("from", "V300").param("equipmentIds", "1009", "1010")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_LENDING)
                        .session(preparation.session())
                        .with(csrf())
                        .param("equipmentIds", "1009", "1010")
                        .param("oneTimeToken", preparation.token())
                        .param("requestComment", "社内会議で利用する。")
                        .with(userPrincipal("USER01", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE + "?messageId=MSG_I_002"));

        flushPersistenceContext();
        long latestRequestId = latestRequestId();
        assertThat(countRowsByApplicant("USER01")).isEqualTo(1);
        assertThat(requestStatus(latestRequestId)).isEqualTo("PENDING_APPROVAL");
        assertThat(requestComment(latestRequestId)).isEqualTo("社内会議で利用する。");
        assertThat(countDetailsByRequest(latestRequestId)).isEqualTo(2);
        assertThat(equipmentStatus(1009L)).isEqualTo("PENDING_LENDING");
        assertThat(equipmentStatus(1010L)).isEqualTo("PENDING_LENDING");
        assertHistoryRecorded(
                latestRequestId,
                "HFP-EL-SCS001_register-lending-request_service",
                2,
                2,
                java.util.List.of(1009L, 1010L)
        );
    }

    @Test
    @Transactional
    void userCanRegisterReturnRequestFromV400() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST,
                "USER02",
                UserRole.USER,
                builder -> builder.param("from", "V100").param("requestId", "2002")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_RETURN)
                        .session(preparation.session())
                        .with(csrf())
                        .param("requestId", "2002")
                        .param("version", "1")
                        .param("oneTimeToken", preparation.token())
                        .param("returnRequestComment", "返却しました。")
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE + "?messageId=MSG_I_003"));

        flushPersistenceContext();
        assertThat(requestStatus(2002L)).isEqualTo("PENDING_RETURN_CONFIRMATION");
        assertThat(returnRequestComment(2002L)).isEqualTo("返却しました。");
        assertThat(requestVersion(2002L)).isEqualTo(2);
        assertHistoryRecorded(
                2002L,
                "HFP-EL-SCS002_register-return-request_service",
                0,
                0,
                java.util.List.of()
        );
    }

    @Test
    @Transactional
    void userCanConfirmRejectedRequestFromV400() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV400_USER_LENDING_REQUEST,
                "USER02",
                UserRole.USER,
                builder -> builder.param("from", "V100").param("requestId", "2004")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV400_USER_LENDING_REQUEST_REJECTED_CONFIRM)
                        .session(preparation.session())
                        .with(csrf())
                        .param("requestId", "2004")
                        .param("version", "1")
                        .param("oneTimeToken", preparation.token())
                        .with(userPrincipal("USER02", UserRole.USER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV100_USER_MYPAGE + "?messageId=MSG_I_004"));

        flushPersistenceContext();
        assertThat(requestStatus(2004L)).isEqualTo("COMPLETED");
        assertThat(requestVersion(2004L)).isEqualTo(2);
        assertThat(completedAt(2004L)).isNotNull();
        assertHistoryRecorded(
                2004L,
                "HFP-EL-SCS003_confirm-rejected-request_service",
                0,
                0,
                java.util.List.of()
        );
    }

    @Test
    void adminLoginRedirectsToAdminMypage() throws Exception {
        mockMvc.perform(post(RoutePaths.LOGIN)
                        .with(csrf())
                        .param("userId", "ADMIN1")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE));
    }

    @Test
    @Transactional
    void adminCanApprovePendingApprovalRequestFromV500() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("requestId", "2001")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_APPROVE)
                        .session(preparation.session())
                        .with(csrf())
                        .param("requestId", "2001")
                        .param("version", "0")
                        .param("oneTimeToken", preparation.token())
                        .param("reviewComment", "承認する。")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE + "?messageId=MSG_I_005"));

        flushPersistenceContext();
        assertThat(requestStatus(2001L)).isEqualTo("LENT");
        assertThat(requestVersion(2001L)).isEqualTo(1);
        assertThat(equipmentStatus(1001L)).isEqualTo("LENT");
        assertThat(equipmentStatus(1004L)).isEqualTo("LENT");
        assertHistoryRecorded(
                2001L,
                "HFP-EL-SCS010_approve-lending-request_service",
                0,
                2,
                java.util.List.of(1001L, 1004L)
        );
    }

    @Test
    @Transactional
    void adminCanRejectPendingApprovalRequestFromV500() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("requestId", "2001")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_REJECT)
                        .session(preparation.session())
                        .with(csrf())
                        .param("requestId", "2001")
                        .param("version", "0")
                        .param("oneTimeToken", preparation.token())
                        .param("reviewComment", "今回は却下する。")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE + "?messageId=MSG_I_006"));

        flushPersistenceContext();
        assertThat(requestStatus(2001L)).isEqualTo("REJECTED");
        assertThat(requestVersion(2001L)).isEqualTo(1);
        assertThat(equipmentStatus(1001L)).isEqualTo("AVAILABLE");
        assertThat(equipmentStatus(1004L)).isEqualTo("AVAILABLE");
        assertHistoryRecorded(
                2001L,
                "HFP-EL-SCS011_reject-lending-request_service",
                0,
                2,
                java.util.List.of(1001L, 1004L)
        );
    }

    @Test
    @Transactional
    void adminCanConfirmReturnFromV500() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("requestId", "2003")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV500_ADMIN_LENDING_REVIEW_RETURN_CONFIRM)
                        .session(preparation.session())
                        .with(csrf())
                        .param("requestId", "2003")
                        .param("version", "2")
                        .param("oneTimeToken", preparation.token())
                        .param("returnConfirmComment", "返却を確認した。")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV200_ADMIN_MYPAGE + "?messageId=MSG_I_007"));

        flushPersistenceContext();
        assertThat(requestStatus(2003L)).isEqualTo("COMPLETED");
        assertThat(requestVersion(2003L)).isEqualTo(3);
        assertThat(equipmentStatus(1003L)).isEqualTo("AVAILABLE");
        assertThat(completedAt(2003L)).isNotNull();
        assertHistoryRecorded(
                2003L,
                "HFP-EL-SCS012_return-confirm_service",
                0,
                1,
                java.util.List.of(1003L)
        );
    }

    @Test
    @Transactional
    void adminCanRegisterEquipmentFromV700() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("mode", "create")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_REGISTER)
                        .session(preparation.session())
                        .with(csrf())
                        .param("equipmentName", "追加ライト")
                        .param("equipmentType", "PROJECTOR")
                        .param("storageLocation", "第3倉庫")
                        .param("statusCode", "UNAVAILABLE")
                        .param("oneTimeToken", preparation.token())
                        .param("remarks", "予備機")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH + "?messageId=MSG_I_008"));

        flushPersistenceContext();
        long equipmentId = latestEquipmentId();
        assertThat(equipmentStatus(equipmentId)).isEqualTo("UNAVAILABLE");
        assertThat(systemRegisteredDate(equipmentId)).isEqualTo(currentTimeProvider.currentDateTime().toLocalDate().toString());
        assertThat(equipmentType(equipmentId)).isEqualTo("PROJECTOR");
        assertThat(countEquipmentHistoryByEquipmentId(equipmentId)).isEqualTo(1);
        assertThat(singleDistinctValue("H_EQUIPMENT_HISTORY", "COMMAND_SERVICE_ID")).contains("register-equipment_service");
    }

    @Test
    @Transactional
    void adminCanUpdateEquipmentInfoFromV700() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("mode", "edit").param("equipmentId", "1009")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .session(preparation.session())
                        .with(csrf())
                        .param("equipmentId", "1009")
                        .param("equipmentName", "更新後のプロジェクター")
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "棚卸し対象")
                        .param("oneTimeToken", preparation.token())
                        .param("version", "0")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RoutePaths.HFP_ELV600_ADMIN_EQUIPMENT_SEARCH + "?messageId=MSG_I_009"));

        flushPersistenceContext();
        assertThat(equipmentName(1009L)).isEqualTo("更新後のプロジェクター");
        assertThat(equipmentStatus(1009L)).isEqualTo("UNAVAILABLE");
        assertThat(equipmentRemarks(1009L)).isEqualTo("棚卸し対象");
        assertThat(equipmentVersion(1009L)).isEqualTo(1);
        assertThat(countEquipmentHistoryByEquipmentId(1009L)).isEqualTo(1);
        assertThat(singleDistinctValue("H_EQUIPMENT_HISTORY", "COMMAND_SERVICE_ID")).contains("update-equipment-info_service");
    }

    @Test
    @Transactional
    void adminCannotUpdatePendingEquipmentFromV700() throws Exception {
        SubmissionPreparation preparation = prepareSubmission(
                RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT,
                "ADMIN1",
                UserRole.ADMIN,
                builder -> builder.param("mode", "edit").param("equipmentId", "1001")
        );

        mockMvc.perform(post(RoutePaths.HFP_ELV700_ADMIN_EQUIPMENT_EDIT_UPDATE)
                        .session(preparation.session())
                        .with(csrf())
                        .param("equipmentId", "1001")
                        .param("equipmentName", "更新不可の長机")
                        .param("statusCode", "UNAVAILABLE")
                        .param("remarks", "更新不可")
                        .param("oneTimeToken", preparation.token())
                        .param("version", "0")
                        .with(userPrincipal("ADMIN1", UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("貸出申請中と貸出中の備品状態は更新できません")));
    }


    private long latestEquipmentId() {
        Long equipmentId = jdbcTemplate.queryForObject("SELECT MAX(EQUIPMENT_ID) FROM M_EQUIPMENT", Long.class);
        return equipmentId == null ? 0L : equipmentId;
    }

    private String systemRegisteredDate(long equipmentId) {
        return jdbcTemplate.queryForObject(
                "SELECT CAST(SYSTEM_REGISTERED_DATE AS VARCHAR) FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                String.class,
                equipmentId
        );
    }

    private String equipmentType(long equipmentId) {
        return jdbcTemplate.queryForObject(
                "SELECT EQUIPMENT_TYPE FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                String.class,
                equipmentId
        );
    }

    private String equipmentName(long equipmentId) {
        return jdbcTemplate.queryForObject(
                "SELECT EQUIPMENT_NAME FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                String.class,
                equipmentId
        );
    }

    private String equipmentRemarks(long equipmentId) {
        return jdbcTemplate.queryForObject(
                "SELECT REMARKS FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                String.class,
                equipmentId
        );
    }

    private int equipmentVersion(long equipmentId) {
        Integer version = jdbcTemplate.queryForObject(
                "SELECT VERSION FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                Integer.class,
                equipmentId
        );
        return version == null ? -1 : version;
    }

    private long latestRequestId() {
        Long requestId = jdbcTemplate.queryForObject(
                "SELECT MAX(LENDING_REQUEST_ID) FROM T_LENDING_REQUEST",
                Long.class
        );
        return requestId == null ? 0L : requestId;
    }

    private String requestStatus(long lendingRequestId) {
        return jdbcTemplate.queryForObject(
                "SELECT STATUS_CODE FROM T_LENDING_REQUEST WHERE LENDING_REQUEST_ID = ?",
                String.class,
                lendingRequestId
        );
    }

    private String requestComment(long lendingRequestId) {
        return jdbcTemplate.queryForObject(
                "SELECT REQUEST_COMMENT FROM T_LENDING_REQUEST WHERE LENDING_REQUEST_ID = ?",
                String.class,
                lendingRequestId
        );
    }

    private String returnRequestComment(long lendingRequestId) {
        return jdbcTemplate.queryForObject(
                "SELECT RETURN_REQUEST_COMMENT FROM T_LENDING_REQUEST WHERE LENDING_REQUEST_ID = ?",
                String.class,
                lendingRequestId
        );
    }

    private int requestVersion(long lendingRequestId) {
        Integer version = jdbcTemplate.queryForObject(
                "SELECT VERSION FROM T_LENDING_REQUEST WHERE LENDING_REQUEST_ID = ?",
                Integer.class,
                lendingRequestId
        );
        return version == null ? -1 : version;
    }

    private String equipmentStatus(long equipmentId) {
        return jdbcTemplate.queryForObject(
                "SELECT STATUS_CODE FROM M_EQUIPMENT WHERE EQUIPMENT_ID = ?",
                String.class,
                equipmentId
        );
    }

    private SubmissionPreparation prepareSubmission(
            String path,
            String userId,
            UserRole userRole,
            Consumer<MockHttpServletRequestBuilder> customizer
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(path).with(userPrincipal(userId, userRole));
        customizer.accept(builder);
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        String token = extractOneTimeToken(result.getResponse().getContentAsString());
        return new SubmissionPreparation(session, token);
    }

    private String extractOneTimeToken(String html) {
        Matcher matcher = Pattern.compile("<input name=\"oneTimeToken\" value=\"([^\"]+)\" type=\"hidden\">").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private record SubmissionPreparation(MockHttpSession session, String token) {
    }

    private java.sql.Timestamp completedAt(long lendingRequestId) {
        return jdbcTemplate.queryForObject(
                "SELECT COMPLETED_AT FROM T_LENDING_REQUEST WHERE LENDING_REQUEST_ID = ?",
                java.sql.Timestamp.class,
                lendingRequestId
        );
    }

    private int countRows(String tableName) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        return count == null ? 0 : count;
    }

    private int countRowsByApplicant(String applicantUserId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM T_LENDING_REQUEST WHERE APPLICANT_USER_ID = ?",
                Integer.class,
                applicantUserId
        );
        return count == null ? 0 : count;
    }

    private int countRowsByStatus(String statusCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM T_LENDING_REQUEST WHERE STATUS_CODE = ?",
                Integer.class,
                statusCode
        );
        return count == null ? 0 : count;
    }

    private int countDetailsByRequest(long lendingRequestId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM T_LENDING_REQUEST_DETAIL WHERE LENDING_REQUEST_ID = ?",
                Integer.class,
                lendingRequestId
        );
        return count == null ? 0 : count;
    }

    private void assertHistoryRecorded(
            long lendingRequestId,
            String commandServiceId,
            int expectedDetailHistoryCount,
            int expectedEquipmentHistoryCount,
            java.util.List<Long> equipmentIds
    ) {
        assertThat(countRequestHistoryByRequestId(lendingRequestId)).isEqualTo(1);
        assertThat(countDetailHistoryByRequestId(lendingRequestId)).isEqualTo(expectedDetailHistoryCount);
        assertThat(countRows("H_LENDING_REQUEST_HISTORY")).isEqualTo(1);
        assertThat(countRows("H_LENDING_REQUEST_DETAIL_HISTORY")).isEqualTo(expectedDetailHistoryCount);
        assertThat(countRows("H_EQUIPMENT_HISTORY")).isEqualTo(expectedEquipmentHistoryCount);
        assertThat(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "COMMAND_SERVICE_ID")).isEqualTo(commandServiceId);
        assertThat(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATION_ID")).isNotBlank();
        assertThat(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATED_AT")).isNotBlank();

        if (expectedDetailHistoryCount > 0) {
            assertThat(singleDistinctValue("H_LENDING_REQUEST_DETAIL_HISTORY", "COMMAND_SERVICE_ID")).isEqualTo(commandServiceId);
            assertThat(singleDistinctValue("H_LENDING_REQUEST_DETAIL_HISTORY", "OPERATION_ID"))
                    .isEqualTo(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATION_ID"));
            assertThat(singleDistinctValue("H_LENDING_REQUEST_DETAIL_HISTORY", "OPERATED_AT"))
                    .isEqualTo(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATED_AT"));
        }

        if (expectedEquipmentHistoryCount > 0) {
            assertThat(singleDistinctValue("H_EQUIPMENT_HISTORY", "COMMAND_SERVICE_ID")).isEqualTo(commandServiceId);
            assertThat(singleDistinctValue("H_EQUIPMENT_HISTORY", "OPERATION_ID"))
                    .isEqualTo(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATION_ID"));
            assertThat(singleDistinctValue("H_EQUIPMENT_HISTORY", "OPERATED_AT"))
                    .isEqualTo(singleDistinctValue("H_LENDING_REQUEST_HISTORY", "OPERATED_AT"));
        }

        for (Long equipmentId : equipmentIds) {
            assertThat(countEquipmentHistoryByEquipmentId(equipmentId)).isEqualTo(1);
        }
    }

    private int countRequestHistoryByRequestId(long lendingRequestId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM H_LENDING_REQUEST_HISTORY WHERE LENDING_REQUEST_ID = ?",
                Integer.class,
                lendingRequestId
        );
        return count == null ? 0 : count;
    }

    private int countDetailHistoryByRequestId(long lendingRequestId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM H_LENDING_REQUEST_DETAIL_HISTORY WHERE LENDING_REQUEST_ID = ?",
                Integer.class,
                lendingRequestId
        );
        return count == null ? 0 : count;
    }

    private int countEquipmentHistoryByEquipmentId(long equipmentId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM H_EQUIPMENT_HISTORY WHERE EQUIPMENT_ID = ?",
                Integer.class,
                equipmentId
        );
        return count == null ? 0 : count;
    }

    private String singleDistinctValue(String tableName, String columnName) {
        java.util.List<String> values = jdbcTemplate.queryForList(
                "SELECT DISTINCT " + columnName + " FROM " + tableName,
                String.class
        );
        assertThat(values).hasSize(1);
        return values.getFirst();
    }

    private void flushPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
