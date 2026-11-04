@echo off
rem Builds the project into .\out and runs it.
rem Usage: run.bat          - interactive console
rem        run.bat --demo   - scripted demo of all patterns
cd /d "%~dp0"
if not exist out mkdir out
javac --release 17 -encoding UTF-8 -d out -sourcepath src src\com\restaurant\Main.java
if errorlevel 1 exit /b 1
java -cp out com.restaurant.Main %*
