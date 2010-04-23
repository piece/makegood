@ECHO OFF
REM $Id$

REM *************************************************************
REM ** Stagehand_TestRunner CLI for Windows based systems (based on symfony.bat)
REM *************************************************************

REM This script will do the following:
REM - check for PHP_COMMAND env, if found, use it.
REM   - if not found detect php, if found use it, otherwise err and terminate

IF "%OS%"=="Windows_NT" @SETLOCAL

REM %~dp0 is expanded pathname of the current script under NT
SET SCRIPT_DIR=%~dp0

GOTO INIT

:INIT

IF "%PHP_COMMAND%" == "" GOTO NO_PHPCOMMAND

IF EXIST ".\phpunitrunner" (
  %PHP_COMMAND% -d html_errors=off -d open_basedir= -q ".\phpunitrunner" %1 %2 %3 %4 %5 %6 %7 %8 %9
) ELSE (
  %PHP_COMMAND% -d html_errors=off -d open_basedir= -q "%SCRIPT_DIR%phpunitrunner" %1 %2 %3 %4 %5 %6 %7 %8 %9
)
GOTO CLEANUP

:NO_PHPCOMMAND
REM ECHO ------------------------------------------------------------------------
REM ECHO WARNING: Set environment var PHP_COMMAND to the location of your php.exe
REM ECHO          executable (e.g. C:\PHP\php.exe).  (assuming php.exe on PATH)
REM ECHO ------------------------------------------------------------------------
SET PHP_COMMAND=php.exe
GOTO INIT

:CLEANUP
IF "%OS%"=="Windows_NT" @ENDLOCAL
REM PAUSE

REM Local Variables:
REM mode: bat-generic
REM coding: iso-8859-1
REM indent-tabs-mode: nil
REM End:
