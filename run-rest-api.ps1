# PowerShell script to compile and run REST API Server
Write-Host "Compiling project..." -ForegroundColor Green
mvn compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nStarting REST API Server on port 8081..." -ForegroundColor Green
    Write-Host "API Base URL: http://localhost:8081/api`n" -ForegroundColor Cyan
    mvn exec:java "-Dexec.mainClass=api.RestApiServer"
} else {
    Write-Host "`nCompilation failed! Please fix errors and try again." -ForegroundColor Red
    exit 1
}

