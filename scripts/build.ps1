$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$output = Join-Path $projectRoot "out\classes"
New-Item -ItemType Directory -Force -Path $output | Out-Null
$sources = Get-ChildItem -Path (Join-Path $projectRoot "src") -Recurse -Filter *.java | ForEach-Object FullName
if (-not $sources) { throw "No production Java sources were found." }
& javac --release 17 -encoding UTF-8 -d $output $sources
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Production sources compiled successfully to $output"
