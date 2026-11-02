@echo off
setlocal
pushd "%~dp0"

call run.bat < nul
if errorlevel 1 exit /b 1

if exist "out\test-classes" rmdir /s /q "out\test-classes"
mkdir "out\test-classes"

set "JAVAC=%JAVA_HOME%\bin\javac.exe"
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\javac.exe" set "JAVAC=C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\javac.exe"

"%JAVAC%" --release 17 -parameters -cp "lib\*;out\classes" -d "out\test-classes" src\test\java\Sprint8MultipartTest.java src\test\java\Sprint11RoleAuthTest.java
if errorlevel 1 exit /b 1

"%JAVAC:javac.exe=java.exe%" -cp "lib\*;out\classes;out\test-classes" Sprint8MultipartTest
if errorlevel 1 exit /b 1

"%JAVAC:javac.exe=java.exe%" -cp "lib\*;out\classes;out\test-classes" Sprint11RoleAuthTest
set "RESULT=%ERRORLEVEL%"
popd
exit /b %RESULT%