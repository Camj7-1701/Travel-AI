@echo off
set JAVA_HOME=C:\Program Files\Java\latest\jdk1.8.0_121
set CP=target\classes
for %%f in (lib\*.jar) do call :addpath "%%f"
"%JAVA_HOME%\bin\javac.exe" -d "target\classes" -cp %CP% src\main\java\com\travel\ai\TravelAiApplication.java
echo Compilation done!

:addpath
set CP=%CP%;%~1
goto :eof