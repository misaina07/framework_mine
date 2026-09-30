@echo off
setlocal enabledelayedexpansion

pushd "%~dp0"

REM --------------------------
REM VARIABLES
REM --------------------------
set "APP_NAME=my-framework"
set "SRC_DIR=src\java"
set "WEB_DIR=src\webapps"
set "OUT_DIR=out"
set "CLASSES_DIR=%OUT_DIR%\classes"
set "LIB_DIR=%OUT_DIR%\lib"
set "LIB=lib"

echo.
echo ===================================
echo Nettoyage...
echo ===================================

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"

mkdir "%CLASSES_DIR%"
mkdir "%LIB_DIR%"

echo.
echo ===================================
echo Compilation Java...
echo ===================================

subst B: "%CD%" >nul 2>&1

dir /s /b "B:\%SRC_DIR%\*.java" > "%TEMP%\sources.txt"

javac -cp "B:\%LIB%\*" -d "B:\%CLASSES_DIR%" @"%TEMP%\sources.txt"

set "RESULT=%ERRORLEVEL%"

del "%TEMP%\sources.txt" 2>nul
subst B: /d >nul 2>&1

if %RESULT% neq 0 (
    echo.
    echo ERREUR : Compilation echouee.
    popd
    pause
    exit /b 1
)

echo.
echo ===================================
echo Copie des ressources Web...
echo ===================================

if exist "%WEB_DIR%" (
    xcopy "%WEB_DIR%\*" "%OUT_DIR%\" /E /I /Y /Q >nul
)

echo.
echo ===================================
echo Extraction des JARs...
echo ===================================

for %%j in ("%LIB%\*.jar") do (

    if exist "%%~fj" (

        echo Extraction de %%~nxj

        REM Ignorer servlet-api.jar si souhaite
        REM if /i not "%%~nxj"=="servlet-api.jar" (lsl

        pushd "%CLASSES_DIR%"
        jar xf "%%~fj"
        popd

        REM )

    )
)

echo.
echo ===================================
echo Preparation de l'application web...
echo ===================================

mkdir "%OUT_DIR%\WEB-INF\classes" 2>nul
xcopy "%CLASSES_DIR%\*" "%OUT_DIR%\WEB-INF\classes\" /E /I /Y /Q >nul

echo.
echo ===================================
echo Creation du my-framework.jar...
echo ===================================

jar cf "%LIB_DIR%\%APP_NAME%.jar" -C "%CLASSES_DIR%" .

if errorlevel 1 (
    echo ERREUR : Creation du JAR impossible.
    popd
    pause
    exit /b 1
)

echo.
echo JAR cree :
echo %LIB_DIR%\%APP_NAME%.jar

echo.
echo ===================================
echo BUILD TERMINE
echo ===================================

popd
pause
endlocal