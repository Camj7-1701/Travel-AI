@echo off
set JAVA_HOME=C:\Program Files\Java\latest\jdk1.8.0_121
set CP=target\classes
for %%f in (lib\*.jar) do call :addpath "%%f"
"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -parameters -d "target\classes" -cp %CP% src\main\java\com\travel\ai\*.java src\main\java\com\travel\ai\common\*.java src\main\java\com\travel\ai\controller\*.java src\main\java\com\travel\ai\entity\*.java src\main\java\com\travel\ai\mapper\*.java src\main\java\com\travel\ai\security\*.java src\main\java\com\travel\ai\service\*.java src\main\java\com\travel\ai\service\impl\*.java src\main\java\com\travel\ai\util\*.java src\main\java\com\travel\ai\config\*.java
echo All files compiled!

:addpath
set CP=%CP%;%~1
goto :eof