package util;
import java.time.LocalDateTime;
public class LogUtil { public static void info(String msg){ System.out.println(LocalDateTime.now() + " INFO " + msg); } public static void error(String msg, Exception e){ System.err.println(LocalDateTime.now() + " ERROR " + msg + " - " + e.getMessage()); } }
