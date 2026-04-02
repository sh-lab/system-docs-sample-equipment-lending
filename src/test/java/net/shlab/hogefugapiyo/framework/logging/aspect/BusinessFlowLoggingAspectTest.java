package net.shlab.hogefugapiyo.framework.logging.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import net.shlab.hogefugapiyo.framework.operation.OperationContextAspect;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;
import net.shlab.hogefugapiyo.framework.core.service.CommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class BusinessFlowLoggingAspectTest {

    private Logger businessLogger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        businessLogger = (Logger) LoggerFactory.getLogger(BusinessFlowLoggingAspect.BUSINESS_INFO_LOGGER_NAME);
        listAppender = new ListAppender<>();
        listAppender.start();
        businessLogger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        businessLogger.detachAppender(listAppender);
        listAppender.stop();
        OperationContextHolder.clear();
        MDC.remove(BusinessFlowLoggingAspect.OPERATION_ID_MDC_KEY);
    }

    @Test
    void applicationServiceLogsStartAndSuccessfulEnd() {
        TestApplicationService proxy = createProxy(new TestApplicationServiceImpl());

        String actual = proxy.handle("sample");

        assertThat(actual).isEqualTo("handled:sample");
        assertThat(messages()).containsExactly(
                "event=application-service-start service=TestApplicationServiceImpl method=handle",
                "event=application-service-end service=TestApplicationServiceImpl method=handle outcome=success"
        );
        assertThat(operationIds()).hasSize(2);
        assertThat(operationIds().get(0)).isNotBlank();
        assertThat(operationIds().get(0)).isEqualTo(operationIds().get(1));
        assertThat(OperationContextHolder.get()).isNull();
    }

    @Test
    void applicationServiceLogsFailureEndWhenExceptionOccurs() {
        TestFailingApplicationService proxy = createProxy(new TestFailingApplicationServiceImpl());

        assertThatThrownBy(proxy::handle).isInstanceOf(IllegalStateException.class);
        assertThat(messages()).containsExactly(
                "event=application-service-start service=TestFailingApplicationServiceImpl method=handle",
                "event=application-service-end service=TestFailingApplicationServiceImpl method=handle outcome=failure"
        );
        assertThat(operationIds()).hasSize(2);
        assertThat(operationIds().get(0)).isNotBlank();
        assertThat(operationIds().get(0)).isEqualTo(operationIds().get(1));
        assertThat(OperationContextHolder.get()).isNull();
    }

    @Test
    void commandServiceLogsExecuteEvent() {
        TestCommandService proxy = createProxy(new TestCommandServiceImpl());

        proxy.execute("sample");

        assertThat(proxy.lastProcessed()).isEqualTo("SAMPLE");
        assertThat(messages()).containsExactly(
                "event=command-service-execute service=TestCommandServiceImpl method=execute"
        );
        assertThat(listAppender.list.get(0).getMDCPropertyMap()).doesNotContainKey(BusinessFlowLoggingAspect.OPERATION_ID_MDC_KEY);
    }

    @Test
    void commandServiceInsideApplicationServiceUsesSameOperationId() {
        TestCommandService commandProxy = createProxy(new TestCommandServiceImpl());
        TestCommandInvokingApplicationService applicationProxy =
                createProxy(new TestCommandInvokingApplicationServiceImpl(commandProxy));

        applicationProxy.handle("sample");

        assertThat(commandProxy.lastProcessed()).isEqualTo("SAMPLE");
        assertThat(messages()).containsExactly(
                "event=application-service-start service=TestCommandInvokingApplicationServiceImpl method=handle",
                "event=command-service-execute service=TestCommandServiceImpl method=execute",
                "event=application-service-end service=TestCommandInvokingApplicationServiceImpl method=handle outcome=success"
        );
        assertThat(operationIds()).hasSize(3);
        assertThat(operationIds().get(0)).isNotBlank();
        assertThat(operationIds()).containsOnly(operationIds().get(0));
        assertThat(OperationContextHolder.get()).isNull();
    }

    private List<String> messages() {
        return listAppender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private List<String> operationIds() {
        return listAppender.list.stream()
                .map(event -> event.getMDCPropertyMap().get(BusinessFlowLoggingAspect.OPERATION_ID_MDC_KEY))
                .toList();
    }

    private <T> T createProxy(T target) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(new OperationContextAspect());
        proxyFactory.addAspect(new BusinessFlowLoggingAspect());
        return proxyFactory.getProxy();
    }

    private interface TestApplicationService extends ApplicationService {
        String handle(String value);
    }

    private static class TestApplicationServiceImpl implements TestApplicationService {
        @Override
        public String handle(String value) {
            return "handled:" + value;
        }
    }

    private interface TestFailingApplicationService extends ApplicationService {
        void handle();
    }

    private static class TestFailingApplicationServiceImpl implements TestFailingApplicationService {
        @Override
        public void handle() {
            throw new IllegalStateException("failure");
        }
    }

    private interface TestCommandService extends CommandService<String> {
        String lastProcessed();
    }

    private static class TestCommandServiceImpl implements TestCommandService {
        private String lastProcessed;

        @Override
        public void execute(String request) {
            lastProcessed = request.toUpperCase();
        }

        @Override
        public String lastProcessed() {
            return lastProcessed;
        }
    }

    private interface TestCommandInvokingApplicationService extends ApplicationService {
        void handle(String value);
    }

    private static class TestCommandInvokingApplicationServiceImpl implements TestCommandInvokingApplicationService {

        private final TestCommandService testCommandService;

        private TestCommandInvokingApplicationServiceImpl(TestCommandService testCommandService) {
            this.testCommandService = testCommandService;
        }

        @Override
        public void handle(String value) {
            testCommandService.execute(value);
        }
    }
}
