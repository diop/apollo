package io.muun.apollo.domain.model;

import io.muun.apollo.domain.model.base.HoustonIdModel;
import io.muun.apollo.domain.utils.DateUtils;
import io.muun.common.crypto.hd.MuunAddress;
import io.muun.common.model.OperationDirection;
import io.muun.common.model.OperationStatus;
import io.muun.common.utils.Preconditions;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import org.threeten.bp.ZonedDateTime;

import javax.money.MonetaryAmount;
import javax.validation.constraints.NotNull;

public class Operation extends HoustonIdModel {

    @VisibleForTesting
    public static final long NO_HID = -1;
    private static final String NO_HASH = null;

    /**
     * Create a new Operation, with default values for Houston-generated fields.
     */
    public static Operation createOutgoing(@Nullable PublicProfile myProfile,
                                           @Nullable PublicProfile receiverProfile,
                                           String address,
                                           String addressDerivationPath,
                                           PreparedPayment preparedPayment) {
        return new Operation(
                null,
                NO_HID,
                null,
                myProfile == null || receiverProfile == null,
                myProfile,
                false,
                receiverProfile,
                receiverProfile == null,
                address,
                addressDerivationPath,
                preparedPayment.amount,
                preparedPayment.fee,
                0L,
                NO_HASH,
                preparedPayment.description,
                OperationStatus.CREATED,
                ZonedDateTime.now(),
                preparedPayment.rateWindowHid,
                null,
                null
        );
    }

    /**
     * Create a new Submarine Swap Operation.
     */
    public static Operation createSwap(@Nullable PublicProfile myProfile,
                                       PreparedPayment preparedPayment,
                                       SubmarineSwap swap) {
        return new Operation(
                null,
                NO_HID,
                OperationDirection.OUTGOING,
                true,
                myProfile,
                false,
                null,
                true,
                swap.getFundingOutput().getOutputAddress(),
                null,
                preparedPayment.amount,
                preparedPayment.fee,
                0L,
                NO_HASH,
                preparedPayment.description,
                OperationStatus.CREATED,
                DateUtils.now(),
                preparedPayment.rateWindowHid,
                swap,
                null
        );
    }

    @Nullable
    public OperationDirection direction;

    @NotNull
    public final Boolean isExternal;

    @Nullable
    public final PublicProfile senderProfile;

    @NotNull
    public final Boolean senderIsExternal;

    @Nullable
    public PublicProfile receiverProfile;

    @NotNull
    public Boolean receiverIsExternal;

    @Nullable
    public String receiverAddress;

    @Nullable
    public String receiverAddressDerivationPath;

    @Nullable
    public MuunAddress changeAddress;

    @NotNull
    public final BitcoinAmount amount;

    @NotNull
    public final BitcoinAmount fee;

    @NotNull
    public final Long confirmations;

    @Nullable
    public String hash;

    @Nullable
    public final String description;

    @NotNull
    public OperationStatus status;

    @NotNull
    public final ZonedDateTime creationDate;

    @NotNull
    public final Long exchangeRateWindowHid;

    @Nullable
    public final SubmarineSwap swap;

    @Nullable
    public final IncomingSwap incomingSwap;

    /**
     * Constructor.
     */
    public Operation(
            @Nullable Long id,
            @NotNull long hid,
            @NotNull OperationDirection direction,
            @NotNull Boolean isExternal,
            @Nullable PublicProfile senderProfile,
            @NotNull Boolean senderIsExternal,
            @Nullable PublicProfile receiverProfile,
            @NotNull Boolean receiverIsExternal,
            @Nullable String receiverAddress,
            @Nullable String receiverAddressDerivationPath,
            @NotNull BitcoinAmount amount,
            @NotNull BitcoinAmount fee,
            @NotNull Long confirmations,
            @Nullable String hash,
            @Nullable String description,
            @NotNull OperationStatus status,
            @NotNull ZonedDateTime creationDate,
            @NotNull Long exchangeRateWindowHid,
            @Nullable SubmarineSwap swap,
            @Nullable IncomingSwap incomingSwap) {

        super(id, hid);
        this.direction = direction;
        this.isExternal = isExternal;
        this.senderProfile = senderProfile;
        this.senderIsExternal = senderIsExternal;
        this.receiverProfile = receiverProfile;
        this.receiverIsExternal = receiverIsExternal;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDerivationPath = receiverAddressDerivationPath;
        this.amount = amount;
        this.fee = fee;
        this.confirmations = confirmations;
        this.hash = hash;
        this.description = description;
        this.status = status;
        this.creationDate = creationDate;
        this.exchangeRateWindowHid = exchangeRateWindowHid;
        this.swap = swap;
        this.incomingSwap = incomingSwap;
    }

    /**
     * Whether this operation was sent and received by the same User.
     */
    public boolean isCyclical() {
        return direction == OperationDirection.CYCLICAL;
    }

    public boolean isFailed() {

        return status.equals(OperationStatus.DROPPED) || status.equals(OperationStatus.FAILED);
    }

    public boolean isLendingSwap() {

        return swap != null && swap.isLend();
    }

    /**
     * Get the creation date of the operation, localized to the system timezone.
     */
    public ZonedDateTime getLocalizedCreationDate() {

        return DateUtils.toSystemDefault(creationDate);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public MonetaryAmount getTotal() {
        return this.amount.inInputCurrency.add(this.fee.inInputCurrency);
    }

    /**
     * Merge this Operation with an updated copy, choosing whether to replace each field.
     */
    public Operation mergeWithUpdate(Operation other) {
        Preconditions.checkArgument(other.getId() == null || getId().equals(other.getId()));

        return new Operation(
                getId(),
                other.getHid(),
                other.direction,
                isExternal,
                senderProfile,
                senderIsExternal,
                other.receiverProfile,
                other.receiverIsExternal,
                other.receiverAddress,
                other.receiverAddressDerivationPath,
                amount,
                fee,
                confirmations,
                hash,
                description,
                other.status,
                creationDate,
                exchangeRateWindowHid,
                swap,
                incomingSwap
        );
    }
}
