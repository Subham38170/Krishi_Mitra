package com.example.krishimitra.data.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.krishimitra.domain.repo.NetworkConnectivityObserver
import com.example.krishimitra.domain.repo.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class NetworkConnectivityObserverImpl(
    context: Context,
    scope: CoroutineScope
) : NetworkConnectivityObserver {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkStatus = observer().stateIn(
        scope,
        SharingStarted.WhileSubscribed(5000),
        NetworkStatus.Disconnected
    )
    override val networkStatus: StateFlow<NetworkStatus> = _networkStatus

    private fun observer(): Flow<NetworkStatus> {
        return callbackFlow {
            val connectivityCallback = object : NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(NetworkStatus.Disconnected)

                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkStatus.Connected)

                }
            }
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
            connectivityManager.registerNetworkCallback(request, connectivityCallback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(connectivityCallback)
            }
        }.distinctUntilChanged()
    }
}