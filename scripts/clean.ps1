$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$generated = @(
    (Join-Path $projectRoot "out"),
    (Join-Path $projectRoot "bin"),
    (Join-Path $projectRoot "build"),
    (Join-Path $projectRoot "dist"),
    (Join-Path $projectRoot "target")
)
foreach ($path in $generated) {
    $resolvedRoot = [System.IO.Path]::GetFullPath($projectRoot)
    $resolvedPath = [System.IO.Path]::GetFullPath($path)
    if (-not $resolvedPath.StartsWith($resolvedRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to clean outside the project directory: $resolvedPath"
    }
    if (Test-Path -LiteralPath $resolvedPath) { Remove-Item -Recurse -Force -LiteralPath $resolvedPath }
}
Write-Host "Generated build directories removed. Data files were not changed."
