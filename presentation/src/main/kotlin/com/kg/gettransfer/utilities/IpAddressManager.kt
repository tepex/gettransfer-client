package com.kg.gettransfer.utilities

import org.koin.core.KoinComponent
import java.net.Inet4Address
import java.net.NetworkInterface.getNetworkInterfaces

class IpAddressManager : KoinComponent {

    fun getIpAddress(): String? {
        getNetworkInterfaces().toList().forEach { networkInterface ->
            networkInterface.inetAddresses.toList().forEach { address ->
                if (!address.isLoopbackAddress && address is Inet4Address) return address.hostAddress
            }
        }
        return null
    }
}