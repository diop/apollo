package io.muun.apollo.domain.action.incoming_swap

import android.util.Log
import io.muun.apollo.data.net.HoustonClient
import io.muun.apollo.data.preferences.KeysRepository
import io.muun.apollo.domain.libwallet.errors.UnfulfillableIncomingSwapError
import io.muun.apollo.domain.model.IncomingSwap
import org.bitcoinj.core.NetworkParameters
import rx.Completable
import javax.inject.Inject

class VerifyFulfillableAction
@Inject constructor(
    private val keysRepository: KeysRepository,
    private val houstonClient: HoustonClient,
    private val networkParameters: NetworkParameters,
    private val incomingSwap: io.muun.apollo.domain.libwallet.IncomingSwap
) {
    fun action(swap: IncomingSwap): Completable {

        return keysRepository.basePrivateKey
            .flatMapCompletable { userKey ->

                try {
                    incomingSwap.verifyFulfillable(
                        swap,
                        userKey,
                        networkParameters
                    )

                } catch (e: UnfulfillableIncomingSwapError) {
                    Log.w("", "Will expire invoice due to unfulfillable swap", e)

                    houstonClient.expireInvoice(swap.paymentHash)
                }

                Completable.complete()

            }.toCompletable()
    }
}