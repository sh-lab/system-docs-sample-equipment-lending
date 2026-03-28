package net.shlab.hogefugapiyo.framework.service;

import java.util.UUID;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContext;
import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandBaseServiceTest {

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void executeRecordsHistoryOnlyAfterSuccessfulExecution() {
        TestCommandBaseService service = new TestCommandBaseService(false);
        OperationContextHolder.set(OperationContext.create());

        service.execute("command");

        assertThat(service.executedRequest).isEqualTo("command");
        assertThat(service.recordedHistory).isEqualTo("command-done");
    }

    @Test
    void executeDoesNotRecordHistoryWhenExecutionFails() {
        TestCommandBaseService service = new TestCommandBaseService(true);
        OperationContextHolder.set(OperationContext.create());

        assertThatThrownBy(() -> service.execute("command"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");
        assertThat(service.recordedHistory).isNull();
    }

    @Test
    void executeThrowsBeforeStateChangeWhenOperationContextIsMissing() {
        TestCommandBaseService service = new TestCommandBaseService(false);

        assertThatThrownBy(() -> service.execute("command"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Operation context is not available.");
        assertThat(service.executedRequest).isNull();
        assertThat(service.recordedHistory).isNull();
    }

    @Test
    void currentOperationIdReturnsOperationContextValue() {
        TestCommandBaseService service = new TestCommandBaseService(false);
        OperationContext context = OperationContext.create();
        OperationContextHolder.set(context);

        UUID operationId = service.readCurrentOperationId();

        assertThat(operationId).isEqualTo(context.operationId());
    }

    @Test
    void currentOperationIdThrowsWhenOperationContextIsMissing() {
        TestCommandBaseService service = new TestCommandBaseService(false);

        assertThatThrownBy(service::readCurrentOperationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Operation context is not available.");
    }

    private static final class TestCommandBaseService extends CommandBaseService<String, String> {

        private final boolean failOnExecute;
        private String executedRequest;
        private String recordedHistory;

        private TestCommandBaseService(boolean failOnExecute) {
            this.failOnExecute = failOnExecute;
        }

        @Override
        protected String commandId() {
            return "TEST-COMMAND";
        }

        @Override
        protected String doExecute(String request) {
            executedRequest = request;
            if (failOnExecute) {
                throw new IllegalStateException("boom");
            }
            return request + "-done";
        }

        @Override
        protected void recordHistory(String result) {
            recordedHistory = result;
        }

        private UUID readCurrentOperationId() {
            return currentOperationId();
        }
    }
}
