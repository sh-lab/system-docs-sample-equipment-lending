package net.shlab.hogefugapiyo.framework.core.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;
import net.shlab.hogefugapiyo.framework.operation.OperationContextAspect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class OperationContextAspectTest {

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void applicationServiceSetsAndClearsOperationContext() {
        TestApplicationServiceImpl target = new TestApplicationServiceImpl();
        TestApplicationService proxy = createProxy(target);

        OperationContext actual = proxy.handle();

        assertThat(actual).isNotNull();
        assertThat(actual.operationId()).isNotNull();
        assertThat(target.capturedContext()).isSameAs(actual);
        assertThat(OperationContextHolder.get()).isNull();
    }

    @Test
    void applicationServiceClearsOperationContextWhenExceptionOccurs() {
        TestFailingApplicationServiceImpl target = new TestFailingApplicationServiceImpl();
        TestFailingApplicationService proxy = createProxy(target);

        assertThatThrownBy(proxy::handle).isInstanceOf(IllegalStateException.class);
        assertThat(target.capturedContext()).isNotNull();
        assertThat(target.capturedContext().operationId()).isNotNull();
        assertThat(OperationContextHolder.get()).isNull();
    }

    private <T> T createProxy(T target) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(new OperationContextAspect());
        return proxyFactory.getProxy();
    }

    private interface TestApplicationService extends ApplicationService {
        OperationContext handle();
    }

    private static class TestApplicationServiceImpl implements TestApplicationService {

        private OperationContext capturedContext;

        @Override
        public OperationContext handle() {
            capturedContext = OperationContextHolder.get();
            return capturedContext;
        }

        private OperationContext capturedContext() {
            return capturedContext;
        }
    }

    private interface TestFailingApplicationService extends ApplicationService {
        void handle();
    }

    private static class TestFailingApplicationServiceImpl implements TestFailingApplicationService {

        private OperationContext capturedContext;

        @Override
        public void handle() {
            capturedContext = OperationContextHolder.get();
            throw new IllegalStateException("failure");
        }

        private OperationContext capturedContext() {
            return capturedContext;
        }
    }
}
