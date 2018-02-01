package mp4editor.util;

public interface LoggingEvents {

//    int SetStatus(String strMessage);

    int printLog(LogType type, Throwable ex, String strMessage);
//    boolean isRunning();
}
