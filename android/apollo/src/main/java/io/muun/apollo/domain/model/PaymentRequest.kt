package io.muun.apollo.domain.model

import io.muun.apollo.data.external.Globals
import io.muun.apollo.domain.libwallet.DecodedInvoice
import io.muun.apollo.domain.libwallet.LibwalletBridge
import io.muun.common.utils.Preconditions
import javax.money.MonetaryAmount

data class PaymentRequest(val type: Type,
                          val amount: MonetaryAmount? = null,
                          val description: String? = null,
                          val contact: Contact? = null,
                          val address: String? = null,
                          val invoice: DecodedInvoice? = null,
                          val swap: SubmarineSwap? = null,
                          val feeInSatoshisPerByte: Double?, //initially null for AmountLess Invoice
                          val takeFeeFromAmount: Boolean = false) {

    enum class Type {
        TO_CONTACT,
        TO_ADDRESS,
        TO_LN_INVOICE
    }

    /**
     * Return a cloned PaymentRequest with a new amount and a new description.
     */
    fun withChanges(newAmount: MonetaryAmount, newDesc: String): PaymentRequest {
        return copy(amount = newAmount, description = newDesc)
    }

    /**
     * Warning: this change may leave the PayReq in an inconsistent state (specially for swaps).
     * Use it with caution. So far, we use it ONLY for special analysis after INSUFFICIENT_FUNDS.
     */
    fun withAmount(newAmount: MonetaryAmount) =
        copy(amount = newAmount)

    /**
     * Use this with caution. So far, we use it ONLY for special analysis after INSUFFICIENT_FUNDS.
     */
    fun withSwapAmount(newAmountInSat: Long) =
        copy(swap = swap?.withAmount(newAmountInSat))

    fun withFeeRate(newFeeInSatoshisPerByte: Double) =
        copy(feeInSatoshisPerByte = newFeeInSatoshisPerByte)

    fun withTakeFeeFromAmount(takeFeeFromAmount: Boolean) =
        copy(takeFeeFromAmount = takeFeeFromAmount)

    fun withSwap(swap: SubmarineSwap) =
        copy(swap = swap)

    fun toJson() =
        PaymentRequestJson(
            type,
            amount,
            description,
            contact?.toJson(),
            address,
            invoice?.original,
            swap?.toJson(),
            feeInSatoshisPerByte,
            takeFeeFromAmount
        )

    companion object {

        /** Create from {@link PaymentRequestJson}*/
        @JvmStatic
        fun fromJson(payReqJson: PaymentRequestJson) =
            PaymentRequest(
                payReqJson.type!!,
                payReqJson.amount,
                payReqJson.description,
                Contact.fromJson(payReqJson.contact),
                payReqJson.address,
                payReqJson.invoice?.let {
                    LibwalletBridge.decodeInvoice(Globals.INSTANCE.network, it)
                },
                SubmarineSwap.fromJson(payReqJson.swap),
                payReqJson.feeInSatoshisPerByte,
                payReqJson.takeFeeFromAmount
            )

        /** Create a PaymentRequest to send money to a contact. */
        @JvmStatic
        fun toContact(contact: Contact,
                      amount: MonetaryAmount,
                      description: String,
                      feeInSatoshisPerByte: Double): PaymentRequest {

            Preconditions.checkNotNull(contact)

            return PaymentRequest(
                Type.TO_CONTACT,
                amount = amount,
                description = description,
                contact = contact,
                feeInSatoshisPerByte = feeInSatoshisPerByte
            )
        }

        /** Create a PaymentRequest to send money to an address. */
        @JvmStatic
        fun toAddress(address: String,
                      amount: MonetaryAmount?,
                      description: String?,
                      feeInSatoshisPerByte: Double): PaymentRequest {

            Preconditions.checkNotNull(address)

            return PaymentRequest(
                Type.TO_ADDRESS,
                amount = amount,
                description = description,
                address = address,
                feeInSatoshisPerByte = feeInSatoshisPerByte
            )
        }

        /** Create a PaymentRequest to send money to an Invoice. */
        @JvmStatic
        fun toLnInvoice(invoice: DecodedInvoice,
                        amount: MonetaryAmount?,
                        description: String,
                        submarineSwap: SubmarineSwap,
                        feeInSatoshisPerByte: Double?): PaymentRequest {

            Preconditions.checkNotNull(invoice)

            return PaymentRequest(
                Type.TO_LN_INVOICE,
                amount = amount,
                description = description,
                invoice = invoice,
                swap = submarineSwap,
                feeInSatoshisPerByte = feeInSatoshisPerByte
            )
        }
    }
}
