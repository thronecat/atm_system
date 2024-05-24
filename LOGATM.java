import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LOGATM {
    private static final String LOG_FILE_PATH = "D://rizhi.txt"; // 日志文件的路径
    private static BufferedWriter writer; // 用于写入日志的BufferedWriter对象

    // 初始化日志文件和BufferedWriter对象
    static {
        try {
            writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LOGATM(){
        log("rizhi");
    }
    // 记录日志信息的方法
    public static void log(String message) {
        LocalDateTime localDateTime = LocalDateTime.now(); // 获取当前时间
        String logEntry = localDateTime + " - " + message; // 创建日志条目
        try {
            writer.write(logEntry); // 写入日志条目
            writer.newLine(); // 添加新行
            writer.flush(); // 刷新BufferedWriter，确保信息写入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            writer.close(); // 关闭BufferedWriter对象
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
