import org.apache.log4j.PropertyConfigurator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.currentTimeMillis;

@Aspect
public class LoggerMethods {
    private long startTime; //начало выполнения метода
    private boolean methodFound = true;//обнаружен ли метод
    private boolean firstLaunch = true;//первый запуск, для одноразовой установки настроек логгирования
    private String logText; //текст для логгирования
    public static int loggingMode = 1; //режим логгирования
    //1 - в консоль, 2 - в файл, 3 - в файл и консоль, по умолчанию в файл
    Logger logger;

    private String getObjectValue(Object obj){

        if (    obj.getClass() == Byte.class ||
                obj.getClass() == Short.class||
                obj.getClass() == Integer.class||
                obj.getClass() == Long.class||
                obj.getClass() == Float.class||
                obj.getClass() == Double.class||
                obj.getClass() == Character.class||
                obj.getClass() == Boolean.class)
            return obj.toString();

        if (obj.getClass() == String.class){
            return "\""+obj+"\"";
        }

        return "значение объекта";
    }

    @Pointcut("@annotation(Trace)")
    public void aspectFound(){
    }

    @Before("aspectFound()")
    public void before(JoinPoint joinPoint){
        if (firstLaunch) {
            logger = LoggerFactory.getLogger(LoggerMethods.class);
            String logFilePath;
            switch (loggingMode){
                case 1: logFilePath = "src/logOutputProperties/consoleOut.properties"; break;
                case 3: logFilePath = "src/logOutputProperties/consoleAndFileOut.properties"; break;
                default: logFilePath = "src/logOutputProperties/fileOut.properties"; break;
            }

            PropertyConfigurator.configure(logFilePath);
            firstLaunch = false;
        }

        if (methodFound){
            startTime = currentTimeMillis();

            String methodName = joinPoint.getSignature().getName();
            StringBuilder methodArgs = new StringBuilder("(");
            Object[] args = joinPoint.getArgs();
            for (Object obj: args) {
                methodArgs.append(obj.getClass().getSimpleName());
                methodArgs.append(" ").append(getObjectValue(obj)).append(", ");
            }

            if (!methodArgs.toString().equals("(")) {
                int methodArgsLength = methodArgs.length();
                methodArgs.delete(methodArgsLength - 2, methodArgsLength);
            }
            methodArgs.append(')');

            logText = methodName+" "+methodArgs+" | execution time: ";
            methodFound = false;
        }
    }

    @AfterReturning("aspectFound()")
    public void after(){

        if (!methodFound) {
            long finishTime = currentTimeMillis() - startTime;
            logText += finishTime + " mls.";

            logger.info(logText);
            methodFound = true;
        }

    }


}
