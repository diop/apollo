package io.muun.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    @Nullable
    public String hash;

    public Long confirmations;

    public Boolean isReplaceableByFee;

    @Nullable // nullable just for retrocompat.
    public Long sizeInVbytes;

    @Nullable // nullable just for retrocompat.
    public Long feeInSat;

    @Nullable // Null if transaction is incoming or is not in the mempool.
    public Set<DescendantFeesJson> inMempoolDescendantFees; // excluding this one.

    @Nullable // Null if transaction is incoming.
    public List<SizeForAmountJson> spentOutputs;

    /**
     * Json constructor.
     */
    public Transaction() {
    }

    /**
     * Houston constructor.
     */
    public Transaction(@Nullable String hash,
                       Long confirmations,
                       Boolean isReplaceableByFee,
                       @Nullable Long sizeInVbytes,
                       @Nullable Long feeInSat,
                       @Nullable Set<DescendantFeesJson> inMempoolDescendantFees,
                       @Nullable List<SizeForAmountJson> spentOutputs) {

        this.hash = hash;
        this.confirmations = confirmations;
        this.isReplaceableByFee = isReplaceableByFee;
        this.inMempoolDescendantFees = inMempoolDescendantFees;
        this.feeInSat = feeInSat;
        this.sizeInVbytes = sizeInVbytes;
        this.spentOutputs = spentOutputs;
    }
}
