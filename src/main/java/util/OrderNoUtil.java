package util;
import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
public class OrderNoUtil { public static String generate(){ return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); } }
