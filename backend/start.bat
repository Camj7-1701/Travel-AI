@echo off
set JAVA_HOME=C:\Program Files\Java\latest\jdk1.8.0_121
set CLASSPATH=target\classes
for %%f in (lib\*.jar) do call :addpath "%%f"
"%JAVA_HOME%\bin\java.exe" -cp %CLASSPATH% com.travel.ai.TravelAiApplication
pause

:addpath
set CLASSPATH=%CLASSPATH%;%~1
goto :eof