<#
.SYNOPSIS
  DeliveryFlow 로컬 PostgreSQL의 관리자 또는 기사 계정 비밀번호를 재설정합니다.

.DESCRIPTION
  localhost 데이터베이스만 대상으로 합니다. 새 비밀번호와 PostgreSQL 비밀번호는 화면에 표시하거나
  파일에 저장하지 않습니다. 실행 직전에 RESET을 입력해야 실제 데이터베이스를 변경합니다.
#>
[CmdletBinding()]
param(
    [ValidateSet('admin', 'driver', 'both')]
    [string]$Account = 'both',

    [ValidateSet('localhost', '127.0.0.1', '::1')]
    [string]$DatabaseHost = 'localhost',

    [ValidateRange(1, 65535)]
    [int]$DatabasePort = 5432,

    [string]$DatabaseName = 'deliveryflow',
    [string]$DatabaseUser = 'postgres',
    [string]$AdminEmail = 'admin@deliveryflow.local',
    [string]$DriverEmail = 'driver@deliveryflow.local'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Find-Psql {
    $command = Get-Command psql.exe -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($null -ne $command) { return $command.Source }

    $candidates = @(
        'C:\Program Files\PostgreSQL\17\bin\psql.exe',
        'C:\Program Files\PostgreSQL\16\bin\psql.exe',
        'C:\Program Files\PostgreSQL\15\bin\psql.exe'
    )
    foreach ($candidate in $candidates) {
        if (Test-Path -LiteralPath $candidate) { return $candidate }
    }

    throw 'psql.exe was not found. Install PostgreSQL client tools or add psql.exe to PATH.'
}

function ConvertTo-PlainText {
    param([Parameter(Mandatory)][Security.SecureString]$SecureValue)

    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureValue)
    try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer) }
}

function Read-NewPassword {
    param([Parameter(Mandatory)][string]$Label)

    $first = Read-Host "New password for $Label" -AsSecureString
    $second = Read-Host "Confirm new password for $Label" -AsSecureString
    $firstPlain = $null
    $secondPlain = $null

    try {
        $firstPlain = ConvertTo-PlainText -SecureValue $first
        $secondPlain = ConvertTo-PlainText -SecureValue $second
        if ($firstPlain.Length -lt 8) { throw 'Use a password with at least 8 characters.' }
        if ($firstPlain -cne $secondPlain) { throw 'The two password entries do not match.' }
        return $firstPlain
    }
    finally {
        $secondPlain = $null
        $first.Dispose()
        $second.Dispose()
    }
}

function ConvertTo-PostgreSqlLiteral {
    param([Parameter(Mandatory)][string]$Value)

    return "'" + $Value.Replace("'", "''") + "'"
}

function Reset-AccountPassword {
    param(
        [Parameter(Mandatory)][string]$PsqlPath,
        [Parameter(Mandatory)][string]$Email,
        [Parameter(Mandatory)][string]$Role,
        [Parameter(Mandatory)][string]$NewPassword
    )

    $passwordLiteral = ConvertTo-PostgreSqlLiteral -Value $NewPassword
    $emailLiteral = ConvertTo-PostgreSqlLiteral -Value $Email
    $roleLiteral = ConvertTo-PostgreSqlLiteral -Value $Role
    $query = "UPDATE users`nSET password_hash = crypt($passwordLiteral, gen_salt('bf', 10))`nWHERE email = $emailLiteral AND role = $roleLiteral AND active = true`nRETURNING email || '|' || role;"
    $result = & $PsqlPath -X --no-psqlrc --set 'ON_ERROR_STOP=1' --host $DatabaseHost --port $DatabasePort --username $DatabaseUser --dbname $DatabaseName --tuples-only --no-align --quiet -c $query
    if ($LASTEXITCODE -ne 0) { throw "Failed to reset '$Email'. Check the PostgreSQL connection and credentials." }
    $matchingRows = @($result | Where-Object { $_.Trim() -eq "$Email|$Role" })
    if ($matchingRows.Count -ne 1) {
        throw "Active $Role account '$Email' was not found. No password was changed."
    }
}

$databaseSecurePassword = $null
$databasePassword = $null
$adminPassword = $null
$driverPassword = $null
$previousPgPassword = $env:PGPASSWORD
$hadPreviousPgPassword = Test-Path Env:PGPASSWORD

try {
    $psqlPath = Find-Psql
    Write-Host 'DeliveryFlow local account password reset' -ForegroundColor Cyan
    Write-Host "Database: $DatabaseHost`:$DatabasePort/$DatabaseName"
    Write-Host "Target account: $Account"
    Write-Host ''

    $databaseSecurePassword = Read-Host "PostgreSQL password for $DatabaseUser" -AsSecureString
    $databasePassword = ConvertTo-PlainText -SecureValue $databaseSecurePassword
    if ($Account -in @('admin', 'both')) { $adminPassword = Read-NewPassword -Label $AdminEmail }
    if ($Account -in @('driver', 'both')) { $driverPassword = Read-NewPassword -Label $DriverEmail }

    $confirmation = Read-Host 'This changes local account password hashes. Type RESET to continue'
    if ($confirmation -cne 'RESET') {
        Write-Host 'Password reset cancelled. No data was changed.' -ForegroundColor Yellow
        return
    }

    $env:PGPASSWORD = $databasePassword
    & $psqlPath -X --no-psqlrc --set 'ON_ERROR_STOP=1' --host $DatabaseHost --port $DatabasePort --username $DatabaseUser --dbname $DatabaseName --quiet -c 'CREATE EXTENSION IF NOT EXISTS pgcrypto;'
    if ($LASTEXITCODE -ne 0) { throw 'Could not enable PostgreSQL pgcrypto extension required for BCrypt password hashing.' }

    if ($null -ne $adminPassword) {
        Reset-AccountPassword -PsqlPath $psqlPath -Email $AdminEmail -Role 'ADMIN' -NewPassword $adminPassword
        Write-Host "Administrator password reset succeeded: $AdminEmail" -ForegroundColor Green
    }
    if ($null -ne $driverPassword) {
        Reset-AccountPassword -PsqlPath $psqlPath -Email $DriverEmail -Role 'DRIVER' -NewPassword $driverPassword
        Write-Host "Driver password reset succeeded: $DriverEmail" -ForegroundColor Green
    }

    Write-Host ''
    Write-Host 'Run test-delivery-flow.ps1 again with the new passwords.' -ForegroundColor Cyan
}
finally {
    $databasePassword = $null
    $adminPassword = $null
    $driverPassword = $null
    if ($null -ne $databaseSecurePassword) { $databaseSecurePassword.Dispose() }
    if ($hadPreviousPgPassword) { $env:PGPASSWORD = $previousPgPassword }
    else { Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue }
}
