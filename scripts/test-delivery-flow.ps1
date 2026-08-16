<#
.SYNOPSIS
  관리자와 기사의 JWT를 분리해 DeliveryFlow 핵심 운영 흐름을 테스트합니다.

.DESCRIPTION
  주문 등록 -> 기사 배정 -> 기사 배송 시작 -> 기사 배송 완료 -> 이력/대시보드 조회를 순서대로 실행합니다.
  비밀번호와 JWT는 파일에 저장하거나 출력하지 않습니다.

.EXAMPLE
  .\scripts\test-delivery-flow.ps1

.EXAMPLE
  .\scripts\test-delivery-flow.ps1 -BaseUrl "https://deliveryflow-production.up.railway.app"
#>
[CmdletBinding()]
param(
    [ValidatePattern('^https?://')]
    [string]$BaseUrl = 'http://localhost:8080',

    [string]$AdminEmail = 'admin@deliveryflow.local',

    [string]$DriverEmail = 'driver@deliveryflow.local',

    [ValidatePattern('^\d{4}-\d{2}-\d{2}$')]
    [string]$ScheduledDate = (Get-Date).Date.AddDays(1).ToString('yyyy-MM-dd')
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')

function ConvertTo-PlainText {
    param([Parameter(Mandatory)][Security.SecureString]$SecureValue)

    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureValue)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    }
}

function Invoke-DeliveryFlowApi {
    param(
        [Parameter(Mandatory)][ValidateSet('GET', 'POST', 'PATCH')][string]$Method,
        [Parameter(Mandatory)][string]$Path,
        [hashtable]$Headers,
        [object]$Body
    )

    $request = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
        ErrorAction = 'Stop'
    }

    if ($null -ne $Headers) {
        $request.Headers = $Headers
    }

    if ($null -ne $Body) {
        $request.ContentType = 'application/json'
        $request.Body = $Body | ConvertTo-Json -Depth 5
    }

    Invoke-RestMethod @request
}

function Login {
    param(
        [Parameter(Mandatory)][string]$Email,
        [Parameter(Mandatory)][string]$Password
    )

    try {
        return Invoke-DeliveryFlowApi -Method POST -Path '/api/v1/auth/login' -Body @{
            email = $Email
            password = $Password
        }
    }
    catch {
        $statusCode = $null
        if ($null -ne $_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
        }

        if ($statusCode -eq 401) {
            throw "Login failed for '$Email'. Check the password currently stored for this local account. Bootstrap settings only create an account when the email does not already exist."
        }

        throw
    }
}

function Assert-Equal {
    param(
        [Parameter(Mandatory)]$Actual,
        [Parameter(Mandatory)]$Expected,
        [Parameter(Mandatory)][string]$Description
    )

    if ($Actual -ne $Expected) {
        throw "$Description. Expected: $Expected / Actual: $Actual"
    }
}

$adminSecurePassword = $null
$driverSecurePassword = $null
$adminPassword = $null
$driverPassword = $null
$adminToken = $null
$driverToken = $null

try {
    Write-Host "DeliveryFlow integration test" -ForegroundColor Cyan
    Write-Host "Target: $BaseUrl"
    Write-Host "Scheduled date: $ScheduledDate"
    Write-Host ''

    $health = Invoke-DeliveryFlowApi -Method GET -Path '/api/v1/health'
    Assert-Equal -Actual $health.status -Expected 'UP' -Description 'Health check failed'
    Write-Host '[1/8] Health check succeeded.' -ForegroundColor Green

    $adminSecurePassword = Read-Host "Password for $AdminEmail" -AsSecureString
    $driverSecurePassword = Read-Host "Password for $DriverEmail" -AsSecureString
    $adminPassword = ConvertTo-PlainText -SecureValue $adminSecurePassword
    $driverPassword = ConvertTo-PlainText -SecureValue $driverSecurePassword

    $adminLogin = Login -Email $AdminEmail -Password $adminPassword
    Assert-Equal -Actual $adminLogin.role -Expected 'ADMIN' -Description 'Administrator login failed'
    $driverLogin = Login -Email $DriverEmail -Password $driverPassword
    Assert-Equal -Actual $driverLogin.role -Expected 'DRIVER' -Description 'Driver login failed'

    $adminToken = $adminLogin.accessToken
    $driverToken = $driverLogin.accessToken
    $adminHeaders = @{ Authorization = "Bearer $adminToken"; 'Accept-Language' = 'ko' }
    $driverHeaders = @{ Authorization = "Bearer $driverToken"; 'Accept-Language' = 'ko' }
    Write-Host '[2/8] Administrator and driver login succeeded.' -ForegroundColor Green

    $confirmation = Read-Host 'This test will create an order and delivery. Type RUN to continue'
    if ($confirmation -cne 'RUN') {
        Write-Host 'Test cancelled before creating data.' -ForegroundColor Yellow
        return
    }

    $runId = Get-Date -Format 'yyyyMMddHHmmss'
    $order = Invoke-DeliveryFlowApi -Method POST -Path '/api/v1/orders' -Headers $adminHeaders -Body @{
        recipientName = "API Test $runId"
        recipientPhone = '010-9999-9999'
        address = '서울특별시 중구 테스트로 1'
        requestedDate = $ScheduledDate
    }
    Write-Host "[3/8] Order created: $($order.orderNo) (ID $($order.id))" -ForegroundColor Green

    $drivers = @(Invoke-DeliveryFlowApi -Method GET -Path '/api/v1/drivers' -Headers $adminHeaders)
    $driver = $drivers | Where-Object { $_.email -eq $DriverEmail } | Select-Object -First 1
    if ($null -eq $driver) {
        throw "Active driver '$DriverEmail' was not found."
    }

    $delivery = Invoke-DeliveryFlowApi -Method POST -Path '/api/v1/deliveries' -Headers $adminHeaders -Body @{
        orderId = $order.id
        driverId = $driver.id
        scheduledDate = $ScheduledDate
    }
    Assert-Equal -Actual $delivery.status -Expected 'ASSIGNED' -Description 'Delivery assignment failed'
    Write-Host "[4/8] Delivery assigned: ID $($delivery.id)" -ForegroundColor Green

    $myDeliveries = Invoke-DeliveryFlowApi -Method GET -Path "/api/v1/deliveries/me?scheduledDate=$ScheduledDate" -Headers $driverHeaders
    $assignedDelivery = @($myDeliveries.content | Where-Object { $_.id -eq $delivery.id }) | Select-Object -First 1
    if ($null -eq $assignedDelivery) {
        throw "Assigned delivery $($delivery.id) is not visible to the driver."
    }
    Write-Host '[5/8] Driver delivery list check succeeded.' -ForegroundColor Green

    $inDelivery = Invoke-DeliveryFlowApi -Method PATCH -Path "/api/v1/deliveries/$($delivery.id)/status?status=IN_DELIVERY" -Headers $driverHeaders
    Assert-Equal -Actual $inDelivery.status -Expected 'IN_DELIVERY' -Description 'Failed to start delivery'
    Write-Host '[6/8] Delivery status changed to IN_DELIVERY.' -ForegroundColor Green

    $delivered = Invoke-DeliveryFlowApi -Method PATCH -Path "/api/v1/deliveries/$($delivery.id)/status?status=DELIVERED" -Headers $driverHeaders
    Assert-Equal -Actual $delivered.status -Expected 'DELIVERED' -Description 'Failed to complete delivery'
    Write-Host '[7/8] Delivery status changed to DELIVERED.' -ForegroundColor Green

    $histories = @(Invoke-DeliveryFlowApi -Method GET -Path "/api/v1/deliveries/$($delivery.id)/histories" -Headers $adminHeaders | ForEach-Object { $_ })
    $dashboard = Invoke-DeliveryFlowApi -Method GET -Path "/api/v1/dashboard/delivery-status?scheduledDate=$ScheduledDate" -Headers $adminHeaders
    $historyStatuses = @($histories | ForEach-Object { $_.currentStatus })
    $expectedStatuses = @('ASSIGNED', 'IN_DELIVERY', 'DELIVERED')
    $missingStatuses = @($expectedStatuses | Where-Object { $_ -notin $historyStatuses })
    if ($missingStatuses.Count -gt 0) {
        throw "Expected delivery history statuses were not found: $($missingStatuses -join ', ')."
    }

    Write-Host '[8/8] Delivery history and dashboard check succeeded.' -ForegroundColor Green
    Write-Host ''
    [PSCustomObject]@{
        OrderId = $order.id
        OrderNo = $order.orderNo
        DeliveryId = $delivery.id
        FinalStatus = $delivered.status
        HistoryCount = $histories.Count
        DashboardDate = $dashboard.scheduledDate
    } | Format-List
}
finally {
    $adminPassword = $null
    $driverPassword = $null
    $adminToken = $null
    $driverToken = $null

    if ($null -ne $adminSecurePassword) { $adminSecurePassword.Dispose() }
    if ($null -ne $driverSecurePassword) { $driverSecurePassword.Dispose() }
}
