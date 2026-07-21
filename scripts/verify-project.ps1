$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

$requiredPaths = @(
    "build.xml",
    "manifest.mf",
    "nbproject\project.xml",
    "nbproject\project.properties",
    "src\smartparkingmanagementsystem\SmartParkingManagementSystem.java"
)

$missingPaths = foreach ($relativePath in $requiredPaths) {
    if (-not (Test-Path -LiteralPath (Join-Path $projectRoot $relativePath))) {
        $relativePath
    }
}

if ($missingPaths) {
    throw "The project is incomplete. Missing: $($missingPaths -join ', ')"
}

[xml]$projectXml = Get-Content -Raw -LiteralPath (Join-Path $projectRoot "nbproject\project.xml")
[xml]$buildXml = Get-Content -Raw -LiteralPath (Join-Path $projectRoot "build.xml")

if ($projectXml.project.type -ne "org.netbeans.modules.java.j2seproject") {
    throw "nbproject\project.xml is not a NetBeans Java SE project."
}
if ($buildXml.project.default -ne "jar") {
    throw "build.xml does not expose the expected default build target."
}

Write-Host "Project structure is valid for Apache NetBeans and VS Code."
