@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  amzur-test-gorm startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and AMZUR_TEST_GORM_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\amzur-test-gorm-0.1.jar;%APP_HOME%\lib\micronaut-hibernate-gorm-1.0.4.jar;%APP_HOME%\lib\micronaut-runtime-groovy-1.0.4.jar;%APP_HOME%\lib\micronaut-validation-1.0.4.jar;%APP_HOME%\lib\micronaut-http-client-1.0.4.jar;%APP_HOME%\lib\micronaut-http-server-netty-1.0.4.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\postgresql-42.7.3.jar;%APP_HOME%\lib\micronaut-gorm-common-1.0.4.jar;%APP_HOME%\lib\grails-datastore-gorm-hibernate5-6.1.8.RELEASE.jar;%APP_HOME%\lib\grails-datastore-gorm-hibernate-core-6.1.8.RELEASE.jar;%APP_HOME%\lib\grails-datastore-gorm-6.1.8.RELEASE.jar;%APP_HOME%\lib\grails-datastore-gorm-validation-6.1.8.RELEASE.jar;%APP_HOME%\lib\grails-datastore-core-6.1.8.RELEASE.jar;%APP_HOME%\lib\groovy-2.5.4.jar;%APP_HOME%\lib\micronaut-http-netty-1.0.4.jar;%APP_HOME%\lib\micronaut-http-server-1.0.4.jar;%APP_HOME%\lib\micronaut-runtime-1.0.4.jar;%APP_HOME%\lib\micronaut-websocket-1.0.4.jar;%APP_HOME%\lib\micronaut-router-1.0.4.jar;%APP_HOME%\lib\micronaut-http-1.0.4.jar;%APP_HOME%\lib\micronaut-spring-1.0.4.jar;%APP_HOME%\lib\micronaut-aop-1.0.4.jar;%APP_HOME%\lib\micronaut-inject-1.0.4.jar;%APP_HOME%\lib\micronaut-buffer-netty-1.0.4.jar;%APP_HOME%\lib\micronaut-core-1.0.4.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.22.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\hibernate-validator-5.2.5.Final.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\rxjava-2.2.2.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.30.Final.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\checker-qual-3.42.0.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\snakeyaml-1.23.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\reactive-streams-1.0.2.jar;%APP_HOME%\lib\netty-codec-http-4.1.30.Final.jar;%APP_HOME%\lib\netty-handler-4.1.30.Final.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.9.8.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.9.8.jar;%APP_HOME%\lib\jackson-databind-2.9.8.jar;%APP_HOME%\lib\netty-codec-socks-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-4.1.30.Final.jar;%APP_HOME%\lib\netty-transport-4.1.30.Final.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\spring-orm-5.0.10.RELEASE.jar;%APP_HOME%\lib\hibernate-core-5.1.9.Final.jar;%APP_HOME%\lib\javax.el-api-2.2.4.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\netty-buffer-4.1.30.Final.jar;%APP_HOME%\lib\jackson-annotations-2.9.8.jar;%APP_HOME%\lib\jackson-core-2.9.8.jar;%APP_HOME%\lib\netty-resolver-4.1.30.Final.jar;%APP_HOME%\lib\spring-jdbc-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-tx-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-context-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-aop-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-beans-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-expression-5.0.10.RELEASE.jar;%APP_HOME%\lib\spring-core-5.0.10.RELEASE.jar;%APP_HOME%\lib\javassist-3.21.0-GA.jar;%APP_HOME%\lib\jta-1.1.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.0.1.Final.jar;%APP_HOME%\lib\jboss-logging-3.3.0.Final.jar;%APP_HOME%\lib\hibernate-jpa-2.1-api-1.0.0.Final.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\geronimo-jta_1.1_spec-1.1.1.jar;%APP_HOME%\lib\jandex-2.0.3.Final.jar;%APP_HOME%\lib\classmate-1.3.0.jar;%APP_HOME%\lib\dom4j-1.6.1.jar;%APP_HOME%\lib\netty-common-4.1.30.Final.jar;%APP_HOME%\lib\spring-jcl-5.0.10.RELEASE.jar;%APP_HOME%\lib\commons-validator-1.5.1.jar;%APP_HOME%\lib\concurrentlinkedhashmap-lru-1.4.2.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar

@rem Execute amzur-test-gorm
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %AMZUR_TEST_GORM_OPTS%  -classpath "%CLASSPATH%" com.amzur.test.Application %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable AMZUR_TEST_GORM_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%AMZUR_TEST_GORM_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
