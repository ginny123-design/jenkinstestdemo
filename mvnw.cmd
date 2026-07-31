@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script for Windows
@REM ----------------------------------------------------------------------------

@if "%DEBUG%" == "" @echo off
@setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set MAVEN_PROJECT_ROOT=%DIRNAME%

set MAVEN_VERSION=3.9.9
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin\apache-maven-%MAVEN_VERSION%
set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd

if exist "%MVN_CMD%" (
    goto execute
)

echo Maven not found in PATH or Wrapper cache. Downloading Apache Maven %MAVEN_VERSION%...
set ZIP_DEST=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin.zip
set EXTRACT_DEST=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin

if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"

powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip', '%ZIP_DEST%')"

if errorlevel 1 (
    echo Error downloading Maven. Please ensure internet access is available.
    exit /b 1
)

echo Extracting Apache Maven %MAVEN_VERSION%...
powershell -Command "Expand-Archive -Path '%ZIP_DEST%' -DestinationPath '%EXTRACT_DEST%' -Force"

:execute
echo Executing Maven...
call "%MVN_CMD%" %*
exit /b %ERRORLEVEL%
