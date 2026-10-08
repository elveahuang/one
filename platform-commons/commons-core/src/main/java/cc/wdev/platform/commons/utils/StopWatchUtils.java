package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;

import java.text.NumberFormat;

/**
 * @author elvea
 */
@Slf4j
public class StopWatchUtils {

    public static StopWatch createStopWatch(String id) {
        return new CustomStopWatch(id);
    }

    public static class CustomStopWatch extends StopWatch {

        public CustomStopWatch(String id) {
            super(id);
        }

        @Override
        public @NotNull String shortSummary() {
            return "StopWatch '" + getId() + "': running time = " + getTotalTimeMillis() + " ms";
        }

        @Override
        public @NotNull String prettyPrint() {
            StringBuilder sb = new StringBuilder(shortSummary());
            sb.append('\n');
            if (this.getTaskCount() <= 0) {
                sb.append("No task info kept");
            } else {
                sb.append("---------------------------------------------\n");
                sb.append("ms      %     Task name\n");
                sb.append("---------------------------------------------\n");
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMinimumIntegerDigits(3);
                nf.setGroupingUsed(true);
                NumberFormat pf = NumberFormat.getPercentInstance();
                pf.setMinimumIntegerDigits(3);
                pf.setGroupingUsed(false);
                for (TaskInfo task : getTaskInfo()) {
                    sb.append(nf.format(task.getTimeMillis())).append("  ");
                    sb.append(pf.format((double) task.getTimeMillis() / getTotalTimeMillis())).append("  ");
                    sb.append(task.getTaskName()).append('\n');
                }
            }
            return sb.toString();
        }
    }

    public static void log(StopWatch sw) {
        log.info(sw.prettyPrint());
    }

}
