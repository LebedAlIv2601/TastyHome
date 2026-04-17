sealed class IosSpmDeps(
    val cinteropName: String,
    val url: String,
    val products: List<String>,
    val version: String
) {
    data object DeviceKit : IosSpmDeps(
        cinteropName = "deviceKitBridge",
        url = "https://github.com/devicekit/DeviceKit.git",
        products = listOf("DeviceKit"),
        version = "5.7.0"
    )
}