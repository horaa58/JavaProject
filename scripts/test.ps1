$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$output = Join-Path $projectRoot "out\test-classes"
if (Test-Path -LiteralPath $output) { Remove-Item -Recurse -Force -LiteralPath $output }
New-Item -ItemType Directory -Force -Path $output | Out-Null
$production = Get-ChildItem -Path (Join-Path $projectRoot "src") -Recurse -Filter *.java | ForEach-Object FullName
$tests = Get-ChildItem -Path (Join-Path $projectRoot "test") -Recurse -Filter *.java | ForEach-Object FullName
& javac --release 17 -encoding UTF-8 -d $output $production $tests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Push-Location $projectRoot
try { & java -ea -cp $output smartparkingmanagementsystem.AllTests }
finally { Pop-Location }
exit $LASTEXITCODE
