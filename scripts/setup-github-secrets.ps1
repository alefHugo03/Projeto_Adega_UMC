<#
.SYNOPSIS
  Read a .env file and set GitHub Actions repository secrets using gh CLI.

USAGE
  From the repository root in PowerShell (Windows):
    .\scripts\setup-github-secrets.ps1

#>
param(
  [string]$EnvFile = '.env'
)

if (-not (Test-Path $EnvFile)) {
  Write-Error "No $EnvFile found in $(Get-Location). Create one with variables to upload as secrets."
  exit 1
}

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
  Write-Error "gh CLI not found. Install GitHub CLI and run 'gh auth login' first."
  exit 1
}

Get-Content $EnvFile | ForEach-Object {
  $line = $_.Trim()
  if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith('#')) { return }
  if ($line -notmatch '=') { Write-Warning "Skipping invalid line: $line"; return }
  $parts = $line -split '=', 2
  $key = $parts[0].Trim()
  $value = $parts[1].Trim()
  if ([string]::IsNullOrEmpty($key)) { Write-Warning "Skipping line with empty key: $line"; return }
  Write-Host "Setting secret: $key"
  gh secret set $key --body $value
}

Write-Host "Done. Confirm secrets in GitHub Settings → Secrets and variables → Actions."
