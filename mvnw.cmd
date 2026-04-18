@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

for /f "tokens=2 delims==" %%A in ('findstr /b "distributionUrl" "%MAVEN_WRAPPER_PROPERTIES%"') do set DISTRIBUTION_URL=%%A
for %%I in ("%DISTRIBUTION_URL%") do set DIST_FILE=%%~nxI
set DIST_NAME=%DIST_FILE:.zip=%

if "%MAVEN_USER_HOME%"=="" (
    set MAVEN_USER_HOME_LOCAL=%USERPROFILE%
) else (
    set MAVEN_USER_HOME_LOCAL=%MAVEN_USER_HOME%
)

set MAVEN_HOME_LOCAL=%MAVEN_USER_HOME_LOCAL%\.m2\wrapper\dists
set MAVEN_DIST_DIR=%MAVEN_HOME_LOCAL%\%DIST_NAME%

if not exist "%MAVEN_DIST_DIR%" (
    echo Downloading Maven %DIST_NAME%...
    mkdir "%MAVEN_DIST_DIR%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%MAVEN_DIST_DIR%.zip'; Expand-Archive -Path '%MAVEN_DIST_DIR%.zip' -DestinationPath '%MAVEN_DIST_DIR%' -Force; Remove-Item '%MAVEN_DIST_DIR%.zip'"
)

for /d %%D in ("%MAVEN_DIST_DIR%\*") do (
    set MAVEN_HOME=%%~fD
    goto runMaven
)

echo Maven home was not found in %MAVEN_DIST_DIR%.
exit /b 1

:runMaven
"%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
