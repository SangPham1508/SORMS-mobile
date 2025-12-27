@echo off
setlocal

REM Windows wrapper launcher for projects that only have the Unix gradlew script.
REM Requires either Git Bash (bash.exe) or WSL to be installed.

set "DIR=%~dp0"

REM Prefer Git for Windows bash if available
where bash >nul 2>&1
if %ERRORLEVEL%==0 (
  bash "%DIR%gradlew" %*
  exit /b %ERRORLEVEL%
)

REM Fallback to WSL if available
where wsl >nul 2>&1
if %ERRORLEVEL%==0 (
  wsl bash "%DIR%gradlew" %*
  exit /b %ERRORLEVEL%
)

echo ERROR: Neither 'bash' nor 'wsl' was found in PATH.
echo Install Git for Windows (includes bash) or enable WSL, then re-run.
exit /b 1

