package mr.cookie.matrix.model.utils;

import mr.cookie.matrix.model.CountDownLatchMatrix;
import mr.cookie.matrix.model.SingleThreadMatrix;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

class RunnableLatchMultiplyRowTaskTest {

    @Test
    void getOutputRowThrowsWhenCalledBeforeFinishingTask() {
        CountDownLatch latch = new CountDownLatch(1);
        RunnableLatchMultiplyRowTask task = new RunnableLatchMultiplyRowTask(
                1, SingleThreadMatrix.random(), CountDownLatchMatrix.random(), latch);

        Assertions.assertThatThrownBy(task::getOutputRow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Latch did not count down to 0 yet. Its counter is [%d].", latch.getCount());
    }

    @Test
    void getOutputRowThrowsWhenOutputIsNull() {
        CountDownLatch latch = new CountDownLatch(0);
        RunnableLatchMultiplyRowTask task = new RunnableLatchMultiplyRowTask(
                1, SingleThreadMatrix.random(), CountDownLatchMatrix.random(), latch);

        Assertions.assertThatThrownBy(task::getOutputRow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("This task was not run before calling this getter.");
    }

}